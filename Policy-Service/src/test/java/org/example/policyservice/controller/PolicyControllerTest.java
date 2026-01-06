package org.example.policyservice.controller;

import java.util.List;

import org.example.policyservice.dto.CoverageChangeDTO;
import org.example.policyservice.dto.PolicyEnrollDTO;
import org.example.policyservice.dto.PolicyUserDTO;
import org.example.policyservice.model.entity.Policy;
import org.example.policyservice.service.PolicyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
class PolicyControllerTest {

    @Mock
    private PolicyService policyService;

    @InjectMocks
    private PolicyController policyController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(policyController).build();
    }

    @Test
    void enrollUser_returnsCreatedAndPolicy() throws Exception {
        PolicyEnrollDTO request = new PolicyEnrollDTO("user1", 1L, "agent1");
        Policy policy = new Policy();
        policy.setId(100L);
        policy.setUserId("user1");

        when(policyService.enrollUser(any(PolicyEnrollDTO.class))).thenReturn(policy);

        mockMvc.perform(post("/api/policy/enroll")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.userId").value("user1"));

        verify(policyService).enrollUser(any(PolicyEnrollDTO.class));
    }

    @Test
    void cancelPolicy_returnsNoContent() throws Exception {
        PolicyUserDTO request = new PolicyUserDTO("user1", 100L, null);

        mockMvc.perform(delete("/api/policy/cancel")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());

        verify(policyService).cancelPolicy(any(PolicyUserDTO.class));
    }

    @Test
    void renewPolicy_returnsCreatedAndPolicy() throws Exception {
        PolicyUserDTO request = new PolicyUserDTO("user1", 100L, null);
        Policy policy = new Policy();
        policy.setId(100L);
        policy.setRenewalCounter(1);

        when(policyService.renewPolicy(any(PolicyUserDTO.class))).thenReturn(policy);

        mockMvc.perform(post("/api/policy/renew")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.renewalCounter").value(1));

        verify(policyService).renewPolicy(any(PolicyUserDTO.class));
    }

    @Test
    void getAllEnrolledPolicy_returnsList() throws Exception {
        String userId = "user1";
        Policy policy = new Policy();
        policy.setUserId(userId);

        when(policyService.viewAllRegisteredPolicies(userId)).thenReturn(List.of(policy));

        mockMvc.perform(get("/api/policy/get/all/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value(userId));

        verify(policyService).viewAllRegisteredPolicies(userId);
    }

    @Test
    void changeClaimedAmount_returnsUpdatedPolicy() throws Exception {
        CoverageChangeDTO request = new CoverageChangeDTO(100L, 500.0);
        Policy policy = new Policy();
        policy.setId(100L);
        policy.setRemainingCoverage(4500.0);

        when(policyService.changeCoverage(any(CoverageChangeDTO.class))).thenReturn(policy);

        mockMvc.perform(put("/api/policy/change/claimed-amount")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.remainingCoverage").value(4500.0));

        verify(policyService).changeCoverage(any(CoverageChangeDTO.class));
    }

    @Test
    void getPolicy_byId_returnsPolicy() throws Exception {
        Long policyId = 100L;
        Policy policy = new Policy();
        policy.setId(policyId);

        when(policyService.getPolicyById(policyId)).thenReturn(policy);

        mockMvc.perform(get("/api/policy/get/{policyId}", policyId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(policyId));

        verify(policyService).getPolicyById(policyId);
    }

    @Test
    void getPolicy_checkEnrollment_returnsPolicy() throws Exception {
        String userId = "user1";
        Long planId = 50L;
        Policy policy = new Policy();
        policy.setUserId(userId);

        when(policyService.getEnrollment(userId, planId)).thenReturn(policy);

        mockMvc.perform(get("/api/policy/check/enrollment/{userId}/{planId}", userId, planId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userId));

        verify(policyService).getEnrollment(userId, planId);
    }
}