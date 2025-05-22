package com.spring.springRestDemo.controller;

import org.springframework.security.core.Authentication;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.spring.springRestDemo.model.Account;
import com.spring.springRestDemo.model.Album;
import com.spring.springRestDemo.payload.auth.album.AlbumPayloadDTO;
import com.spring.springRestDemo.payload.auth.album.AlbumViewDTO;
import com.spring.springRestDemo.service.AccountService;
import com.spring.springRestDemo.service.AlbumService;
import com.spring.springRestDemo.util.constants.AlbumError;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/album")
@Tag(name="Album  Controller",description="controller for album and photo management")
@Slf4j
public class AlbumController {


    @Autowired
    private AccountService accountService;

    @Autowired
    private AlbumService albumService;
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



    @GetMapping(value="/albums",produces = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
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
}
