package org.example.billingservice.controller;

import org.example.billingservice.dto.CreateOrderDTO;
import org.example.billingservice.dto.PayoutDTO;
import org.example.billingservice.dto.VerifyPaymentDTO;
import org.example.billingservice.model.entity.Transaction;
import org.example.billingservice.model.enums.Purpose;
import org.example.billingservice.model.enums.Status;
import org.example.billingservice.service.PayoutService;
import org.example.billingservice.service.PolicyPaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
class PaymentControllerTest {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private PolicyPaymentService paymentService;

    @Mock
    private PayoutService payoutService;

    @InjectMocks
    private PaymentController paymentController;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(paymentController).build();
    }

    @Test
    void createOrder_returnsCreatedTransaction() throws Exception {
        CreateOrderDTO request = new CreateOrderDTO("user-1", 500.0, Purpose.POLICY_ENROLLMENT);
        Transaction transaction = new Transaction("order_123",null,500.0,"user-1",
                Purpose.POLICY_ENROLLMENT,Status.PENDING);

        when(paymentService.createOrder(any(CreateOrderDTO.class))).thenReturn(transaction);

        mockMvc.perform(post("/api/payment/create/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.orderId").value("order_123"))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void verifyOrder_returnsTrue() throws Exception {
        VerifyPaymentDTO request = new VerifyPaymentDTO("order_123", "pay_123", "sig_123");

        when(paymentService.verifyPayment(any(VerifyPaymentDTO.class))).thenReturn(true);

        mockMvc.perform(post("/api/payment/verify/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));
    }

    @Test
    void payoutUser_callsService() throws Exception {
        PayoutDTO request = new PayoutDTO(1L, "user-1", 10L, 1200.0, 20L);

        doNothing().when(payoutService).payout(any(PayoutDTO.class));

        mockMvc.perform(post("/api/payment/payout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(payoutService).payout(any(PayoutDTO.class));
    }
}
