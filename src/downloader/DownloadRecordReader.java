/*2
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package downloader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;

/**
 *
 * @author LSAdmin
 */
public class DownloadRecordReader extends RecordReader<IntWritable, Text>{
    
    private String value;
    private long key;
    private boolean nextVal;
    @Override
    public void initialize(InputSplit is, TaskAttemptContext tac) throws IOException, InterruptedException {
        FileSplit fs=(FileSplit)is;
        Path path=fs.getPath();
        Configuration conf=tac.getConfiguration();
        FileSystem fss=path.getFileSystem(conf);
        key=fs.getStart();
        nextVal=false;
        
        BufferedReader br=new BufferedReader(new InputStreamReader(fss.open(path)));
        String line;
        value="";
        while((line=br.readLine())!=null){
            value=value+line+"\n";
        }
        br.close();
    }

    @Override
    public boolean nextKeyValue() throws IOException, InterruptedException {
        if(nextVal==false){
            nextVal=true;
            return true;
        }
        else
            return false;
    }

    @Override
    public IntWritable getCurrentKey() throws IOException, InterruptedException {
        return new IntWritable((int)key);
    }

    @Override
    public Text getCurrentValue() throws IOException, InterruptedException {
        return new Text(value);
    }

    @Override
    public float getProgress() throws IOException, InterruptedException {
        if(nextVal){
            return 1.0f;
        }else{
            return 0.0f;
        }
    }

    @Override
    public void close() throws IOException {
        return;
    }
    
}
