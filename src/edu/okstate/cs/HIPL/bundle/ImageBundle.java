package edu.okstate.cs.hipl.bundle;

import edu.okstate.cs.hipl.image.HImage;
import edu.okstate.cs.hipl.image.HImageEncoder;

import java.io.File;
import java.io.InputStream;


import org.apache.hadoop.fs.Path;

public interface ImageBundle {
    /**
     * Adds Image to the ImageBundle. 
     * @param himage HImage class object holds image and image header data.
     * @see edu.okstate.cs.hipl.image.HImage
     */
    void addImage(HImage himage);
    /**
     *  Adds Image to the ImageBundle.
     *  @param inputStream reads data from InputStream
     *  @see java.io.InputStream
     */
    void addImage(InputStream inputStream);
    /**
     *Adds Image to the ImageBundle
     * @param file reads data from File
     * @see java.io.File
     */
    void addImage(File file);
    /**
     *Adds Image to the ImageBundle
     * @param path reads data from the path
     * @see @link java.nio.file.Path
     */
    void addImage(Path path);
    /**
     * Gets Images from the ImageBundle
     * @return Returns HImage Array from the Image Bundle
     * @see edu.okstate.cs.hipl.image.HImage
     */
    public HImage[] getImages();
    /**
     *Gets the image count in the image bundle
     * @return returns image count
     * 
     */
    
    int getImageCount();
    /**
     * closes the stream for this current bundle
     *
     */
    public void close();
    /**
     *Gets the current image bundle path of the HDFS 
     * @return returns the imagebundle path
     * @see java.nio.file.Path
     */
    Path getPath();
    /**
     *Checks whether if there are any images in the bundle.
     * @return a boolean indicating if there are any images there in image bundle
     */
    boolean hasNext();
    /**
     * Gets the current Image from the bundle 
     *@param himage HImage class object holds image and image header data.
     * @return the current image of the image bundle 
     * @see edu.okstate.cs.hipl.image.HImage
     * 
     */
    HImage getCurrentImage();
    /**
     * Method to open image bundle in Read Mode
     */
    void openToRead();
    /**
     * Method to open image bundle in Write Mode
     */
    void openToWrite();
    /**
     * Gets HImage from the bundle 
     * @param himage HImage class object holds image and image header data.
     * @return the current image of the image bundle 
     * @see edu.okstate.cs.hipl.image.HImage
     * 
     */
    HImage getImage();
    /**
     * Method to open image bundle
     * @param mode determines to read or write
     * @param overwrite overwrites file 
     */
    
    void open(int mode, int overwrite);
    /**
     *Method to open image bundle
     * @param mode mode determines to read or write
     */
    void open(int mode);
    /**
     *
     * @param himage Image class object holds image and image header data.
     * @param encoder used to encode the image.
     * @param type defines the image type PNG, JPEG
     */
    void addImage(HImage himage, HImageEncoder encoder, int type);
    /**
     *@param himage Image class object holds image and image header data.
     * @param encoder used to encode the image.
     */
    void addImage(HImage himage, HImageEncoder encoder);
    /**
     *
     * @param himage Image class object holds image and image header data.
     * @param type defines the image type PNG, JPEG
     */
    void addImage(HImage himage, int type);
}
