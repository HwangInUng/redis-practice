package com.redispractice.service

import com.redispractice.domain.entity.ReaderBoardPlayer
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
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

@ExtendWith(MockitoExtension::class)
// PER_CLASS는 모든 테스트에서 공유되는 인스턴스를 사용하기 때문에 각 테스트 케이스에서 의도하지 않은 상태를 공유할 수 있음
// 메서드마다 상태가 초기화되어야 한다면 PER_METHOD를 사용하여 각 테스트마다 새로운 인스턴스를 생성
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class ReaderBoardServiceTest {
    @InjectMocks
    private lateinit var readerBoardService: ReaderBoardService

    @Mock(lenient = true)
    private lateinit var readerBoardRepository: ReaderBoardRepository

    private fun createUserAndScoreList(): List<ReaderBoardPlayer> = listOf(
        ReaderBoardPlayer(1L, "player1", 100),
        ReaderBoardPlayer(2L, "player2", 90),
        ReaderBoardPlayer(3L, "player3", 130)
    )

    fun createUserAndScore(score: Int): ReaderBoardPlayer = ReaderBoardPlayer(1L, "player1", 100)

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
            val userAndScoreList = createUserAndScoreList()

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
            val userAndScoreList = createUserAndScoreList()

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

    @Nested
    @DisplayName("리더보드 점수 수정")
    inner class UpdateScore {


        @Test
        @DisplayName("특정 사용자의 점수를 수정")
        fun updateScore() {
            // given
            val updatedPlayer = createUserAndScore(200)

            // when
            Mockito.`when`(readerBoardRepository.updateScore(updatedPlayer)).thenReturn(true)

            // then
            val result = readerBoardService.updateScore(updatedPlayer);
            val expected = "${updatedPlayer.name}의 점수 수정 성공"
            assertEquals(expected, result);
        }

        @Test
        @DisplayName("네트워크 오류 등 비정상 상황에서 예외 발생")
        fun updateScoreInternalException() {
            // given
            val updatedPlayer = createUserAndScore(300)

            // when
            Mockito.`when`(readerBoardRepository.updateScore(updatedPlayer)).thenReturn(false)

            // then
            val ex = assertFailsWith<RuntimeException> {
                readerBoardService.updateScore(updatedPlayer)
            }

            val expected = "서버 내부에서 오류가 발생했습니다."
            assertEquals(expected, ex.message)
        }
    }

    @Test
    @DisplayName("스코어가 가장 높은 상위 5명을 조회")
    fun getTop5Scores() {
        // given
        val key = "reader-board:20231001"
        val sortedUserAndScoreListDesc = createUserAndScoreList().sortedWith(compareByDescending { it.score })

        // when
        Mockito.`when`(readerBoardRepository.getTop5Scores(key)).thenReturn(sortedUserAndScoreListDesc)

        // then
        val result = readerBoardService.getTop5Scores(key)
        assertTrue(result.get(0).score >= result.get(result.size - 1).score)
    }

    @Test
    @DisplayName("스코어가 가장 낮은 하위 5명을 조회하며")
    fun getBottomTop5Scores() {
        // given
        val key = "reader-board:20231001"
        val sortedUserAndScoreListAsc = createUserAndScoreList().sortedWith(compareBy { it.score })

        // when
        Mockito.`when`(readerBoardRepository.getBottom5Scores(key)).thenReturn(sortedUserAndScoreListAsc)

        // then
        val result = readerBoardService.getBottom5Scores(key)
        assertTrue(result.get(0).score <= result.get(result.size - 1).score)
    }
}
