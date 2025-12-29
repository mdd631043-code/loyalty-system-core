package com.loyalty.core.redis;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import java.util.Collections;

/**
 * Защита от Double Spending.
 * Используем Lua-скрипт для атомарной проверки и установки ключа в Redis.
 */
@Service
public class IdempotencyService {

    private final StringRedisTemplate redisTemplate;
    
    // Скрипт проверяет: если ключа нет (SETNX), ставит его и дает TTL (время жизни)
    private static final String LUA_SCRIPT = 
        "if redis.call('SETNX', KEYS[1], 'processing') == 1 then " +
        "   redis.call('PEXPIRE', KEYS[1], ARGV[1]); " +
        "   return 1; " +
        "else " +
        "   return 0; " +
        "end";

    public IdempotencyService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public boolean shouldProcess(String requestId) {
        Long result = redisTemplate.execute(
            new DefaultRedisScript<>(LUA_SCRIPT, Long.class),
            Collections.singletonList("lock:request:" + requestId),
            "60000" // Блокируем повторы на 1 минуту
        );
        return result != null && result == 1;
    }
}
