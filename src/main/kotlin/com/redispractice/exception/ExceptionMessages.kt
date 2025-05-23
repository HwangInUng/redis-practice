package com.redispractice.exception

object ExceptionMessages {
    const val EMPTY_INPUT = "요청 데이터는 비어 있을 수 없습니다."

    fun isBlankName(id: Long): String =
        "${id}의 이름이 비어 있습니다."

    fun someRegisterFailed(target: String): String =
        "$target 등록에 실패했습니다."
}