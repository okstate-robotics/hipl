package edu.okstate.cs.hipl.header;

import java.awt.Dimension;
import java.awt.image.BufferedImage;

import java.util.Date;
import java.util.List;
import javax.imageio.metadata.IIOMetadata;

public class HImageHeader {
    IIOMetadata metadata=null;
    List<BufferedImage> thumbnails=null;
    
    public HImageHeader(IIOMetadata metadata, List thumbs){
        this.metadata=metadata;
        this.thumbnails=thumbs;
    }
    public List<BufferedImage> getThumbnails(){
        return thumbnails;
    }
    public IIOMetadata getMetadata(){
        return metadata;
    }
    
    /**
     * Returns the file name of the Image
     * @return filename as String 
     */
    
    public String getFileName() {
        return "";
    }
    /**
     *Gets the file size in bytes
     * @return file size as long integer
     */
    public long getFileSize() {
        return 0;
    }
    /**
     * Gets the modification time.
     * @return Modification time as Date.
     */

    public Date getModificationTime() {
        return null;
    }
    /**
     *Gets the creation time. 
     * @return creation time as Date.
     */
    public Date getCreationTime() {
        return null;
    }
    /**
     *Gets the image file type (JPEG, PNG, etc.,)
     * @return image type as String.
     */
    public String getFileType() {
        return "";
    }
    /**
     * Gets the MIME type (image/JPEG, image/PNG, etc.,) 
     * @return mime type as String
     */
    public String getMimetype() {
        return "";
    }
    /**
     * Gets the Camera Model Number.
     * @return camera model as String
     */
    public String getCameraModel() {
        return "";
    }
    /**
     * Gets the horizontal width resolution. 
     * @return horizontal width of pixels in int
     */
    public int getXResolution() {
        return 0;
    }
    /**
     *  Gets the vertical height resolution.
     *  @return vertical height of pixels in int
     */
    public int getYResolution() {
        return 0;
    }
    /**
     *Gets horizontal and vertical resolution 
     * @return resolution as Dimension
     */
    public Dimension getResolution() {
        return null;
    }
    /**
     *Gets the GPS Location X Coordinate.
     * @return x coordinate as double
     */
    public double getXCoordinate() {
        return 0;
    }
    /**
     *Gets the GPS Location y Coordinate.
     * @return y coordinate as double
     */
    public double getYCoordinate() {
        return 0;
    }
    /**
     * Sets the filename in exifdata
     * @param filename - filename as String
     */
    public void setFileName(String filename) {
    }
    
   /* ExifData getExifInformation() {
        return null;
    }*/
}
