package org.example.billingservice.service;

import java.lang.reflect.Field;

import org.example.billingservice.dto.CreateOrderDTO;
import org.example.billingservice.dto.VerifyPaymentDTO;
import org.example.billingservice.exception.TransactionNotFoundException;
import org.example.billingservice.model.entity.Transaction;
import org.example.billingservice.model.enums.Purpose;
import org.example.billingservice.model.enums.Status;
import org.example.billingservice.repository.TransactionRepository;
import org.example.billingservice.service.implementation.PolicyPaymentServiceImpl;
import org.json.JSONObject;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.razorpay.Order;
import com.razorpay.OrderClient;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;

@ExtendWith(MockitoExtension.class)
class PolicyPaymentServiceImplTest {

    @Mock
    private RazorpayClient razorpayClient;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private PolicyPaymentServiceImpl policyPaymentService;

    private OrderClient mockOrderClient;

    @BeforeEach
    void setup() throws Exception {
        Field secretField = PolicyPaymentServiceImpl.class.getDeclaredField("razorpaySecret");
        secretField.setAccessible(true);
        secretField.set(policyPaymentService, "test_secret_key");

        mockOrderClient = mock(OrderClient.class);
        Field ordersField = RazorpayClient.class.getDeclaredField("orders");
        ordersField.setAccessible(true);
        ordersField.set(razorpayClient, mockOrderClient);
    }

    @Test
    void createOrder_continuesFlowWhenRazorpayCreatesOrder() throws RazorpayException {
        String userId = "user-123";
        Double amount = 500.0;
        Purpose purpose = Purpose.POLICY_ENROLLMENT;
        CreateOrderDTO request = new CreateOrderDTO(userId, amount, purpose);

        JSONObject orderJson = new JSONObject();
        orderJson.put("id", "order_rzp_111");
        orderJson.put("amount", 50000);
        Order razorpayOrder = new Order(orderJson);

        when(mockOrderClient.create(any(JSONObject.class))).thenReturn(razorpayOrder);
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(i -> i.getArgument(0));

        Transaction result = policyPaymentService.createOrder(request);

        assertNotNull(result);
        assertEquals("order_rzp_111", result.getOrderId());
        assertEquals(Status.PENDING, result.getStatus());
        assertEquals(amount, result.getAmount());

        verify(mockOrderClient).create(any(JSONObject.class));
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void createOrder_throwsExceptionWhenRazorpayFails() throws RazorpayException {
        CreateOrderDTO request = new CreateOrderDTO("u1", 100.0, Purpose.POLICY_RENEWAL);

        when(mockOrderClient.create(any(JSONObject.class)))
                .thenThrow(new RazorpayException("API Error"));

        RazorpayException exception = assertThrows(
                RazorpayException.class,
                () -> policyPaymentService.createOrder(request)
        );

        assertEquals("Cant create order with razor pay client", exception.getMessage());
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void verifyPayment_updatesStatusToSuccessWhenSignatureIsValid() throws RazorpayException {
        String orderId = "order_rzp_123";
        String payId = "pay_123";
        String signature = "valid_signature";
        VerifyPaymentDTO request = new VerifyPaymentDTO(orderId, payId, signature);

        Transaction existingTransaction =
                new Transaction(orderId, null, 500.0, "u1", Purpose.POLICY_ENROLLMENT, Status.PENDING);

        when(transactionRepository.findByOrderId(orderId)).thenReturn(existingTransaction);

        try (MockedStatic<Utils> mockedUtils = mockStatic(Utils.class)) {
            mockedUtils.when(() ->
                    Utils.verifyPaymentSignature(any(JSONObject.class), eq("test_secret_key"))
            ).thenReturn(true);

            Boolean result = policyPaymentService.verifyPayment(request);

            assertTrue(result);
            assertEquals(Status.SUCCESS, existingTransaction.getStatus());
            assertEquals(payId, existingTransaction.getPaymentId());
            assertNotNull(existingTransaction.getCreatedAt());

            ArgumentCaptor<Transaction> txCaptor = ArgumentCaptor.forClass(Transaction.class);
            verify(transactionRepository).save(txCaptor.capture());
            assertEquals(Status.SUCCESS, txCaptor.getValue().getStatus());
        }
    }

    @Test
    void verifyPayment_updatesStatusToFailureWhenSignatureIsInvalid() throws RazorpayException {
        String orderId = "order_rzp_bad";
        VerifyPaymentDTO request = new VerifyPaymentDTO(orderId, "pay_bad", "bad_sig");

        Transaction existingTransaction =
                new Transaction(orderId, null, 500.0, "u1", Purpose.POLICY_ENROLLMENT, Status.PENDING);

        when(transactionRepository.findByOrderId(orderId)).thenReturn(existingTransaction);

        try (MockedStatic<Utils> mockedUtils = mockStatic(Utils.class)) {
            mockedUtils.when(() ->
                    Utils.verifyPaymentSignature(any(JSONObject.class), eq("test_secret_key"))
            ).thenReturn(false);

            Boolean result = policyPaymentService.verifyPayment(request);

            assertFalse(result);
            assertEquals(Status.FAILURE, existingTransaction.getStatus());

            verify(transactionRepository).save(existingTransaction);
        }
    }

    @Test
    void verifyPayment_throwsExceptionWhenTransactionNotFound() {
        VerifyPaymentDTO request = new VerifyPaymentDTO("order_unknown", "pay_x", "sig_x");
        when(transactionRepository.findByOrderId("order_unknown")).thenReturn(null);

        TransactionNotFoundException ex = assertThrows(
                TransactionNotFoundException.class,
                () -> policyPaymentService.verifyPayment(request)
        );

        assertEquals("transaction not found", ex.getMessage());
    }

}
