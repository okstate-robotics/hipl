/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package edu.okstate.cs.hipl.bundleIO;

import edu.okstate.cs.hipl.bundle.BundleFile;
import edu.okstate.cs.hipl.image.HImage;
import edu.okstate.cs.hipl.util.Config;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.MapFile;



/**
 *
 * @author sridhar
 */
public class MapBundleWriter implements BundleWriter{
    private Config _hConf;
    private MapFile.Writer _mapWriter;
    private long _mapTotal;
    private BundleFile _file;
    
    public MapBundleWriter(BundleFile file){
        _file=file;
        _hConf=file.getHConfig();
        openToWrite();
    }
    
    public MapBundleWriter(String path, Configuration conf){
        _hConf=new Config(path, conf);
        _file=new BundleFile(path,conf);
        openToWrite();
    }
    
    public MapBundleWriter(Path path, Configuration conf){
        _hConf=new Config(path, conf);
        _file=new BundleFile(path,conf);
        openToWrite();
    }
    
    @Override
    public void openToWrite() {
        try {
            
            createDir(_hConf.getPath(),_hConf.getConfiguration());
            // Option opt1=MapFile.Writer.;
           
            _mapWriter = new MapFile.Writer(_hConf.getConfiguration(),_hConf.getFileSystem(),_hConf.getPath().toString(),LongWritable.class,BytesWritable.class);
            _mapTotal=1;
        } catch (IOException ex) {
            Logger.getLogger(MapBundleWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    @Override
    public void appendImage(HImage himage) {
        try {
              if(himage.getImageBytes()!=null){
                    _mapWriter.append(new LongWritable(_mapTotal), new BytesWritable(himage.getImageBytes()));
                    _mapTotal++;
              }
        } catch (IOException ex) {
            Logger.getLogger(SequenceBundleWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public void appendImage(InputStream inputstream) {
        HImage temp=new HImage(inputstream);
        appendImage(temp);
    }
    
    @Override
    public void appendImage(File file) {
        HImage temp=new HImage(file);
        appendImage(temp);
    }
    
    @Override
    public Config getConfiguration() {
        return _hConf;
    }
    
    @Override
    public long getImageCount() {
        return _mapTotal;
    }
    
    @Override
    public void close() {
        try {
            if(_mapWriter!=null){
                _mapWriter.close();
            }
        } catch (IOException ex) {
            Logger.getLogger(SequenceBundleReader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public void appendBundle(Path path,Configuration conf) {
        
        try {
            System.out.println("MERGE STARTED: "+_hConf.getPath()+" and "+path +":"+System.currentTimeMillis());
            long start=System.currentTimeMillis();
            MapBundleReader reader=new MapBundleReader(path,conf);
            while(reader.hasNext()){
                _mapWriter.append(new LongWritable(_mapTotal), reader.getValue());
                _mapTotal++;
            }
            reader.close();
            FileSystem fs=FileSystem.get(conf);
            fs.delete(path, true);
            long end=System.currentTimeMillis();
            System.out.println("MERGE ENDED : "+_hConf.getPath()+" and "+path+" in "+(end-start)+" ms");
        } catch (IOException ex) {
            Logger.getLogger(SequenceBundleWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    public void appendBundle(BundleFile file){
        appendBundle(file.getPath(),file.getConfiguration());
    }
    
    @Override
    public BundleFile getBundleFile(){
        return _file;
    }
    
    public void createDir(Path path, Configuration conf) throws IOException {
        Path output_path = (path);
        
        FileSystem fs = FileSystem.get(conf);
        
        if (!fs.exists(output_path)) {
            fs.mkdirs(output_path);
        }
    }
}
