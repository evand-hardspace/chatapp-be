package com.evandhardspace.chatapp.service

import com.evandhardspace.chatapp.domain.event.ModuleChatEvent
import com.evandhardspace.chatapp.domain.exception.ChatParticipantNotFoundException
import com.evandhardspace.chatapp.domain.exception.InvalidProfilePictureException
import com.evandhardspace.chatapp.domain.model.ProfilePictureUploadCredentials
import com.evandhardspace.chatapp.domain.type.UserId
import com.evandhardspace.chatapp.infra.database.repository.ChatParticipantRepository
import com.evandhardspace.chatapp.infra.storage.SupabaseStorageService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProfilePictureService(
    @param:Value($$"${supabase.url}") private val supabaseUrl: String,
    private val supabaseStorageService: SupabaseStorageService,
    private val chatParticipantRepository: ChatParticipantRepository,
    private val applicationEventPublisher: ApplicationEventPublisher,
    ) {

    private val logger = LoggerFactory.getLogger(ProfilePictureService::class.java)

    fun generateUploadCredentials(
        userId: UserId,
        mimeType: String,
    ): ProfilePictureUploadCredentials {
        return supabaseStorageService.generateSignedLoadUrl(
            userId = userId,
            mimeType = mimeType,
        )
    }

    @Transactional
    fun deleteProfilePicture(userId: UserId) {
        val participant = chatParticipantRepository.findByIdOrNull(userId)
            ?: throw ChatParticipantNotFoundException(userId)

        participant.profilePictureUrl?.let { url ->
            chatParticipantRepository.save(
                participant.apply { profilePictureUrl = null },
            )
            supabaseStorageService.deleteFile(url)

            applicationEventPublisher.publishEvent(
                ModuleChatEvent.ProfilePictureUpdatedEvent(
                    userId = userId,
                    newUrl = null,
                )
            )
        }
    }

    @Transactional
    fun confirmProfilePictureUpload(
        userId: UserId,
        publicUrl: String,
    ) {
        if(publicUrl.startsWith(supabaseUrl).not()) {
            throw InvalidProfilePictureException("Invalid profile picture URL.")
        }

        val participant = chatParticipantRepository.findByIdOrNull(userId)
            ?: throw ChatParticipantNotFoundException(userId)

        val oldUrl = participant.profilePictureUrl

        chatParticipantRepository.save(
            participant.apply { profilePictureUrl = publicUrl },
        )

        try {
            oldUrl?.let {
                supabaseStorageService.deleteFile(oldUrl)
            }
        } catch (e: Exception) {
            logger.warn("Deleting old profile picture for $userId failed", e)
        }

        applicationEventPublisher.publishEvent(
            ModuleChatEvent.ProfilePictureUpdatedEvent(
                userId = userId,
                newUrl = publicUrl,
            )
        )
    }
}
