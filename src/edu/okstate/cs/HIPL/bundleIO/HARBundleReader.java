/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.okstate.cs.hipl.bundleIO;

import edu.okstate.cs.hipl.bundle.BundleFile;
import edu.okstate.cs.hipl.bundleIO.BundleReader;
import edu.okstate.cs.hipl.image.HImage;
import edu.okstate.cs.hipl.util.Config;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.HarFileSystem;
import org.apache.hadoop.fs.Path;

/**
 *
 * @author Sridhar
 */
public class HARBundleReader implements BundleReader {
    private BundleFile _file;
    private Config _hConf;
    private HarFileSystem _harFS=null;
    private FileStatus[] _harFiles;
    private int _current=0;
    private int _harTotal;
    
    private FSDataInputStream _reader;
    private byte[] _tempImageBytes;
    private int _tempType;
   
    
    
    public HARBundleReader(BundleFile file) {
        _file=file;
        _hConf=file.getHConfig();
        openToRead();
        
    }

    public HARBundleReader(Path path, Configuration conf) {
        _file=new BundleFile(path, conf);
        _hConf=new Config(path,conf);
        openToRead();
    }

    @Override
    public void openToRead() {
        try {
            _harFS=new HarFileSystem(FileSystem.get(_hConf.getConfiguration()));
            Path path=new Path("har://"+_file.getPath().toUri().getPath());
            System.out.println("MY PATH IS :"+path.toUri().toString());
            _harFS.initialize(path.toUri(), _hConf.getConfiguration());
            
            _harFiles=_harFS.listStatus(path);
            _harTotal=_harFiles.length;
            System.out.println("HAR TOTAL"+_harTotal); 
            _current=0;
        
        } catch (IOException ex) {
            Logger.getLogger(HARBundleReader.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    @Override
    public boolean hasNext() {
         try {
        if(_current<_harTotal){
                _reader=_harFS.open(_harFiles[_current].getPath());
                _tempType=(int)(_reader.readInt());
                _tempImageBytes=new byte[_reader.available()];
                System.out.println("Count "+_tempType+" bytes length "+_tempImageBytes.length);
                _reader.read(_tempImageBytes);
                _current++;
                return true;
            }
               else{
              return false;
                }
} catch (IOException ex) {
                return false;
}
    }

    @Override
    public HImage next() {
        if(_harFiles!=null){
           return new HImage(_tempImageBytes);
        }
        return null;
    }

    @Override
    public long getReturnCount() {
        return _current;
    }

    @Override
    public Config getConfiguration() {
        return _hConf;
        
    }

    @Override
    public void close() {
        if(_reader!=null){
            try {
                _reader.close();
                _harFS.close();
            } catch (IOException ex) {
                Logger.getLogger(HARBundleReader.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    
}
