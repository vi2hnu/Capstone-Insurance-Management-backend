package org.example.claimsservice.model.entity;


import lombok.Data;

@Data
public class BankAccount {
    String bankName;
    String accountNumber;
    String ifscCode;
}
