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
        require(updatedPlayer.name.isNotBlank()) { ExceptionMessages.isBlankName(updatedPlayer.id) }

        readerBoardRepository.score(key, updatedPlayer.name) ?: throw ApiException(
            HttpStatus.BAD_REQUEST,
            ExceptionMessages.updateEntityNotExist("점수")
        )
        // 실제로는 수정 대상이 없어도 새로 데이터가 추가되지만 실습을 위한 조건 추가
        readerBoardRepository.increment(key, updatedPlayer.name, updatedPlayer.score.toDouble())

        return SuccessMessages.updateSuccess("점수")
    }

    fun getTopScores(key: String, rankCount: Long): List<Map<String, Double>> {
        val topScores = readerBoardRepository.top(key, rankCount)
        return topScores.map { mapOf(it.first.toString() to it.second) }
    }

    fun getBottomScores(key: String, rankCount: Long): List<Map<String, Double>> {
        val bottomScores = readerBoardRepository.bottom(key, rankCount)
        return bottomScores.map { mapOf(it.first.toString() to it.second) }
    }
}