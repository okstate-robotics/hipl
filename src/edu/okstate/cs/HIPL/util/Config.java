package edu.okstate.cs.hipl.util;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

public class Config {
    private Configuration _conf;
    private Path _path;
    private FileSystem _fs;
    
    
    public Config() {
        super();
    }
    
    public Config(Path path,Configuration conf){
        this._path=path;
        this._conf=conf;
        generate();
        
        
    }
    
    public Config(String path, Configuration conf){
     
            this._conf=conf;
            this._path=new Path(path);
           
        
    }

    private void generate() {
        try {
            this._fs=FileSystem.get(this._path.toUri(),this._conf);
        } catch (IOException ex) {
            Logger.getLogger(Config.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public Path getPath(){
        return this._path;
    }
    
    public FileSystem getFileSystem(){
        return this._fs;
    }
    
    public Configuration getConfiguration(){
        return this._conf;
    }
}
