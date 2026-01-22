package com.evandhardspace.chatapp.infra.pushnotification

import com.evandhardspace.chatapp.domain.model.DeviceToken
import com.evandhardspace.chatapp.domain.model.PushNotification
import com.evandhardspace.chatapp.domain.model.PushNotificationSendResult
import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.messaging.AndroidConfig
import com.google.firebase.messaging.ApnsConfig
import com.google.firebase.messaging.Aps
import com.google.firebase.messaging.BatchResponse
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingException
import com.google.firebase.messaging.Message
import com.google.firebase.messaging.MessagingErrorCode
import com.google.firebase.messaging.Notification
import jakarta.annotation.PostConstruct
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.ResourceLoader
import org.springframework.stereotype.Service

@Service
class FirebasePushNotificationService(
    @param:Value($$"${firebase.credentials-path}")
    private val credentialsPath: String,
    private val resourceLoader: ResourceLoader,
) {

    private val logger = LoggerFactory.getLogger(FirebasePushNotificationService::class.java)

    @PostConstruct
    fun initialize() {
        try {
            val service = resourceLoader.getResource(credentialsPath)
            val options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(service?.inputStream))
                .build()
            FirebaseApp.initializeApp(options)
            logger.info("Firebase Admin SDK initialized successfully")
        } catch (e: Exception) {
            logger.error("Error initializing Firebase Admin SDK", e)
            throw e
        }
    }

    fun isValidToken(token: String?): Boolean {
        val message = Message
            .builder()
            .setToken(token)
            .build()

        return try {
            FirebaseMessaging.getInstance().send(message, true)
            true
        } catch (e: FirebaseMessagingException) {
            logger.warn("Failed to validate Firebase Token", e)
            false
        }
    }

    fun sendNotification(notification: PushNotification): PushNotificationSendResult {
        val messages = notification.recipients.map { recipient ->
            Message.builder()
                .setToken(recipient.token)
                .setNotification(
                    Notification.builder()
                        .setTitle(notification.title)
                        .setBody(notification.message)
                        .build(),
                ).apply {
                    notification.data.forEach { (key, value) ->
                        putData(key, value)
                    }

                    when (recipient.platform) {
                        DeviceToken.Platform.Android -> {
                            setAndroidConfig(
                                AndroidConfig.builder()
                                    .setPriority(AndroidConfig.Priority.HIGH)
                                    .setCollapseKey(notification.chatId.toString())
                                    .setRestrictedPackageName("com.evandhardspace.chatapp")
                                    .build(),
                            )
                        }

                        DeviceToken.Platform.IOS -> {
                            setApnsConfig(
                                ApnsConfig.builder()
                                    .setAps(
                                        Aps.builder()
                                            .setSound("default")
                                            .setThreadId(notification.chatId.toString())
                                            .build(),
                                    )
                                    .build()
                            )
                        }
                    }
                }
                .build()
        }

        return FirebaseMessaging
            .getInstance()
            .sendEach(messages)
            .toSendResult(
                logger = logger,
                allDeviceTokens = notification.recipients,
            )
    }
}

private fun BatchResponse.toSendResult(
    logger: Logger,
    allDeviceTokens: List<DeviceToken>,
): PushNotificationSendResult {
    val succeeded = mutableListOf<DeviceToken>()
    val temporaryFailures = mutableListOf<DeviceToken>()
    val permanentFailures = mutableListOf<DeviceToken>()

    responses.forEachIndexed { index, sendResponse ->
        val deviceToken = allDeviceTokens[index]
        if (sendResponse.isSuccessful) {
            succeeded += deviceToken
        } else {
            val errorCode = sendResponse.exception?.messagingErrorCode

            logger.warn("Failed to send notification to token ${deviceToken.token}: $errorCode")

            when (errorCode) {
                MessagingErrorCode.UNREGISTERED,
                MessagingErrorCode.SENDER_ID_MISMATCH,
                MessagingErrorCode.INVALID_ARGUMENT,
                MessagingErrorCode.THIRD_PARTY_AUTH_ERROR -> permanentFailures += deviceToken

                MessagingErrorCode.INTERNAL,
                MessagingErrorCode.QUOTA_EXCEEDED,
                MessagingErrorCode.UNAVAILABLE,
                null -> temporaryFailures += deviceToken
            }
        }
    }

    logger.debug(
        "Push notifications sent. Succeeded: ${succeeded.size}, " +
                "temporary failures: ${temporaryFailures.size}, permanent failures: ${permanentFailures.size}"
    )

    return PushNotificationSendResult(
        succeeded = succeeded.toList(),
        temporaryFailures = temporaryFailures.toList(),
        permanentFailures = permanentFailures.toList(),
    )
}
