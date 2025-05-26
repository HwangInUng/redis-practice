package com.redispractice.repository

import com.fasterxml.jackson.databind.ObjectMapper
import com.redispractice.domain.entity.LeaderBoardPlayer
import com.redispractice.fixtures.LeaderBoardPlayerFixtures
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
class LeaderBoardRepositoryTest {
    @Autowired
    private lateinit var redisTemplate: RedisTemplate<String, String>
    private lateinit var leaderBoardRepository: LeaderBoardRepository<LeaderBoardPlayer>
    private val key = "reader-board:test"

    @BeforeAll
    fun setUp() {
        leaderBoardRepository =
            LeaderBoardRepository(redisTemplate, ObjectMapper(), LeaderBoardPlayer::class.java)
    }

    @AfterEach
    fun tearDown() {
        println("Removed test keys")
        redisTemplate.delete(key)
    }

    private fun addOneToKey(key: String, userAndScore: LeaderBoardPlayer): Boolean {
        return leaderBoardRepository.add(key, userAndScore.name, userAndScore.score.toDouble())
    }

    private fun addAllToKey(
        key: String,
        userAndScoreList: List<LeaderBoardPlayer>,
    ): Long {
        return leaderBoardRepository.addAll(key, userAndScoreList, { it.name }, { it.score.toDouble() })
    }

    @Test
    @DisplayName("단일 사용자 점수 저장")
    fun addSingleData() {
        // given
        val userAndScore = LeaderBoardPlayerFixtures.create()

        // when
        val result = addOneToKey(key, userAndScore)

        // then
        assertTrue(result)
    }

    @Test
    @DisplayName("복수 사용자 점수 저장")
    fun addMultiData() {
        // given
        val userScoreList = LeaderBoardPlayerFixtures.createList(listOf(100, 90, 130))

        // when
        val result = addAllToKey(key, userScoreList)

        // then
        val expected = userScoreList.size.toLong()
        assertEquals(expected, result)
    }

    @Test
    @DisplayName("사용자 점수 수정")
    fun updateScore() {
        // given
        val LeaderBoardPlayer = LeaderBoardPlayerFixtures.createWithScore(100)
        val updateScore = 10.0
        addOneToKey(key, LeaderBoardPlayer)

        // when
        val result = leaderBoardRepository.increment(key, LeaderBoardPlayer.name, updateScore)

        // then
        val expected = LeaderBoardPlayer.score + updateScore
        assertEquals(expected, result)
    }

    @Test
    @DisplayName("사용자 삭제")
    fun deleteUser() {
        // given
        val LeaderBoardPlayerList = LeaderBoardPlayerFixtures.createList(listOf(100, 90, 130))
        addAllToKey(key, LeaderBoardPlayerList)

        // when
        val result = leaderBoardRepository.delete(key, LeaderBoardPlayerList[1].name)

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
            val userScoreList = LeaderBoardPlayerFixtures.createList(scoreList)
            addAllToKey(key, userScoreList)
        }

        @Test
        @DisplayName("상위 점수 조회")
        fun getTop5Scores() {
            // given
            val count = 5L

            // when
            val result = leaderBoardRepository.top(key, count)
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
            val result = leaderBoardRepository.bottom(key, count)

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
            val userScore = LeaderBoardPlayer(1L, "player1", 100)
            val otherUser = "player2"
            addOneToKey(key, userScore)

            // when
            val result = leaderBoardRepository.score(key, otherUser)

            // then
            assertNull(result)
        }

        @Test
        @DisplayName("점수 조회 대상이 존재하는 경우 해당 점수 반환")
        fun existsMemberThenReturnScore() {
            // given
            val userScore = LeaderBoardPlayer(1L, "player1", 100)
            addOneToKey(key, userScore)

            // when
            val result = leaderBoardRepository.score(key, userScore.name)

            // then
            assertEquals(userScore.score.toDouble(), result)
        }
    }

    @Nested
    @DisplayName("리더보드 합집합")
    inner class BasicUnionReaderBoard {
        private val otherKey = "reader:test2"

        @AfterEach
        fun cleanUp() {
            redisTemplate.delete(otherKey)
        }

        @Test
        @DisplayName("집합 내 동일한 member가 존재하면 합산된 스코어로 반환")
        fun unionWithScoresSameValue() {
            // given
            val userScoreList = LeaderBoardPlayerFixtures.createList(listOf(100, 90, 130))
            val otherUserScoreList = LeaderBoardPlayerFixtures.createList(listOf(100, 90, 150))
            addAllToKey(key, userScoreList)
            addAllToKey(otherKey, otherUserScoreList)

            // when
            val actual = leaderBoardRepository.unionWithScores(key, otherKey)

            // then
            // 동일한 이름의 유저가 있을 경우, 스코어는 합산되어야 함
            val expectedList = (userScoreList + otherUserScoreList)
                .groupBy { it.name }
                .map { (_, players) ->
                    players.first().copy(score = players.sumOf { it.score })
                }
            val expectedNameSet = expectedList.map { it.name }.toSet()
            val expectedScoreSet = expectedList.map { it.score.toDouble() }.toSet()

            val actualValueSet = actual.map { it.value }.toSet()
            val actualScoreSet = actual.map { it.score }.toSet()

            assertEquals(expectedNameSet, actualValueSet)
            assertEquals(expectedScoreSet, actualScoreSet)
        }

        @Test
        @DisplayName("집합 대상 키 중 존재하지 않는 키가 있는 경우 있는 존재하는 값의 집합만 반환")
        fun unionWithNonExistentKey() {
            // given
            val userScoreList = LeaderBoardPlayerFixtures.createList(listOf(100, 90, 130))
            addAllToKey(key, userScoreList)

            // when
            val actual = leaderBoardRepository.unionWithScores(key, otherKey)

            // then
            val expectedNameSet = userScoreList.map { it.name }.toSet()
            val expectedScoreSet = userScoreList.map { it.score.toDouble() }.toSet()

            val actualValueSet = actual.map { it.value }.toSet()
            val actualScoreSet = actual.map { it.score }.toSet()

            assertEquals(expectedNameSet, actualValueSet)
            assertEquals(expectedScoreSet, actualScoreSet)
        }

        @Test
        @DisplayName("두 개의 합집합 결과를 반환")
        fun unionOfTwoSets() {
            // given
            val userScoreList = LeaderBoardPlayerFixtures.createList(listOf(100, 90, 130, 110, 120, 140))
            val aList = userScoreList.subList(0, userScoreList.size / 2)
            val bList = userScoreList.subList(userScoreList.size / 2, userScoreList.size)
            addAllToKey(key, aList)
            addAllToKey(otherKey, bList)

            // when
            val actual = leaderBoardRepository.unionWithScores(key, otherKey)

            // then
            val expectedNameSet = userScoreList.map { it.name }.toSet()
            val expectedScoreSet = userScoreList.map { it.score.toDouble() }.toSet()

            val actualNameSet = actual.map { it.value }.toSet()
            val actualScoreSet = actual.map { it.score }.toSet()

            assertEquals(expectedNameSet, actualNameSet)
            assertEquals(expectedScoreSet, actualScoreSet)
        }
    }
}
