/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/

package edu.okstate.cs.hipl.process;

import edu.okstate.cs.hipl.util.Random;
import edu.okstate.cs.hipl.bundle.BundleFile;
import edu.okstate.cs.hipl.bundleIO.BundleWriter;
import edu.okstate.cs.hipl.image.HImage;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.BooleanWritable;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.SequenceFileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;

/**
 *
 * @author Sridhar
 */
public class ImageProcess extends Configured implements Tool{

   
    
    
    public static class ImageProcessMapper extends Mapper<LongWritable, BytesWritable, BooleanWritable, Text>{
        private static Configuration conf;
        private static BundleWriter bw=null;
        private static BundleFile bf=null;
        private static String temp="";
        @Override
        public void setup(Context jc){
            conf=jc.getConfiguration();
            temp= temp+conf.get("process.output")+"/_tmp/";
            
            try {
                createDir(temp, conf);
            } catch (IOException ex) {
                Logger.getLogger(ImageProcess.class.getName()).log(Level.SEVERE, null, ex);
            }
           // temp=temp+Random.random()+".out";
            bf=new BundleFile(new Path(temp+Random.random()+"."+conf.get("process.outtype")),conf);
            bw=bf.getBundleWriter();
        }
        
        @Override
        public void map(LongWritable key, BytesWritable value, Context context) throws IOException, InterruptedException{
            
            if(value!=null){
                /*       if(size>=blockSize){
                bw.close();
                context.write(new BooleanWritable(true), new Text(temp));
                temp=conf.get("processpath")+"."+Random.random()+".out";
                bf=new BundleFile(new Path(conf.get("processpath")+"."+Random.random()+".out"),conf);
                bw=bf.getBundleWriter();
                size=0;
                }*/
                HImage him=new HImage(value.getBytes());
                //him = runThreaded(him);
                him=runThreaded(him);
                bw.appendImage(him);
            }
        }
        
