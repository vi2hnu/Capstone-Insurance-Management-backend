package org.example.claimsservice.controller;

import org.example.claimsservice.dto.ClaimStatusCountDTO;
import org.example.claimsservice.model.entity.Claim;
import org.example.claimsservice.model.enums.ClaimStatus;
import org.example.claimsservice.service.AnalyticService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AnalyticControllerTest {

    @Mock
    private AnalyticService analyticService;

    @InjectMocks
    private AnalyticController analyticController;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(analyticController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
    }

    @Test
    void getClaimsByStatus_returnsOkAndList() throws Exception {
        ClaimStatusCountDTO dto = new ClaimStatusCountDTO(ClaimStatus.APPROVED, 10L);
        when(analyticService.getClaimCountByStatus()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/admin/get/claims/by-status"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].status").value("APPROVED"))
                .andExpect(jsonPath("$[0].count").value(10));
        
        verify(analyticService).getClaimCountByStatus();
    }

    @Test
    void getHighValueClaimsLastMonth_returnsList() throws Exception {
        Claim claim = new Claim();
        claim.setId(200L);
        when(analyticService.getTopHighValueClaimsLastMonth()).thenReturn(List.of(claim));

        mockMvc.perform(get("/api/admin/claims/high-value/last-month"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(200));

        verify(analyticService).getTopHighValueClaimsLastMonth();
    }
}