package org.example.billingservice.service;

import com.razorpay.RazorpayException;
import org.example.billingservice.dto.CreateOrderDTO;
import org.example.billingservice.dto.VerifyPaymentDTO;
import org.example.billingservice.model.entity.Transaction;

public interface PaymentService {
    Transaction createOrder(CreateOrderDTO request) throws RazorpayException;
    Boolean verifyPayment(VerifyPaymentDTO request) throws RazorpayException;
}
