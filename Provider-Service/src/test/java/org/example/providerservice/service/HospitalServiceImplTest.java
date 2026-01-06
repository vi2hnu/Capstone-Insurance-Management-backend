package org.example.providerservice.service;

import java.util.List;
import java.util.Optional;

import org.example.providerservice.dto.AddHospitalDTO;
import org.example.providerservice.dto.BankDetailsDTO;
import org.example.providerservice.dto.HospitalAuthorityDTO;
import org.example.providerservice.dto.RegisterPlanDTO;
import org.example.providerservice.exception.HospitalAlreadyExistsException;
import org.example.providerservice.exception.HospitalBankNotFoundException;
import org.example.providerservice.exception.HospitalNotFoundException;
import org.example.providerservice.exception.PlanAlreadyRegisteredException;
import org.example.providerservice.exception.PlanNotFoundException;
import org.example.providerservice.exception.UserAlreadyRegisteredException;
import org.example.providerservice.model.entity.Hospital;
import org.example.providerservice.model.entity.HospitalAuthority;
import org.example.providerservice.model.entity.HospitalBank;
import org.example.providerservice.model.entity.HospitalPlan;
import org.example.providerservice.model.enums.NetworkType;
import org.example.providerservice.repository.HospitalAuthorityRepository;
import org.example.providerservice.repository.HospitalBankRepository;
import org.example.providerservice.repository.HospitalPlanRepository;
import org.example.providerservice.repository.HospitalRepository;
import org.example.providerservice.service.implementation.HospitalServiceImpl;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class HospitalServiceImplTest {

    @Mock
    private HospitalRepository hospitalRepository;
    @Mock
    private HospitalAuthorityRepository hospitalAuthorityRepository;
    @Mock
    private HospitalBankRepository hospitalBankRepository;
    @Mock
    private HospitalPlanRepository hospitalPlanRepository;

    @InjectMocks
    private HospitalServiceImpl hospitalService;

    @Test
    void addHospital_shouldSaveHospital_whenNew() {
        AddHospitalDTO request = new AddHospitalDTO("City Hospital", "New York", "1234567890", "info@city.com");
        when(hospitalRepository.existsByhospitalNameAndCityName(request.hospitalName(), request.cityName()))
                .thenReturn(false);
        when(hospitalRepository.save(any(Hospital.class))).thenAnswer(i -> i.getArguments()[0]);

        Hospital result = hospitalService.addHospital(request);

        assertNotNull(result);
        assertEquals("City Hospital", result.getHospitalName());
        verify(hospitalRepository).save(any(Hospital.class));
    }

    @Test
    void addHospital_shouldThrowException_whenExists() {
        AddHospitalDTO request = new AddHospitalDTO("City Hospital", "New York", "1234567890", "info@city.com");
        when(hospitalRepository.existsByhospitalNameAndCityName(request.hospitalName(), request.cityName()))
                .thenReturn(true);

        assertThrows(HospitalAlreadyExistsException.class, () -> hospitalService.addHospital(request));
        verify(hospitalRepository, never()).save(any());
    }

    @Test
    void mapUserToHospital_shouldMap_whenValid() {
        HospitalAuthorityDTO dto = new HospitalAuthorityDTO(1L, "user1");
        Hospital hospital = new Hospital();
        hospital.setId(1L);

        when(hospitalRepository.findById(1L)).thenReturn(Optional.of(hospital));
        when(hospitalAuthorityRepository.existsByUserId("user1")).thenReturn(false);
        when(hospitalAuthorityRepository.save(any(HospitalAuthority.class))).thenAnswer(i -> i.getArguments()[0]);

        HospitalAuthority result = hospitalService.mapUserToHospital(dto);

        assertEquals("user1", result.getUserId());
        assertEquals(hospital, result.getHospital());
    }

    @Test
    void mapUserToHospital_shouldThrow_whenUserRegistered() {
        HospitalAuthorityDTO dto = new HospitalAuthorityDTO(1L, "user1");
        Hospital hospital = new Hospital();

        when(hospitalRepository.findById(1L)).thenReturn(Optional.of(hospital));
        when(hospitalAuthorityRepository.existsByUserId("user1")).thenReturn(true);

        assertThrows(UserAlreadyRegisteredException.class, () -> hospitalService.mapUserToHospital(dto));
    }

    @Test
    void addHospitalBank_shouldSave_whenAuthorized() {
        BankDetailsDTO dto = new BankDetailsDTO("user1", 1L, "HDFC", "12345", "HDFC001");
        Hospital hospital = new Hospital();
        hospital.setId(1L);

        when(hospitalRepository.findById(1L)).thenReturn(Optional.of(hospital));
        when(hospitalAuthorityRepository.existsByHospitalAndUserId(hospital, "user1")).thenReturn(true);
        when(hospitalBankRepository.save(any(HospitalBank.class))).thenAnswer(i -> i.getArguments()[0]);

        HospitalBank result = hospitalService.addHospitalBank(dto);

        assertEquals("HDFC", result.getBankName());
    }

    @Test
    void addHospitalBank_shouldThrow_whenNotAuthorized() {
        BankDetailsDTO dto = new BankDetailsDTO("user1", 1L, "HDFC", "12345", "HDFC001");
        Hospital hospital = new Hospital();

        when(hospitalRepository.findById(1L)).thenReturn(Optional.of(hospital));
        when(hospitalAuthorityRepository.existsByHospitalAndUserId(hospital, "user1")).thenReturn(false);

        assertThrows(HospitalNotFoundException.class, () -> hospitalService.addHospitalBank(dto));
    }

    @Test
    void registerPlan_shouldRegister_whenInNetworkAndBankExists() {
        RegisterPlanDTO dto = new RegisterPlanDTO("user1", 1L, 100L, NetworkType.IN_NETWORK);
        Hospital hospital = new Hospital();

        when(hospitalRepository.findById(1L)).thenReturn(Optional.of(hospital));
        when(hospitalAuthorityRepository.existsByHospitalAndUserId(hospital, "user1")).thenReturn(true);
        when(hospitalPlanRepository.existsByHospitalAndPlanId(hospital, 100L)).thenReturn(false);
        when(hospitalBankRepository.existsByHospital(hospital)).thenReturn(true);
        when(hospitalPlanRepository.save(any(HospitalPlan.class))).thenAnswer(i -> i.getArguments()[0]);

        HospitalPlan result = hospitalService.registerPlan(dto);

        assertEquals(NetworkType.IN_NETWORK, result.getNetworkType());
        assertEquals(100L, result.getPlanId());
    }

    @Test
    void registerPlan_shouldThrow_whenInNetworkAndNoBank() {
        RegisterPlanDTO dto = new RegisterPlanDTO("user1", 1L, 100L, NetworkType.IN_NETWORK);
        Hospital hospital = new Hospital();

        when(hospitalRepository.findById(1L)).thenReturn(Optional.of(hospital));
        when(hospitalAuthorityRepository.existsByHospitalAndUserId(hospital, "user1")).thenReturn(true);
        when(hospitalPlanRepository.existsByHospitalAndPlanId(hospital, 100L)).thenReturn(false);
        when(hospitalBankRepository.existsByHospital(hospital)).thenReturn(false);

        assertThrows(HospitalBankNotFoundException.class, () -> hospitalService.registerPlan(dto));
    }

    @Test
    void registerPlan_shouldThrow_whenPlanAlreadyRegistered() {
        RegisterPlanDTO dto = new RegisterPlanDTO("user1", 1L, 100L, NetworkType.OUT_NETWORK);
        Hospital hospital = new Hospital();

        when(hospitalRepository.findById(1L)).thenReturn(Optional.of(hospital));
        when(hospitalAuthorityRepository.existsByHospitalAndUserId(hospital, "user1")).thenReturn(true);
        when(hospitalPlanRepository.existsByHospitalAndPlanId(hospital, 100L)).thenReturn(true);

        assertThrows(PlanAlreadyRegisteredException.class, () -> hospitalService.registerPlan(dto));
    }

    @Test
    void getAllHospitalsByPlan_shouldReturnList() {
        when(hospitalPlanRepository.findByPlanId(1L)).thenReturn(List.of(new HospitalPlan()));
        assertFalse(hospitalService.getAllHospitalsByPlan(1L).isEmpty());
    }

    @Test
    void checkHospitalPlan_shouldReturnTrue_whenExists() {
        Hospital hospital = new Hospital();
        when(hospitalRepository.findById(1L)).thenReturn(Optional.of(hospital));
        when(hospitalPlanRepository.existsByHospitalAndPlanId(hospital, 100L)).thenReturn(true);

        assertTrue(hospitalService.checkHospitalPlan(100L, 1L));
    }

    @Test
    void checkAssociation_shouldReturnTrue_whenExists() {
        Hospital hospital = new Hospital();
        when(hospitalRepository.findById(1L)).thenReturn(Optional.of(hospital));
        when(hospitalAuthorityRepository.existsByHospitalAndUserId(hospital, "user1")).thenReturn(true);

        assertTrue(hospitalService.checkAssociation("user1", 1L));
    }

    @Test
    void getProviderType_shouldReturnNetworkType() {
        Hospital hospital = new Hospital();
        HospitalPlan plan = new HospitalPlan(hospital, 100L, NetworkType.IN_NETWORK);

        when(hospitalRepository.findById(1L)).thenReturn(Optional.of(hospital));
        when(hospitalPlanRepository.findByHospitalAndPlanId(hospital, 100L)).thenReturn(plan);

        assertEquals(NetworkType.IN_NETWORK, hospitalService.getProviderType(100L, 1L));
    }

    @Test
    void getProviderType_shouldThrow_whenPlanNotRegistered() {
        Hospital hospital = new Hospital();

        when(hospitalRepository.findById(1L)).thenReturn(Optional.of(hospital));
        when(hospitalPlanRepository.findByHospitalAndPlanId(hospital, 100L)).thenReturn(null);

        assertThrows(PlanNotFoundException.class, () -> hospitalService.getProviderType(100L, 1L));
    }

    @Test
    void getAssociatedHospital_shouldReturnHospital() {
        Hospital hospital = new Hospital();
        HospitalAuthority auth = new HospitalAuthority(hospital, "user1");

        when(hospitalAuthorityRepository.findByUserId("user1")).thenReturn(auth);

        assertEquals(hospital, hospitalService.getAssociatedHospital("user1"));
    }
}