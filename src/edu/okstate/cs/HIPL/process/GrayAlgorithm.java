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
public class GrayAlgorithm implements GenericAlgorithm{
    
  //  GrayAlgorithm(H)
    HImage himage;
    
    @Override
    public void run() {
      /*  Mat mat=himage.getMatImage();
        Mat mat1 = new Mat(himage.getBufferedImage().getHeight(),himage.getBufferedImage().getWidth(),CvType.CV_8UC1);
        
        Imgproc.cvtColor(mat, mat1, Imgproc.COLOR_RGB2GRAY);
        HImage temp=new HImage(mat1);
        temp.setImageHeader(himage.getImageHeader());
        himage=temp;
        */
        try {
            BufferedImage bf=himage.getBufferedImage();
            int width=bf.getWidth();
            int height=bf.getHeight();
            ByteArrayOutputStream baos=new ByteArrayOutputStream();
            Color c;
            for(int i=0; i<height; i++){
                for(int j=0; j<width; j++){
                    c = new Color(bf.getRGB(j, i));
                    int red = (int)(c.getRed() * 0.299);
                    int green = (int)(c.getGreen() * 0.587);
                    int blue = (int)(c.getBlue() *0.114);
                    c = new Color(red+green+blue,
                            red+green+blue,red+green+blue);
                    bf.setRGB(j,i,c.getRGB());
                }
            }
            ImageIO.write(bf, "jpg", baos);
            HImage temp=new HImage(baos.toByteArray());
            temp.setImageHeader(himage.getImageHeader());
            himage=temp;
        } catch (IOException ex) {
            Logger.getLogger(GrayAlgorithm.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }

    @Override
    public HImage getProcessedImage() {
        return himage;
    }

    @Override
    public void setHImage(HImage himage) {
        this.himage=himage;
    }
    
}
