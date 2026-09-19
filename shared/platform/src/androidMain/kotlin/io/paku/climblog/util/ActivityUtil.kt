package io.paku.climblog.util

import androidx.activity.ComponentActivity
import java.lang.ref.WeakReference

object ActivityUtil {
    private var activityRef: WeakReference<ComponentActivity>? = null

    fun setActivity(activity: ComponentActivity) {
        activityRef = WeakReference(activity)
    }

    fun getActivity(): ComponentActivity? = activityRef?.get()
}