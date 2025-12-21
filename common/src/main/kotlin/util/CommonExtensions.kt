package util

import kotlin.contracts.contract

inline fun <T : Any>  T?.requireNotNull(lazyMessage: () -> Any): T {
    contract {
        returns() implies (this@requireNotNull != null)
    }
    return requireNotNull(this, lazyMessage)
}
