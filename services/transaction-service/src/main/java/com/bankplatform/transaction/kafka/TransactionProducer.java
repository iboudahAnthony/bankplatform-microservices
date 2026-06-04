package com.bankplatform.transaction.kafka;

import com.bankplatform.transaction.entity.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class TransactionProducer {

    private static final Logger log = LoggerFactory.getLogger(TransactionProducer.class);

    private final KafkaTemplate<String, TransactionEvent> kafkaTemplate;

    @Autowired
    public TransactionProducer(KafkaTemplate<String, TransactionEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishTransactionCompleted(Transaction transaction) {
        TransactionEvent event = new TransactionEvent(
            "TRANSACTION_COMPLETED",
            transaction.getId(),
            transaction.getReference(),
            transaction.getType().name(),
            transaction.getSourceAccountId(),
            transaction.getDestinationAccountId(),
            transaction.getAmount(),
            transaction.getCurrency(),
            transaction.getStatus().name(),
            transaction.getInitiatedBy()
        );

        kafkaTemplate.send(KafkaConfig.TRANSACTION_COMPLETED_TOPIC,
                           transaction.getId().toString(), event);

        log.info("[KAFKA] Événement publié : TRANSACTION_COMPLETED - Ref: {}",
                 transaction.getReference());
    }

    public void publishTransactionFailed(Transaction transaction) {
        TransactionEvent event = new TransactionEvent(
            "TRANSACTION_FAILED",
            transaction.getId(),
            transaction.getReference(),
            transaction.getType().name(),
            transaction.getSourceAccountId(),
            transaction.getDestinationAccountId(),
            transaction.getAmount(),
            transaction.getCurrency(),
            transaction.getStatus().name(),
            transaction.getInitiatedBy()
        );

        kafkaTemplate.send(KafkaConfig.TRANSACTION_FAILED_TOPIC,
                           transaction.getId().toString(), event);

        log.info("[KAFKA] Événement publié : TRANSACTION_FAILED - Ref: {}",
                 transaction.getReference());
    }
}
