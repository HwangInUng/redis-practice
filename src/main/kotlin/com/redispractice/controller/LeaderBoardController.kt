package com.redispractice.controller

import com.redispractice.domain.entity.LeaderBoardPlayer
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/api/Leader-board")
@RestController
class LeaderBoardController {

    @GetMapping("/total-score")
    fun getTotalScoreLeaderBoard(): ResponseEntity<List<LeaderBoardPlayer>> {
        return ResponseEntity.ok(null);
    }

    @PostMapping
    fun registerScoreByDate(): ResponseEntity<String> {
        return ResponseEntity.ok("");
    }
}