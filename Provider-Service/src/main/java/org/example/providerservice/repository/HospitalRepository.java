package org.example.providerservice.repository;

import org.example.providerservice.model.entity.Hospital;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HospitalRepository extends JpaRepository<Hospital, Long> {
    Boolean existsByhospitalNameAndCityName(String hospitalName,String cityName);
}
