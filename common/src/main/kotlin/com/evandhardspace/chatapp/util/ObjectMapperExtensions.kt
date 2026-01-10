package com.evandhardspace.chatapp.util

import tools.jackson.databind.ObjectMapper

inline fun <reified T> ObjectMapper.readValue(content: String): T =
    readValue(content, T::class.java)
