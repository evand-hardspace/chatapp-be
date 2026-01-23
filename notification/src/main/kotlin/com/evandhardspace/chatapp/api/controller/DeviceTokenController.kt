package com.evandhardspace.chatapp.api.controller

import com.evandhardspace.chatapp.api.dto.DeviceTokenDto
import com.evandhardspace.chatapp.api.dto.RegisterDeviceRequest
import com.evandhardspace.chatapp.api.mapper.toDeviceTokenDto
import com.evandhardspace.chatapp.api.mapper.toPlatformDto
import com.evandhardspace.chatapp.api.util.requestUserId
import com.evandhardspace.chatapp.service.PushNotificationService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/notification")
class DeviceTokenController(
    private val pushNotificationService: PushNotificationService,
) {

    @PostMapping("/register")
    fun registerDeviceToken(
        @[Valid RequestBody] body: RegisterDeviceRequest,
    ): DeviceTokenDto {
        return pushNotificationService.registerDevice(
            userId = requestUserId,
            token = body.token,
            platform = body.platform.toPlatformDto(),
        ).toDeviceTokenDto()
    }

    @DeleteMapping("/{token}")
    fun unregisterDeviceToken(
        @PathVariable("token") token: String,
    ) {
        pushNotificationService.unregisterDevice(token)
    }
}
