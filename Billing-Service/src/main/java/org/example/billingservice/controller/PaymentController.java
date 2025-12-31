package org.example.billingservice.controller;

import com.razorpay.RazorpayException;
import jakarta.validation.Valid;
import org.example.billingservice.dto.CreateOrderDTO;
import org.example.billingservice.dto.PayoutDTO;
import org.example.billingservice.dto.VerifyPaymentDTO;
import org.example.billingservice.model.entity.Transaction;
import org.example.billingservice.service.PayoutService;
import org.example.billingservice.service.PolicyPaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    private final PolicyPaymentService paymentService;
    private final PayoutService  payoutService;
    public PaymentController(PolicyPaymentService paymentService, PayoutService payoutService) {
        this.paymentService = paymentService;
        this.payoutService = payoutService;
    }

    @PostMapping("/create/order")
    public ResponseEntity<Transaction> createOrder(@RequestBody @Valid CreateOrderDTO request) throws RazorpayException {
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentService.createOrder(request));
    }

    @PostMapping("/verify/order")
    public ResponseEntity<Boolean> verifyOrder(@RequestBody @Valid VerifyPaymentDTO request) throws RazorpayException {
        return ResponseEntity.status(HttpStatus.OK).body(paymentService.verifyPayment(request));
    }

    @PostMapping("/payout")
    public void payoutUser(@RequestBody @Valid PayoutDTO request){
        payoutService.payout(request);
    }
}
