/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.okstate.cs.hipl.extract;

import java.io.File;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

/**
 *
 * @author Sridhar
 */
public class BundleExtractor {
    
    ArrayList args=new ArrayList<String>();
    

    public BundleExtractor(String input,String output){
        args.add(input);
       
        File file=new File(output);
        if(file.exists()){
            if(!file.isDirectory()){
                System.out.println("is not a directory");
                System.exit(1);
            }
        }else{
             output=trim(output)+"/";
            file.mkdirs();
        }
        args.add(output);
    }
    
    public void init() {
       String[] sri= new String[this.args.size()];
                this.args.toArray(sri);
       try {
            ToolRunner.run((Tool) new ImageExtractor(), sri);
        } catch (Exception ex) {
            Logger.getLogger(BundleExtractor.class.getName()).log(Level.SEVERE, null, ex);
        }
       // ImageExtract.init(sri);
    }

 
    public void setInputPath(String input) {
        args.set(0,input);
    }

  
    public void setOutputPath(String output) {
        args.set(1, output);
    }

 
    public String getInputPath() {
        return (String) args.get(0);
    }

   
    public String getOutputPath() {
        return (String) args.get(0);
    }

    private String trim(String output) {
        if(output.charAt(output.length()-1)=='/' || output.charAt(output.length()-1)=='/' ){
            return output.substring(0,output.length()-1);
        }
        return output;
    }
    
    
    
}
