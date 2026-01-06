package org.example.claimsservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.example.claimsservice.dto.ClaimDTO;
import org.example.claimsservice.dto.ClaimsOfficerValidationDTO;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ClaimsOfficerControllerTest {

    @Mock
    private ClaimService claimService;

    @InjectMocks
    private ClaimsOfficerController claimsOfficerController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        mockMvc = MockMvcBuilders.standaloneSetup(claimsOfficerController)
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .build();
    }

    @Test
    void validateClaim_returnsUpdatedClaim_whenValid() throws Exception {
        ClaimsOfficerValidationDTO request = new ClaimsOfficerValidationDTO(
                100L, "officer-1", ReviewStatus.APPROVED, "Approved"
        );
        Claim claim = new Claim();
        claim.setId(100L);

        when(claimService.claimsOfficerValidation(any(ClaimsOfficerValidationDTO.class))).thenReturn(claim);

        mockMvc.perform(put("/api/claims-officer/validate/claim")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(100));

        verify(claimService).claimsOfficerValidation(any(ClaimsOfficerValidationDTO.class));
    }

    @Test
    void validateClaim_returnsBadRequest_whenValidationFails() throws Exception {
        ClaimsOfficerValidationDTO invalidRequest = new ClaimsOfficerValidationDTO(
                null, null, null, null
        );

        mockMvc.perform(put("/api/claims-officer/validate/claim")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getClaimsForOfficer_returnsList() throws Exception {
        Claim claim = new Claim();
        claim.setId(200L);
        ClaimDTO claimDTO = ClaimDTO.fromEntity(claim, "testUser");
        when(claimService.getClaimsForOfficer()).thenReturn(List.of(claimDTO));

        mockMvc.perform(get("/api/claims-officer/get/all/claim"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(200));

        verify(claimService).getClaimsForOfficer();
    }
}