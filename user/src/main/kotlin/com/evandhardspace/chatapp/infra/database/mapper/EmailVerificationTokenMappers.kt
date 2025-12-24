package com.evandhardspace.chatapp.infra.database.mapper

import com.evandhardspace.chatapp.domain.model.EmailVerificationToken
import com.evandhardspace.chatapp.infra.database.entity.EmailVerificationTokenEntity


fun EmailVerificationTokenEntity.toEmailVerificationToken(): EmailVerificationToken =
    EmailVerificationToken(
        id = id,
        token = token,
        user = user.toUser(),
    )