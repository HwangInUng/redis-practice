package com.redispractice.controller

import com.redispractice.domain.entity.ReaderBoardPlayer
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/api/reader-board")
@RestController
class ReaderBoardController {

    @GetMapping("/total-score")
    fun getTotalScoreReaderBoard(): ResponseEntity<List<ReaderBoardPlayer>> {
        return ResponseEntity.ok(null);
    }

    @PostMapping
    fun registerScoreByDate(): ResponseEntity<String> {
        return ResponseEntity.ok("");
    }
}