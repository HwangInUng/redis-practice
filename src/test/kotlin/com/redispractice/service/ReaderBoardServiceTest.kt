package com.redispractice.service

import com.redispractice.common.SuccessMessages
import com.redispractice.domain.entity.ReaderBoardPlayer
import com.redispractice.exception.ApiException
import com.redispractice.exception.ExceptionMessages
import com.redispractice.exception.NoWriteTestMethodException
import com.redispractice.fixtures.ReaderBoardPlayerFixtures
import com.redispractice.repository.ReaderBoardRepository
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
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
import org.springframework.http.HttpStatus
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

@ExtendWith(MockitoExtension::class)
// PER_CLASS는 모든 테스트에서 공유되는 인스턴스를 사용하기 때문에 각 테스트 케이스에서 의도하지 않은 상태를 공유할 수 있음
// 메서드마다 상태가 초기화되어야 한다면 PER_METHOD를 사용하여 각 테스트마다 새로운 인스턴스를 생성
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class ReaderBoardServiceTest {
    @InjectMocks
    private lateinit var sut: ReaderBoardService

    @Mock(lenient = true)
    private lateinit var readerBoardRepository: ReaderBoardRepository<ReaderBoardPlayer>
    private val key = "reader-board:test"

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
            val emptyData = emptyList<ReaderBoardPlayer>();

            // when
            val actual = assertFailsWith<IllegalArgumentException> { sut.registerAll(emptyData) };

            // then
            val expected = ExceptionMessages.EMPTY_INPUT
            assertTrue(actual.message!!.contains(expected))
        }

        @Test
        @DisplayName("특정 요소의 이름이 blank인 경우 IllegalArgumentException 발생")
        fun someDataNameIsBlankThrowsIllegalArgumentException() {
            // given
            val userScoreListWithNameBlank = listOf(
                ReaderBoardPlayer(1L, "player1", 100),
                ReaderBoardPlayer(2L, "", 130),
                ReaderBoardPlayer(3L, "player3", 90),
            )
            val blankId = 2L

            // when
            val actual = assertFailsWith<IllegalArgumentException> { sut.registerAll(userScoreListWithNameBlank) }

            // then
            val expected = ExceptionMessages.isBlankName(blankId)
            assertTrue(actual.message!!.contains(expected))
        }

        @Test
        @DisplayName("반환 값이 0이면 ApiException을 발생")
        fun returnValueZeroThrowsApiExceptionWithBAD_REQEUST() {
            // given
            val userScoreList = ReaderBoardPlayerFixtures.createList(listOf(100, 120, 90, 150))

            // when
            Mockito.`when`(readerBoardRepository.addAll(eq(key), eq(userScoreList), any(), any()))
                .thenReturn(0L)
            val ex = assertFailsWith<ApiException> { sut.registerAll(userScoreList) }

            // then
            val expected = ExceptionMessages.someRegisterFailed("점수")
            assertEquals(HttpStatus.BAD_REQUEST, ex.status)
            assertTrue(ex.message.contains(expected))
        }

        @Test
        @DisplayName("사용자 점수 등록")
        fun registerUserScores() {
            // given
            val userScoreList = ReaderBoardPlayerFixtures.createList(listOf(100, 120, 90, 150))

            // when
            Mockito.`when`(readerBoardRepository.addAll(eq(key), eq(userScoreList), any(), any()))
                .thenReturn(userScoreList.size.toLong())
            val result = sut.registerAll(userScoreList);

            // then
            val expected = SuccessMessages.registerSuccess("점수")
            assertTrue(result.contains(expected))
        }
    }

    @Nested
    @DisplayName("리더보드 점수 수정")
    inner class UpdateScore {

        @Test
        @DisplayName("특정 사용자의 점수를 수정")
        fun test1() {
            throw NoWriteTestMethodException()
        }

        @Test
        @DisplayName("내부 오류로 인해 null을 반환하면 예외 발생")
        fun test2() {
            throw NoWriteTestMethodException()
        }
    }

    @Nested
    @DisplayName("리더보드 점수 조회")
    inner class getScores {

        @Test
        @DisplayName("조회 대상이 비어있는 경우")
        fun test1() {
            throw NoWriteTestMethodException()
        }

        @Test
        @DisplayName("상위 5명을 조회")
        fun test2() {
            throw NoWriteTestMethodException()
        }

        @Test
        @DisplayName("하위 5명을 조회하며")
        fun test3() {
            throw NoWriteTestMethodException()
        }
    }
}
