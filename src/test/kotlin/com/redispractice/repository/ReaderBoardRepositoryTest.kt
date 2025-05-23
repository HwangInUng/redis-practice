package com.redispractice.repository

import com.fasterxml.jackson.databind.ObjectMapper
import com.redispractice.domain.entity.ReaderBoardPlayer
import com.redispractice.fixtures.ReaderBoardPlayerFixtures
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
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

    @AfterEach
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
        val result = readerBoardRepository.addAll(key, userScoreList, { it.name }, { it.score.toDouble() })

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

    @Test
    @DisplayName("사용자 삭제")
    fun deleteUser() {
        // given
        val readerBoardPlayerList = ReaderBoardPlayerFixtures.createList(listOf(100, 90, 130))
        readerBoardRepository.addAll(key, readerBoardPlayerList, { it.name }, { it.score.toDouble() })

        // when
        val result = readerBoardRepository.delete(key, readerBoardPlayerList[1].name)

        // then
        val expected = 1L
        assertEquals(expected, result)
    }

    @Nested
    @DisplayName("리더보드 범위 점수 조회")
    inner class GetReaderBoardRangeScore {
        private val scoreList = listOf(100, 90, 130, 110, 120, 140)

        @BeforeEach
        fun setUp() {
            val userScoreList = ReaderBoardPlayerFixtures.createList(scoreList)
            readerBoardRepository.addAll(key, userScoreList, { it.name }, { it.score.toDouble() })
        }

        @Test
        @DisplayName("상위 점수 조회")
        fun getTop5Scores() {
            // given
            val count = 5L

            // when
            val result = readerBoardRepository.top(key, count)
            assertEquals(count, result.size.toLong())

            val topScores = result.map { it.second }

            // then
            val expectedMax = scoreList.maxBy { it }
            val expectedMin = scoreList.minBy { it }

            assertEquals(expectedMax.toDouble(), topScores.first())
            assertFalse(topScores.contains(expectedMin.toDouble()))
        }

        @Test
        @DisplayName("하위 점수 조회")
        fun getBottom5Scores() {
            // given
            val count = 3L

            // when
            val result = readerBoardRepository.bottom(key, count)

            // then
            assertEquals(count, result.size.toLong())

            val bottomScores = result.map { it.second }

            val expectedMax = scoreList.maxBy { it }
            val expectedMin = scoreList.minBy { it }

            assertEquals(expectedMin.toDouble(), bottomScores.first().toDouble())
            assertFalse(bottomScores.contains(expectedMax.toDouble()))
        }
    }

    @Nested
    @DisplayName("리더보드 점수 조회")
    inner class GetReaderBoardScore {
        @Test
        @DisplayName("점수 조회 대상이 존재하지 않는 경우 null 반환")
        fun notExistsMemberThenReturnNull() {
            // given
            val userScore = ReaderBoardPlayer(1L, "player1", 100)
            val otherUser = "player2"

            // when
            readerBoardRepository.add(key, userScore.name, userScore.score.toDouble())
            val result = readerBoardRepository.score(key, otherUser)

            // then
            assertNull(result)
        }

        @Test
        @DisplayName("점수 조회 대상이 존재하는 경우 해당 점수 반환")
        fun existsMemberThenReturnScore() {
            // given
            val userScore = ReaderBoardPlayer(1L, "player1", 100)

            // when
            readerBoardRepository.add(key, userScore.name, userScore.score.toDouble())
            val result = readerBoardRepository.score(key, userScore.name)

            // then
            assertEquals(userScore.score.toDouble(), result)
        }
    }
}
