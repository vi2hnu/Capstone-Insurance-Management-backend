package org.example.claimsservice.service;

import java.io.IOException;
import java.util.Map;

import org.example.claimsservice.exception.UnsupportedFileTypeException;
import org.example.claimsservice.service.implementation.MediaServiceImpl;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.Uploader;

@ExtendWith(MockitoExtension.class)
class MediaServiceImplTest {

    @Mock
    private Cloudinary cloudinary;

    @Mock
    private Uploader uploader;

    @Mock
    private MultipartFile multipartFile;

    @InjectMocks
    private MediaServiceImpl mediaService;

    @Test
    void uploadPdf_returnsUrl_whenExtensionIsPdfAndUploadSucceeds() throws IOException {
        String expectedUrl = "https://cloudinary.com/secure/doc.pdf";
        byte[] fileBytes = "test content".getBytes();

        when(multipartFile.getOriginalFilename()).thenReturn("document.pdf");
        when(multipartFile.getBytes()).thenReturn(fileBytes);
        
        when(cloudinary.uploader()).thenReturn(uploader);
        when(uploader.upload(any(byte[].class), anyMap())).thenReturn(Map.of("secure_url", expectedUrl));

        String result = mediaService.uploadPdf(multipartFile);

        assertEquals(expectedUrl, result);
        verify(cloudinary).uploader();
        verify(uploader).upload(eq(fileBytes), anyMap());
    }

    @Test
    void uploadPdf_throwsUnsupportedFileTypeException_whenExtensionIsNotPdf() {
        when(multipartFile.getOriginalFilename()).thenReturn("image.png");

        UnsupportedFileTypeException ex = assertThrows(UnsupportedFileTypeException.class,
                () -> mediaService.uploadPdf(multipartFile));

        assertEquals("Failed to upload document", ex.getMessage());
        
        verifyNoInteractions(cloudinary);
    }

    @Test
    void uploadPdf_throwsUnsupportedFileTypeException_whenCloudinaryThrowsException() throws IOException {
        when(multipartFile.getOriginalFilename()).thenReturn("test.pdf");
        when(multipartFile.getBytes()).thenReturn(new byte[0]);

        when(cloudinary.uploader()).thenReturn(uploader);
        when(uploader.upload(any(byte[].class), anyMap())).thenThrow(new IOException("Cloudinary unavailable"));

        UnsupportedFileTypeException ex = assertThrows(UnsupportedFileTypeException.class,
                () -> mediaService.uploadPdf(multipartFile));

        assertEquals("Failed to upload document", ex.getMessage());
    }
}