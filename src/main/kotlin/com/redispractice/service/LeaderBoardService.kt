package com.redispractice.service

import com.redispractice.common.SuccessMessages
import com.redispractice.domain.entity.LeaderBoardPlayer
import com.redispractice.exception.ApiException
import com.redispractice.exception.ExceptionMessages
import com.redispractice.repository.LeaderBoardRepository
import org.springframework.http.HttpStatus

class LeaderBoardService(private val leaderBoardRepository: LeaderBoardRepository<LeaderBoardPlayer>) {
    private val key = "Leader-board:test"

    fun registerAll(userAndScoreList: List<LeaderBoardPlayer>): String {
        require(userAndScoreList.isNotEmpty()) { ExceptionMessages.EMPTY_INPUT }
        userAndScoreList.forEach {
            require(it.name.isNotBlank()) { ExceptionMessages.isBlankName(it.id) }
        }

        val result = leaderBoardRepository.addAll(key, userAndScoreList, { it.name }, { it.score.toDouble() })

        if (result != userAndScoreList.size.toLong()) {
            throw ApiException(HttpStatus.BAD_REQUEST, ExceptionMessages.someRegisterFailed("점수"))
        }

        return SuccessMessages.registerSuccess("점수");
    }

    fun updateScore(updatedPlayer: LeaderBoardPlayer): String {
        require(updatedPlayer.name.isNotBlank()) { ExceptionMessages.isBlankName(updatedPlayer.id) }

        leaderBoardRepository.score(key, updatedPlayer.name) ?: throw ApiException(
            HttpStatus.BAD_REQUEST,
            ExceptionMessages.updateEntityNotExist("점수")
        )
        // 실제로는 수정 대상이 없어도 새로 데이터가 추가되지만 실습을 위한 조건 추가
        leaderBoardRepository.increment(key, updatedPlayer.name, updatedPlayer.score.toDouble())

        return SuccessMessages.updateSuccess("점수")
    }

    fun getTopScores(key: String, rankCount: Long): List<Map<String, Double>> {
        val topScores = leaderBoardRepository.top(key, rankCount)
        return topScores.map { mapOf(it.first.toString() to it.second) }
    }

    fun getBottomScores(key: String, rankCount: Long): List<Map<String, Double>> {
        val bottomScores = leaderBoardRepository.bottom(key, rankCount)
        return bottomScores.map { mapOf(it.first.toString() to it.second) }
    }

    fun getSumScores(key: String, otherKey: String): List<Map<String, Double?>> {
        require(key.isNotBlank() && otherKey.isNotBlank()) { ExceptionMessages.NULL_INPUT }

        return leaderBoardRepository.unionWithScores(key, otherKey)
            .map { mapOf(it.value.toString() to it.score) }
    }
}