package edu.okstate.cs.hipl.rip;

import java.io.File;

public interface Ripper {
    /**
     * Gets the URLs of all the images using the ripper
     * @return a array of URLs as String Array
     */
    public String[] getURLs();
    /**
     * Writes the obtained URLs to the file
     * @param file - URLs are added to the referenced File.
     */
    public void writeURLFile(File file);
    
}
