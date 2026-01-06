package org.example.billingservice.service.implementation;

import java.time.LocalDateTime;

import org.example.billingservice.dto.CreateOrderDTO;
import org.example.billingservice.dto.VerifyPaymentDTO;
import org.example.billingservice.exception.TransactionNotFoundException;
import org.example.billingservice.model.entity.Transaction;
import org.example.billingservice.model.enums.Status;
import org.example.billingservice.repository.TransactionRepository;
import org.example.billingservice.service.PolicyPaymentService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;

@Service
public class PolicyPaymentServiceImpl implements PolicyPaymentService {

    @Value("${razorpay.key.secret}")
    private String razorpaySecret;


    private final RazorpayClient razorpayClient;
    private final TransactionRepository transactionRepository;

    public PolicyPaymentServiceImpl(RazorpayClient razorpayClient, TransactionRepository transactionRepository) {
        this.razorpayClient = razorpayClient;
        this.transactionRepository = transactionRepository;
    }

    @Override
    public Transaction createOrder(CreateOrderDTO request) throws RazorpayException {
        try{
            JSONObject json = new JSONObject();
            json.put("amount",(request.amount() * 100));
            json.put("currency","INR");
            Order order = razorpayClient.orders.create(json);
            Transaction transaction = new Transaction(order.get("id"),null,request.amount(),request.userId()
            ,request.purpose(),Status.PENDING);
            return transactionRepository.save(transaction);
        }
        catch (RazorpayException e){
            throw new RazorpayException("Cant create order with razor pay client");
        }
    }

    @Override
    public Boolean verifyPayment(VerifyPaymentDTO request) throws RazorpayException {
        Transaction transaction = transactionRepository
                .findByOrderId(request.razorpayOrderId());
        if(transaction==null){
            throw new TransactionNotFoundException("transaction not found");
        }
        try {
            JSONObject options = new JSONObject();
            options.put("razorpay_order_id", request.razorpayOrderId());
            options.put("razorpay_payment_id", request.razorpayPaymentId());
            options.put("razorpay_signature", request.razorpaySignature());

            boolean isValid = Utils.verifyPaymentSignature(
                    options,
                    razorpaySecret
            );
            transaction.setPaymentId(request.razorpayPaymentId());
            transaction.setCreatedAt(LocalDateTime.now());

            if(!isValid){
                transaction.setStatus(Status.FAILURE);
                transactionRepository.save(transaction);
                return false;
            }

            transaction.setStatus(Status.SUCCESS);
            transactionRepository.save(transaction);
            return true;

        } catch (RazorpayException e) {
            throw new RazorpayException("Payment verification failed");
        }
    }
}
