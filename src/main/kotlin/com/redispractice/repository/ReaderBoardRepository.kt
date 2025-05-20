package com.redispractice.repository

import com.redispractice.domain.entity.ReaderBoardPlayer
import org.springframework.data.redis.core.RedisTemplate
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ReaderBoardRepository(
    private val redisTemplate: RedisTemplate<String ,String>
) {

    fun saveAll(userAndScoreList: List<ReaderBoardPlayer>): Int {
        val currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
        userAndScoreList.forEach { player ->
            redisTemplate.opsForZSet().add("reader-board:$currentDate", player.name, player.score.toDouble())
        }

        return redisTemplate.opsForZSet().size("reader-board:$currentDate")?.toInt() ?: 0
    }
}