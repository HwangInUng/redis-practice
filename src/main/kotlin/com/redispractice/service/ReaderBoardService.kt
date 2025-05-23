package com.redispractice.service

import com.redispractice.domain.entity.ReaderBoardPlayer
import com.redispractice.repository.ReaderBoardRepository

class ReaderBoardService(private val readerBoardRepository: ReaderBoardRepository<ReaderBoardPlayer>) {
    fun registerAll(userAndScoreList: List<ReaderBoardPlayer>): String {
        val key = "reader-board:test"
        val savedCount =
            readerBoardRepository.addAll(key, userAndScoreList, { it.name }, { it.score.toDouble() })

        if (savedCount == 0L) {
            throw RuntimeException("요청 데이터 중 일부가 누락되어 저장에 실패 하였습니다.")
        }

        return "${savedCount}명의 점수 등록 성공";
    }

    fun updateScore(updatedPlayer: ReaderBoardPlayer): String {
        val result = readerBoardRepository.increment("", updatedPlayer.name, 0.0)

        if (result != 0.0) {
            throw RuntimeException("서버 내부에서 오류가 발생했습니다.")
        }

        return "${updatedPlayer.name}의 점수 수정 성공"
    }

    fun getTop5Scores(key: String): List<String> {
        return listOf("")
    }

    fun getBottom5Scores(key: String): List<String> {
        return listOf("")
    }
}