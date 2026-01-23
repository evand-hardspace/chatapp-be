package com.evandhardspace.chatapp.infra.mapper

import com.evandhardspace.chatapp.domain.model.DeviceToken
import com.evandhardspace.chatapp.infra.database.DeviceTokenEntity

fun DeviceTokenEntity.toDeviceToken(): DeviceToken {
    return DeviceToken(
        userId = userId,
        token = token,
        platform = platform.toPlatform(),
        createdAt = createdAt,
        id = id,
    )
}
