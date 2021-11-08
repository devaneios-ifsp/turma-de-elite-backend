package com.devaneios.turmadeelite.services.impl;

import com.devaneios.turmadeelite.entities.Attachment;
import com.devaneios.turmadeelite.services.DataStorageService;
import org.springframework.context.annotation.Profile;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;

@Service
// @Profile("test")
public class MockStorage implements DataStorageService {

    @Override
    public void uploadFile(String key, Object inputStream) throws IOException {
        System.out.println("Call mocked method");
    }

    @Override
    public InputStream downloadFile(String key) throws IOException {
        MockMultipartFile multipartFile = new MockMultipartFile("file", "fis".getBytes());
        return multipartFile.getInputStream();
    }

    @Override
    public String getKey(String path, String fileName) {
        return "/key";
    }

    @Override
    public void deleteObject(String key) {
        System.out.println("Call mocked method");
    }

    @Override
    public Attachment from(MultipartFile multipartFile, String path) throws IOException, NoSuchAlgorithmException {
        return Attachment
                .builder()
                .filename("document")
                .fileMd5("1234")
                .bucketKey("/key")
                .build();
    }
}

