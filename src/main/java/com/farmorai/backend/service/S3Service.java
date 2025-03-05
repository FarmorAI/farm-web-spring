package com.farmorai.backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.IOException;
import java.util.UUID;



@Service
@RequiredArgsConstructor
@Slf4j
public class S3Service {


    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.region.static}")
    private String region;

    private final S3Client s3Client;

    /**
     * S3에 파일 업로드
     * @param file
     * @return
     */
    public String uploadFile(MultipartFile file){
        String fileName = UUID.randomUUID()+"_"+file.getOriginalFilename(); // 파일명 중복 방지
        try {
            s3Client.putObject(PutObjectRequest.builder().bucket(bucket).key(fileName).build()
                    , RequestBody.fromBytes(file.getBytes()));
            log.info("files uploaded to S3: {}", fileName);
            return getPublicUrl(fileName);
        } catch (S3Exception | IOException e) {
            log.error("파일 업로드 실패 : {}",e.getMessage());
            throw new RuntimeException("파일 업로드 중 오류가 발생했습니다.",e);
        }

    }

    /**
     * S3에 파일 삭제
     * @param fileName
     * @return fileName
     */
    public String deleteFile(String fileName){
        try{
            s3Client.deleteObject(builder -> builder.bucket(bucket).key(getPublicUrl(fileName)));
            log.info("files deleted from S3: {}", fileName);
            return getPublicUrl(fileName);
        }catch (S3Exception e){
            log.error("파일 삭제 실패 : {}",e.getMessage());
            throw new RuntimeException("파일 삭제 중 오류가 발생했습니다.",e);
        }

    }





    private String getPublicUrl(String fileName) {
        return String.format("https://%s.s3.%s.amazonaws.com/%s", bucket,region,fileName);
    }








}
