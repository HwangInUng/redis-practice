package com.redispractice.service

import com.redispractice.common.SuccessMessages
import com.redispractice.domain.entity.ReaderBoardPlayer
import com.redispractice.exception.ApiException
import com.redispractice.exception.ExceptionMessages
import com.redispractice.repository.ReaderBoardRepository
import org.springframework.http.HttpStatus

class ReaderBoardService(private val readerBoardRepository: ReaderBoardRepository<ReaderBoardPlayer>) {
    private val key = "reader-board:test"

    fun registerAll(userAndScoreList: List<ReaderBoardPlayer>): String {
        require(userAndScoreList.isNotEmpty()) { ExceptionMessages.EMPTY_INPUT }
        userAndScoreList.forEach {
            require(it.name.isNotBlank()) { ExceptionMessages.isBlankName(it.id) }
        }

        val result = readerBoardRepository.addAll(key, userAndScoreList, { it.name }, { it.score.toDouble() })

        if (result != userAndScoreList.size.toLong()) {
            throw ApiException(HttpStatus.BAD_REQUEST, ExceptionMessages.someRegisterFailed("점수"))
        }

        return SuccessMessages.registerSuccess("점수");
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