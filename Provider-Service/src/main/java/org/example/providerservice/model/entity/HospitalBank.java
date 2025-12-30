package org.example.providerservice.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Data;

@Entity
@Data
public class HospitalBank {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @OneToOne
    @JoinColumn(name = "hospital_id", unique = true, nullable = false)
    Hospital hospital;
    String bankName;
    String accountNumber;
    String ifsc;

    public HospitalBank(Hospital hospital, String bankName, String accountNumber, String ifsc) {
        this.hospital = hospital;
        this.bankName = bankName;
        this.accountNumber = accountNumber;
        this.ifsc = ifsc;
    }

    public HospitalBank() {
        
    }

}
