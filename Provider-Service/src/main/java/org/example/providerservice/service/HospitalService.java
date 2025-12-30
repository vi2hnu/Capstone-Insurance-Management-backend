package org.example.providerservice.service;

import org.example.providerservice.dto.AddHospitalDTO;
import org.example.providerservice.dto.HospitalAuthorityDTO;
import org.example.providerservice.model.entity.Hospital;
import org.example.providerservice.model.entity.HospitalAuthority;

public interface HospitalService {
    Hospital addHospital(AddHospitalDTO request);
    HospitalAuthority mapUserToHospital(HospitalAuthorityDTO hospitalAuthorityDTO);
}
