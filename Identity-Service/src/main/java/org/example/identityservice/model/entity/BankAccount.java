package org.example.identityservice.model.entity;

import lombok.Data;

@Data
public class BankAccount {
    String bankName;
    String accountNumber;
    String ifscCode;

    public BankAccount(String bankName, String accountNumber, String ifscCode) {
        this.bankName = bankName;
        this.accountNumber = accountNumber;
        this.ifscCode = ifscCode;
    }
}
