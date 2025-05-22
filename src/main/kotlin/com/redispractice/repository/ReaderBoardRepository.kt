package com.redispractice.repository

import com.fasterxml.jackson.databind.ObjectMapper
import com.redispractice.domain.entity.ReaderBoardPlayer
import org.springframework.data.redis.core.DefaultTypedTuple
import org.springframework.data.redis.core.RedisTemplate
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ReaderBoardRepository<T : Any>(
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
        scoreSelector: (T) -> Double
    ): Long {
        val tuples = values.map {
            val json = objectMapper.writeValueAsString(it)
            DefaultTypedTuple(json, scoreSelector(it))
        }.toSet()

        return redisTemplate.opsForZSet().add(key, tuples) ?: 0
    }

    fun delete(key: String, value: T): Long {
        return ops.remove(key, serialize(value)) ?: 0
    }

    fun increment(key: String, value: String, delta: Double): Double {
        return ops.incrementScore(key, value, delta) ?: 0.0
    }

    fun top(key: String, count: Long): List<T> {
        return ops.reverseRangeWithScores(key, 0, count - 1)
            ?.map { deserialize(it.value.toString()) } ?: emptyList()
    }

    fun bottom(key: String, count: Long): List<T> {
        return ops.rangeWithScores(key, 0, count - 1)
            ?.map { deserialize(it.value.toString()) } ?: emptyList()
    }

    fun size(key: String): Long {
        return ops.size(key) ?: 0
    }
}