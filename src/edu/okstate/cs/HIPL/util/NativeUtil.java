/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/

package edu.okstate.cs.hipl.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 *
 * @author Sridhar
 */
public class NativeUtil {
    
    public static void loadFromJar(String path) throws IOException{
       
        String[] parts=path.split("/");
      //  System.out.println(parts.length);
        String filename=(parts.length>1)?parts[parts.length-1]:null;
        String prefix="";
        String suffix=null;
        //Logger.getLogger("FILENAME "+filename).log(Level.WARNING, null, filename);
        System.out.println(filename);
        if (filename != null) {
            parts = filename.split("\\.");
            prefix = parts[0];
            suffix = (parts.length > 1) ? "."+parts[parts.length - 1] : null;
        }
        System.out.println(prefix);
        System.out.println(suffix);
        // Logger.getLogger("parts "+parts.length).log(Level.WARNING, null, filename);
        File x=null;
        if(System.getProperty("os.name").toLowerCase().contains("windows")){
            x=new File("C:/tmp/HIPL/");
            if(!x.exists()){
                x.mkdirs();
            }
        } else if (System.getProperty("os.name").toLowerCase().contains("linux")){
            x=new File("/tmp/HIPL/");
            if(!x.exists()){
                x.mkdirs();
            }
        }
        
        
        
        File temp= new File(x.getAbsolutePath()+"/"+prefix+suffix);
        if(temp.exists()){
            temp.delete();
        }
       // temp.deleteOnExit();
        
       
        
        InputStream is=NativeUtil.class.getResourceAsStream(path);
         //Logger.getLogger("parts "+NativeUtil.class.getCanonicalName()).log(Level.WARNING, null, filename);
         
        if (is == null) {
            throw new FileNotFoundException("File " + path + " was not found inside JAR.");
        }
        System.out.println(is.available());
        byte[] buffer =new byte[is.available()];
        int read; 
        OutputStream os=new FileOutputStream(temp);
        if(is!=null){
            while((read=is.read(buffer))!=-1){
                os.write(buffer,0,read);
            }
            os.close();
            is.close();
        }
        System.out.println(temp.getAbsolutePath());
        System.load(temp.getAbsolutePath());
     //   Mat x;
    }
    
    public static void main(String args[]) throws IOException{
       loadFromJar("/lib/x64/opencv_java249.dll");
    }
    
}


