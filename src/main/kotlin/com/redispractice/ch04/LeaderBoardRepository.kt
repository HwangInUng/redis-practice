package com.redispractice.ch04

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.data.redis.core.DefaultTypedTuple
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.ZSetOperations.TypedTuple
import org.springframework.http.HttpStatus

class LeaderBoardRepository<T : Any>(
    private val redisTemplate: RedisTemplate<String, String>,
    private val objectMapper: ObjectMapper,
    private val clazz: Class<T>,
) {
    private val ops = redisTemplate.opsForZSet()

    private fun serialize(value: T): String =
        objectMapper.writeValueAsString(value)

    private fun deserialize(json: String): T =
        objectMapper.readValue(json, clazz)

    fun add(key: String, value: String, score: Double): Boolean {
        return ops.add(key, value, score) ?: false
    }

    // ZSet의 addAll에서는 DefaultTypedTuple을 인자로 받기 때문에
    // List<T>를 DefaultTypedTuple로 변환하여 addAll을 호출
    // 이때, scoreSelector를 통해 각 요소의 점수를 계산하여 DefaultTypedTuple을 생성
    fun <T : Any> addAll(
        key: String,
        values: List<T>,
        nameSelector: (T) -> String,
        scoreSelector: (T) -> Double,
    ): Long {
        val tuples = values.map {
            DefaultTypedTuple(nameSelector(it), scoreSelector(it))
        }.toSet()

        return redisTemplate.opsForZSet().add(key, tuples) ?: 0
    }

    fun delete(key: String, value: String): Long {
        return ops.remove(key, value) ?: 0
    }

    fun increment(key: String, value: String, delta: Double): Double? {
        return ops.incrementScore(key, value, delta) ?: throw ApiException(
            HttpStatus.INTERNAL_SERVER_ERROR,
            ExceptionMessages.INTERNAL_SERVER_ERROR
        )
    }

    fun top(key: String, count: Long): List<Pair<String?, Double>> {
        return ops.reverseRangeWithScores(key, 0, count - 1)
            ?.map { it.value to (it.score ?: 0.0) } ?: emptyList()
    }

    fun bottom(key: String, count: Long): List<Pair<String?, Double>> {
        return ops.rangeWithScores(key, 0, count - 1)
            ?.map { it.value to (it.score ?: 0.0) } ?: emptyList()
    }

    fun size(key: String): Long {
        return ops.size(key) ?: 0
    }

    fun score(key: String, value: String): Double? {
        return ops.score(key, value)
    }

    // 각 스코어가 합산되지 않고, 원래 집합의 스코어로 반환
    fun unionWithScores(key: String, otherKey: String): List<TypedTuple<String>> {
        return ops.unionWithScores(key, otherKey)
            ?.map { DefaultTypedTuple(it.value, it.score ?: 0.0) }
            ?: emptyList()
    }

    // 스코어가 합산되어 새로운 집합에 저장됨
    fun unionAndStore(
        key: String,
        otherKey: String,
        destinationKey: String
    ): Long {
        return ops.unionAndStore(key, otherKey, destinationKey)
            ?: throw ApiException(
                HttpStatus.INTERNAL_SERVER_ERROR,
                ExceptionMessages.INTERNAL_SERVER_ERROR
            )
    }
}