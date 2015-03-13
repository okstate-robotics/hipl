package edu.okstate.cs.hipl.rip;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FolderRipper implements Ripper {
    
    File file=null;
    
    public FolderRipper(String path){
        this.file=new File(path);
    }
    public FolderRipper(File file){
        this.file=file;
    }
    
    public File getFile(){
        return this.file;
    }
    
    public void setFile(String path){
        this.file=new File(path);
    }
    
    public void setFile(File file){
        this.file=file;
    }
    /**
     * Gets the URLs of all the images using the ripper
     * @return a array of URLs as String Array
     */
    
    @Override
    public String[] getURLs() {
        // TODO Implement this method
        File[] files=null;
        if(file.isDirectory()){
            files=file.listFiles();
        }
        else{
            System.out.println("INVALID DIRECTORY");
        }
        String[] out=new String[files.length];
        for(int i=0;i<files.length;i++){
            out[i]=files[i].getAbsolutePath();
        }
        return new String[0];
    }
    /**
     * Writes the obtained URLs to the file
     * @param file - URLs are added to the referenced File.
     */
    @Override
    public void writeURLFile(File file) {
        // TODO Implement this method
        String path=file.getAbsolutePath();
        path.replaceAll("\\","/");
        path=path.substring(0, path.lastIndexOf("/"));
        File temp=new File(path);
        if(temp.isDirectory()){
            temp.mkdirs();
        }
        String[] urls=getURLs();
        BufferedWriter bw;
        try {
            bw = new BufferedWriter(new FileWriter(file, true));
            for(int i=0;i<urls.length;i++){
                    bw.write(urls[i]);
                    bw.newLine();
            }
            bw.flush();
        } catch (IOException e) {
            System.out.println("INVALID WRITE EXCEPTION");
        }    
    }
}
