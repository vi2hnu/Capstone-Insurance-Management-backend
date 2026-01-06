package org.example.policyservice.controller;

import java.util.List;

import org.example.policyservice.dto.PlanCountDTO;
import org.example.policyservice.dto.PlanDTO;
import org.example.policyservice.dto.PolicyStatusCountDTO;
import org.example.policyservice.model.entity.Plan;
import org.example.policyservice.model.enums.Status;
import org.example.policyservice.service.PlanService;
import org.example.policyservice.service.implementation.PolicyServiceImpl;
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
class AdminControllerTest {

    @Mock
    private PlanService planService;

    @Mock
    private PolicyServiceImpl policyService;

    @InjectMocks
    private AdminController adminController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(adminController)
                .build();
    }

    @Test
    void addPlan_returnsCreatedAndPlan() throws Exception {
        PlanDTO planDTO = new PlanDTO("Gold Plan", "Comprehensive coverage", 200.0, 5000.0, 12);
        Plan plan = new Plan("Gold Plan", "Comprehensive coverage", 200.0, 5000.0, 12, Status.ACTIVE);
        plan.setId(1L);

        when(planService.addPlan(any(PlanDTO.class))).thenReturn(plan);

        mockMvc.perform(post("/api/admin/plan/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(planDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Gold Plan"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));

        verify(planService).addPlan(any(PlanDTO.class));
    }

    @Test
    void getAnalytics_returnsOkAndList() throws Exception {
        List<PlanCountDTO> analytics = List.of(
                new PlanCountDTO("Gold", 50),
                new PlanCountDTO("Silver", 30)
        );

        when(policyService.getMostPurchasedPlansLastMonth()).thenReturn(analytics);

        mockMvc.perform(get("/api/admin/plan/analytics"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].planName").value("Gold"))
                .andExpect(jsonPath("$[0].count").value(50));

        verify(policyService).getMostPurchasedPlansLastMonth();
    }

    @Test
    void getPoliciesByStatus_returnsOkAndList() throws Exception {
        List<PolicyStatusCountDTO> statusCounts = List.of(
                new PolicyStatusCountDTO(Status.ACTIVE, 100L),
                new PolicyStatusCountDTO(Status.EXPIRED, 20L)
        );

        when(policyService.getPolicyCountByStatus()).thenReturn(statusCounts);

        mockMvc.perform(get("/api/admin/get/policies/by-status"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].status").value("ACTIVE"))
                .andExpect(jsonPath("$[0].count").value(100));

        verify(policyService).getPolicyCountByStatus();
    }
}