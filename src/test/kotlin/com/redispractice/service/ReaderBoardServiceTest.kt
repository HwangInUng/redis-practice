package com.redispractice.service

import com.redispractice.domain.entity.ReaderBoardPlayer
import com.redispractice.repository.ReaderBoardRepository
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

@ExtendWith(MockitoExtension::class)
// PER_CLASS는 모든 테스트에서 공유되는 인스턴스를 사용하기 때문에 각 테스트 케이스에서 의도하지 않은 상태를 공유할 수 있음
// 메서드마다 상태가 초기화되어야 한다면 PER_METHOD를 사용하여 각 테스트마다 새로운 인스턴스를 생성
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class ReaderBoardServiceTest {
    @InjectMocks
    private lateinit var readerBoardService: ReaderBoardService

    @Mock(lenient = true)
    private lateinit var readerBoardRepository: ReaderBoardRepository

    // Kotlin에서 @Nested를 사용하려면 inner class를 반드시 명시
    // Kotlin의 중첩 클래스는 기본적으로 static이기 때문에 외부 클래스의 인스턴에서 접근 불가능
    // 이를 해결하기 위해 inner class를 사용하여 명시적으로 외부 클래스에 접근할 수 있도록 지정이 필요
    // 추가로 생명주기에 문제없도록 동작하기 위해 @TestInstance를 사용하여 테스트 인스턴스를 생성
    @Nested
    @DisplayName("리더보드 점수 등록")
    inner class RegisterScore {
        @Test
        @DisplayName("리더보드 사용자의 점수를 등록")
        fun registUserAndScore() {
            // given
            val userAndScoreList = listOf(
                ReaderBoardPlayer(1L, "player1", 100),
                ReaderBoardPlayer(2L, "player2", 90),
                ReaderBoardPlayer(3L, "player3", 130),
            )

            // when
            Mockito.`when`(readerBoardRepository.saveAll(userAndScoreList)).thenReturn(userAndScoreList.size)

            // then
            val result = readerBoardService.registerAll(userAndScoreList);
            val expected = "${userAndScoreList.size}명의 점수 등록 성공"
            assertEquals(expected, result);
        }

        @DisplayName("등록을 요청한 사용자 수와 저장된 수가 다른 경우 예외 발생")
        @Test
        fun registerScoreIsNotSameCountThenException() {
            // given
            val userAndScoreList = listOf(
                ReaderBoardPlayer(1L, "player1", 100),
                ReaderBoardPlayer(2L, "player2", 90),
                ReaderBoardPlayer(3L, "player3", 130),
            )

            // when
            Mockito.`when`(readerBoardRepository.saveAll(userAndScoreList)).thenReturn(userAndScoreList.size - 1)

            // then
            val ex = assertFailsWith<RuntimeException> {
                readerBoardService.registerAll(userAndScoreList)
            }

            val expected = "요청 데이터 중 일부가 누락되어 저장에 실패 하였습니다."
            assertEquals(expected, ex.message)
        }
    }
}
