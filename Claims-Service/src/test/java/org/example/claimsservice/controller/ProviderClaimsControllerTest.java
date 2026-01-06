package org.example.claimsservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.example.claimsservice.dto.AddClaimsDTO;
import org.example.claimsservice.dto.ProviderVerificationDTO;
import org.example.claimsservice.model.entity.Claim;
import org.example.claimsservice.model.enums.ReviewStatus;
import org.example.claimsservice.service.ClaimService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ProviderClaimsControllerTest {

    @Mock
    private ClaimService claimService;

    @InjectMocks
    private ProviderClaimsController providerClaimsController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        mockMvc = MockMvcBuilders.standaloneSetup(providerClaimsController)
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .build();
    }

    @Test
    void getAllClaims_returnsList_whenClaimsExist() throws Exception {
        Long providerId = 1L;
        Claim claim = new Claim();
        claim.setId(101L);

        when(claimService.getClaimByProviderId(providerId)).thenReturn(List.of(claim));

        mockMvc.perform(get("/api/provider/get/all/claims/{providerId}", providerId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(101));

        verify(claimService).getClaimByProviderId(providerId);
    }

    @Test
    void verifyClaim_returnsUpdatedClaim_whenValid() throws Exception {
        ProviderVerificationDTO request = new ProviderVerificationDTO(
                101L, "prov-1", ReviewStatus.APPROVED, "Verified"
        );

        Claim claim = new Claim();
        claim.setId(101L);

        when(claimService.providerVerification(any(ProviderVerificationDTO.class))).thenReturn(claim);

        mockMvc.perform(put("/api/provider/claim/verify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(101));

        verify(claimService).providerVerification(any(ProviderVerificationDTO.class));
    }

    @Test
    void addClaim_returnsClaim_whenValid() throws Exception {
        AddClaimsDTO request = new AddClaimsDTO(
                100L, "user1", 1L, 5000.0, "doc.pdf", null
        );

        Claim claim = new Claim();
        claim.setId(200L);

        when(claimService.providerAddClaim(any(AddClaimsDTO.class))).thenReturn(claim);

        mockMvc.perform(post("/api/provider/add/claim")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(200));

        verify(claimService).providerAddClaim(any(AddClaimsDTO.class));
    }

    @Test
    void getSubmittedClaim_returnsList() throws Exception {
        Long providerId = 55L;
        Claim claim = new Claim();
        claim.setId(300L);

        when(claimService.getSubmittedClaimsOfProvider(providerId)).thenReturn(List.of(claim));

        mockMvc.perform(get("/api/provider/get/submitted/claims/{providerId}", providerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(300));

        verify(claimService).getSubmittedClaimsOfProvider(providerId);
    }

    @Test
    void addClaim_returnsBadRequest_whenValidationFails() throws Exception {
        AddClaimsDTO invalidRequest = new AddClaimsDTO(
                null, null, null, null, null, null
        );

        mockMvc.perform(post("/api/provider/add/claim")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void verifyClaim_returnsBadRequest_whenValidationFails() throws Exception {
        ProviderVerificationDTO invalidRequest = new ProviderVerificationDTO(
                null, null, null, null
        );

        mockMvc.perform(put("/api/provider/claim/verify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }
}
