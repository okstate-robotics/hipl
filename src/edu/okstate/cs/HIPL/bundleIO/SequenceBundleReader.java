/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.okstate.cs.hipl.bundleIO;

import edu.okstate.cs.hipl.bundle.BundleFile;
import edu.okstate.cs.hipl.image.HImage;
import edu.okstate.cs.hipl.util.Config;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.SequenceFile.Reader.Option;

/**
 *
 * @author sridhar
 */
public class SequenceBundleReader implements BundleReader{

    private SequenceFile.Reader _seqReader;
    private long _seqTotal=0;
    private Config _hConf=null;
    private BundleFile _file=null;
    long _tempKey=0;
    HImage _tempImage=null;
    private BytesWritable _tempImageBytes=null;
    
    public SequenceBundleReader(BundleFile file){
        _file=file;
        _hConf=file.getHConfig();
        openToRead();
    }
    
    public SequenceBundleReader(String path, Configuration conf){
        _hConf=new Config(path,conf);
        _file=new BundleFile(path, conf);
        openToRead();
    }
    
    public SequenceBundleReader(Path path, Configuration conf){
        _hConf=new Config(path,conf);
        _file=new BundleFile(path, conf);
        openToRead();
    }
    
    @Override
    public void openToRead() {
        try {
            Option opt1=SequenceFile.Reader.file(_hConf.getPath());
            //Option opt2=SequenceFile.Reader.keyClass(LongWritable.class);
            //Option opt3=SequenceFile.Writer.valueClass(BytesWritable.class);
            _seqReader=new SequenceFile.Reader(_hConf.getConfiguration(),opt1);
            
           // _seqReader=new SequenceFile.Reader(_hConf.getFileSystem(), _hConf.getPath(), _hConf.getConfiguration());
        } catch (IOException ex) {
            Logger.getLogger(SequenceBundleReader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public boolean hasNext() {
        try {
            LongWritable key=new LongWritable();
            BytesWritable image=new BytesWritable();
            
            if(_seqReader.next(key,image)){
                _tempKey=key.get();   
                _tempImageBytes=image;
                _tempImage=new HImage(image.getBytes());
                return true;
            }
            return false;
        } catch (IOException ex) {
            return false;
        }
    }

    @Override
    public HImage next() {
        _seqTotal++;
        return _tempImage;
    }

    @Override
    public void close() {
        try {
            if(_seqReader!=null){ 
                    _seqReader.close();
            }
        } catch (IOException ex) {
            Logger.getLogger(SequenceBundleReader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public long getReturnCount() {
        return _seqTotal;
    }

    @Override
    public Config getConfiguration() {
        return _hConf;
    }
    
    public BytesWritable getValue(){
        return _tempImageBytes;
    }
}
