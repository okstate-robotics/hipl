package edu.okstate.cs.hipl.exdown;


import edu.okstate.cs.hipl.bundle.BundleFile;
import edu.okstate.cs.hipl.bundleIO.BundleWriter;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.BooleanWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

/**
 * A utility MapReduce program that takes a list of image URL's, downloads them, and creates
 * a {@link hipi.imagebundle.HipiImageBundle} from them.
 *
 * When running this program, the user must specify 3 parameters. The first is the location
 * of the list of URL's (one URL per line), the second is the output path for the HIB that will
 * be generated, and the third is the number of nodes that should be used during the
 * program's execution. This final parameter should be chosen with respect to the total
 * bandwidth your particular cluster is able to handle. An example usage would be:
 * <br /><br />
 * downloader.jar /path/to/urls.txt /path/to/output.hib 10
 * <br /><br />
 * This program will automatically force 10 nodes to download the set of URL's contained in
 * the input list, thus if your list contains 100,000 images, each node in this example will
 * be responsible for downloading 10,000 images.
 *
 */
public class Downloader extends Configured implements Tool{
    
    private String[] args;
  
    public static class DownloaderMapper extends Mapper<IntWritable, Text, BooleanWritable, Text>
    {
        private static Configuration conf;
        // This method is called on every node
        @Override
        public void setup(Context jc) throws IOException
        {
            conf = jc.getConfiguration();
            
        }
        
        @Override
        public void map(IntWritable key, Text value, Context context)
                throws IOException, InterruptedException
        {
            
           
            /*  if(conf.get("downloader.outtype").equals("map")){
            String temp_path=conf.get("downloader.outpath")+key.get()+"_temp/";
            createDir(temp_path, conf);
            System.out.println("Outpath :"+temp_path);
            bw=new MapBundleWriter(temp_path,conf);
            }
            
            if(conf.get("downloader.outtype").equals("seq")){
            
            }
            */
            String temp_path = conf.get("downloader.outpath") + key.get() +"."+ conf.get("downloader.outtype");
            System.out.println("Temp path: " + temp_path);
            BundleFile bf=new BundleFile(temp_path,conf);
            
             BundleWriter bw=bf.getBundleWriter();
            
            //conf.set("mapreduce.map.java.opts", "-Xmx3000m");
            //conf.set("mapreduce.reduce.java.opts", "-Xmx6000m");
            
            String word = value.toString();
            BufferedReader reader = new BufferedReader(new StringReader(word));
            String uri;
            int i = key.get();
            
            long size=0;
            long blockSize=getBlockSize()-(1024*1024);
            while((uri = reader.readLine()) != null)
            {
                long startT=0;
                long stopT=0;
                startT = System.currentTimeMillis();
                
                try {
                    String type = "";
                    URLConnection conn;
                    // Attempt to download
                    context.progress();
                    
                    try {
                        URL link = new URL(uri);
                        System.out.println("Downloading " + link.toString());
                        conn = link.openConnection();
                        conn.connect();
                        type = conn.getContentType();
                        size=size+conn.getContentLengthLong();
                    } catch (Exception e)
                    {
                        System.out.println("Connection error to image : " + uri);
                        continue;
                    }
                    
                    if (type == null)
                        continue;
                    
                    if (type != null && compareMIMEType(type)){
                        if(!conf.get("downloader.outtype").equals("har")){
                            if(size>=blockSize){
                                bw.close();
                                context.write(new BooleanWritable(true), new Text(bw.getBundleFile().getPath().toString()));
                                temp_path = conf.get("downloader.outpath") + i +"."+ conf.get("downloader.outtype");
                                bf=new BundleFile(temp_path,conf);
                                bw = bf.getBundleWriter();
                                size=0;
                            }
                        }
                        bw.appendImage(conn.getInputStream());
                    }
                    
                } catch(Exception e)
                {
                    e.printStackTrace();
                    System.out.println("Error... probably cluster downtime");
                    try
                    {
                        Thread.sleep(1000);
                    } catch (InterruptedException e1)
                    {
                        e1.printStackTrace();
                    }
                }   
                i++;

                // Emit success
                stopT = System.currentTimeMillis();
                float el = (float)(stopT-startT)/1000.0f;
                System.out.println("> Took " + el + " seconds\n");
            }
            
            
            try
            {
                reader.close();
                bw.close();
                context.write(new BooleanWritable(true), new Text(temp_path));
                
                } catch (Exception e)
            {
                e.printStackTrace();
            }
            
        }
        
