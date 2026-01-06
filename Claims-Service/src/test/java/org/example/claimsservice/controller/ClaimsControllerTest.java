package org.example.claimsservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.example.claimsservice.dto.AddClaimsDTO;
import org.example.claimsservice.model.entity.Claim;
import org.example.claimsservice.service.ClaimService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ClaimsControllerTest {

    @Mock
    private ClaimService claimService;

    @InjectMocks
    private ClaimsController claimsController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        mockMvc = MockMvcBuilders.standaloneSetup(claimsController)
                .build();
    }

    @Test
    void addClaim_returnsCreatedAndClaim_whenValid() throws Exception {
        AddClaimsDTO request = new AddClaimsDTO(100L, "user1", 1L, 5000.0, "doc.pdf", null);
        Claim claim = new Claim();
        claim.setId(10L);
        
        when(claimService.addClaim(any(AddClaimsDTO.class))).thenReturn(claim);

        mockMvc.perform(post("/api/claim/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10));

        verify(claimService).addClaim(any(AddClaimsDTO.class));
    }

    @Test
    void getClaimsByUser_returnsList() throws Exception {
        String userId = "user123";
        Claim claim = new Claim();
        claim.setUserId(userId);

        when(claimService.getClaimsByUserId(userId)).thenReturn(List.of(claim));

        mockMvc.perform(get("/api/claim/user/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value(userId));

        verify(claimService).getClaimsByUserId(userId);
    }

    @Test
    void getClaimById_returnsClaim() throws Exception {
        Long claimId = 55L;
        Claim claim = new Claim();
        claim.setId(claimId);

        when(claimService.getClaimById(claimId)).thenReturn(claim);

        mockMvc.perform(get("/api/claim/get/{id}", claimId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(claimId));

        verify(claimService).getClaimById(claimId);
    }

    @Test
    void markAsPaid_returnsUpdatedClaim() throws Exception {
        Long claimId = 55L;
        Claim claim = new Claim();
        claim.setId(claimId);

        when(claimService.changeStatus(claimId)).thenReturn(claim);

        mockMvc.perform(put("/api/claim/mark/paid/{claimId}", claimId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(claimId));

        verify(claimService).changeStatus(claimId);
    }

    @Test
    void addClaim_returnsBadRequest_whenValidationFails() throws Exception {
        AddClaimsDTO invalidRequest = new AddClaimsDTO(null, null, null, null, null, null);

        mockMvc.perform(post("/api/claim/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }
}