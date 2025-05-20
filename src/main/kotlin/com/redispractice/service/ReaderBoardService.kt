package com.redispractice.service

import com.redispractice.domain.entity.ReaderBoardPlayer
import com.redispractice.repository.ReaderBoardRepository

class ReaderBoardService(private val readerBoardRepository: ReaderBoardRepository) {
    fun registerAll(userAndScoreList: List<ReaderBoardPlayer>): String {
        val savedCount = readerBoardRepository.saveAll(userAndScoreList)

        if (savedCount != userAndScoreList.size) {
            throw RuntimeException("요청 데이터 중 일부가 누락되어 저장에 실패 하였습니다.")
        }

        return "${savedCount}명의 점수 등록 성공";
    }
}