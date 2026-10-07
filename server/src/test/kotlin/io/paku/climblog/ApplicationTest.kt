package io.paku.climblog

import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import io.ktor.server.config.MapApplicationConfig
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals

class ApplicationTest {

    @Test
    fun testRoot() = testApplication {
        environment {
            config = MapApplicationConfig(
                "db.driver" to "org.h2.Driver",
                "db.url" to "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1",
                "redis.host" to "localhost",
                "redis.port" to "6379",
                "jwt.secret" to "test-secret",
                "jwt.issuer" to "test-issuer",
                "jwt.audience" to "test-audience",
                "aws.accessKey" to "test-key",
                "aws.secretKey" to "test-secret",
                "aws.region" to "ap-northeast-2",
                "aws.s3Bucket" to "test-bucket",
                "aws.cloudFrontDomain" to "test.cloudfront.net"
            )
        }
        application {
            module()
        }
        val response = client.get("/api/v1/videos")
        assertEquals(HttpStatusCode.Unauthorized, response.status)
    }
}