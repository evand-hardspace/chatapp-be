package com.evandhardspace.chatapp.api.controller

import com.evandhardspace.chatapp.api.dto.ChatDto
import com.evandhardspace.chatapp.api.dto.CreateChatRequest
import com.evandhardspace.chatapp.api.mapper.toChatDto
import com.evandhardspace.chatapp.api.util.requestUserId
import com.evandhardspace.chatapp.service.ChatService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/chat")
class ChatController(
    private val chatService: ChatService,
) {

    @PostMapping
    fun createChat(
        @[Valid RequestBody] body: CreateChatRequest,
    ): ChatDto {
        return chatService.createChat(
            creatorId = requestUserId,
            otherUserIds = body.otherUserIds.toSet()
        ).toChatDto()
    }
}