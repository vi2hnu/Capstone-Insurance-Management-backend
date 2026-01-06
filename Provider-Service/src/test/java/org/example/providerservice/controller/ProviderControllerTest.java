package org.example.providerservice.controller;

import java.util.List;

import org.example.providerservice.dto.BankDetailsDTO;
import org.example.providerservice.dto.RegisterPlanDTO;
import org.example.providerservice.model.entity.Hospital;
import org.example.providerservice.model.entity.HospitalBank;
import org.example.providerservice.model.entity.HospitalPlan;
import org.example.providerservice.model.enums.NetworkType;
import org.example.providerservice.service.HospitalService;
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
class ProviderControllerTest {

    @Mock
    private HospitalService hospitalService;

    @InjectMocks
    private ProviderController providerController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(providerController).build();
    }

    @Test
    void addBank_returnsOkAndBank() throws Exception {
        BankDetailsDTO request = new BankDetailsDTO("user1", 1L, "HDFC", "123456", "HDFC001");
        HospitalBank bank = new HospitalBank();
        bank.setId(10L);
        bank.setBankName("HDFC");

        when(hospitalService.addHospitalBank(any(BankDetailsDTO.class))).thenReturn(bank);

        mockMvc.perform(post("/api/provider/authority/add/bank")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.bankName").value("HDFC"));

        verify(hospitalService).addHospitalBank(any(BankDetailsDTO.class));
    }

    @Test
    void registerPlan_returnsOkAndPlan() throws Exception {
        RegisterPlanDTO request = new RegisterPlanDTO("user1", 1L, 100L, NetworkType.IN_NETWORK);
        HospitalPlan plan = new HospitalPlan();
        plan.setId(20L);
        plan.setNetworkType(NetworkType.IN_NETWORK);

        when(hospitalService.registerPlan(any(RegisterPlanDTO.class))).thenReturn(plan);

        mockMvc.perform(post("/api/provider/authority/register/plan")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(20))
                .andExpect(jsonPath("$.networkType").value("IN_NETWORK"));

        verify(hospitalService).registerPlan(any(RegisterPlanDTO.class));
    }

    @Test
    void getAllPlan_returnsList() throws Exception {
        Long planId = 100L;
        HospitalPlan plan = new HospitalPlan();
        plan.setPlanId(planId);

        when(hospitalService.getAllHospitalsByPlan(planId)).thenReturn(List.of(plan));

        mockMvc.perform(get("/api/provider/get/all/{planId}", planId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].planId").value(planId));

        verify(hospitalService).getAllHospitalsByPlan(planId);
    }

    @Test
    void getAssociatedHospital_returnsHospital() throws Exception {
        String userId = "user1";
        Hospital hospital = new Hospital();
        hospital.setId(1L);
        hospital.setHospitalName("City Hospital");

        when(hospitalService.getAssociatedHospital(userId)).thenReturn(hospital);

        mockMvc.perform(get("/api/provider/get/associated/hospital/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.hospitalName").value("City Hospital"));

        verify(hospitalService).getAssociatedHospital(userId);
    }

    @Test
    void checkPlan_returnsBoolean() throws Exception {
        Long planId = 100L;
        Long hospitalId = 1L;

        when(hospitalService.checkHospitalPlan(planId, hospitalId)).thenReturn(true);

        mockMvc.perform(get("/api/provider/check/plan/{planId}/{hospitalId}", planId, hospitalId))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        verify(hospitalService).checkHospitalPlan(planId, hospitalId);
    }

    @Test
    void checkAssociation_returnsBoolean() throws Exception {
        String userId = "user1";
        Long hospitalId = 1L;

        when(hospitalService.checkAssociation(userId, hospitalId)).thenReturn(true);

        mockMvc.perform(get("/api/provider/check/association/{userId}/{hospitalId}", userId, hospitalId))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        verify(hospitalService).checkAssociation(userId, hospitalId);
    }

    @Test
    void getProviderType_returnsNetworkType() throws Exception {
        Long planId = 100L;
        Long hospitalId = 1L;

        when(hospitalService.getProviderType(planId, hospitalId)).thenReturn(NetworkType.IN_NETWORK);

        mockMvc.perform(get("/api/provider/get/type/{planId}/{hospitalId}", planId, hospitalId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("IN_NETWORK"));

        verify(hospitalService).getProviderType(planId, hospitalId);
    }
}
