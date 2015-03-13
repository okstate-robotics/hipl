package edu.okstate.cs.hipl.rip;

import com.flickr4java.flickr.Flickr;
import com.flickr4java.flickr.FlickrException;
import com.flickr4java.flickr.REST;
import com.flickr4java.flickr.groups.pools.PoolsInterface;
import com.flickr4java.flickr.people.PeopleInterface;
import com.flickr4java.flickr.photos.Photo;
import com.flickr4java.flickr.photos.PhotoList;
import com.flickr4java.flickr.photos.PhotosInterface;
import com.flickr4java.flickr.photos.SearchParameters;
import com.flickr4java.flickr.util.AuthStore;
import com.flickr4java.flickr.util.FileAuthStore;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import java.util.logging.Level;
import java.util.logging.Logger;


public class FlickrRipper implements Ripper {
    /**
     * Gets the URLs of all the images using the ripper
     * @return a array of URLs as String Array
     */
    private String _apiKey;
    private String _secret;
    private Flickr _flickr;
    private REST _rest;
    private final String nsid;
    private AuthStore authStore;
    private ArrayList<String> urls=new ArrayList<String>();
    
    FlickrRipper(String api, String secret,String nsid, File authsDir){
        _rest=new REST();
        this._apiKey=api;
        this._secret=secret;
        this.nsid = nsid;
        
          this._flickr=new Flickr(_apiKey,_secret, _rest);
        if (authsDir != null) {
            try {
                this.authStore = new FileAuthStore(authsDir);
            } catch (FlickrException ex) {
                Logger.getLogger(FlickrRipper.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
      
    }
    
    private void search(String[] args){
        
            SearchParameters searchParams=new SearchParameters();
            searchParams.setSort(SearchParameters.INTERESTINGNESS_DESC);
            searchParams.setTags(args);
            PhotosInterface photosInterface=_flickr.getPhotosInterface();
            
            
            PhotoList photoList=null;
        try {
            photoList = photosInterface.search(searchParams,1600,1000);
        } catch (FlickrException ex) {
            Logger.getLogger(FlickrRipper.class.getName()).log(Level.SEVERE, null, ex);
        }
           
            
            if(photoList!=null){
                for(int i=0;i<photoList.size();i++){
                    
                    
                    Photo photo=(Photo)photoList.get(i);
                   /* String ID=photo.getUrl();
                    String name="/"+photo.getId();
                    ID=ID.replace(name,"");
                    name="http://www.flickr.com/photos/";
                    ID=ID.replace(name,"");
                    System.out.println(ID);
                    */
                  //  PoolsInterface x=_flickr.getPoolsInterface();
                   // PhotoList pl=x.getPhotos(,null, 1000, 1);
                    if(photo!=null){
                     if(!"".equals(photo.getOriginalSecret())){
                             urls.add("https://farm"+photo.getFarm()+".staticflickr.com/"+photo.getServer()+"/"+photo.getId()+"_"+photo.getOriginalSecret()+"_o.jpg");
                           //  https://farm4.staticflickr.com/3540/3632163083_4f8ccb6a00_l.jpg"
                     }
                    }
                   
                    
                    
                }
            }
        
    }
    
    private void getGroupPools(String[] arr){
        for(int i=0;i<arr.length;i++){
            getGroupPool(arr[i]);
        }
    }
    
    
    @Override
    public String[] getURLs() {
        // TODO Implement this method
        String[] arr=(String[])urls.toArray(new String[urls.size()]);
        
        return arr;
    }
    /**
     * Writes the obtained URLs to the file
     * @param file - URLs are added to the referenced File.
     */
    @Override
    public void writeURLFile(File file) {
        try {
            // TODO Implement this method
            FileWriter fw=new FileWriter(file);
            for(int i=0;i<urls.size();i++){
                fw.write((String)urls.get(i)+"\n");
            }
            fw.close();
            
        } catch (IOException ex) {
            Logger.getLogger(FlickrRipper.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }
    
    public static void main(String[] args) throws FlickrException{
        
            FlickrRipper rip=new FlickrRipper("273d4e4538bc7650a9049e6dc9b6b476", "db2e4a9d2866485c","125815714@N07",new File("flickr"));
            String arr[]={"space"};
            for(int i=0;i<arr.length;i++){
                String temp[]={arr[i]};
                rip.search(temp);
            }
            rip.writeURLFile(new File("urls2.txt"));
        
          /*  String grps[]={"1408810@N24"};
            rip.getGroupPools(grps);
            rip.writeURLFile(new File("birds.txt"));
        */
        /*   rip.getUserPool("1408810@N24");
            rip.writeURLFile(new File("birds.txt"));*/
    }
    private void getUserPools(String[] usrs){
        for(int i=0;i<usrs.length;i++){
            getUserPool(usrs[i]);
        }
    }
    
    
    private void getUserPool(String usr){
        try {
            PeopleInterface x=_flickr.getPeopleInterface();
            PhotoList pl=x.getPublicPhotos(usr, 1000, 1);
            for(int i=0;i<pl.size();i++){
                Photo p=(Photo) pl.get(i);
                if(!p.getOriginalSecret().equals("")){
                    urls.add(p.getOriginalUrl());
                }
            }
        } catch (FlickrException ex) {
            Logger.getLogger(FlickrRipper.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    private void getGroupPool(String arr) {
        try {
            PoolsInterface x=_flickr.getPoolsInterface();
            PhotoList pl=x.getPhotos(arr,null, 1000, 1);
            for(int i=0;i<pl.size();i++){
                Photo p=(Photo) pl.get(i);
                if(!p.getOriginalSecret().equals("")){
                    urls.add(p.getOriginalUrl());
                }
            }
        } catch (FlickrException ex) {
            Logger.getLogger(FlickrRipper.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
