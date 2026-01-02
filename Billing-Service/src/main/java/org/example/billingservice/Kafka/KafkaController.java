package org.example.billingservice.Kafka;

import org.example.billingservice.dto.PayoutDTO;
import org.example.billingservice.service.PayoutService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaController {
    
    private final PayoutService payoutService;
    public KafkaController(PayoutService payoutService) {
        this.payoutService = payoutService;
    }

    @KafkaListener(topics="claim-payout")
    public void listenClaimPayoutTopic(PayoutDTO request){
        payoutService.payout(request);
    }
}
