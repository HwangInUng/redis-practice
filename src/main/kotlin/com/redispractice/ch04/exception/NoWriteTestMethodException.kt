package com.redispractice.ch04.exception

class NoWriteTestMethodException() : RuntimeException("테스트 메서드의 내부 구현이 없습니다.") {
}