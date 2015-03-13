package edu.okstate.cs.hipl.bundle;

import edu.okstate.cs.hipl.image.HImage;
import edu.okstate.cs.hipl.image.HImageEncoder;
import edu.okstate.cs.hipl.util.Config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.SequenceFile;

public class SequenceImageBundle implements ImageBundle {
    /**
     * @associates <{uml.ImageHeader}>
     */
    
    
    private SequenceFile.Writer _SeqWriter;
    private SequenceFile.Reader _SeqReader;
    Config _hConf;
    long addedImages=0;
    long _tempKey;
    HImage _tempImage;
    
    public SequenceImageBundle(Path path, Configuration conf) {
        _hConf=new Config(path,conf);       
    }

     /**
     * Adds Image to the SequenceImageBundle.
     * @param himage HImage class object holds image and image header data.
     * @see edu.okstate.cs.hipl.image.HImage
     */
    
     @Override
    public void addImage(HImage himage) {
        try {
            if(_SeqWriter==null){
                openToWrite();
            }
            addedImages++;
            _SeqWriter.append(new LongWritable(addedImages), new BytesWritable(himage.getImageBytes()));
        } catch (IOException ex) {
            Logger.getLogger(SequenceImageBundle.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
     /**
     *  Adds Image to the SequenceImageBundle.
     *  @param inputStream reads data from InputStream
     *  @see java.io.InputStream
     */
     @Override
    public void addImage(InputStream inputStream) {
        HImage temp=new HImage(inputStream);
        addImage(temp);
    }
     /**
     *Adds Image to the SequenceImageBundle
     * @param file reads data from File
     * @see java.io.File
     */
     @Override
    public void addImage(File file) {
        HImage temp=new HImage(file);
        addImage(temp);
    }
     /**
     *Adds Image to the SequenceImageBundle
     * @param path reads data from the path
     * @see @link java.nio.file.Path
     */
    @Override
    public void addImage(Path path) {
    }
     /**
      * Gets Images from the SequenceImageBundle
      * @return Returns HImage Array from the Image Bundle
      * @see edu.okstate.cs.hipl.image.HImage
      */
     @Override
     public HImage[] getImages(){
        HImage[] l=new HImage[10];
        return l;
     }
     /**
     *Gets the image count in the SequenceImageBundle
     * @return returns image count
     *
     */
     @Override
    public int getImageCount() {
        return 10;
    }
     /**
      * closes the stream for this current SequenceImageBundle
      *
      */
     @Override
     public void close(){
         try {
         if(_SeqReader!=null){
             
                 _SeqReader.close();
         
         }
         if(_SeqWriter!=null){
             _SeqWriter.close();
         }
             } catch (IOException ex) {
                 Logger.getLogger(SequenceImageBundle.class.getName()).log(Level.SEVERE, null, ex);
             }
     }
     /**
     *Gets the current SequenceImageBundle path of the HDFS
     * @return returns the imagebundle path
     * @see java.nio.file.Path
     */
     @Override
    public Path getPath() {
        Path x=null;
        return x;
    }
     
     /**
     *Checks whether if there are any images in the SequenceImageBundle.
     * @return a boolean indicating if there are any images there in SequenceImageBundle
     */
     @Override
    public boolean hasNext() {
        try {
            LongWritable key =new LongWritable();
            BytesWritable image=new BytesWritable();
            if(_SeqReader.next(key,image)){
                _tempKey=(long)key.get();
                _tempImage=new HImage(image.getBytes());
                return true;
            }
        } catch (IOException ex) {
            Logger.getLogger(SequenceImageBundle.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
     /**
     * Gets the current Image from the SequenceImageBundle
     *@param himage HImage class object holds image and image header data.
     * @return the current image of the SequenceImageBundle
     * @see edu.okstate.cs.hipl.image.HImage
     *
     */
     @Override
    public HImage getCurrentImage() {
        return new HImage();
    }
     /**
     * Method to open SequenceImageBundle in Read Mode
     */
     @Override
    public void openToRead() {
        try {
            _SeqReader=new SequenceFile.Reader(this._hConf.getFileSystem(), this._hConf.getPath(),this._hConf.getConfiguration());
        } catch (IOException ex) {
            Logger.getLogger(SequenceImageBundle.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
     /**
     * Method to open SequenceImageBundle in Write Mode
     */
     @Override
    public void openToWrite() {
        try {
            _SeqWriter=new SequenceFile.Writer(this._hConf.getFileSystem(), this._hConf.getConfiguration(),this._hConf.getPath(), LongWritable.class, BytesWritable.class);
        } catch (IOException ex) {
            Logger.getLogger(SequenceImageBundle.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
     /**
     * Gets HImage from the SequenceImageBundle
     * @param himage HImage class object holds image and image header data.
     * @return the current image of the SequenceImageBundle
     * @see edu.okstate.cs.hipl.image.HImage
     *
     */
     @Override
    public HImage getImage() {
        return _tempImage;
    }
     /**
     * Method to open SequenceImageBundle
     * @param mode determines to read or write
     * @param overwrite overwrites file
     */
     @Override
    public void open(int mode, int overwrite) {

    }
     /**
     *Method to open SequenceImageBundle
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
