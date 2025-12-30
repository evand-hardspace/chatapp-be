package com.evandhardspace.chatapp.infra.ratelimiting

@JvmInline
value class Backoff(val seconds: IntArray)
