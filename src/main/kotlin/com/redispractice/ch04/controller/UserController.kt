package com.redispractice.ch04.controller

import com.redispractice.ch04.entity.User
import com.redispractice.ch04.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/api/user")
@RestController
// Kotlin에서는 primary constructor를 사용하여 생성자를 정의할 수 있고,
// constructor injection을 통해 의존성을 주입할 수 있기 때문에 lombok 없이도 의존성 주입 간결하게 가능
// @RequiredArgsConstructor은 코틀린에서 불필요
class UserController (private val userService: UserService) {
    @PostMapping("/sign-up")
    fun signUp(@RequestBody user: User): ResponseEntity<String> {
        val result = userService.signUp(user)
        return ResponseEntity.ok(result)
    }
}