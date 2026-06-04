package com.bankplatform.transaction.service;

import com.bankplatform.transaction.dto.TransactionRequest;
import com.bankplatform.transaction.dto.TransactionResponse;
import com.bankplatform.transaction.entity.Transaction;
import com.bankplatform.transaction.entity.TransactionStatus;
import com.bankplatform.transaction.entity.TransactionType;
import com.bankplatform.transaction.repository.TransactionRepository;
import com.bankplatform.transaction.kafka.TransactionProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    private static final Logger log = LoggerFactory.getLogger(TransactionService.class);

    private final TransactionRepository transactionRepository;
    private final WebClient webClient;
    private final TransactionProducer transactionProducer;

    @Value("${account-service.url}")
    private String accountServiceUrl;

    @Autowired
    public TransactionService(TransactionRepository transactionRepository,
                              WebClient.Builder webClientBuilder,
                              TransactionProducer transactionProducer) {
        this.transactionRepository = transactionRepository;
        this.webClient = webClientBuilder.build();
        this.transactionProducer = transactionProducer;
    }

    private String generateReference() {
        return "TXN-" + System.currentTimeMillis() + "-" + (int)(Math.random() * 10000);
    }

    @Transactional
    public TransactionResponse processTransaction(TransactionRequest request) {
        // 1. Créer la transaction en état PENDING
        Transaction transaction = Transaction.builder()
                .reference(generateReference())
                .type(request.getType())
                .sourceAccountId(request.getSourceAccountId())
                .destinationAccountId(request.getDestinationAccountId())
                .amount(request.getAmount())
                .fees(BigDecimal.ZERO)
                .commission(BigDecimal.ZERO)
                .currency(request.getCurrency() != null ? request.getCurrency() : "XOF")
                .status(TransactionStatus.PENDING)
                .description(request.getDescription())
                .initiatedBy(request.getInitiatedBy())
                .operatorSourceId(request.getOperatorSourceId())
                .operatorDestinationId(request.getOperatorDestinationId())
                .build();

        transaction = transactionRepository.save(transaction);

        try {
            // 2. Exécuter selon le type
            switch (request.getType()) {
                case DEPOT -> executeDeposit(transaction);
                case RETRAIT -> executeWithdrawal(transaction);
                case TRANSFERT_INTRA, TRANSFERT_INTER -> executeTransfer(transaction);
                default -> throw new IllegalArgumentException("Type de transaction non supporté");
            }

            // 3. Marquer comme COMPLETED
            transaction.setStatus(TransactionStatus.COMPLETED);
            transaction.setCompletedAt(LocalDateTime.now());
            transaction = transactionRepository.save(transaction);

            // 4. Publier l'événement Kafka
            try {
                transactionProducer.publishTransactionCompleted(transaction);
            } catch (Exception e) {
                log.warn("[KAFKA] Impossible de publier l'événement (Kafka non disponible): {}", e.getMessage());
            }

        } catch (Exception e) {
            transaction.setStatus(TransactionStatus.FAILED);
            transactionRepository.save(transaction);

            // Publier l'événement d'échec
            try {
                transactionProducer.publishTransactionFailed(transaction);
            } catch (Exception ke) {
                log.warn("[KAFKA] Impossible de publier l'événement d'échec: {}", ke.getMessage());
            }

            throw new RuntimeException("Transaction échouée : " + e.getMessage());
        }

        return TransactionResponse.from(transaction);
    }

    private void executeDeposit(Transaction transaction) {
        // Créditer le compte destination
        webClient.post()
                .uri(accountServiceUrl + "/accounts/" + transaction.getDestinationAccountId() + "/credit")
                .bodyValue(Map.of("amount", transaction.getAmount()))
                .retrieve()
                .bodyToMono(Object.class)
                .block();
    }

    private void executeWithdrawal(Transaction transaction) {
        // Débiter le compte source
        webClient.post()
                .uri(accountServiceUrl + "/accounts/" + transaction.getSourceAccountId() + "/debit")
                .bodyValue(Map.of("amount", transaction.getAmount()))
                .retrieve()
                .bodyToMono(Object.class)
                .block();
    }

    private void executeTransfer(Transaction transaction) {
        // Débiter source
        webClient.post()
                .uri(accountServiceUrl + "/accounts/" + transaction.getSourceAccountId() + "/debit")
                .bodyValue(Map.of("amount", transaction.getAmount()))
                .retrieve()
                .bodyToMono(Object.class)
                .block();
        // Créditer destination
        webClient.post()
                .uri(accountServiceUrl + "/accounts/" + transaction.getDestinationAccountId() + "/credit")
                .bodyValue(Map.of("amount", transaction.getAmount()))
                .retrieve()
                .bodyToMono(Object.class)
                .block();
    }

    public TransactionResponse getTransactionById(UUID id) {
        return TransactionResponse.from(transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction introuvable : " + id)));
    }

    public TransactionResponse getTransactionByReference(String reference) {
        return TransactionResponse.from(transactionRepository.findByReference(reference)
                .orElseThrow(() -> new RuntimeException("Transaction introuvable : " + reference)));
    }

    public List<TransactionResponse> getTransactionsByAccount(UUID accountId) {
        List<Transaction> sent = transactionRepository.findBySourceAccountId(accountId);
        List<Transaction> received = transactionRepository.findByDestinationAccountId(accountId);
        sent.addAll(received);
        return sent.stream()
                .map(TransactionResponse::from)
                .collect(Collectors.toList());
    }

    public List<TransactionResponse> getAllTransactions() {
        return transactionRepository.findAll()
                .stream().map(TransactionResponse::from).collect(Collectors.toList());
    }
}
