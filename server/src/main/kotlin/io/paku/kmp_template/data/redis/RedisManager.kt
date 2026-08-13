package io.paku.kmp_template.data.redis

import io.lettuce.core.ClientOptions
import io.lettuce.core.ExperimentalLettuceCoroutinesApi
import io.lettuce.core.RedisClient
import io.lettuce.core.api.StatefulRedisConnection
import io.lettuce.core.api.coroutines
import io.lettuce.core.api.coroutines.RedisCoroutinesCommands
import java.time.Duration

@OptIn(ExperimentalLettuceCoroutinesApi::class)
internal class RedisManager(
    url: String
) {
    private val client = RedisClient.create(url).apply {
        options = ClientOptions.builder()
            .autoReconnect(true)
            .build()
    }
    private val connection: StatefulRedisConnection<String, String> = client.connect().apply {
        timeout = Duration.ofSeconds(5)
    }

    val commands: RedisCoroutinesCommands<String, String> = connection.coroutines()

    fun close() {
        connection.close()
        client.shutdown()
    }
}