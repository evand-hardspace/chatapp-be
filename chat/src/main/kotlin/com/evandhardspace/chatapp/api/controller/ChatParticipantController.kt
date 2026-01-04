package com.evandhardspace.chatapp.api.controller

import com.evandhardspace.chatapp.api.dto.ChatParticipantDto
import com.evandhardspace.chatapp.api.mapper.toChatParticipantDto
import com.evandhardspace.chatapp.api.util.requestUserId
import com.evandhardspace.chatapp.service.ChatParticipantService
import com.evandhardspace.chatapp.util.normalized
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/api/chat/participants")
class ChatParticipantController(private val chatParticipantService: ChatParticipantService) {

    @GetMapping
    fun getChatParticipantByUsernameOrEmail(
        @RequestParam(required = false) query: String?,
    ): ChatParticipantDto {
        val participant = if (query == null) {
            chatParticipantService.findChatParticipantById(requestUserId)
        } else {
            chatParticipantService.findChatParticipantByEmailOrUsername(query.normalized())
        }

        return participant?.toChatParticipantDto()
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Chat participant not found.")
    }
}