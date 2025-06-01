package com.redispractice.ch04.exception

import org.springframework.http.HttpStatus

class ApiException(var status: HttpStatus, override var message: String) : RuntimeException(message)