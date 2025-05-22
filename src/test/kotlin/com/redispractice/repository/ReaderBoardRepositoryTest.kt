package com.redispractice.repository

import com.fasterxml.jackson.databind.ObjectMapper
import com.redispractice.domain.entity.ReaderBoardPlayer
import com.redispractice.fixtures.ReaderBoardPlayerFixtures
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest
import org.springframework.data.redis.core.RedisTemplate

@DataRedisTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ReaderBoardRepositoryTest {
    @Autowired
    private lateinit var redisTemplate: RedisTemplate<String, String>
    private lateinit var readerBoardRepository: ReaderBoardRepository<ReaderBoardPlayer>
    private val key = "reader-board:test"

    @BeforeAll
    fun setUp() {
        readerBoardRepository =
            ReaderBoardRepository(redisTemplate, ObjectMapper(), ReaderBoardPlayer::class.java)
    }

    @AfterAll
    fun tearDown() {
        println("Removed test keys")
        redisTemplate.delete(key)
    }

    @Test
    @DisplayName("단일 사용자 점수 저장")
    fun addSingleData() {
        // given
        val userAndScore = ReaderBoardPlayerFixtures.create()

        // when
        val result = readerBoardRepository.add(key, userAndScore.name, userAndScore.score.toDouble())

        // then
        assertTrue(result)
    }

    @Test
    @DisplayName("복수 사용자 점수 저장")
    fun addMultiData() {
        // given
        val userScoreList = ReaderBoardPlayerFixtures.createList(listOf(100, 90, 130))

        // when
        val result = readerBoardRepository.addAll(key, userScoreList) { it.score.toDouble() }

        // then
        val expected = userScoreList.size.toLong()
        assertEquals(expected, result)
    }

    @Test
    @DisplayName("사용자 점수 수정")
    fun updateScore() {
        // given
        val readerBoardPlayer = ReaderBoardPlayerFixtures.createWithScore(100)
        val updateScore = 10.0
        readerBoardRepository.add(key, readerBoardPlayer.name, readerBoardPlayer.score.toDouble())

        // when
        val result = readerBoardRepository.increment(key, readerBoardPlayer.name, updateScore)

        // then
        val expected = readerBoardPlayer.score + updateScore
        assertEquals(expected, result)
    }
}