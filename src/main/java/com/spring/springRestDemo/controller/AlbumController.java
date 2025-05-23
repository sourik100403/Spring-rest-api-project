package com.spring.springRestDemo.controller;

import org.springframework.security.core.Authentication;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
// import java.net.http.HttpHeaders;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpHeaders;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import com.spring.springRestDemo.model.Account;
import com.spring.springRestDemo.model.Album;
import com.spring.springRestDemo.model.Photo;
import com.spring.springRestDemo.payload.auth.album.AlbumPayloadDTO;
import com.spring.springRestDemo.payload.auth.album.AlbumViewDTO;
import com.spring.springRestDemo.service.AccountService;
import com.spring.springRestDemo.service.AlbumService;
import com.spring.springRestDemo.service.PhotoService;
import com.spring.springRestDemo.util.AppUtils.AppUtil;
import com.spring.springRestDemo.util.constants.AlbumError;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1")
@Tag(name="Album  Controller",description="controller for album and photo management")
@Slf4j
public class AlbumController {


    //forresize the file
    static final String PHOTOS_FOLDER_NAME="photos";
    static final String THUMBNAIL_FOLDER_NAME="thumbnails";
    static final int THUMBNAIL_WIDTH=300;

    @Autowired
    private AccountService accountService;

    @Autowired
    private AlbumService albumService;

