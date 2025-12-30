package org.example.providerservice.repository;

import java.util.List;

import org.example.providerservice.model.entity.Hospital;
import org.example.providerservice.model.entity.HospitalPlan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HospitalPlanRepository extends JpaRepository<HospitalPlan, Long> {
    Boolean existsByHospitalAndPlanId(Hospital hospital, Long planId);
    List<HospitalPlan> findByPlanId(Long planId);
}
