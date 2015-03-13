/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.okstate.cs.hipl.process;

import edu.okstate.cs.hipl.image.HImage;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Sridhar
 */
public class Threader implements Runnable {
    HImage image=null;
    String cls=null;
    public Threader(HImage img, String className){
        image=img;
        cls=className;
    }
    
    @Override
    public void run() {
        try {
            GenericAlgorithm pi=(GenericAlgorithm)Class.forName(cls).newInstance();
            // GenericAlgorithm pi=(GenericAlgorithm) o;
            pi.setHImage(image);
            pi.run();
            image=pi.getProcessedImage();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Threader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(Threader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(Threader.class.getName()).log(Level.SEVERE, null, ex);
        }                    
    }
    
   
}
