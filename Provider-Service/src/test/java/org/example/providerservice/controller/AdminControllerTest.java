package org.example.providerservice.controller;

import java.util.List;

import org.example.providerservice.dto.AddHospitalDTO;
import org.example.providerservice.dto.HospitalAuthorityDTO;
import org.example.providerservice.model.entity.Hospital;
import org.example.providerservice.model.entity.HospitalAuthority;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
class AdminControllerTest {

    @Mock
    private HospitalService hospitalService;

    @InjectMocks
    private AdminController adminController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(adminController).build();
    }

    @Test
    void addHospital_returnsOkAndHospital() throws Exception {
        AddHospitalDTO request = new AddHospitalDTO("City Hospital", "New York", "1234567890", "info@city.com");
        Hospital hospital = new Hospital("City Hospital", "New York", "1234567890", "info@city.com");
        hospital.setId(1L);

        when(hospitalService.addHospital(any(AddHospitalDTO.class))).thenReturn(hospital);

        mockMvc.perform(post("/api/admin/add/hospital")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.hospitalName").value("City Hospital"));

        verify(hospitalService).addHospital(any(AddHospitalDTO.class));
    }

    @Test
    void mapUser_returnsOkAndAuthority() throws Exception {
        HospitalAuthorityDTO request = new HospitalAuthorityDTO(1L, "user1");
        Hospital hospital = new Hospital();
        hospital.setId(1L);
        HospitalAuthority authority = new HospitalAuthority(hospital, "user1");

        when(hospitalService.mapUserToHospital(any(HospitalAuthorityDTO.class))).thenReturn(authority);

        mockMvc.perform(post("/api/admin/map/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value("user1"));

        verify(hospitalService).mapUserToHospital(any(HospitalAuthorityDTO.class));
    }

    @Test
    void getAllHospital_returnsList() throws Exception {
        Hospital hospital = new Hospital("General Hospital", "Boston", "9876543210", "info@general.com");
        when(hospitalService.getAll()).thenReturn(List.of(hospital));

        mockMvc.perform(get("/api/admin/get/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].hospitalName").value("General Hospital"))
                .andExpect(jsonPath("$[0].cityName").value("Boston"));

        verify(hospitalService).getAll();
    }
}