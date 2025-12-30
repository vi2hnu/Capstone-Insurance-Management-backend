package org.example.providerservice.service.implementation;

import java.util.List;

import org.example.providerservice.dto.AddHospitalDTO;
import org.example.providerservice.dto.BankDetailsDTO;
import org.example.providerservice.dto.HospitalAuthorityDTO;
import org.example.providerservice.dto.RegisterPlanDTO;
import org.example.providerservice.exception.HospitalBankNotFoundException;
import org.example.providerservice.exception.HospitalNotFoundException;
import org.example.providerservice.exception.PlanAlreadyRegisteredException;
import org.example.providerservice.model.entity.Hospital;
import org.example.providerservice.model.entity.HospitalAuthority;
import org.example.providerservice.model.entity.HospitalBank;
import org.example.providerservice.model.entity.HospitalPlan;
import org.example.providerservice.model.enums.Type;
import org.example.providerservice.repository.HospitalAuthorityRepository;
import org.example.providerservice.repository.HospitalBankRepository;
import org.example.providerservice.repository.HospitalPlanRepository;
import org.example.providerservice.repository.HospitalRepository;
import org.example.providerservice.service.HospitalService;
import org.springframework.stereotype.Service;

@Service
public class HospitalServiceImpl implements HospitalService {

    private final HospitalRepository hospitalRepository;
    private final HospitalAuthorityRepository hospitalAuthorityRepository;
    private final HospitalBankRepository hospitalBankRepository;
    private final HospitalPlanRepository hospitalPlanRepository;

    public HospitalServiceImpl(HospitalRepository hospitalRepository, 
        HospitalAuthorityRepository hospitalAuthorityRepository, HospitalBankRepository hospitalBankRepository, 
        HospitalPlanRepository hospitalPlanRepository) {
        this.hospitalRepository = hospitalRepository;
        this.hospitalAuthorityRepository = hospitalAuthorityRepository;
        this.hospitalBankRepository = hospitalBankRepository;
        this.hospitalPlanRepository = hospitalPlanRepository;
    }

    @Override
    public Hospital addHospital(AddHospitalDTO request) {
        Hospital hospital = new Hospital(request.hospitalName(),request.cityName(),
                request.phoneNumber(),request.email());
        return hospitalRepository.save(hospital);
    }

    @Override
    public HospitalAuthority mapUserToHospital(HospitalAuthorityDTO hospitalAuthorityDTO) {
        Hospital hospital = hospitalRepository.findById(hospitalAuthorityDTO.hospitalId())
                .orElseThrow(() -> new HospitalNotFoundException("Hospital not found"));
        HospitalAuthority hospitalAuthority = new HospitalAuthority(hospital, hospitalAuthorityDTO.userId());
        return hospitalAuthorityRepository.save(hospitalAuthority);
    }

    @Override
    public HospitalBank addHospitalBank(BankDetailsDTO bankDetailsDTO) {
        Hospital hospital = hospitalRepository.findById(bankDetailsDTO.hospitalId())
                .orElseThrow(() -> new HospitalNotFoundException("Hospital not found"));
        
        if(!hospitalAuthorityRepository.existsByHospitalAndUserId(hospital, bankDetailsDTO.userId())) {
            throw new HospitalNotFoundException("User is not authorized for this hospital");
        }
        
        HospitalBank hospitalBank = new HospitalBank(hospital, bankDetailsDTO.bankName(), 
        bankDetailsDTO.accountNumber(), bankDetailsDTO.ifsc());
        return hospitalBankRepository.save(hospitalBank);
    }

    @Override
    public HospitalPlan registerPlan(RegisterPlanDTO request) {
        Hospital hospital = hospitalRepository.findById(request.hospitalId())
                .orElseThrow(() -> new HospitalNotFoundException("Hospital not found"));

        if(!hospitalAuthorityRepository.existsByHospitalAndUserId(hospital, request.userId())) {
            throw new HospitalNotFoundException("User is not authorized for this hospital");
        }

        if(hospitalPlanRepository.existsByHospitalAndPlanId(hospital, request.planId())) {
            throw new PlanAlreadyRegisteredException("Plan already registered for this hospital");
        }

        if(request.type()== Type.IN_NETWORK && !hospitalBankRepository.existsByHospital(hospital)){
            throw new HospitalBankNotFoundException("Hospital bank not found");
        }

        HospitalPlan hospitalPlan = new HospitalPlan(hospital,request.planId(),request.type());
        return hospitalPlanRepository.save(hospitalPlan);
    }
    @Override
    public List<HospitalPlan> getAllHospitalsByPlan(Long planId) {
        return hospitalPlanRepository.findByPlanId(planId);
    }
}