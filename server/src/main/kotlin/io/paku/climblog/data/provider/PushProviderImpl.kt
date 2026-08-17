package io.paku.climblog.data.provider

import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import com.google.firebase.messaging.Notification
import io.paku.climblog.domain.provider.PushProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PushProviderImpl : PushProvider {
    override suspend fun sendPush(
        token: String,
        title: String,
        body: String,
        data: Map<String, String>
    ) = withContext(Dispatchers.IO) {
        try {
            val message = Message.builder()
                .setToken(token)
                .setNotification(
                    Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build()
                )
                .putAllData(data)
                .build()

            FirebaseMessaging.getInstance().send(message)
            Unit
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
