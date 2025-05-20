package com.redispractice.service

import com.redispractice.domain.entity.ReaderBoardPlayer
import com.redispractice.repository.ReaderBoardRepository
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import kotlin.test.assertEquals

@ExtendWith(MockitoExtension::class)
class ReaderBoardServiceTest {
    @InjectMocks
    private lateinit var readerBoardService: ReaderBoardService
    @Mock(lenient = true)
    private lateinit var readerBoardRepository: ReaderBoardRepository

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

    @DisplayName("리더보드 사용자 점수 등록 중 일부가 누락하면 예외 발생")
    @Test
    fun registerScoreContainsNullThenException() {
        // given

        // when

        // then
    }
}