package org.example.providerservice.service;

import java.util.List;

import org.example.providerservice.dto.AddHospitalDTO;
import org.example.providerservice.dto.BankDetailsDTO;
import org.example.providerservice.dto.HospitalAuthorityDTO;
import org.example.providerservice.dto.RegisterPlanDTO;
import org.example.providerservice.model.entity.Hospital;
import org.example.providerservice.model.entity.HospitalAuthority;
import org.example.providerservice.model.entity.HospitalBank;
import org.example.providerservice.model.entity.HospitalPlan;
import org.example.providerservice.model.enums.NetworkType;

public interface HospitalService {
    Hospital addHospital(AddHospitalDTO request);
    HospitalAuthority mapUserToHospital(HospitalAuthorityDTO hospitalAuthorityDTO);
    HospitalBank addHospitalBank(BankDetailsDTO bankDetailsDTO);
    HospitalPlan registerPlan(RegisterPlanDTO request);
    List<HospitalPlan>  getAllHospitalsByPlan(Long planId);
    Boolean checkHospitalPlan(Long planId,Long hospitalId);
    Boolean checkAssociation(String userId, Long hospitalId);
    NetworkType getProviderType(Long planId, Long hospitalId);
}
