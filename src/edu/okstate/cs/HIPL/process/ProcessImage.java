/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/

package edu.okstate.cs.hipl.process;

import edu.okstate.cs.hipl.image.HImage;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author Sridhar
 */
class ProcessImage {
    public static int GRAY=0;
    
    
    private HImage himage;
    private int processType;
    ProcessImage(HImage hi, int type){
        himage=hi;
        processType=type;
        init();
    }
    
    private void init() {
        if(processType==GRAY){
            try {
                BufferedImage bf=himage.getBufferedImage();
                int width=bf.getWidth();
                int height=bf.getHeight();
                ByteArrayOutputStream baos=new ByteArrayOutputStream();
                for(int i=0; i<height; i++){
                    for(int j=0; j<width; j++){
                        Color c = new Color(bf.getRGB(j, i));
                        int red = (int)(c.getRed() * 0.299);
                        int green = (int)(c.getGreen() * 0.587);
                        int blue = (int)(c.getBlue() *0.114);
                        Color newColor = new Color(red+green+blue,
                                red+green+blue,red+green+blue);
                        bf.setRGB(j,i,newColor.getRGB());
                    }
                }
                ImageIO.write(bf, "jpg", baos);
                HImage temp=new HImage(baos.toByteArray());
                //temp.setImageHeader(himage.getImageHeader());
                himage=temp;
                
            } catch (IOException ex) {
                Logger.getLogger(ProcessImage.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
    }
    
     HImage getProcessedImage(){
        return himage;
        
    }
    
}
