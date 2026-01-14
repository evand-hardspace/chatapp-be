package com.evandhardspace.chatapp.api.mapper

import com.evandhardspace.chatapp.api.dto.PictureUploadResponse
import com.evandhardspace.chatapp.domain.model.ProfilePictureUploadCredentials

fun ProfilePictureUploadCredentials.toResponse(): PictureUploadResponse {
    return PictureUploadResponse(
        uploadUrl = uploadUrl,
        publicUrl = publicUrl,
        headers = headers,
        expiresAt = expiresAt,
    )
}
