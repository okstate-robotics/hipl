package edu.okstate.cs.hipl.bundle;

import edu.okstate.cs.hipl.image.HImage;
import edu.okstate.cs.hipl.image.HImageEncoder;

import java.io.File;
import java.io.InputStream;


import java.util.Collection;
import org.apache.hadoop.fs.Path;

public class HARImageBundle implements ImageBundle {
    /**
     * @associates <{uml.ImageHeader}>
     */
    private Collection newAtt;

     /**
     * Adds Image to the HARImageBundle.
     * @param himage HImage class object holds image and image header data.
     * @see edu.okstate.cs.hipl.image.HImage
     */
     @Override
    public void addImage(HImage himage) {

    }
     /**
     *  Adds Image to the HARImageBundle.
     *  @param inputStream reads data from InputStream
     *  @see java.io.InputStream
     */
     @Override
    public void addImage(InputStream inputStream) {

    }
     /**
     *Adds Image to the HARImageBundle
     * @param file reads data from File
     * @see java.io.File
     */
     @Override
    public void addImage(File file) {

    }
     /**
     *Adds Image to the HARImageBundle
     * @param path reads data from the path
     * @see @link java.nio.file.Path
     */
     @Override
    public void addImage(Path path) {

    }
     /**
      * Gets Images from the HARImageBundle
      * @return Returns HImage Array from the HARImageBundle
      * @see edu.okstate.cs.hipl.image.HImage
      */
     @Override
     public HImage[] getImages(){
      
        HImage[] l=new HImage[10];
        return l;
     }
     /**
     *Gets the image count in the HARImageBundle
     * @return returns image count
     *
     */
     @Override
    public int getImageCount() {
        return 10;
    }
     /**
      * closes the stream for this current HARImageBundle
      *
      */
     @Override
     public void close(){
         
     }
     /**
     *Gets the current HARImageBundle path of the HDFS
     * @return returns the imagebundle path
     * @see java.nio.file.Path
     */
     @Override
    public Path getPath() {
        Path x=null;
        return x;
    }
     
     /**
     *Checks whether if there are any images in the bundle.
     * @return a boolean indicating if there are any images there in image bundle
     */
     @Override
    public boolean hasNext() {
        return false;
    }
     /**
     * Gets the current Image from the HARImageBundle
     *@param himage HImage class object holds image and image header data.
     * @return the current image of the image bundle
     * @see edu.okstate.cs.hipl.image.HImage
     *
     */
     @Override
    public HImage getCurrentImage() {
        return new HImage();
    }
     /**
     * Method to open HARImageBundle in Read Mode
     */
     @Override
    public void openToRead() {

    }
     /**
     * Method to open HARImageBundle in Write Mode
     */
     @Override
    public void openToWrite() {

    }
     /**
     * Gets HImage from the HARImageBundle
     * @param himage HImage class object holds image and image header data.
     * @return the current image of the image bundle
     * @see edu.okstate.cs.hipl.image.HImage
     *
     */
     @Override
    public HImage getImage() {
        return new HImage();
    }
     /**
     * Method to open HARImageBundle
     * @param mode determines to read or write
     * @param overwrite overwrites file
     */
     @Override
    public void open(int mode, int overwrite) {

    }
     /**
     *Method to open HARImageBundle
     * @param mode mode determines to read or write
     */
     @Override
    public void open(int mode) {

    }
     /**
     *
     * @param himage Image class object holds image and image header data.
     * @param encoder used to encode the image.
     * @param type defines the image type PNG, JPEG
     */
     @Override
    public void addImage(HImage himage, HImageEncoder encoder, int type) {

    }
     /**
     *@param himage Image class object holds image and image header data.
     * @param encoder used to encode the image.
     */
     @Override
     public void addImage(HImage himage, HImageEncoder encoder){
         
     }
     /**
     *
     * @param himage Image class object holds image and image header data.
     * @param type defines the image type PNG, JPEG
     */
     @Override
    public void addImage(HImage himage, int type) {

    }
}
