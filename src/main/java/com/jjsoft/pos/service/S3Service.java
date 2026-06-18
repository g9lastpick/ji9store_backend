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
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.core.ResponseBytes;

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

    /**
     * S3 객체 다운로드 (bytes)
     */
    public byte[] downloadFile(String fileUrl) {
        try {
            // 저장 URL이 설정 버킷과 다를 수 있어(dev DB에 운영 버킷 URL 혼재) URL에서 버킷/리전/키를 파싱해 서명 요청
            java.net.URL u = new java.net.URL(fileUrl);
            String host = u.getHost();                       // {bucket}.s3.{region}.amazonaws.com
            int dotS3 = host.indexOf(".s3");
            String bkt = dotS3 > 0 ? host.substring(0, dotS3) : bucketName;
            String rgn = region;
            if (dotS3 > 0) {
                String after = host.substring(dotS3 + 3);    // ".{region}.amazonaws.com" 또는 ".amazonaws.com"
                if (after.startsWith(".")) after = after.substring(1);
                int amzn = after.indexOf(".amazonaws.com");
                if (amzn > 0) rgn = after.substring(0, amzn); // region 추출
            }
            String key = u.getPath();
            if (key.startsWith("/")) key = key.substring(1);  // 원본 키(미인코딩) — SDK가 서명 시 인코딩

            S3Client s3 = S3Client.builder()
                    .region(Region.of(rgn))
                    .credentialsProvider(
                            StaticCredentialsProvider.create(
                                    AwsBasicCredentials.create(accessKey, secretKey)
                            )
                    )
                    .build();

            ResponseBytes<GetObjectResponse> bytes = s3.getObjectAsBytes(
                    GetObjectRequest.builder()
                            .bucket(bkt)
                            .key(key)
                            .build());
            return bytes.asByteArray();

        } catch (Exception e) {
            log.error("이미지 다운로드 실패: {}", fileUrl, e);
            throw new RuntimeException("파일 다운로드 실패", e);
        }
    }
}
