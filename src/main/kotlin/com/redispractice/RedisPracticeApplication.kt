package com.redispractice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class RedisPracticeApplication

fun main(args: Array<String>) {
	runApplication<RedisPracticeApplication>(*args)
}
