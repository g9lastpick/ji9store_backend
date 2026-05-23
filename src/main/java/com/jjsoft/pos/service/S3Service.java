package com.jjsoft.pos.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.jjsoft.pos.enums.ResponseCode;
import com.jjsoft.pos.exception.GlobalException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {

	@Value("${aws.s3.bucket}")
    private String bucketName;
    @Value("${aws.s3.region}")
    private String region;
    @Value("${aws.s3.access-key}")
    private String accessKey;
    @Value("${aws.s3.secret-key}")
    private String secretKey;

	/**
     * S3 업로드 (public-read)
     */
    public String uploadFile(MultipartFile file) {
    	
    	 String uniqueFileName = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                 + "_" + UUID.randomUUID().toString().replace("-", "")
                 + "_" + file.getOriginalFilename();
    	 
    	 String filePath = "uploads/" + uniqueFileName;
    	
//        String fileName = "uploads/" +
//                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) +
//                "_" + file.getOriginalFilename();

        try {
            S3Client s3 = S3Client.builder()
                    .region(Region.of(region))
//                    .credentialsProvider(
//                            StaticCredentialsProvider.create(
//                                    AwsBasicCredentials.create(accessKey, secretKey)
//                            )
//                    )
                    .build();

// 업로드 요청 + public-read 권한 설정
            s3.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucketName)
                            .key(filePath)
                            .contentType(file.getContentType())
//                            .acl(ObjectCannedACL.PUBLIC_READ) // 퍼블릭 읽기 권한 부여
                            .build(),
                    software.amazon.awssdk.core.sync.RequestBody.fromBytes(file.getBytes())
            );

            // 퍼블릭 URL 반환
            return "https://" + bucketName + ".s3." + region + ".amazonaws.com/" + filePath;

        } catch (S3Exception | IOException e) {
            log.error("S3 업로드 실패", e);
            throw new GlobalException(ResponseCode.IMAGE_UPLOAD_ERR);
        }
    }
    
    
    public boolean deleteFile(String fileUrl) {
        try {
            // S3 key 추출 (URL에서 버킷 도메인 제거)
            String fileKey = fileUrl.replace("https://" + bucketName + ".s3." + region + ".amazonaws.com/", "");

            S3Client s3 = S3Client.builder()
                    .region(Region.of(region))
//                    .credentialsProvider(
//                            StaticCredentialsProvider.create(
//                                    AwsBasicCredentials.create(accessKey, secretKey)
//                            )
//                    )
                    .build();

            s3.deleteObject(DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileKey)
                    .build());

            log.info("S3 파일 삭제 완료: {}", fileKey);
            return true;

        } catch (S3Exception e) {
            log.error("S3 파일 삭제 실패", e);
            throw new RuntimeException("파일 삭제 실패", e);
        }
    }
}
