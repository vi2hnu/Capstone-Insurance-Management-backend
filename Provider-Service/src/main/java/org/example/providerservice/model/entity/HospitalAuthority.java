package org.example.providerservice.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data
public class HospitalAuthority {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "hospital_id")
    Hospital hospital;
    String userId;

    public HospitalAuthority(Hospital hospital, String userId) {
        this.hospital = hospital;
        this.userId = userId;
    }

    public HospitalAuthority() {

    }
}
