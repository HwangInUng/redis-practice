package com.redispractice.service

import com.redispractice.domain.entity.User
import com.redispractice.repository.RedisRepository
import com.redispractice.repository.RedisRepositoryImpl
import lombok.RequiredArgsConstructor
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service

@Service
@RequiredArgsConstructor
class UserService(private val userRedisRepository: RedisRepository<User>) {
    fun signUp(user: User): String {
        // redis에 회원가입 대상 유저 정보 저장
        userRedisRepository.save(user.id, user)

        // redis에 저장된 유저 정보 가져오기
        val savedUser = userRedisRepository.find(user.id)
        println("redis에 저장된 유저 정보: ${savedUser?.id}")

        return "${savedUser?.id}의 회원가입 성공"
    }
}