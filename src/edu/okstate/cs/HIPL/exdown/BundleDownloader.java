/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.okstate.cs.hipl.exdown;

import edu.okstate.cs.hipl.bundle.BundleFile;
import edu.okstate.cs.hipl.util.HIPLJob;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

/**
 *
 * @author Sridhar
 */
public class BundleDownloader implements HIPLJob {
    
    ArrayList args=new ArrayList<String>();
    

    public BundleDownloader(String input,String output, String filename, String inputType, String outputType){
        generate(input,output,filename,inputType,outputType);
    }
    
    public BundleDownloader(String input,String output, String filename){
        generate(input,output,filename,BundleFile.SEQUENCE_FILE,BundleFile.SEQUENCE_FILE);
    }
    
    public BundleDownloader(String input, String output){
        generate(input,output,"img.down",BundleFile.SEQUENCE_FILE,BundleFile.SEQUENCE_FILE);
    }
    
    private void generate(String input,String output, String filename, String inputType, String outputType){
        args.add(input);
        output=trim(output);
        output=output+"/"+filename+"."+outputType;
        args.add(output);
        args.add(inputType.toLowerCase());
        args.add(outputType.toLowerCase());
    }
    
    @Override
    public void setInputType(String inputType) {
        args.set(2, inputType.toLowerCase());
    }

    @Override
    public void setOutputType(String outputType) {
        args.set(3,outputType.toLowerCase());
    }

    @Override
    public void init() {
        String[] sri= new String[this.args.size()];
                this.args.toArray(sri);
        try {
            ToolRunner.run((Tool) new Downloader(), sri);
        } catch (Exception ex) {
            Logger.getLogger(BundleDownloader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void setInputPath(String input) {
        args.set(0,input);
    }

    @Override
    public void setOutputPath(String output) {
        args.set(1, output);
    }

    @Override
    public String getInputType() {
        return (String) args.get(2);
    }

    @Override
    public String getOuputType() {
        return (String) args.get(3);
    }

    @Override
    public String getInputPath() {
        return (String) args.get(0);
    }

    @Override
    public String getOutputPath() {
            return (String) args.get(1);
    }

    private String trim(String output) {
        if(output.charAt(output.length()-1)=='/' || output.charAt(output.length()-1)=='/' ){
            return output.substring(0,output.length()-1);
        }
        return output;
    }
    
}
