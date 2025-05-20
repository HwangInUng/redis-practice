package com.redispractice.service

import com.redispractice.domain.entity.ReaderBoardPlayer
import com.redispractice.repository.ReaderBoardRepository

class ReaderBoardService(private val readerBoardRepository: ReaderBoardRepository) {
    fun registerAll(userAndScoreList: List<ReaderBoardPlayer>): String {
        val savedCount = readerBoardRepository.saveAll(userAndScoreList)
        return "${savedCount}명의 점수 등록 성공";
    }
}