/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.okstate.cs.hipl.util;

/**
 *
 * @author Sridhar
 */
public interface HIPLJob {
    
    public void setInputType(String inputType);
    
    public void setOutputType(String outputType);
    
    public void init();
    
    public void setInputPath(String input);
    
    public void setOutputPath(String output);
    
    public String getInputType();
    
    public String getOuputType();
    
    public String getInputPath();
    
    public String getOutputPath();
    
    
}
