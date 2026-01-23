package com.evandhardspace.chatapp.infra.mapper

import com.evandhardspace.chatapp.domain.model.DeviceToken
import com.evandhardspace.chatapp.infra.database.PlatformEntity

fun DeviceToken.Platform.toPlatformEntity(): PlatformEntity {
    return when(this) {
        DeviceToken.Platform.Android -> PlatformEntity.Android
        DeviceToken.Platform.IOS -> PlatformEntity.IOS
    }
}

fun PlatformEntity.toPlatform(): DeviceToken.Platform {
    return when(this) {
        PlatformEntity.Android -> DeviceToken.Platform.Android
        PlatformEntity.IOS -> DeviceToken.Platform.IOS
    }
}
