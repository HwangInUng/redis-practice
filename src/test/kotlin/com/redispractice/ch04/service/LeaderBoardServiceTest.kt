package com.redispractice.ch04.service

import com.redispractice.common.SuccessMessages
import com.redispractice.ch04.domain.LeaderBoardPlayer
import com.redispractice.ch04.exception.ApiException
import com.redispractice.common.ExceptionMessages
import com.redispractice.ch04.fixtures.LeaderBoardPlayerFixtures
import com.redispractice.ch04.repository.LeaderBoardRepository
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.springframework.data.redis.core.DefaultTypedTuple
import org.springframework.http.HttpStatus
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

@ExtendWith(MockitoExtension::class)
// PER_CLASS는 모든 테스트에서 공유되는 인스턴스를 사용하기 때문에 각 테스트 케이스에서 의도하지 않은 상태를 공유할 수 있음
// 메서드마다 상태가 초기화되어야 한다면 PER_METHOD를 사용하여 각 테스트마다 새로운 인스턴스를 생성
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class LeaderBoardServiceTest {
    @InjectMocks
    private lateinit var sut: LeaderBoardService

    @Mock(lenient = true)
    private lateinit var leaderBoardRepository: LeaderBoardRepository<LeaderBoardPlayer>
    private val key = "Leader-board:test"

    // Kotlin에서 @Nested를 사용하려면 inner class를 반드시 명시
    // Kotlin의 중첩 클래스는 기본적으로 static이기 때문에 외부 클래스의 인스턴에서 접근 불가능
    // 이를 해결하기 위해 inner class를 사용하여 명시적으로 외부 클래스에 접근할 수 있도록 지정이 필요
    // 추가로 생명주기에 문제없도록 동작하기 위해 @TestInstance를 사용하여 테스트 인스턴스를 생성
    @Nested
    @DisplayName("리더보드 점수 등록")
    inner class RegisterScore {

        @Test
        @DisplayName("요청 데이터가 비어있으면 IllegalArgumentException을 발생")
        fun emptyListInputThrowsIllegalArgumentExceptionWithBAD_REQUEST() {
            // given
            val emptyData = emptyList<LeaderBoardPlayer>();

            // when
            val actual = assertFailsWith<IllegalArgumentException> { sut.registerAll(emptyData) };

            // then
            val expected = ExceptionMessages.EMPTY_INPUT
            assertEquals(expected, actual.message)
        }

        @Test
        @DisplayName("특정 요소의 이름이 blank인 경우 IllegalArgumentException 발생")
        fun someDataNameIsBlankThrowsIllegalArgumentException() {
            // given
            val userScoreListWithNameBlank = listOf(
                LeaderBoardPlayer(1L, "player1", 100),
                LeaderBoardPlayer(2L, "", 130),
                LeaderBoardPlayer(3L, "player3", 90),
            )
            val blankId = 2L

            // when
            val actual = assertFailsWith<IllegalArgumentException> { sut.registerAll(userScoreListWithNameBlank) }

            // then
            val expected = ExceptionMessages.isBlankName(blankId)
            assertEquals(expected, actual.message)
        }

        @Test
        @DisplayName("반환 값이 0이면 ApiException을 발생")
        fun returnValueZeroThrowsApiExceptionWithBAD_REQEUST() {
            // given
            val userScoreList = LeaderBoardPlayerFixtures.createList(listOf(100, 120, 90, 150))

            // when
            Mockito.`when`(leaderBoardRepository.addAll(eq(key), eq(userScoreList), any(), any()))
                .thenReturn(0L)
            val ex = assertFailsWith<ApiException> { sut.registerAll(userScoreList) }

            // then
            val expected = ExceptionMessages.someRegisterFailed("점수")
            assertEquals(HttpStatus.BAD_REQUEST, ex.status)
            assertEquals(expected, ex.message)
        }

        @Test
        @DisplayName("사용자 점수 등록 후 반환받은 값이 인자로 전달된 List의 크기와 같으면 성공 후 메세지 반환")
        fun registerUserScores() {
            // given
            val userScoreList = LeaderBoardPlayerFixtures.createList(listOf(100, 120, 90, 150))

            // when
            Mockito.`when`(leaderBoardRepository.addAll(eq(key), eq(userScoreList), any(), any()))
                .thenReturn(userScoreList.size.toLong())
            val result = sut.registerAll(userScoreList);

            // then
            val expected = SuccessMessages.registerSuccess("점수")
            assertEquals(expected, result)
        }
    }

    @Nested
    @DisplayName("리더보드 점수 수정")
    inner class UpdateScore {

        @Test
        @DisplayName("사용자의 이름이 blank인 경우 IllegalArgumentException 발생")
        fun userNameIsBlankThrowsIllegalArgumentException() {
            // given
            val userScore = LeaderBoardPlayer(1L, "", 100)

            // when
            val actual = assertFailsWith<IllegalArgumentException> { sut.updateScore(userScore) }

            // then
            val expected = ExceptionMessages.isBlankName(userScore.id)
            assertEquals(expected, actual.message)
        }

        @Test
        @DisplayName("수정 대상이 존재하지 않아도 null을 반환하면 ApiException 예외 발생")
        fun notExistsUpdateTargetThrowsApiException() {
            // given
            val userScore = LeaderBoardPlayer(1L, "player1", 100)

            // when
            Mockito.`when`(leaderBoardRepository.score(eq(key), eq(userScore.name)))
                .thenReturn(null)
            val ex = assertFailsWith<ApiException> { sut.updateScore(userScore) }

            // then
            val expected = ExceptionMessages.updateEntityNotExist("점수")
            assertEquals(HttpStatus.BAD_REQUEST, ex.status)
            assertEquals(expected, ex.message)
        }

        @Test
        @DisplayName("점수 수정 성공 시 메세지 반환")
        fun updateSuccessThenReturnMessage() {
            // given
            val savedUserScore = LeaderBoardPlayer(1L, "player1", 100)
            val updateScore = 20

            // when
            Mockito.`when`(leaderBoardRepository.increment(eq(key), eq(savedUserScore.name), any()))
                .thenReturn(updateScore.toDouble())

            // then
            val actual = sut.updateScore(savedUserScore.copy(score = updateScore))
            val expected = SuccessMessages.updateSuccess("점수")

            assertEquals(expected, actual)
        }
    }

    @Nested
    @DisplayName("리더보드 점수 조회")
    inner class getScores {

        @Test
        @DisplayName("조회 대상이 비어있는 경우 빈 리스트 반환")
        fun returnValueIsNullThenEmptyList() {
            // given
            val emptyList = emptyList<Pair<String?, Double>>()
            val rankCount = 5L

            // when
            Mockito.`when`(leaderBoardRepository.top(eq(key), any()))
                .thenReturn(emptyList)
            Mockito.`when`(leaderBoardRepository.bottom(eq(key), any()))
                .thenReturn(emptyList)

            // then
            val topActual = sut.getTopScores(key, rankCount)
            val bottomActual = sut.getBottomScores(key, rankCount)
            assertTrue(topActual.isEmpty())
            assertTrue(bottomActual.isEmpty())
        }

        @Test
        @DisplayName("전달받은 인자의 값만큼 상위 점수의 리스트 반환")
        fun getTopScoresEqualsRankCountToListSize() {
            // given
            val rankCount = 5L
            val reverseSortedTopScores = LeaderBoardPlayerFixtures.createList(listOf(100, 120, 90, 150, 130))
                .map { it.name to it.score.toDouble() }
                .sortedByDescending { it.second }

            // when
            Mockito.`when`(leaderBoardRepository.top(eq(key), any()))
                .thenReturn(reverseSortedTopScores)

            // then
            val actual = sut.getTopScores(key, rankCount)
            assertEquals(rankCount, actual.size.toLong())
            assertTrue(reverseSortedTopScores.all { it.first in actual.map { it.keys.first() } })
            assertTrue(reverseSortedTopScores.all { it.second in actual.map { it.values.first() } })
        }

        @Test
        @DisplayName("전달받은 인자의 값만큼 하위 점수의 리스트를 반환")
        fun getBottomScoresEqualsRankCountToListSize() {
            // given
            val rankCount = 3L
            val sortedBottomScores = LeaderBoardPlayerFixtures.createList(listOf(100, 120, 90))
                .map { it.name to it.score.toDouble() }
                .sortedBy { it.second }

            // when
            Mockito.`when`(leaderBoardRepository.bottom(eq(key), any()))
                .thenReturn(sortedBottomScores)

            // then
            val actual = sut.getBottomScores(key, rankCount)
            assertEquals(rankCount, actual.size.toLong())
            assertTrue(sortedBottomScores.all { it.first in actual.map { it.keys.first() } })
            assertTrue(sortedBottomScores.all { it.second in actual.map { it.values.first() } })
        }
    }

    @Nested
    @DisplayName("리더보드 스코어 합산")
    inner class UnionScore {
        @Test
        @DisplayName("합산 대상 키가 1개라도 Blank라면 IllegalArgumentException 발생")
        fun unionWithScoresNullKeyThrowsIllegalArgumentException() {
            // given
            val otherKey = ""

            // when
            val ex = assertFailsWith<IllegalArgumentException> { sut.getSumScores(key, otherKey) }

            // then
            assertEquals(ExceptionMessages.NULL_INPUT, ex.message)
        }

        @Test
        @DisplayName("반환된 데이터를 Map<String, Double?> 형태로 변환하여 반환")
        fun returnValueConvertsToMap() {
            // given
            val otherKey = "reader-board:other"
            val unionScores = listOf(
                DefaultTypedTuple("player1", 100.0),
                DefaultTypedTuple("player2", 130.0),
            )

            // when
            Mockito.`when`(leaderBoardRepository.unionWithScores(eq(key), eq(otherKey)))
                .thenReturn(unionScores)

            // then
            val actual = sut.getSumScores(key, otherKey)

            assertEquals(unionScores.size, actual.size)
            assertTrue(unionScores.all { it.value in actual.map { it.keys.first() } })
            assertTrue(unionScores.all { it.score in actual.map { it.values.first() } })
        }
    }
}
