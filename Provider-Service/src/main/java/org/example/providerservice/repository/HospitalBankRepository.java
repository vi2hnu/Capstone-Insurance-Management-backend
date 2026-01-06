package org.example.providerservice.repository;

import org.example.providerservice.model.entity.Hospital;
import org.example.providerservice.model.entity.HospitalBank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HospitalBankRepository extends JpaRepository<HospitalBank, Long> {
    Boolean existsByHospital(Hospital hospital);
}
