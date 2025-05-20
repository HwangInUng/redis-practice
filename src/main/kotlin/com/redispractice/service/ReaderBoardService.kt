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

    fun updateScore(updatedPlayer: ReaderBoardPlayer): String {
        val result = readerBoardRepository.updateScore(updatedPlayer)

        if (result == false) {
            throw RuntimeException("서버 내부에서 오류가 발생했습니다.")
        }

        return "${updatedPlayer.name}의 점수 수정 성공"
    }
}