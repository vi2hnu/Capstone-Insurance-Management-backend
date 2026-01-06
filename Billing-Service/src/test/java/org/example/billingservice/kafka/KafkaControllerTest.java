package org.example.billingservice.kafka;

import org.example.billingservice.dto.PayoutDTO;
import org.example.billingservice.service.PayoutService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class KafkaControllerTest {

    @Mock
    private PayoutService payoutService;

    @InjectMocks
    private KafkaController kafkaController;

    @Test
    void listenClaimPayoutTopic_callsPayoutService() {
        PayoutDTO request = new PayoutDTO(1L,"user-1",100L,1500.0,200L);

        kafkaController.listenClaimPayoutTopic(request);

        verify(payoutService).payout(request);
    }
}
