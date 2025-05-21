package com.redispractice.repository

import com.redispractice.domain.entity.ReaderBoardPlayer
import org.springframework.data.redis.core.RedisTemplate
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ReaderBoardRepository(
    private val redisTemplate: RedisTemplate<String, String>
) {

    fun saveAll(userAndScoreList: List<ReaderBoardPlayer>): Int {
        val currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
        userAndScoreList.forEach { player ->
            redisTemplate.opsForZSet().add("reader-board:$currentDate", player.name, player.score.toDouble())
        }

        return redisTemplate.opsForZSet().size("reader-board:$currentDate")?.toInt() ?: 0
    }

    fun updateScore(updatedPlayer: ReaderBoardPlayer): Boolean {
        val currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))

        return redisTemplate.opsForZSet()
            .add("reader-board:$currentDate", updatedPlayer.name, updatedPlayer.score.toDouble()) ?: false
    }

    fun getTop5Scores(key: String): List<ReaderBoardPlayer> {
        val topPlayers = redisTemplate.opsForZSet().reverseRangeWithScores(key, 0, 4)
        return topPlayers?.map { ReaderBoardPlayer(0, it.value.toString(), it.score?.toInt() ?: 0) } ?: emptyList()
    }

    fun getBottom5Scores(key: String): List<ReaderBoardPlayer> {
        val bottomPlayers = redisTemplate.opsForZSet().rangeWithScores(key, 0, 4)
        return bottomPlayers?.map { ReaderBoardPlayer(0, it.value.toString(), it.score?.toInt() ?: 0) } ?: emptyList()
    }
}