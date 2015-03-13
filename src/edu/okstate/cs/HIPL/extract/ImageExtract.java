/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.okstate.cs.hipl.extract;

import edu.okstate.cs.hipl.bundle.BundleFile;
import edu.okstate.cs.hipl.bundleIO.BundleReader;
import edu.okstate.cs.hipl.image.HImage;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.hadoop.conf.Configuration;

/**
 *
 * @author Sridhar
 */
public class ImageExtract {
    
    public static void init(String[] args){
        
       BundleFile bf;
        bf = new BundleFile(args[0],new Configuration());
       BundleReader br=bf.getBundleReader();
       
       while(br.hasNext()){
           FileOutputStream fos=null;
           try {
               HImage x=br.next();
               fos = new FileOutputStream(args[1]+br.getReturnCount()+".jpg");
               fos.write(x.getImageBytes());
           } catch (FileNotFoundException ex) {
               Logger.getLogger(ImageExtract.class.getName()).log(Level.SEVERE, null, ex);
           } catch (IOException ex) {
               Logger.getLogger(ImageExtract.class.getName()).log(Level.SEVERE, null, ex);
           } finally {
               try {
                   fos.close();
               } catch (IOException ex) {
                   Logger.getLogger(ImageExtract.class.getName()).log(Level.SEVERE, null, ex);
               }
           }
       }    
    }
}
