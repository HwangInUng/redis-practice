package com.redispractice.repository

import com.redispractice.domain.entity.ReaderBoardPlayer

class ReaderBoardRepository {

    fun saveAll(userAndScoreList: List<ReaderBoardPlayer>): Int {
        return userAndScoreList.size
    }
}