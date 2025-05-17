package com.redispractice.repository

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.data.redis.core.RedisTemplate
import java.time.Duration

class RedisRepositoryImpl<T : Any>(
    private val redisTemplate: RedisTemplate<String, Any>,
    private val objectMapper: ObjectMapper,
    private val clazz: Class<T> // 역직렬화 시 사용
) : RedisRepository<T> {
    override fun save(key: String, value: T, ttl: Duration?) {
        val ops = redisTemplate.opsForValue()
        if (ttl != null) {
            ops.set(key, value, ttl)
        } else {
            ops.set(key, value)
        }
    }

    override fun find(key: String): T? {
        val raw = redisTemplate.opsForValue().get(key) ?: return null
        return objectMapper.convertValue(raw, clazz)
    }

    override fun delete(key: String) {
        redisTemplate.delete(key)
    }
}