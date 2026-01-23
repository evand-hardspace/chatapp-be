package com.evandhardspace.chatapp.api.mapper

import com.evandhardspace.chatapp.api.dto.DeviceTokenDto
import com.evandhardspace.chatapp.api.dto.PlatformDto
import com.evandhardspace.chatapp.domain.model.DeviceToken

fun DeviceToken.toDeviceTokenDto(): DeviceTokenDto {
    return DeviceTokenDto(
        userId = userId,
        token = token,
        createdAt = createdAt,
    )
}

fun PlatformDto.toPlatformDto(): DeviceToken.Platform {
    return when(this) {
        PlatformDto.Android -> DeviceToken.Platform.Android
        PlatformDto.IOS -> DeviceToken.Platform.IOS
    }
}
