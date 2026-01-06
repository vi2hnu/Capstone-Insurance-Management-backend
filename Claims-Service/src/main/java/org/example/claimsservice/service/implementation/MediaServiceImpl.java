package org.example.claimsservice.service.implementation;

import java.io.IOException;
import java.util.Map;

import org.example.claimsservice.exception.UnsupportedFileTypeException;
import org.example.claimsservice.service.MediaService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MediaServiceImpl implements MediaService {

    private final Cloudinary cloudinary;

    public MediaServiceImpl(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    @Override
    public String uploadPdf(MultipartFile file) {
        try {
            String extension = StringUtils.getFilenameExtension(file.getOriginalFilename());
            if(!extension.equals("pdf")) {
                log.error("unsupported file type was uploaded: {}",extension);
                throw new UnsupportedFileTypeException("Unsupported file type uploaded");
            }
            Map result = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "resource_type", "raw",
                            "folder", "pdfs"
                    )
            );
            return (String) result.get("secure_url");
        } 
        catch (IOException | UnsupportedFileTypeException e) {
            throw new UnsupportedFileTypeException("Failed to upload document");
        }
    }
}
