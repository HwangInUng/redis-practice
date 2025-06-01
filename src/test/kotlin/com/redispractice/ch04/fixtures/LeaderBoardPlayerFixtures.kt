package com.redispractice.ch04.fixtures

import com.redispractice.ch04.domain.LeaderBoardPlayer

object LeaderBoardPlayerFixtures {

    fun create(): LeaderBoardPlayer {
        return LeaderBoardPlayer(
            id = 1L,
            name = "player1",
            score = 100
        )
    }

    fun createWithScore(score: Int): LeaderBoardPlayer {
        return LeaderBoardPlayer(
            id = 1L,
            name = "player1",
            score = score
        )
    }

    fun createList(scores: List<Int>): List<LeaderBoardPlayer> {
        return scores.mapIndexed { index, score ->
            LeaderBoardPlayer(
                id = index.toLong(),
                name = "player$index",
                score = score
            )
        }
    }
}