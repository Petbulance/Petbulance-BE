package com.example.Petbulance_BE.global.common.s3;

import com.example.Petbulance_BE.global.common.error.exception.CustomException;
import com.example.Petbulance_BE.global.common.error.exception.ErrorCode;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.net.URL;
import java.time.Duration;
//ec2에 배포할때는 iam 사용자가 아닌 role로 수정하는 것도 좋아 보임!
@Service
@Slf4j
public class S3Service {

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.region.static}")
    private String region;

    @Value("${cloud.aws.credentials.access-key}")
    private String accessKey;

    @Value("${cloud.aws.credentials.secret-key}")
    private String secretKey;

    private S3Client s3;
    private S3Presigner presigner;

    @PostConstruct
    public void init() {
        StaticCredentialsProvider creds = StaticCredentialsProvider.create(
                AwsBasicCredentials.create(accessKey, secretKey)
        );

        s3 = S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(creds)
                .build();

        presigner = S3Presigner.builder()
                .region(Region.of(region))
                .credentialsProvider(creds)
                .build();
    }

    //key는 s3 객체 키(예: user/profile/userId.png)
    //expireSeconds는 url 만료 시간
    public URL createPresignedPutUrl(String key, String contentType, long expireSeconds) {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(contentType)
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .putObjectRequest(putObjectRequest)
                .signatureDuration(Duration.ofSeconds(expireSeconds))
                .build();

        PresignedPutObjectRequest presignedRequest = presigner.presignPutObject(presignRequest);
        return presignedRequest.url();
    }

    //객체 존재 확인
    public boolean doesObjectExist(String key) {
        try{
            HeadObjectRequest headReq = HeadObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .build();
            s3.headObject(headReq);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    //객체 삭제
    public void deleteObject(String key) {
        try{
            DeleteObjectRequest delReq = DeleteObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .build();
            s3.deleteObject(delReq);
        } catch (Exception e) {
            log.error("이미지 삭제에서 발생한 예외 {}", e.getMessage());
            throw new CustomException(ErrorCode.FAIL_DELETE_IMAGE);
        }
    }

    //객체 조회 URL
    public String getObject(String key) {
        return "https://" + bucket + ".s3." + region + ".amazonaws.com/" + key;
    }


}
