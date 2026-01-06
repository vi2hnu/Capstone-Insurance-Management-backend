package org.example.policyservice.controller;

import java.util.List;

import org.example.policyservice.dto.GetUserIdDTO;
import org.example.policyservice.feign.IdentityService;
import org.example.policyservice.model.entity.Policy;
import org.example.policyservice.model.enums.Gender;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
class AgentControllerTest {

    @Mock
    private IdentityService identityService;

    @Mock
    private PolicyService policyService;

    @InjectMocks
    private AgentController agentController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(agentController).build();
    }

    @Test
    void enrollUser_returnsCreatedAndUserId() throws Exception {
        GetUserIdDTO request = new GetUserIdDTO("John Doe", "john@example.com", Gender.MALE);
        String expectedUserId = "user-123";

        when(identityService.getUserId(any(GetUserIdDTO.class))).thenReturn(expectedUserId);

        mockMvc.perform(post("/api/agent/get/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().string(expectedUserId));

        verify(identityService).getUserId(any(GetUserIdDTO.class));
    }

    @Test
    void getAllEnrollment_returnsListOfPolicies() throws Exception {
        String agentId = "agent-007";
        Policy policy = new Policy();
        policy.setId(101L);
        policy.setAgentId(agentId);

        when(policyService.getAllAgentEnrolledPolicies(agentId)).thenReturn(List.of(policy));

        mockMvc.perform(get("/api/agent/get/all/enrollment/{agentId}", agentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(101))
                .andExpect(jsonPath("$[0].agentId").value(agentId));

        verify(policyService).getAllAgentEnrolledPolicies(agentId);
    }
}