package org.example.providerservice.repository;

import org.example.providerservice.model.entity.Hospital;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HospitalRepository extends JpaRepository<Hospital, Long> {
    Boolean existsByhospitalNameAndCityName(String hospitalName,String cityName);
}
