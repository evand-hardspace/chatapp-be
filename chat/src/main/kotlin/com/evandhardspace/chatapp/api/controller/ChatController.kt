package com.evandhardspace.chatapp.api.controller

import com.evandhardspace.chatapp.api.dto.AddParticipantToChatDto
import com.evandhardspace.chatapp.api.dto.ChatDto
import com.evandhardspace.chatapp.api.dto.ChatMessageDto
import com.evandhardspace.chatapp.api.dto.CreateChatRequest
import com.evandhardspace.chatapp.api.mapper.toChatDto
import com.evandhardspace.chatapp.api.util.requestUserId
import com.evandhardspace.chatapp.domain.model.Chat
import com.evandhardspace.chatapp.domain.type.ChatId
import com.evandhardspace.chatapp.service.ChatService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import java.time.Instant

private const val DEFAULT_PAGE_SIZE = 20

@RestController
@RequestMapping("/api/chat")
class ChatController(
    private val chatService: ChatService,
) {

    @GetMapping("/{chatId}/messages")
    fun getMessagesForChat(
        @PathVariable chatId: ChatId,
        @RequestParam("before", required = false) before: Instant? = null,
        @RequestParam("pageSize", required = false) pageSize: Int = DEFAULT_PAGE_SIZE,
    ): List<ChatMessageDto> {
        return chatService.getChatMessages(
            chatId = chatId,
            before = before,
            pageSize = pageSize,
        )
    }

    @GetMapping("/{chatId}")
    fun getChat(
        @PathVariable chatId: ChatId,
    ): ChatDto {
        return chatService.getChatById(
            chatId = chatId,
            requestUserId = requestUserId,
        )?.toChatDto() ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)
    }

    @GetMapping
    fun getChatsForUser(): List<ChatDto> {
        return chatService.findChatByUser(
            userId = requestUserId,
        ).map(Chat::toChatDto)
    }

    @PostMapping
    fun createChat(
        @[Valid RequestBody] body: CreateChatRequest,
    ): ChatDto {
        return chatService.createChat(
            creatorId = requestUserId,
            otherUserIds = body.otherUserIds.toSet()
        ).toChatDto()
    }

    @PostMapping("/{chatId}/add")
    fun addChatParticipants(
        @PathVariable chatId: ChatId,
        @[Valid RequestBody] body: AddParticipantToChatDto,
    ): ChatDto {
        return chatService.addParticipantsToChat(
            requestUserId = requestUserId,
            chatId = chatId,
            userIds = body.userIds.toSet(),
        ).toChatDto()
    }

    @DeleteMapping("/{chatId}/leave")
    fun leaveChat(
        @PathVariable chatId: ChatId,
    ) {
        chatService.removeParticipantFromChat(
            userId = requestUserId,
            chatId = chatId,
        )
    }
}
