package com.spring.springRestDemo.util.AppUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;

import org.imgscalr.Scalr;
import org.springframework.core.io.UrlResource;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.core.io.Resource;
//this is use for crete category type upload image in folder
public class AppUtil {
    public static String get_photo_upload_path(String fileName,String folder_name,long album_id) throws IOException{
        String path="src\\main\\resources\\static\\uploads\\"+album_id+"\\"+folder_name;
        Files.createDirectories(Paths.get(path));
        return new File(path).getAbsolutePath()+"\\"+fileName;
    }

    //for useing imag escaler resize for fast loading 
    public static BufferedImage getThumbnail(MultipartFile orginalFile,Integer width) throws IOException{
        BufferedImage thumbImg=null;
        BufferedImage img=ImageIO.read(orginalFile.getInputStream());
        thumbImg =Scalr.resize(img,Scalr.Method.AUTOMATIC, Scalr.Mode.AUTOMATIC,width,Scalr.OP_ANTIALIAS);
        return thumbImg;
    }
    //for download api
    public static Resource getFileAsResource(long album_id,String folder_name,String file_name) throws IOException{
        String location="src\\main\\resources\\static\\uploads\\"+album_id+"\\"+folder_name+"\\"+file_name;
        File file=new File(location);
        if(file.exists()){
            Path path=Paths.get(file.getAbsolutePath());
            return new UrlResource(path.toUri());
        }else{
            return null;
        }
    }
    
}
