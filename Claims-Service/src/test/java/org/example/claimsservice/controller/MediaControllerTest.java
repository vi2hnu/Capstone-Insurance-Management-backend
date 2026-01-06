package org.example.claimsservice.controller;

import org.example.claimsservice.service.MediaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class MediaControllerTest {

    @Mock
    private MediaService mediaService;

    @InjectMocks
    private MediaController mediaController;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(mediaController).build();
    }

    @Test
    void uploadPdf_returnsCreatedAndUrl_whenUploadSucceeds() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.pdf",  MediaType.APPLICATION_PDF_VALUE,
         "test data".getBytes());
        
        String expectedUrl = "http://cloudinary.com/doc.pdf";
        
        when(mediaService.uploadPdf(any(MultipartFile.class))).thenReturn(expectedUrl);

        mockMvc.perform(multipart("/api/media/upload").file(file))
                .andExpect(status().isCreated())
                .andExpect(content().string(expectedUrl));

        verify(mediaService).uploadPdf(any(MultipartFile.class));
    }

    @Test
    void uploadPdf_returnsBadRequest_whenFilePartIsMissing() throws Exception {
        mockMvc.perform(multipart("/api/media/upload"))
                .andExpect(status().isBadRequest());
    }
}
