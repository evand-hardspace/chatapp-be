package com.evandhardspace.chatapp.domain.exception

import com.evandhardspace.chatapp.domain.type.UserId

class ChatParticipantNotFoundException(
    participantId: UserId,
): RuntimeException(
    "The chat participant with thr ID $participantId was not found.",
)