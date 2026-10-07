package io.paku.climblog.worker

data class WorkerConfig(
    val ktorBaseUrl: String = System.getenv("KTOR_BASE_URL") ?: "http://localhost:8080",
    val workerAuthToken: String = System.getenv("WORKER_AUTH_TOKEN") ?: "dev-worker-auth-token",
    val workerId: String = System.getenv("WORKER_ID") ?: "climblog-worker-01",
    val r2AccessKey: String = System.getenv("R2_ACCESS_KEY") ?: "dev-r2-access-key",
    val r2SecretKey: String = System.getenv("R2_SECRET_KEY") ?: "dev-r2-secret-key",
    val r2Bucket: String = System.getenv("R2_BUCKET") ?: "paku-climblog-service-dev",
    val r2Endpoint: String = System.getenv("R2_ENDPOINT") ?: "https://dev-account-id.r2.cloudflarestorage.com",
    val r2PublicBaseUrl: String = System.getenv("R2_PUBLIC_BASE_URL") ?: "https://media.climblog.io",
    val pollIntervalSeconds: Long = System.getenv("WORKER_POLL_INTERVAL_SECONDS")?.toLongOrNull() ?: 5L,
    val leaseSeconds: Long = System.getenv("WORKER_LEASE_SECONDS")?.toLongOrNull() ?: 600L,
    val workDir: String = System.getenv("WORK_DIR") ?: "./work",
    val ffmpegPath: String = System.getenv("FFMPEG_PATH") ?: "ffmpeg",
    val ffprobePath: String = System.getenv("FFPROBE_PATH") ?: "ffprobe"
)
