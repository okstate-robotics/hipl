package edu.okstate.cs.hipl.image;

import edu.okstate.cs.hipl.header.HImageHeader;
import edu.okstate.cs.hipl.util.NativeUtil;


import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.IIOImage;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

public class HImage {
    /**
     * Constructor to create an HImage which holds the image and header data.
     * @param file - Generates HImage from a File.
     */
    static boolean loaded=false;
    
    static{
        if(System.getProperty("os.name").toLowerCase().contains("windows")){
            try {
                
                NativeUtil.loadFromJar("/lib/x64/opencv_java246.dll");
            } catch (IOException ex) {
                Logger.getLogger(HImage.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        else if(System.getProperty("os.name").toLowerCase().contains("linux")){
          /*  try {
                NativeUtil.loadFromJar("/lib/linux/libopencv_java246.so");
            } catch (IOException ex) {
                Logger.getLogger(HImage.class.getName()).log(Level.SEVERE, null, ex);
            }*/
        }
        System.out.println(System.getProperty("os.name"));
    }
    
    private byte[] imagebytes=null;
    private BufferedImage bufferedImage=null;
    private Mat mat=null;
    private String ext="jpg";
    private HImageHeader imageheader=null;
    
    public HImage(byte[] by){
        this.imagebytes=by;
    }
    
    public HImage(Mat mat){
        this.mat=mat;
        byte[] data=new byte[mat.rows()*mat.cols()*(int)(mat.elemSize())];
        mat.get(0,0,data);
        
        this.bufferedImage= new BufferedImage(mat.cols(),mat.rows(), BufferedImage.TYPE_BYTE_GRAY);
        this.bufferedImage.getRaster().setDataElements(0, 0, mat.cols(), mat.rows(), data);
        generateImageBytes(this.bufferedImage);
    }
    
    public HImage(BufferedImage bf){
        generateImageBytes(bf);
        //generateMatImage();
    }
    
    
    
    public HImage(File file) {
        FileInputStream fis=null;
        try {
            fis = new FileInputStream(file);
            imagebytes=new byte[(int)file.length()];
            fis.read(imagebytes);
        } catch (FileNotFoundException e) {
            System.out.println("FILE INPUT STREAM NOT FOUND");
        } catch (IOException e) {
            System.out.println("IO NOT FOUND");
        }
    }
    
    
    
    /**
     * Constructor to create an HImage which holds the image and header data.
     * @param inputstream - Generates HImage from InputStream.
     */
    public HImage(InputStream inputstream){
        try {
            ByteArrayOutputStream baos=new ByteArrayOutputStream();
            byte buffer[]=new byte[1024];
            int read=0;
            while((read=inputstream.read(buffer,0,buffer.length))!=-1){
                baos.write(buffer,0,read);
                /*if(baos.size()>3000000){
                imagebytes=null;
                return;
                }*/
            }
            baos.flush();
            imagebytes=baos.toByteArray();
            baos.close();
        } catch (IOException ex) {
            Logger.getLogger(HImage.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    /**
     * Constructor to create an HImage which holds the image and header data.
     */
    public HImage(){
    }
    /**
     * Gets the BufferedImage for the current HImage object.
     * @return image pixel data is returned as BufferedImage
     */
    public BufferedImage getBufferedImage() {
        if(bufferedImage==null){
            generateBufferedImage();
        }
        return bufferedImage;
    }
    /**
     * Gets the Mat for the current HImage object.
     * @return image pixel data is returned as Mat.
     */
    public Mat getMatImage() {
        if(mat==null){
            generateMatImage();
        }
        return mat;
    }
    /**
     * Gets the image header data or exif data from the image.
     * @return exif data is returned as HImageHeader
     */
    public HImageHeader getImageHeader() {
        if(imageheader==null){
            generateImageHeader();
        }
        return imageheader;
    }
    /**
     * Sets the Image or adds image to the object
     * @param bufferedImage - adds BufferedImage image data.
     */
    public void setImage(BufferedImage bufferedImage){
        this.bufferedImage=bufferedImage;
    }
    /**
     * Sets the Image or adds image to the object
     * @param mat - adds Mat image data.
     */
    public void setImage(Mat mat){
        this.mat=mat;
    }
    /**
     *Sets the image header or exif data tothe object
     * @param imageheader - adds imageheader to current HImageObject
     */
    public void setImageHeader(HImageHeader imageheader){
        if(imageheader != null){
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageOutputStream ios =null;
            try {
                ios = ImageIO.createImageOutputStream(baos);
            } catch (IOException ex) {
                Logger.getLogger(HImage.class.getName()).log(Level.SEVERE, null, ex);
            }

            Iterator writers = ImageIO.getImageWritersByFormatName(ext);
            ImageWriter imageWriter = (ImageWriter) writers.next();
            JPEGImageWriteParam jp=new JPEGImageWriteParam(Locale.getDefault());
            jp.setOptimizeHuffmanTables(true);
            imageWriter.setOutput(ios);
            IIOImage iio=new IIOImage(getBufferedImage(),imageheader.getThumbnails() ,imageheader.getMetadata());
            try {
                imageWriter.write(imageheader.getMetadata(),iio,jp);
                ios.flush();
                imageWriter.dispose();
                ios.close();
                //imagebytes=baos.toByteArray();
            } catch (IOException ex) {
                //imagebytes=imagebytes;
            }
        }
        
        /*  try {
        IIOImage image = new IIOImage(this.getBufferedImage(), imageheader.getThumbnails(),imageheader.getMetadata());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageOutputStream ios = ImageIO.createImageOutputStream(baos) ;
        Iterator writers = ImageIO.getImageWritersByFormatName(this.ext);
        ImageWriter imageWriter = (ImageWriter) writers.next();
        imageWriter.setOutput(ios);
        imageWriter.write(image);
        ios.flush();
        imageWriter.dispose();
        ios.close();
        this.imagebytes=baos.toByteArray();
        this.imageheader=imageheader;
        } catch (IOException ex) {
        Logger.getLogger(HImage.class.getName()).log(Level.SEVERE, null, ex);
        }
        */
    }
    
    private void generateMatImage(){
        if(bufferedImage==null)
            generateBufferedImage();
        byte[] temp=((DataBufferByte)bufferedImage.getRaster().getDataBuffer()).getData();
        mat=new Mat(bufferedImage.getWidth(),bufferedImage.getHeight(),CvType.CV_8UC3);
        mat.put(0,0,temp);
    }
    
    private void generateBufferedImage(){
        InputStream in=new ByteArrayInputStream(imagebytes);
        try {
            bufferedImage = ImageIO.read(in);
        } catch (IOException e) {
            System.out.println("IO NOT FOUND");
        }
        
    }
    
    private void generateImageHeader() {
        Iterator readers = ImageIO.getImageReadersByFormatName(ext);
        ImageReader ir = (ImageReader) readers.next();
        ImageReadParam param=ir.getDefaultReadParam();
        ImageInputStream iis=null;
        try {
            iis = ImageIO.createImageInputStream(new ByteArrayInputStream(imagebytes));
            ir.setInput(iis);
        } catch (IOException ex) {
            Logger.getLogger(HImage.class.getName()).log(Level.SEVERE, null, ex);
        }
        IIOImage iio=null;
        try {
            iio = ir.readAll(0, param);
            IIOMetadata meta=iio.getMetadata();
            List x=iio.getThumbnails();
            imageheader=new HImageHeader(meta,x);
            ir.dispose();
        } catch (IOException ex) {
            Logger.getLogger(HImage.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void main(String args[]) {
        /* Create and display the form */
        HImage x=new HImage(new File("./img.jpg"));
        /*Mat y=x.getMatImage();
        BufferedImage z=x.getBufferedImage();
        System.out.println(z.getHeight());
        try {
        Metadata metadata = ImageMetadataReader.readMetadata(new File("./img.jpg"));
        System.out.println(metadata.getDirectoryCount());
        for (Directory directory : metadata.getDirectories()) {
        for (Tag tag : directory.getTags()) {
        System.out.println(tag);
        }
        }
        } catch (ImageProcessingException ex) {
        Logger.getLogger(HImage.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
        Logger.getLogger(HImage.class.getName()).log(Level.SEVERE, null, ex);
        }*/
        
        HImageHeader head=x.getImageHeader();
        HImage y=new HImage(new File("./gray.jpg"));
        y.setImageHeader(head);
        y.saveToFile(y.getImageBytes());
    }
    
    public void saveToFile(byte[] bytes){
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream("./final.jpg");
            fos.write(bytes);
            fos.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(HImage.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(HImage.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                fos.close();
            } catch (IOException ex) {
                Logger.getLogger(HImage.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    public byte[] getImageBytes() {
        return imagebytes;
    }

    private void generateImageBytes(BufferedImage bf) {
        if(this.imagebytes==null){
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write( bf, ext, baos);
            baos.flush();
            this.imagebytes = baos.toByteArray();
        } catch (IOException ex) {
            Logger.getLogger(HImage.class.getName()).log(Level.SEVERE, null, ex);
        }
        }
    }
}
