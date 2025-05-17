package com.redispractice.repository

import java.time.Duration

interface RedisRepository<T : Any> {
    fun save(key: String, value: T, ttl: Duration? = null)
    fun find(key: String): T?
    fun delete(key: String)
}