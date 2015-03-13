/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package downloader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.BlockLocation;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileSplit;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.JobContext;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
/**
 *
 * @author LSAdmin
 */
public class DownloadInputFormat extends FileInputFormat<IntWritable, Text> {

    
    public List<InputSplit> getSplits(JobContext job){
        List<InputSplit> splits=new ArrayList<InputSplit>();
        ArrayList<String> nodes=new ArrayList<String>();  
        try {  
            String temppath="hadoop_img_temp";
            Configuration conf=job.getConfiguration();
            FileSystem filesystem=FileSystem.get(conf);
            Path tempdir=new Path(temppath);
            if(filesystem.exists(tempdir)){
                filesystem.delete(tempdir, true);
            }
            
            filesystem.mkdirs(tempdir);
            for(int i=0;i<10;i++){
                for(int j=0;j<nodes.size();j++){
                    String temp=temppath+"/"+j+"_temp";
                    Path tempp=new Path(temp);
                    FSDataOutputStream fdos=filesystem.create(tempp);
                    fdos.write(1);
                    fdos.close();
                    
                    FileStatus status=filesystem.getFileStatus(tempp);
                    long len=status.getLen();
                    BlockLocation[] blocks=filesystem.getFileBlockLocations(status, 0, len);
                    int k;
                    for(k=0;k<nodes.size();k++){
                        if(blocks[0].getHosts()[0].compareTo(nodes.get(j))==0){
                            System.out.println("Pinged the same host");
                            break;
                        }
                    }
                    if(k==nodes.size()){
                        nodes.add(blocks[0].getHosts()[0]);
                    }
                }
            }
            FileStatus inpFile=listStatus(job).get(0);
            Path path=inpFile.getPath();
            BufferedReader br=new BufferedReader(new InputStreamReader(filesystem.open(path)));
            int count=0;
            while(br.readLine()!=null){
                count++;
            }
            br.close();
            
            int defsize=(int) Math.ceil(((double)(count)/(float)nodes.size()));
            int lastsize=count-defsize*(nodes.size()-1);
            
            FileSplit[] fs=new FileSplit[nodes.size()];
            int i;
            for(i=0;i<fs.length-1;i++){
                String thisNode=nodes.get(i);
                String arr[]={thisNode};
                splits.add(new FileSplit(path, i*defsize,defsize,arr));
            }
            if(lastsize>1){
                String thisNode=nodes.get(i);
                String arr[]={thisNode};
                splits.add(new FileSplit(path,i*defsize,lastsize,arr));
            }

        } catch (IOException ex) {
            Logger.getLogger(DownloadInputFormat.class.getName()).log(Level.SEVERE, null, ex);
        }
        return splits;
    }

    @Override
    public RecordReader<IntWritable, Text> createRecordReader(InputSplit is, TaskAttemptContext tac) throws IOException, InterruptedException {
                return (RecordReader<IntWritable, Text>) new DownloadRecordReader();
    }
    
    
    
}
