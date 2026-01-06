package com.evandhardspace.chatapp.api.exceptionhanding

fun RuntimeException.toHandlerResponse(code: String): Map<String, Any> = buildMap {
    put("code", code)
    message?.let { put("message", it) }
}
