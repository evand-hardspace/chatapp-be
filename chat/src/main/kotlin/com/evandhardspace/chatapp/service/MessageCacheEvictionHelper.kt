package com.evandhardspace.chatapp.service

import com.evandhardspace.chatapp.domain.type.ChatId
import org.springframework.cache.annotation.CacheEvict
import org.springframework.stereotype.Component

@Component
class MessageCacheEvictionHelper {
    @CacheEvict(
        value = ["messages"],
        key = "#chatId",
    )
    fun evictMessageCache(chatId: ChatId) {
        /* NO-OP: Let Spring handle the cache evict */
    }
}