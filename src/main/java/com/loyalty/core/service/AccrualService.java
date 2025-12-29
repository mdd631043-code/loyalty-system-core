package com.loyalty.core.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Сервис начисления баллов. 
 * Используем Transactional Outbox вместо прямой отправки в Kafka,
 * чтобы избежать рассинхрона, если база сохранится, а брокер упадет.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AccrualService {

    private final WalletRepository walletRepository;
    private final OutboxRepository outboxRepository;

    @Transactional
    public void processAccrual(AccrualRequest request) {
        log.info("Processing accrual for user: {}", request.userId());

        // 1. Атомарно обновляем баланс в Postgres
        walletRepository.incrementBalance(request.userId(), request.amount());

        // 2. Сохраняет событие в ту же транзакцию БД. 
        // Отсюда его заберет планировщик или Debezium (CDC)
        String eventPayload = String.format("{\"userId\": %d, \"amount\": %d}", 
            request.userId(), request.amount());
        
        outboxRepository.save(new OutboxEvent("BONUS_ACCRUED", request.externalId(), eventPayload));
        
        log.info("Accrual stored in outbox for externalId: {}", request.externalId());
    }
}
