package com.devaneios.turmadeelite.services;

import com.devaneios.turmadeelite.entities.Attachment;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;

public interface DataStorageService {
    void uploadFile(String key, Object inputStream)throws IOException ;
    InputStream downloadFile(String key)throws IOException;
    String getKey(String path, String fileName);
    void deleteObject(String key);
    Attachment from(MultipartFile multipartFile, String path) throws IOException, NoSuchAlgorithmException;
}
