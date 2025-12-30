package org.example.providerservice.service.implementation;

import org.example.providerservice.dto.AddHospitalDTO;
import org.example.providerservice.dto.HospitalAuthorityDTO;
import org.example.providerservice.exception.HospitalNotFoundException;
import org.example.providerservice.model.entity.Hospital;
import org.example.providerservice.model.entity.HospitalAuthority;
import org.example.providerservice.repository.HospitalAuthorityRepository;
import org.example.providerservice.repository.HospitalRepository;
import org.example.providerservice.service.HospitalService;
import org.springframework.stereotype.Service;

@Service
public class HospitalServiceImpl implements HospitalService {

    private final HospitalRepository hospitalRepository;
    private final HospitalAuthorityRepository hospitalAuthorityRepository;

    public HospitalServiceImpl(HospitalRepository hospitalRepository, HospitalAuthorityRepository hospitalAuthorityRepository) {
        this.hospitalRepository = hospitalRepository;
        this.hospitalAuthorityRepository = hospitalAuthorityRepository;
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

}