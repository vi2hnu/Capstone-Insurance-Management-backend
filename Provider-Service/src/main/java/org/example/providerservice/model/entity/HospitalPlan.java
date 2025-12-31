package org.example.providerservice.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.example.providerservice.model.enums.NetworkType;

@Entity
@Data
public class HospitalPlan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "hospital_id")
    Hospital hospital;
    Long planId;

    @Enumerated(EnumType.STRING)
    NetworkType networkType;

    public HospitalPlan(Hospital hospital, Long planId, NetworkType networkType) {
        this.hospital = hospital;
        this.planId = planId;
        this.networkType = networkType;
    }

    public HospitalPlan() {

    }
}
