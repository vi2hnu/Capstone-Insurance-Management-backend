package org.example.billingservice.service;

import org.example.billingservice.dto.PayoutDTO;

public interface PayoutService {
    void payout(PayoutDTO request);
}
