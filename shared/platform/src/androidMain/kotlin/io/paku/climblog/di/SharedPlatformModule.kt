package io.paku.climblog.di

import io.paku.climblog.business.domain.provider.VideoCompressor
import io.paku.climblog.core.AndroidVideoCompressor
import org.koin.dsl.module

actual val sharedPlatformModule = module {
    single<VideoCompressor> { AndroidVideoCompressor(get()) }
}