        public HImage runNormal(HImage him) {
            try {
                GenericAlgorithm pi=(GenericAlgorithm)Class.forName(conf.get("process.class")).newInstance();
                // GenericAlgorithm pi=(GenericAlgorithm) o;
                pi.setHImage(him);
                
                pi.run();
                HImage hi=pi.getProcessedImage();
                hi.setImageHeader(him.getImageHeader());
                return hi;
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(ImageProcess.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InstantiationException ex) {
                Logger.getLogger(ImageProcess.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(ImageProcess.class.getName()).log(Level.SEVERE, null, ex);
            }
            return null;
        }
        
        
        public HImage runThreaded(HImage him) throws InterruptedException{
            HImage[] split=splitImages(him);
            Thread[] thr=new Thread[split.length];
            for(int i=0;i<split.length;i++){
                thr[i]=new Thread(new Threader(split[i],conf.get("process.class")));
                thr[i].start();
            }
            for(int j=0;j<thr.length;j++){
                thr[j].join();
                
            }
            HImage out=mergeImages(split);
            out.setImageHeader(him.getImageHeader());
            return out;
        } 
       
        @Override
        public void cleanup(Context context){
            try {
                context.write(new BooleanWritable(true), new Text(temp));
                bw.close();
            } catch (IOException ex) {
                Logger.getLogger(ImageProcess.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InterruptedException ex) {
                Logger.getLogger(ImageProcess.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        
       public HImage[] splitImages(HImage img){
        int rows = 8; //You should decide the values for rows and cols variables
        int cols = 1;
        int chunks = rows * cols;
        BufferedImage image=img.getBufferedImage();
        int chunkWidth = image.getWidth() / cols; // determines the chunk width and height
        int chunkHeight = image.getHeight() / rows;
        int count = 0;
        BufferedImage imgs[] = new BufferedImage[chunks]; //Image array to hold image chunks
        for (int x = 0; x < rows; x++) {
            for (int y = 0; y < cols; y++) {
                //Initialize the image array with image chunks
                imgs[count] = new BufferedImage(chunkWidth, chunkHeight, image.getType());
                
                // draws the image chunk
                Graphics2D gr = imgs[count++].createGraphics();
                gr.drawImage(image, 0, 0, chunkWidth, chunkHeight, chunkWidth * y, chunkHeight * x, chunkWidth * y + chunkWidth, chunkHeight * x + chunkHeight, null);
                gr.dispose();
            }
        }
        HImage him[]=new HImage[imgs.length];
        for(int i=0;i<imgs.length;i++){
            him[i]=new HImage(imgs[i]);
        }
        return him;
    }
    public static HImage mergeImages(HImage[] buff){
        int rows = 8;   //we assume the no. of rows and cols are known and each chunk has equal width and height
        int cols = 1;
        
        
        int chunkWidth, chunkHeight;
        int type;
        BufferedImage[] buffImages=new BufferedImage[buff.length];
        for(int i=0;i<buff.length;i++){
            buffImages[i]=buff[i].getBufferedImage();
        }
        
        type = buffImages[0].getType();
        chunkWidth = buffImages[0].getWidth();
        chunkHeight = buffImages[0].getHeight();
        
        //Initializing the final image
        BufferedImage finalImg = new BufferedImage(chunkWidth*cols, chunkHeight*rows, type);
        
        int num = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                finalImg.createGraphics().drawImage(buffImages[num], chunkWidth * j, chunkHeight * i, null);
                num++;
            }
        }
        return new HImage(finalImg);
    }
        
        
    }
    
    
    public static class ImageProcessReducer extends Reducer<BooleanWritable, Text, BooleanWritable, Text>{
        private Configuration conf;
        private static BundleWriter bw=null;
        private static BundleFile bf=null;
        private static String temp="";
        @Override
        public void setup(Context jc) throws IOException{
            conf=jc.getConfiguration();
            temp= conf.get("process.output")+"/_tmp/";
            bf=new BundleFile(new Path(conf.get("process.output")+"."+conf.get("process.outtype")), conf);
            bw=bf.getBundleWriter();
        }
        
        @Override
        public void reduce(BooleanWritable key, Iterable<Text> values, Context context) throws IOException, InterruptedException{
            FileSystem fs = FileSystem.get(conf);
            FileStatus[] status = fs.listStatus(new Path(temp));
            for (int i=0;i<status.length;i++){
                Path temp_path = status[i].getPath();
                bw.appendBundle(temp_path,conf);
            }
            bw.close();
            
        }
        
        @Override
        public void cleanup(Context context){
            try {
                removeDir(temp, conf);
            } catch (IOException ex) {
                Logger.getLogger(ImageProcess.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    
    
    @Override
    public int run(String[] args) throws Exception {
        
        Configuration conf=new Configuration();
        //conf.set("mapreduce.map.java.opts", "-Xmx6000m");
       // conf.set("mapreduce.reduce.java.opts", "-Xmx6000m");
        
        String outfile=args[1];
        
        conf.setStrings("process.input", args[0]);
        conf.setStrings("process.output", args[1]);
        conf.setStrings("process.inptype", args[2]);
        conf.setStrings("process.outtype", args[3]);
        conf.setStrings("process.class", args[4]);
       
        
        Job job=new Job(conf,"Processor");
        job.setJarByClass(ImageProcess.class);
        job.setMapperClass(ImageProcessMapper.class);
        job.setReducerClass(ImageProcessReducer.class);
        job.setNumReduceTasks(1);
        
        job.setOutputKeyClass(BooleanWritable.class);
        job.setOutputValueClass(Text.class);
        switch(conf.get("process.inptype")){
            case "seq": job.setInputFormatClass(SequenceFileInputFormat.class);
                        break;
            case "map": job.setInputFormatClass(SequenceFileInputFormat.class);
                        break;
            default : job.setInputFormatClass(SequenceFileInputFormat.class);
                        break;
        }   
        
        job.setMapOutputKeyClass(BooleanWritable.class);
        job.setMapOutputValueClass(Text.class);
        FileOutputFormat.setOutputPath(job, new Path( outfile+ "_mr_pro"));
        if(conf.get("process.inptype").equals("map")){
            FileInputFormat.addInputPath(job, new Path(args[0]+"/data"));
        }else{
            FileInputFormat.addInputPath(job, new Path(args[0]));
        }
        return job.waitForCompletion(true)?0:1;
    }
    
    public static void createDir(String path, Configuration conf) throws IOException {
        Path output_path = new Path(path);
        
        FileSystem fs = FileSystem.get(conf);
        
        if (!fs.exists(output_path)) {
            fs.mkdirs(output_path);
        }
    }
    
    public static void removeDir(String path, Configuration conf) throws IOException {
        Path output_path = new Path(path);
        FileSystem fs = FileSystem.get(conf);
        if (!fs.exists(output_path)) {
            fs.delete(output_path,true);
        }
    }
}
