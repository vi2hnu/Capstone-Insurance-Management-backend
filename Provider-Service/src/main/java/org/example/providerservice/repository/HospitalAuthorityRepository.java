package org.example.providerservice.repository;

import org.example.providerservice.model.entity.HospitalAuthority;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HospitalAuthorityRepository extends JpaRepository<HospitalAuthority, Long> {
    
}
