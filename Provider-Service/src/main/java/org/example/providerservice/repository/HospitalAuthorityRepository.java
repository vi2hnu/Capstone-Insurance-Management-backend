package org.example.providerservice.repository;

import org.example.providerservice.model.entity.Hospital;
import org.example.providerservice.model.entity.HospitalAuthority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HospitalAuthorityRepository extends JpaRepository<HospitalAuthority, Long> {

    boolean existsByHospitalAndUserId(Hospital hospital, String userId);
    Boolean existsByUserId(String userId);

    HospitalAuthority findByUserId(String userId);
}
