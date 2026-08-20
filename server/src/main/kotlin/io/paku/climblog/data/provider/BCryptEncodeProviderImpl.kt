package io.paku.climblog.data.provider

import io.paku.climblog.domain.provider.BCryptEncodeProvider
import org.mindrot.jbcrypt.BCrypt

internal class BCryptEncodeProviderImpl : BCryptEncodeProvider {
    override fun hash(password: String): String =
        BCrypt.hashpw(password, BCrypt.gensalt())

    override fun verify(password: String, hash: String): Boolean =
        BCrypt.checkpw(password, hash)
}