package com.redispractice.service

import com.redispractice.domain.entity.ReaderBoardPlayer
import com.redispractice.repository.ReaderBoardRepository
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.Mockito

class ReaderBoardServiceTest(
    private val readerBoardService: ReaderBoardService,
    private val readerBoardRepository: ReaderBoardRepository
) {


    @DisplayName("리더보드 사용자의 점수를 등록")
    @Test
    fun registerUserAndScore() {
        // given
        val userAndScoreList = listOf(
            ReaderBoardPlayer(1L, "player1", 100),
            ReaderBoardPlayer(2L, "player2", 90),
            ReaderBoardPlayer(3L, "player3", 130),
        )

        // when
         Mockito.`when`(readerBoardRepository.saveAll(userAndScoreList)).thenReturn(userAndScoreList)

        // then
         Mockito.verify(readerBoardRepository).saveAll(userAndScoreList)

    }

    @DisplayName("리더보드 사용자 점수 등록 중 일부가 누락하면 예외 발생")
    @Test
    fun registerScoreContainsNullThenException() {
        // given

        // when

        // then
    }
}