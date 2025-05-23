package com.redispractice.exception

object ExceptionMessages {
    const val EMPTY_INPUT = "요청 데이터는 비어 있을 수 없습니다."
    const val INTERNAL_SERVER_ERROR = "서버 내부에서 오류가 발생했습니다."

    fun isBlankName(id: Long): String =
        "${id}의 이름이 비어 있습니다."

    fun someRegisterFailed(target: String): String =
        "$target 등록에 실패했습니다."

    fun updateEntityNotExist(target: String): String =
        "$target 수정 대상이 존재하지 않습니다."

    fun updatedFailed(target: String): String =
        "$target 수정에 실패했습니다."
}