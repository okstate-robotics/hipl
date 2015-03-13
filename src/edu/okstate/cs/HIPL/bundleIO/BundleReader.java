/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.okstate.cs.hipl.bundleIO;

import edu.okstate.cs.hipl.image.HImage;
import edu.okstate.cs.hipl.util.Config;

/**
 *
 * @author sridhar
 */
public interface BundleReader {
    
    void openToRead();
    
    boolean hasNext();
    
    HImage next();
    
    long getReturnCount();
    
    Config getConfiguration();
    
    void close();
    
}
