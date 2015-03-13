/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.okstate.cs.hipl.bundle;

import edu.okstate.cs.hipl.bundleIO.BundleReader;
import edu.okstate.cs.hipl.bundleIO.BundleWriter;
import edu.okstate.cs.hipl.bundleIO.HARBundleWriter;
import edu.okstate.cs.hipl.bundleIO.MapBundleReader;
import edu.okstate.cs.hipl.bundleIO.MapBundleWriter;
import edu.okstate.cs.hipl.bundleIO.SequenceBundleReader;
import edu.okstate.cs.hipl.bundleIO.HARBundleReader;
import edu.okstate.cs.hipl.bundleIO.SequenceBundleWriter;
import edu.okstate.cs.hipl.util.Config;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;

/**
 * @author sridhar
 */
public class BundleFile {
    
    private Path _filepath;
    private Configuration _conf;
    private int type=0;
    private String str=null;
    public static String SEQUENCE_FILE="seq";
    public static String MAP_FILE="map";
    private BundleWriter bw=null;
    private BundleReader br=null;
    public BundleFile(String path, Configuration conf){
        _filepath=new Path(path);
        _conf=conf;
        str=path;
    }
    
    public BundleFile(Path path, Configuration conf){
        _filepath=path;
        _conf=conf;
        str=path.toString();
    }
  
    public Config getHConfig(){
        return new Config(_filepath,_conf);
    } 
    
    public String getName(){
        return null;
    }
    public String getPathAsString(){
        return str;
    }
    
    public Path getPath(){
        return _filepath;
    }
    public Configuration getConfiguration(){
        return _conf;
    }
    
    public int getType(){
        type=generateType();
        return type;
    }
    
    private int generateType() {
        String temp=_filepath.toString();
        int x=temp.lastIndexOf(".");
        String y=temp.substring(x+1);
        y=y.toLowerCase();
        switch(y){
            case "seq": return 0;
            case "map": return 1;
            case "har": return 2;
        }
        return 0;
    }
    
    public BundleWriter getBundleWriter(){
        if(bw==null){
        String temp=_filepath.toString();
        int x=temp.lastIndexOf(".");
        String y=temp.substring(x+1);
        y=y.toLowerCase();
        switch(y){
            case "seq": bw= new SequenceBundleWriter(this);
                        break;
            case "map": bw= new MapBundleWriter(this);
                break;
            case "har": bw= new HARBundleWriter(this);
                break;
            default : bw= new SequenceBundleWriter(this);
                    break;
        }
        
        }
        return bw;
        
    }

    public BundleReader getBundleReader(){
         if(br==null){
        String temp=_filepath.toString();
        int x=temp.lastIndexOf(".");
        String y=temp.substring(x+1);
        y=y.toLowerCase();
        switch(y){
            case "seq": br=new SequenceBundleReader(this);
                        break;
            case "map": br= new MapBundleReader(this);
                        break;
            case "har": br= new HARBundleReader(this);
                        break;
            default   : br=new SequenceBundleReader(this);
                break;
        }
        br=new SequenceBundleReader(this);
         }
         return br;
    }

    
}
