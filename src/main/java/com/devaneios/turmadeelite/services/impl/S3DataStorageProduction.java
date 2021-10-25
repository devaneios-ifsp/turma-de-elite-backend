package com.devaneios.turmadeelite.services.impl;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.devaneios.turmadeelite.entities.Attachment;
import com.devaneios.turmadeelite.services.DataStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

//@Service
//@Profile("!test")
public class S3DataStorageProduction implements DataStorageService {

    private final AmazonS3 s3;

    @Value("${aws.s3.bucketName}")
    String bucketName;

    public S3DataStorageProduction(
            @Value("${aws.s3.region}") String region,
            @Value("${aws.s3.accessKeyId}") String accessKeyId,
            @Value("${aws.s3.accessKey}") String accessKey
    ){
        BasicAWSCredentials credentials = new BasicAWSCredentials(accessKeyId, accessKey);

        this.s3 = AmazonS3ClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(Regions.fromName(region))
                .build();
    }

    @Override
    public void uploadFile(String key, Object fileInputStream) throws IOException {
        FileInputStream inputStream = (FileInputStream) fileInputStream;
        try {
            long size = inputStream.getChannel().size();
            ObjectMetadata metaData = new ObjectMetadata();
            metaData.setContentLength(size);
            this.s3.putObject(bucketName,key,inputStream,metaData);
        } catch (IOException e) {
            throw e;
        } finally {
            inputStream.close();
        }
    }

    @Override
    public InputStream downloadFile(String key) throws IOException {
        S3Object s3Object = this.s3.getObject(bucketName, key);
        return s3Object.getObjectContent();
    }

    @Override
    public String getKey(String path,String fileName) {
        String uuid = UUID.randomUUID().toString();
        String key = path + uuid + fileName;
        return key;
    }

    @Override
    public void deleteObject(String key) {
        this.s3.deleteObject(bucketName,key);
    }

    @Override
    public Attachment from(MultipartFile multipartFile,String path) throws IOException, NoSuchAlgorithmException {
        String key = this.getKey("activities/teachers-posts/", multipartFile.getOriginalFilename());
        String md5 = this.getMd5(multipartFile.getInputStream());
        return Attachment
                .builder()
                .bucketKey(key)
                .fileMd5(md5)
                .filename(multipartFile.getOriginalFilename())
                .build();
    }

    private String getMd5(InputStream inputStream) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        DigestInputStream digestInputStream = new DigestInputStream(inputStream, messageDigest);
        byte[] bytes = digestInputStream.getMessageDigest().digest();
        BigInteger bi = new BigInteger(1, bytes);
        return String.format("%0" + (bytes.length << 1) + "X", bi);
    }
}
