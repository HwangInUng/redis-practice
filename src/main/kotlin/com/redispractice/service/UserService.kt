package com.redispractice.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.redispractice.domain.entity.User
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service

@Service
class UserService(
    private val redisTemplate: RedisTemplate<String, User>,
    private val objectMapper: ObjectMapper,
) {
    fun signUp(user: User): String {
        // redis에 회원가입 대상 유저 정보 저장
        redisTemplate.opsForValue().set(user.id, user)

        // redis에 저장된 유저 정보 가져오기
        val savedUser = redisTemplate.opsForValue().get(user.id)
        println("redis에 저장된 유저 정보: ${savedUser?.id}")

        return "${savedUser?.id}의 회원가입 성공"
    }
}