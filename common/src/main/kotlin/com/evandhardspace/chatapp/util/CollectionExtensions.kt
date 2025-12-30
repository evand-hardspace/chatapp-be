package com.evandhardspace.chatapp.util

inline fun <reified T> Iterable<*>.findIsInstance(): T? =
    this.find { it is T } as? T

inline fun <reified T> Array<in T>.findIsInstance(): T? =
    this.find { it is T } as? T