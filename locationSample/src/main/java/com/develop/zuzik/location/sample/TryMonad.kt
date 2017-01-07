package com.develop.zuzik.location.sample

/**
 * User: zuzik
 * Date: 1/7/17
 */
data class TryMonad<out T> private constructor(val value: T?, val error: Throwable?) {

    companion object Factory {
        fun <T> value(value: T) = TryMonad<T>(value, null)
        fun <T> error(error: Throwable) = TryMonad<T>(null, error)
    }
}