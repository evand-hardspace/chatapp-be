package com.evandhardspace.chatapp.api.controller

import com.evandhardspace.chatapp.api.util.requestUserId
import com.evandhardspace.chatapp.domain.type.ChatMessageId
import com.evandhardspace.chatapp.service.ChatMessageService
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/messages")
class ChatMessageController(
    private val chatMessageService: ChatMessageService,
) {

    @DeleteMapping("/{messageId}")
    fun deleteMessage(
        @PathVariable messageId: ChatMessageId,
    ) {
        chatMessageService.deleteMessage(
            messageId = messageId,
            requestUserId = requestUserId,
        )
    }
}
