package org.example.billingservice.controller;

import com.razorpay.RazorpayException;
import jakarta.validation.Valid;
import org.example.billingservice.dto.CreateOrderDTO;
import org.example.billingservice.dto.VerifyPaymentDTO;
import org.example.billingservice.model.entity.Transaction;
import org.example.billingservice.service.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    private final PaymentService paymentService;
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/create/order")
    public ResponseEntity<Transaction> createOrder(@RequestBody @Valid CreateOrderDTO request) throws RazorpayException {
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentService.createOrder(request));
    }

    @PostMapping("/verify/order")
    public ResponseEntity<Transaction> verifyOrder(@RequestBody @Valid VerifyPaymentDTO request) throws RazorpayException {
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentService.verifyPayment(request));
    }
}
