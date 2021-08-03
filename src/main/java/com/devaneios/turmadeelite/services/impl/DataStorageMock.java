package com.devaneios.turmadeelite.services.impl;

import com.devaneios.turmadeelite.entities.Attachment;
import com.devaneios.turmadeelite.services.DataStorageService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;

@Service
public class DataStorageMock implements DataStorageService  {

    @Override
    public void uploadFile(String key, FileInputStream inputStream) throws IOException {

    }

    @Override
    public InputStream downloadFile(String key) throws IOException {
        return null;
    }

    @Override
    public String getKey(String path, String fileName) {
        return null;
    }

    @Override
    public void deleteObject(String key) {

    }

    @Override
    public Attachment from(MultipartFile multipartFile, String path) throws IOException, NoSuchAlgorithmException {
        return null;
    }
}