    @Autowired
    private PhotoService photoService;

//api for add album
    @PostMapping(value="/albums/add",produces = "application/json",consumes="application/json")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiResponse(responseCode = "400", description = "please add valid name a descripton")
    @ApiResponse(responseCode = "201", description = "Invalid ID")
    @Operation(summary = "add album")
    @SecurityRequirement(name = "sourikspring-demo")
    public ResponseEntity<AlbumViewDTO> addAlbum(@Valid @RequestBody AlbumPayloadDTO albumPayloadDTO,Authentication authentication){
        try {
            Album album=new Album();
            album.setName(albumPayloadDTO.getName());
            album.setDescription(albumPayloadDTO.getDescription());
            String email=authentication.getName();
            Optional<Account> optionalAccount=accountService.findByEmail(email);
            Account account=optionalAccount.get();
            album.setAccount(account);
            album=albumService.save(album);
            AlbumViewDTO albumViewDTO=new AlbumViewDTO(album.getId(),album.getName(),album.getDescription());
            return ResponseEntity.ok(albumViewDTO);
        } catch (Exception e) {
            log.debug(AlbumError.ADD_ALBUM_ERROR.toString()+": "+e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

    }


//view all album
    @GetMapping(value="/albums",produces = "application/json")
    @ApiResponse(responseCode = "200", description = "List of album")
    @ApiResponse(responseCode = "400", description = "please add valid name a descripton")
    @ApiResponse(responseCode = "201", description = "Invalid ID")
    @Operation(summary = "List album api")
    @SecurityRequirement(name = "sourikspring-demo")
    public List<AlbumViewDTO> album(Authentication authentication){
         String email=authentication.getName();
         Optional<Account> optionalAccount=accountService.findByEmail(email);
         Account account=optionalAccount.get();
         List<AlbumViewDTO> albums=new ArrayList<>();
         for(Album album:albumService.findByAccount_id(account.getId())){
            albums.add(new AlbumViewDTO(album.getId(),album.getName(),album.getDescription()));
         }
        return albums;
    }


//photo upload api
    @PostMapping(value="/albums/{album_id}/upload-photos",consumes={"multipart/form-data"},produces = "application/json")
    @ApiResponse(responseCode = "200", description = "Succesfully photo upload in album")
    @ApiResponse(responseCode = "400", description = "please check the payload or token")
    @ApiResponse(responseCode = "201", description = "Unauthorized")
    @Operation(summary = "upload photo in album")
    @SecurityRequirement(name = "sourikspring-demo")
    public ResponseEntity<List<HashMap<String,List<String>>>> photos(@RequestPart(required = true) MultipartFile[] files,@PathVariable long album_id,Authentication authentication){
        String email=authentication.getName();
         Optional<Account> optionalAccount=accountService.findByEmail(email);
         Account account=optionalAccount.get();
         Optional<Album> optinalAlbum=albumService.findById(album_id);
         Album album;
         if(optinalAlbum.isPresent()){
            album=optinalAlbum.get();
            if(account.getId()!=album.getAccount().getId()){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }
         }else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
         }


         List<String> fileNamesWithSuccess=new ArrayList<>();
         List<String> fileNamesWithError=new ArrayList<>();

         Arrays.asList(files).stream().forEach(file->{
           //checking type of file 
           String contentType=file.getContentType();
           if(contentType != null && (contentType.equals("image/png")
           || contentType.equals("image/jpg")
           || contentType.equals("image/jpeg"))){
            fileNamesWithSuccess.add(file.getOriginalFilename());

            //this is for file name random 
            int length=10;
            boolean useLetter=true;
            boolean useNumbers=true;

            try {
                String filename=file.getOriginalFilename();
                String generatedString=RandomStringUtils.random(length,useLetter,useNumbers);
                String final_photo_name=generatedString+filename;
                String absolute_fileLocation=AppUtil.get_photo_upload_path(final_photo_name,PHOTOS_FOLDER_NAME, album_id);
                Path path=Paths.get(absolute_fileLocation);
                Files.copy(file.getInputStream(),path,StandardCopyOption.REPLACE_EXISTING);

         //now start to here how to save photo new photo database 
                Photo photo=new Photo();
                photo.setName(filename);
                photo.setFileName(final_photo_name);
                photo.setOriginalFileName(filename);
                photo.setAlbum(album);
                photoService.save(photo);

        //for this use for thumbnail or resize image for first web page loading
                BufferedImage thumbImg=AppUtil.getThumbnail(file, THUMBNAIL_WIDTH);
                File thumbnail_location=new File(AppUtil.get_photo_upload_path(final_photo_name, THUMBNAIL_FOLDER_NAME, album_id));
                ImageIO.write(thumbImg,file.getContentType().split("/")[1],thumbnail_location);

            } catch (Exception e) {
                log.debug(AlbumError.PHOTO_UPLOAD_ERROR.toString()+": "+e.getMessage());
                fileNamesWithError.add(file.getOriginalFilename());
            }
           }
           else{
            fileNamesWithError.add(file.getOriginalFilename());
           }

         });
         
           //for showing error msg
           HashMap<String,List<String>> result=new HashMap<>();
           result.put("SUCCESS",fileNamesWithSuccess);
           result.put("ERRORS",fileNamesWithError);

           List<HashMap<String,List<String>>> response=new ArrayList<>();
           response.add(result);
        return ResponseEntity.ok(response);
    }
   


//downlaod image api
    @GetMapping("/albums/{album_id}/photos/{photo_id}/download-photo")
    @ApiResponse(responseCode = "200", description = "Succesfully photo download")
    @ApiResponse(responseCode = "400", description = "please check the payload or token")
    @ApiResponse(responseCode = "201", description = "Unauthorized")
    @Operation(summary = "Download photo in album")
    @SecurityRequirement(name = "sourikspring-demo")
    public ResponseEntity<?> downloadPhoto(@PathVariable("album_id") long album_id,
                @PathVariable("photo_id") long photo_id,Authentication authentication){

            //error check login user id or album id matched or not
                String email=authentication.getName();
                Optional<Account> optionalAccount=accountService.findByEmail(email);
                Account account=optionalAccount.get();
                Optional<Album> optinalAlbum=albumService.findById(album_id);
                Album album;
               if(optinalAlbum.isPresent()){
                 album=optinalAlbum.get();
                 if(account.getId()!=album.getAccount().getId()){
                   return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
                }
              }else{
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
                }

                Optional<Photo> optionalPhoto=photoService.findById(photo_id);
                if(optionalPhoto.isPresent()){
                    Photo photo=optionalPhoto.get();
                    Resource resource=null;
                    try {
                        resource=AppUtil.getFileAsResource(album_id, PHOTOS_FOLDER_NAME,photo.getFileName());
                    } catch (IOException e) {
                        return ResponseEntity.internalServerError().build();
                    }
                    if(resource==null){
                        return new ResponseEntity<>("File not found",HttpStatus.NOT_FOUND);
                    }
                    String contentType="application/octet-stream";
                    String headerValue="attachment; filename=\""+photo.getOriginalFileName()+"\"";

                return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, headerValue)
                    .body(resource);
                }else{
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
                }

            }

}
