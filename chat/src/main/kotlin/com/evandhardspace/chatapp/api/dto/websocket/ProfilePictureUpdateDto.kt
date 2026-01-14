package com.evandhardspace.chatapp.api.dto.websocket

import com.evandhardspace.chatapp.domain.type.UserId

data class ProfilePictureUpdateDto(
    val userId: UserId,
    val newUrl: String?,
)
