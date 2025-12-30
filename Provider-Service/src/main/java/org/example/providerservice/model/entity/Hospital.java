package org.example.providerservice.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Hospital {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String hospitalName;
    String cityName;
    String phoneNumber;
    String email;

    public Hospital(String hospitalName, String cityName, String phoneNumber, String email) {
        this.hospitalName = hospitalName;
        this.cityName = cityName;
        this.phoneNumber = phoneNumber;
        this.email = email;
    }

    public Hospital() {

    }
}
