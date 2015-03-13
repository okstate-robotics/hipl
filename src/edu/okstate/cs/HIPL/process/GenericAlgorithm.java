/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.okstate.cs.hipl.process;

import edu.okstate.cs.hipl.image.HImage;

/**
 *
 * @author Sridhar
 */
public interface GenericAlgorithm {
    void run();
    HImage getProcessedImage();
    void setHImage(HImage himage);
}
