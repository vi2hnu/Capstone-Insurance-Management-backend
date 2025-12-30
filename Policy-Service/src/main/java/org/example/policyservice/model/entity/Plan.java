package org.example.policyservice.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import org.example.policyservice.model.enums.Status;

@Data
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Plan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String name;
    String description;
    Double premiumAmount;
    Double coverageAmount;
    int duration; //in months

    @Enumerated(EnumType.STRING)
    Status status;

    public Plan(String name,String description,Double premiumAmount, Double coverageAmount,int duration, Status status){
        this.name = name;
        this.description = description;
        this.premiumAmount = premiumAmount;
        this.coverageAmount = coverageAmount;
        this.duration = duration;
        this.status = status;
    }

    public Plan() {

    }
}
