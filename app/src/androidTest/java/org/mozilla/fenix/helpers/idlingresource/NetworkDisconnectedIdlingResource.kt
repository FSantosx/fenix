package org.mozilla.fenix.helpers.idlingresource

import android.net.ConnectivityManager
import androidx.core.content.getSystemService
import androidx.test.espresso.IdlingResource
import androidx.test.platform.app.InstrumentationRegistry
import org.mozilla.fenix.ext.isOnline

class NetworkDisconnectedIdlingResource : IdlingResource {
    private var resourceCallback: IdlingResource.ResourceCallback? = null
    private val connectionManager = InstrumentationRegistry.getInstrumentation().context.getSystemService<ConnectivityManager>()

    override fun getName(): String {
        return this::javaClass.name
    }

    override fun isIdleNow(): Boolean {
        val idle = !isOnline()
        if (idle)
            resourceCallback?.onTransitionToIdle()
        return idle
    }

    override fun registerIdleTransitionCallback(callback: IdlingResource.ResourceCallback?) {
        if (callback != null)
            resourceCallback = callback
    }

    private fun isOnline(): Boolean {
        return connectionManager!!.isOnline()
    }
}
