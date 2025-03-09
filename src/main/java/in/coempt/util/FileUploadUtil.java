/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package in.coempt.util;

import java.io.File;
import java.io.IOException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

/**
 *
 * @author jeevan
 */
@Component
public class FileUploadUtil {

    public String upoladFileToSpecifiedDirectory(CommonsMultipartFile[] file, String commonpath, String ourpath, String fileName) throws IOException {

        String renamedFile = null;
        String savedpath = null;

        if (file != null && file.length > 0) {
            for (CommonsMultipartFile aFile : file) {

                
                 String s1[] = aFile.getOriginalFilename().split("\\.");
                    if (aFile.getOriginalFilename().contains("..")) {
                        throw new RuntimeException("Failed to Upload , file name should have only one extension ");
                    }
                
                
                if (!aFile.getOriginalFilename().equals("")) {
                    File directory = new File(commonpath + ourpath);

                    if (!directory.exists()) {
                        directory.mkdir();
                    }

                    renamedFile = fileName + "." + aFile.getOriginalFilename().split("\\.")[1];
                    savedpath = ourpath + File.separator + renamedFile;

                    aFile.transferTo(new File(directory + File.separator + renamedFile));
                }
            }
        }

        return savedpath;
    }
    
   public void createRequiredDirectoryIfNotExists(String commonpath, String ourpath)
   {
       File directory = new File(commonpath + ourpath);
        if (!directory.exists()) {
           directory.mkdirs();
        }
   }
    
}
