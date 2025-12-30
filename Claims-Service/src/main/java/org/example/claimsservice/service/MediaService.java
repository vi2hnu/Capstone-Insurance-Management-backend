package org.example.claimsservice.service;

import org.springframework.web.multipart.MultipartFile;

public interface MediaService {
    String uploadPdf(MultipartFile file);
}
