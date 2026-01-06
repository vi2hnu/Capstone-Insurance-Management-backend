package org.example.policyservice.controller;

import java.util.List;

import org.example.policyservice.model.entity.Plan;
import org.example.policyservice.service.PlanService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class PlanControllerTest {

    @Mock
    private PlanService planService;

    @InjectMocks
    private PlanController planController;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(planController).build();
    }

    @Test
    void getAllPlan_returnsList() throws Exception {
        Plan plan = new Plan();
        plan.setId(1L);
        plan.setName("Platinum");
        
        when(planService.getAllPlans()).thenReturn(List.of(plan));

        mockMvc.perform(get("/api/plan/get/all"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Platinum"));

        verify(planService).getAllPlans();
    }

    @Test
    void getPlan_returnsPlan_whenFound() throws Exception {
        Long planId = 10L;
        Plan plan = new Plan();
        plan.setId(planId);
        plan.setName("Gold");

        when(planService.getPlan(planId)).thenReturn(plan);

        mockMvc.perform(get("/api/plan/get/{id}", planId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(planId))
                .andExpect(jsonPath("$.name").value("Gold"));

        verify(planService).getPlan(planId);
    }
}