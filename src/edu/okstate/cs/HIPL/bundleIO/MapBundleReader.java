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
import org.apache.hadoop.io.MapFile;

/**
 *
 * @author sridhar
 */
public class MapBundleReader implements BundleReader{
    private Config _hConf;
    private MapFile.Reader _mapReader;
    private long _totalCount;
    private long _tempKey;
    private HImage _tempImage;
    private BundleFile _file;
    private BytesWritable _tempBytes;
    
    public MapBundleReader(BundleFile file){
        _file=file;
        _hConf=file.getHConfig();
        openToRead();
    }
    
    public MapBundleReader(String path, Configuration conf){
        _hConf=new Config(path,conf);
        _file=new BundleFile(path,conf);
        openToRead();
    }
    
    public MapBundleReader(Path path, Configuration conf){
        _hConf=new Config(path,conf);
        _file=new BundleFile(path,conf);
        openToRead();
    }
    
    @Override
    public void openToRead() {
        try {
            _mapReader=new MapFile.Reader(_hConf.getPath(),_hConf.getConfiguration());
            _totalCount=1;
        } catch (IOException ex) {
            Logger.getLogger(MapBundleReader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public boolean hasNext() {
        try {
            LongWritable key=new LongWritable();
            BytesWritable image=new BytesWritable();
            
            if(_mapReader.next(key,image)){
                _tempKey=key.get();   
                _tempBytes=image;
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
        _totalCount++;
        return _tempImage;
    }

    @Override
    public long getReturnCount() {
        return _totalCount;
    }

    @Override
    public Config getConfiguration() {
        return _hConf;
    }

    @Override
    public void close() {
         try {
            if(_mapReader!=null){ 
                    _mapReader.close();
            }
        } catch (IOException ex) {
            Logger.getLogger(SequenceBundleReader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public BytesWritable getValue() {
        return _tempBytes;
    }
    
}
