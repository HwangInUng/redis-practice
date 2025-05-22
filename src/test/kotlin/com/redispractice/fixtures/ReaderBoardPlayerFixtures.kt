package com.redispractice.fixtures

import com.redispractice.domain.entity.ReaderBoardPlayer

object ReaderBoardPlayerFixtures {

    fun create(): ReaderBoardPlayer {
        return ReaderBoardPlayer(
            id = 1L,
            name = "player1",
            score = 100
        )
    }

    fun createWithScore(score: Int): ReaderBoardPlayer {
        return ReaderBoardPlayer(
            id = 1L,
            name = "player1",
            score = score
        )
    }

    fun createList(scores: List<Int>): List<ReaderBoardPlayer> {
        return scores.mapIndexed { index, score ->
            ReaderBoardPlayer(
                id = index.toLong(),
                name = "player$index",
                score = score
            )
        }
    }
}