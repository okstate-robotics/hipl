/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.okstate.cs.hipl.bundleIO;

import edu.okstate.cs.hipl.bundle.BundleFile;
import edu.okstate.cs.hipl.container.HARIndexContainer;
import edu.okstate.cs.hipl.image.HImage;
import edu.okstate.cs.hipl.util.Config;
import edu.okstate.cs.hipl.util.HARIndexContainerSorter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.HarFileSystem;
import org.apache.hadoop.fs.Path;

/**
 *
 * @author Sridhar
 */
public class HARBundleWriter implements BundleWriter{
    
    private Config _hConf;
    private long _harTotal=0;
    private BundleFile _file;
    private ArrayList<HARIndexContainer> hashIndex;
    private FSDataOutputStream _harWriter=null;
    private FSDataOutputStream _harMainIndexStream=null;
    private FSDataOutputStream _harIndexStream=null;
    
    public HARBundleWriter(BundleFile file){
        _file=file;
        _hConf=file.getHConfig();
        openToWrite();
    }

    @Override
    public void openToWrite() {
        _harTotal=0;
        hashIndex=new ArrayList<HARIndexContainer>();
        Path tmpDir=new Path(_file.getPath().toUri().getPath());
        String tempPart="part-0";
        
        Path tmpOut=new Path(tmpDir,tempPart);
        //_file=new BundleFile(tmpOut, _hConf.getConfiguration());
        try {
            FileSystem outFS=tmpOut.getFileSystem(_hConf.getConfiguration());
            if(outFS.exists(tmpOut)){
                outFS.delete(tmpOut,false);
            }
                _harWriter=outFS.create(tmpOut);
          
        } catch (IOException ex) {
            Logger.getLogger(HARBundleWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void appendImage(HImage himage) {
        if(himage.getImageBytes()!=null){
            try {
                int len=0;
                Path src=new Path(Path.SEPARATOR,"image_"+_harTotal);
                int hash=HarFileSystem.getHarHash(src);
                long _harWriterPos=_harWriter.getPos();
                _harWriter.writeInt(1);
                _harWriter.write(himage.getImageBytes());
                len=Integer.SIZE+himage.getImageBytes().length;
                Path rel =new Path(src.toUri().getPath());
                String part="part-0";
                String value=rel.toString()+" file "+part+" " +_harWriterPos+" "+len+" ";
                String writ=value+"\n";
                hashIndex.add(new HARIndexContainer(hash,writ));
                _harTotal++;
            } catch (IOException ex) {
                Logger.getLogger(HARBundleWriter.class.getName()).log(Level.SEVERE, null, ex);
            }
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
    public void appendBundle(Path path, Configuration conf) {
        System.out.println("MERGE STARTED: "+_hConf.getPath()+" and "+path);
        long start=System.currentTimeMillis();
        HARBundleReader reader=new HARBundleReader(path,conf);
        System.out.println("Reader Path : "+path.toUri().toString());
       
       /* while(reader.hasNext()){
            System.out.println("Image Count :"+reader.getReturnCount());
            HImage im=reader.next();
            if(im!=null){
                System.out.println("Image Length :"+im.getImageBytes().length);
                this.appendImage(im);
                
                System.out.println("HAR COUNT : ########"+_harTotal);
            }
        }
        reader.close();
        */
       
        
        /*Path tmpDir=new Path(_file.getPath().toUri().getPath());
        String tempPart="part-0";
        Path tmpOut=new Path(tmpDir,tempPart);
        */
        
             while(reader.hasNext()){
                HImage im=reader.next();
                if(im!=null){
                    try {
                        int len=0;
                        Path src=new Path(Path.SEPARATOR,"image_"+_harTotal);
                        int hash=HarFileSystem.getHarHash(src);
                        long _harWriterPos=_harWriter.getPos();
                        
                        _harWriter.writeInt(1);
                        _harWriter.write(im.getImageBytes());
                        
                        len=Integer.SIZE+im.getImageBytes().length;
                        Path rel =new Path(src.toUri().getPath());
                        String part="part-0";
                        String value=rel.toString()+" file "+part+" " +_harWriterPos+" "+len+" ";
                        String writ=value+"\n";
                        hashIndex.add(new HARIndexContainer(hash,writ));
                        _harTotal++;
                    } catch (IOException ex) {
                        Logger.getLogger(HARBundleWriter.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            
              FileSystem fs;
        try {
            fs = FileSystem.get(conf);
            fs.delete(path, true);
        } catch (IOException ex) {
            Logger.getLogger(HARBundleWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
              
        /*
        try {
        
            FileSystem fs = FileSystem.get(_hConf.getConfiguration());
            FSDataInputStream in=fs.open(tmpOut);
            backup(_hConf.getConfiguration(),fs,in);
            
            in.close();
            
            FSDataOutputStream out=fs.create(path,true);
            FSDataInputStream backup=fs.open(new Path("/tmp/temp"));
            
            int offset=0;
            int bufferSize=4096;
            int result=0;
            
            byte[] buffer = new byte[bufferSize];
            
            result = backup.read(offset,buffer,0,bufferSize);
            
            while(result==bufferSize){
                out.write(buffer);
                offset+=bufferSize;
                result=backup.read(offset,buffer,0,bufferSize);
            }
            
            if(result>0 && result<bufferSize){
                for(int i=0;i<result;i++){
                    out.write(buffer[i]);
                }
            }
            while(reader.hasNext()){
                HImage im=reader.next();
                if(im!=null){
                    int len=0;
                    Path src=new Path(Path.SEPARATOR,"image_"+_harTotal);
                    int hash=HarFileSystem.getHarHash(src);
                    long _harWriterPos=out.getPos();
                    
                    out.writeInt(1);
                    out.write(im.getImageBytes());
                    
                    len=Integer.SIZE+im.getImageBytes().length;
                    Path rel =new Path(src.toUri().getPath());
                    String part="part-0";
                    String value=rel.toString()+" file "+part+" " +_harWriterPos+" "+len+" ";
                    String writ=value+"\n";
                    hashIndex.add(new HARIndexContainer(hash,writ));
                    _harTotal++;
                }
            }
            fs.delete(new Path("/tmp/temp"));
            
        /*    FileSystem outFS=tmpOut.getFileSystem(_hConf.getConfiguration());
            tempWriter=outFS.append(tmpOut);
                 
          
        while(reader.hasNext()){
            HImage im=reader.next();
            if(im!=null){
                try {
                    int len=0;
                    Path src=new Path(Path.SEPARATOR,"image_"+_harTotal);
                    int hash=HarFileSystem.getHarHash(src);
                    long _harWriterPos=tempWriter.getPos();
                    tempWriter.writeInt(1);
                    tempWriter.write(im.getImageBytes());
                    len=Integer.SIZE+im.getImageBytes().length;
                    Path rel =new Path(src.toUri().getPath());
                    String part="part-0";
                    String value=rel.toString()+" file "+part+" " +_harWriterPos+" "+len+" ";
                    String writ=value+"\n";
                    hashIndex.add(new HARIndexContainer(hash,writ));
                    _harTotal++;
                } catch (IOException ex) {
                    Logger.getLogger(HARBundleWriter.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        tempWriter.close();*/
         
        long end=System.currentTimeMillis();
        System.out.println("MERGE ENDED : "+_hConf.getPath()+" and "+path+" in "+(end-start)+" ms");
    }
    
    @Override
    public Config getConfiguration() {
        return _hConf;
    }

    @Override
    public long getImageCount() {
        return _harTotal;
    }

    @Override
    public void close() {
        System.out.println("outside of close");
        
        if(_harWriter!=null){
            try {
                System.out.println("Calling Close HAR WRITER");
                closeHARWriter();
                _harWriter.close();
            } catch (IOException ex) {
                Logger.getLogger(HARBundleWriter.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public BundleFile getBundleFile() {
        return _file;
    }

    public Path getPath(){
        return _file.getPath();
    }
    
    private void closeHARWriter() throws IOException {
        Path rel=new Path(Path.SEPARATOR);
        String writ=rel.toUri().getPath()+" dir none 0 0";
        for(int i=0;i<_harTotal;i++){
            writ+=" image_"+i;
        }
        writ+=" \n";
        hashIndex.add(new HARIndexContainer(HarFileSystem.getHarHash(rel), writ));
        
        
        Path mainIndex=new Path(_file.getPath(),"_masterindex");
        Path index=new Path(_file.getPath(),"_index");
        FileSystem fs=mainIndex.getFileSystem(_hConf.getConfiguration());
        if(fs.exists(mainIndex)){
            fs.delete(mainIndex,false);
        }
        if(fs.exists(index)){
            fs.delete(index,false);
        }
        
        _harIndexStream=fs.create(index);
        _harMainIndexStream=fs.create(mainIndex);
        
        String ver="1 \n";
        _harMainIndexStream.write(ver.getBytes());
        
        
        long start=0;
        long startIndexHash=0;
        long endIndexHash=0;
        Collections.sort(hashIndex,new HARIndexContainerSorter());
        int i=0;
        int hash=0;
        for(;i<hashIndex.size();i++){
            _harIndexStream.write(hashIndex.get(i).index_output.getBytes());
            hash = hashIndex.get(i).hash;
            
            if(i>0 && i%1000 == 0){
                endIndexHash=hash;
                String mainWrite = startIndexHash+" "+endIndexHash+" "+start+" "+_harIndexStream.getPos()+" \n";
                _harMainIndexStream.write(mainWrite.getBytes());
                start=_harIndexStream.getPos();
                startIndexHash=endIndexHash;
                
            }
        }
            if(i>0 && i%1000 > 0){
                String mainWrite= startIndexHash+ " " + hash +" "+ start+ " "+_harIndexStream.getPos()+" \n";
                _harMainIndexStream.write(mainWrite.getBytes());
            }
            _harMainIndexStream.close();
            _harIndexStream.close();
       
    }

    
}
