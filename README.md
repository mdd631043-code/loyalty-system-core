# Loyalty Core Service (High-load Demo)

**Внимание:** Данный репозиторий содержит примеры ключевых архитектурных решений, реализованных мной в рамках коммерческих проектов (основной код защищен NDA).

### Что здесь реализовано:
1. **Transactional Outbox:** Решение проблемы "Dual Write". Гарантируем, что событие попадет в Kafka только если транзакция в БД была успешной.
2. **Распределенная идемпотентность:** Использование Redis Lua-скриптов для предотвращения Race Conditions и двойных начислений баллов.
3. **Java 21 Virtual Threads:** Оптимизация производительности сервиса при интенсивном межсервисном взаимодействии.
4. **Reliability:** Логика обработки Poison Pill сообщений через DLQ (Dead Letter Queues).

### Технологический стек:
* Java 21 (Project Loom)
* Spring Boot 3.4
* PostgreSQL (Transactional persistence)
* Redis (Idempotency layer)
* Apache Kafka (Event-driven communication)