        private long getBlockSize() {
            
            String x=conf.get("dfs.blocksize");
            x=x.toLowerCase();
            long z=0;
            if(x.matches("[0-9]+")){
                z=Long.parseLong(x);
            }
            else if(x.matches("[0-9]+[A-Za-z]")){
                
                char y=x.charAt(x.length()-1);
                z=Long.parseLong(x.substring(0, x.length()-1));
                System.out.println(y);
                
                switch(y){
                    case 'k': z=z<<10;
                    break;
                    case 'm': z=z<<20;
                    break;
                    case 'g' : z=z<<30;
                    break;
                    case 't' : z=z<<40;
                    break;
                    case 'p' : z=z<<50;
                    break;
                    case 'e' : z=z<<60;
                    break;
                }
                System.out.println(z);
            }
            if(z>0){
                return z;
            }
            return 134217728;
        }
        
        private boolean compareMIMEType(String type) {
            switch(type){
                case "image/jpeg" : return true;
                case "image/png"  : return true;
                case "image/tiff" : return true;
            }
            return false;
        }
    }
    
    public static class DownloaderReducer extends Reducer<BooleanWritable, Text, BooleanWritable, Text> {
        
        private static Configuration conf;
        private static BundleFile bf=null;
        private BundleWriter bw=null;
        @Override
        public void setup(Context jc) throws IOException
        {
            conf = jc.getConfiguration();
            if(bf==null){
                    bf=new BundleFile(new Path(conf.get("downloader.outfile")),conf);
                }
        }
        
        @Override
        public void reduce(BooleanWritable key, Iterable<Text> values, Context context)
                throws IOException, InterruptedException
        {
            if(key.get()){
                bw=bf.getBundleWriter();
                for (Text temp_string : values) {
                    Path temp_path = new Path(temp_string.toString());
                    System.out.println(temp_path.toString());
                    //BundleFile bf=new BundleFile(temp_path,conf);
                    bw.appendBundle(temp_path,conf);
                    
                }
                System.out.println("Apeends Completed");
                //context.write(new BooleanWritable(true), new Text("Done"));
                //context.progress();
                System.out.println("Calling close");
                 bw.close();
                //sbw.appendBundle(new Path(conf.get("downloader.outpath")),conf); 
            }
            
        }
    }
    
    
    
    
    @Override
    public int run(String[] args) throws Exception
    {
        
        // Read in the configuration file
        
        // Setup configuration
        Configuration conf = new Configuration();
        
        String inputFile = args[0];
        String outputFile = args[1];
      
        int nodes = 10;
        
        //String outType=args[3];
        
        String outputPath = outputFile.substring(0, outputFile.lastIndexOf('/')+1);
        System.out.println("Output Bundle: " + outputPath);
        
        
        conf.setInt("downloader.nodes", nodes);
        conf.setStrings("downloader.outfile", outputFile);
        conf.setStrings("downloader.outpath", outputPath);
        conf.setStrings("downloader.inptype", args[2]);
        conf.setStrings("downloader.outtype",args[3]);
        conf.setBoolean("dfs.support.append", true);
        
        
        Job job = new Job(conf, "Bundle Downloader");
        job.setJarByClass(Downloader.class);
        job.setMapperClass(DownloaderMapper.class);
        job.setReducerClass(DownloaderReducer.class);
        
        // Set formats
        job.setOutputKeyClass(BooleanWritable.class);
        job.setOutputValueClass(Text.class);
        job.setInputFormatClass(DownloaderInputFormat.class);
        //*************** IMPORTANT ****************\\
        job.setMapOutputKeyClass(BooleanWritable.class);
        job.setMapOutputValueClass(Text.class);
        FileOutputFormat.setOutputPath(job, new Path(outputFile + "_mr_down"));
        DownloaderInputFormat.setInputPaths(job, new Path(inputFile));
        job.setNumReduceTasks(1);
        return job.waitForCompletion(true) ? 0 : 1;
        
    }
   
    public static void createDir(String path, Configuration conf) throws IOException {
        Path output_path = new Path(path);
        
        FileSystem fs = FileSystem.get(conf);
        
        if (!fs.exists(output_path)) {
            fs.mkdirs(output_path);
        }
    }
    
    public static void init(String[] args) throws Exception {
        int res = ToolRunner.run((Tool) new Downloader(),args);
        System.out.println(res);
    }  
}