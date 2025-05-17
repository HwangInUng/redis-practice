package com.redispractice.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.redispractice.domain.entity.User
import com.redispractice.repository.RedisRepository
import com.redispractice.repository.RedisRepositoryImpl
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.core.RedisTemplate

@Configuration
class RedisRepositoryConfig {

    // User 타입에 대한 RedisRepository 구현체를 생성
    @Bean
    fun userRedisRepository(
        redisTemplate: RedisTemplate<String, Any>,
        objectMapper: ObjectMapper
    ): RedisRepository<User> {
        return RedisRepositoryImpl(redisTemplate, objectMapper, User::class.java)
    }

    // 이런 방식으로 다른 도메인 타입에 대한 RedisRepository 구현체를 생성할 수 있음
}