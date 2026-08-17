package io.paku.climblog.core

import androidx.activity.ComponentActivity
import java.lang.ref.WeakReference

object ActivityProvider {
    private var activityRef: WeakReference<ComponentActivity>? = null

    fun setActivity(activity: ComponentActivity) {
        activityRef = WeakReference(activity)
    }

    fun getActivity(): ComponentActivity? = activityRef?.get()
}
