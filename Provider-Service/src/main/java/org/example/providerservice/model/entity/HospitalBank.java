package org.example.providerservice.model.entity;

import jakarta.persistence.*;
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

}
