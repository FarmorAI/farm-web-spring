package com.farmorai.backend.util;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileUrlResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@Log4j2
@RequiredArgsConstructor
public class FileUploadUtil {

    @Value("${com.farmorai.upload.path}")
    private String uploadPath;

    @PostConstruct
    public void init(){
        File tempFolder = new File(uploadPath);

        if(!tempFolder.exists()){ //폴더가 존재하지 않으면 uploadPath에 해당하는 폴더를 생성
            tempFolder.mkdirs();
        }

        String uploadPath = tempFolder.getAbsolutePath();// uploadPath에 해당하는 폴더의 절대경로를 반환

        log.info("uploadPath : {} " , uploadPath);

    }


    public List<String> saveFiles(List<MultipartFile> files) throws RuntimeException{

        if (files == null || files.isEmpty()) {
            return List.of(); //파일이 없으면 빈 리스트 반환
        }

        List<String> uploadFileNames = new ArrayList<>();

        for (MultipartFile file : files) {
            //원본 파일 이름
            String savedName = UUID.randomUUID()+ "_" +file.getOriginalFilename(); //UUID를 이용하여 파일 이름 중복 방지

            Path savePath = Paths.get(uploadPath, savedName);//저장할 파일의 경로를 생성

            try{
                Files.copy(file.getInputStream(), savePath);//파일 저장

                String contentType = file.getContentType();

                log.info("contentType : {}", contentType);

                if(contentType != null || contentType.startsWith("image")){ //이미지 파일인 경우
                    Path thumbnailPath = Paths.get(uploadPath, "s_" + savedName);//썸네일 이미지 파일 경로 생성

                    Thumbnails.of(savePath.toFile()).size(430,430) //원본 이미지 파일을 430x430 크기로 썸네일 생성
                            .toFile(thumbnailPath.toFile());
                }
                uploadFileNames.add(savedName);//파일 이름을 리스트에 추가

            }catch (IOException e){
                throw new RuntimeException("파일 저장에 실패했습니다.");
            }
        }
        return uploadFileNames;
    }


    public ResponseEntity<Resource> getFile(String fileName) throws RuntimeException{

        try {
            Resource resource = new FileUrlResource(uploadPath+File.separator+fileName); //파일 경로를 이용한 Resource 객체 생성

            if(!resource.exists()){ //파일이 존재하지 않으면
                resource = new FileUrlResource(uploadPath+File.separator+"default.png"); //썸네일 파일 경로를 이용한 Resource 객체 생성
            }

            HttpHeaders headers = new HttpHeaders(); //HttpHeaders 객체 생성
            headers.add("Content-Type",Files.probeContentType(resource.getFile().toPath()));


            return ResponseEntity.ok().headers(headers).body(resource); //Resource 객체를 이용한 ResponseEntity 객체 반환

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


    public void deleteFile(List<String> fileNames) throws RuntimeException{

        if(fileNames == null || fileNames.isEmpty()){
            return;
        }

        fileNames.forEach(fileName -> {
            Path file = Paths.get(uploadPath, fileName); //파일 경로

            try {
                Files.deleteIfExists(file); //파일 삭제

                Path thumbnail = Paths.get(uploadPath, "s_" + fileName); //썸네일 파일 경로

                Files.deleteIfExists(thumbnail); //썸네일 파일 삭제

            } catch (IOException e) {
                throw new RuntimeException(e.getMessage());
            }
        });



    }





}
