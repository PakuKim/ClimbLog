package io.paku.climblog.di

import io.paku.climblog.business.domain.provider.VideoCompressor
import io.paku.climblog.core.IOSVideoCompressor
import org.koin.dsl.module

actual val sharedPlatformModule = module {
    single<VideoCompressor> { IOSVideoCompressor() }
}
