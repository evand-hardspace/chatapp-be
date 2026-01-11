package com.evandhardspace.chatapp.infra.storage

import com.evandhardspace.chatapp.domain.exception.InvalidProfilePictureException
import com.evandhardspace.chatapp.domain.exception.StorageException
import com.evandhardspace.chatapp.domain.model.ProfilePictureUploadCredentials
import com.evandhardspace.chatapp.domain.type.UserId
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient
import org.springframework.web.client.body
import java.time.Instant
import java.util.UUID

private const val UPLOAD_EXPIRATION_SECONDS = 300L

@Service
class SupabaseStorageService(
    @param:Value($$"${supabase.url}") private val supabaseUrl: String,
    private val supabaseRestClient: RestClient,
) {
    private companion object {
        val allowedMimeTypes = mapOf(
            "image/jpeg" to "jpg",
            "image/jpg" to "jpg",
            "image/png" to "png",
            "image/webp" to "webp",
        )
    }

    fun generateSignedLoadUrl(
        userId: UserId,
        mimeType: String,
    ): ProfilePictureUploadCredentials {
        val extension = allowedMimeTypes[mimeType]
            ?: throw InvalidProfilePictureException("Invalid mime type: $mimeType")

        val fileName = "user_${userId}_${UUID.randomUUID()}.$extension"
        val path = "profile-pictures/$fileName"
        val publicUrl = "$supabaseUrl/storage/v1/object/public/$path"

        return ProfilePictureUploadCredentials(
            uploadUrl = createSignedUrl(
                path = path,
                expiresInSeconds = UPLOAD_EXPIRATION_SECONDS.toInt(),
            ),
            publicUrl = publicUrl,
            headers = mapOf(
                "Content-Type" to mimeType,
            ),
            expiresAt = Instant.now().plusSeconds(UPLOAD_EXPIRATION_SECONDS),
        )
    }

    fun deleteFile(url: String) {
        val path = if ("/object/public/" in url) {
            url.substringAfter("/object/public/")
        } else throw StorageException("Invalid file URL.")

        val deleteUrl = "/storage/v1/object/$path"

        val response = supabaseRestClient
            .delete()
            .uri(deleteUrl)
            .retrieve()
            .toBodilessEntity()

        if(response.statusCode.isError) throw StorageException("Unable to delete file: ${response.statusCode.value()}")
    }

    private fun createSignedUrl(
        path: String,
        expiresInSeconds: Int,
    ): String {
        val body = """
            { "expires_in": $expiresInSeconds }
        """.trimIndent()

        val response = supabaseRestClient
            .post()
            .uri("/storage/v1/object/upload/sign/$path")
            .body(body)
            .retrieve()
            .body<SignedUploadResponse>()
            ?: throw StorageException("Failed to create signed URL.")

        return "$supabaseUrl/storage/v1${response.url}"
    }

    private data class SignedUploadResponse(
        val url: String,
    )
}
