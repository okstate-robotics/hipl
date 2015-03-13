/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package downloader;

import edu.okstate.cs.hipl.bundleIO.BundleWriter;
import edu.okstate.cs.hipl.bundleIO.MapBundleWriter;
import edu.okstate.cs.hipl.bundleIO.SequenceBundleWriter;
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
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;

/**
 *
 * @author LSAdmin
 */
public class BundleDownloader  extends Configured implements Tool{
    static String filePath;
    static String filetype;
    static String inpPath;
    
    
    @Override
    public int run(String args[]) throws IOException, InterruptedException, ClassNotFoundException{
        inpPath=args[0];
        filePath=args[1];
        filetype="seq";
        if(args.length>2){
            filetype=args[2];
        }
        
        Configuration conf = new Configuration();
        String outputPath = filePath.substring(0, filePath.lastIndexOf('/')+1);
        System.out.println("Output HIB: " + outputPath);
        
        Job job = new Job(conf, "downloader");
        job.setJarByClass(BundleDownloader.class);
        job.setMapperClass(Map.class);
        job.setReducerClass(DownloaderReducer.class);

        job.setOutputKeyClass(BooleanWritable.class);
        job.setOutputValueClass(Text.class);       
        job.setInputFormatClass(DownloadInputFormat.class);

        FileOutputFormat.setOutputPath(job, new Path(filePath + "_output"));

        DownloadInputFormat.setInputPaths(job, new Path(inpPath));

        job.setNumReduceTasks(1);
        System.exit(job.waitForCompletion(true) ? 0 : 1);
        return 1;
    }
    
    public static class Map extends Mapper<IntWritable, Text, BooleanWritable,Text>{
        public static Configuration conf;
        
        @Override
        public void setup(Context c){
            conf=c.getConfiguration();
        }
        
        public void map(IntWritable k1, Text v1, OutputCollector<BooleanWritable, Text> oc, Reporter rprtr) throws IOException, InterruptedException {
            String temppath=BundleDownloader.filePath+k1.get()+"."+BundleDownloader.filetype+".tmp";
            System.out.println(temppath);
            BundleWriter bw = null;
            if("seq".equals(BundleDownloader.filetype)){
                bw=new SequenceBundleWriter(new Path(temppath), conf);
            }
            else if("map".equals(BundleDownloader.filetype)){
                bw=new MapBundleWriter(new Path(temppath), conf);
            }
            else{
                bw=new SequenceBundleWriter(new Path(temppath), conf);
            }
            String word=v1.toString();
            
            BufferedReader reader=new BufferedReader(new StringReader(word));
            
            String temp;
            int i=k1.get();
            int prev=i;
            while((temp=reader.readLine())!=null){
                if(i>=prev+80){
                    bw.close();
                    oc.collect(new BooleanWritable(true), new Text(temppath));
                    temppath=BundleDownloader.filePath+i+".tmp";
                    if("seq".equals(BundleDownloader.filetype)){
                        bw=new SequenceBundleWriter(new Path(temppath), conf);
                    }
                    else if("map".equals(BundleDownloader.filetype)){
                        bw=new MapBundleWriter(new Path(temppath), conf);
                    }
                    else{
                        bw=new SequenceBundleWriter(new Path(temppath), conf);
                    }
                    prev=i;
                }
                
                long start=System.currentTimeMillis();
                long end=0;
                
                String type=null;
                URLConnection conn;
                
                URL link=new URL(temp);
                System.out.println("Image Downloading "+link.toString());
                conn=link.openConnection();
                conn.connect();
                
                if(type!=null && type.compareTo("image/jpeg")==0)
                    bw.appendImage(conn.getInputStream());
                
                i++;
                
                end=System.currentTimeMillis();
                
                System.out.println("TOOK :"+(float)((end-start)/1000.0)+" seconds\n");
                Thread.sleep(1000);
                reader.close();
                bw.close();
                
            }
            oc.collect(new BooleanWritable(true), new Text(bw.getBundleFile().getPath().toString()));
        } 
    }
    
    public static class DownloaderReducer extends Reducer<BooleanWritable, Text, BooleanWritable, Text> {
      
        private static Configuration conf;
       
        @Override
        public void setup(Context c){
            conf=c.getConfiguration();
        }
        
        @Override
        public void reduce(BooleanWritable key, Iterable<Text> values, Context context) throws IOException, InterruptedException{
            if(key.get()){
                FileSystem fs=FileSystem.get(conf);
                BundleWriter bw = null;
                if("seq".equals(BundleDownloader.filetype)){
                    bw=new SequenceBundleWriter(new Path(BundleDownloader.filePath), conf);
                }
                else if("map".equals(BundleDownloader.filetype)){
                    bw=new MapBundleWriter(new Path(BundleDownloader.filePath), conf);
                }
                else{
                    bw=new SequenceBundleWriter(new Path(BundleDownloader.filePath), conf);
                }
                
                for(Text path:values){
                    Path temp=new Path(path.toString());
                    bw.appendBundle(temp,conf);  
                }
                
                context.write(new BooleanWritable(true), new Text(bw.getBundleFile().getPath().toString()));
		context.progress();
            }
        }    
    }
    
    
    
    
}
