/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hipl;

import edu.okstate.cs.hipl.exdown.BundleDownloader;
import edu.okstate.cs.hipl.extract.BundleExtractor;
import edu.okstate.cs.hipl.process.BundleProcess;
import edu.okstate.cs.hipl.process.GrayAlgorithm;
import java.io.FileOutputStream;
import java.io.PrintStream;

/**
 *
 * @author sridhar
 */
public class HIPL {

    /**
     * @param args the command line arguments
     */
    @SuppressWarnings("empty-statement")
    public static void main(String[] args) throws Exception {
      
          
           BundleDownloader bd=new BundleDownloader("/user/admin/images/input/","/user/admin/images/out3/","img","map","map");
           bd.init();
          // System.out.println("\n\n\n\n BUNDLE : "+bd.getOutputPath()+"\n\n\n\n\n");
           
           BundleProcess bp=new BundleProcess(bd.getOutputPath(),"/user/admin/images/out3/","img-out","map","map",GrayAlgorithm.class);
           bp.init();
           
          BundleExtractor be=new BundleExtractor(bp.getOutputPath(),"/tmp/imgs/");
          be.init();
           
    }
}
