package com.tealium.example.helper

import android.app.Application
import android.util.Log
import android.webkit.WebView
import com.tealium.core.*
import com.tealium.dispatcher.TealiumEvent
import com.tealium.dispatcher.TealiumView
import com.tealium.lifecycle.Lifecycle
import com.tealium.location.Location
import com.tealium.location.LocationTrackingAccuracy
import com.tealium.location.LocationTrackingOptions
import com.tealium.location.location
import com.tealium.remotecommanddispatcher.RemoteCommands
import com.tealium.remotecommanddispatcher.remoteCommands
import com.tealium.remotecommands.braze.BrazeRemoteCommand
import com.tealium.tagmanagementdispatcher.TagManagement

/**
 * This class abstracts interaction with the Tealium library and simplifies comprehension
 */
object TealiumHelper {
    private const val TAG = "TealiumHelper"

    // Identifier for the main Tealium instance
    const val TEALIUM_MAIN = "main"

    @JvmStatic
    fun initialize(application: Application) {
        Log.i(TAG, "initialize(" + application.javaClass.simpleName + ")")
        WebView.setWebContentsDebuggingEnabled(true)
        val config = TealiumConfig(
            application,
            "tealiummobile",
            "braze-tag",
            Environment.DEV,
            modules = mutableSetOf(Modules.Lifecycle)
        ).apply {

            // TagManagement controlled RemoteCommand
            // dispatchers.add(Dispatchers.TagManagement)

            // JSON controlled RemoteCommand
            dispatchers.add(Dispatchers.RemoteCommands)
        }.apply {
            collectors.add(Collectors.Location)
        }

        // Optional: Setup Braze blacklists.
        val sessionHandlingBlacklist: MutableSet<Class<*>> = mutableSetOf()
        val inAppMessageBlacklist: MutableSet<Class<*>> = mutableSetOf()
        // sessionHandlingBlacklist.add(MainActivity::class.java)
        // inAppMessageBlacklist.add(UserActivity::class.java)
        val brc = BrazeRemoteCommand(
            application,
            true,
            sessionHandlingBlacklist,
            true,
            inAppMessageBlacklist
        )

        // Optional: Set config options that may not be supported yet by the Tag in Tealium IQ
        //              or simply to override settings locally.
        brc.registerConfigOverride { builder ->
            // builder.setIsLocationCollectionEnabled(true);
            // builder.setGeofencesEnabled(true);
            builder.setFirebaseCloudMessagingSenderIdKey("...")
                .setIsFirebaseCloudMessagingRegistrationEnabled(true)

            // builder.setPushDeepLinkBackStackActivityEnabled(true);
            // builder.setPushDeepLinkBackStackActivityClass(UserActivity.class);
        }

        Tealium.create(TEALIUM_MAIN, config) {
            // TagManagement controlled RemoteCommand
            // remoteCommands?.add(brc)

            // JSON controlled RemoteCommand
            remoteCommands?.add(brc, "braze-remotecommand.json")
        }

        // Required: Add BrazeRemoteCommand object to your Tealium Instance.
        trackEvent("initialization")
    }

    @JvmStatic
    fun trackView(viewName: String, data: Map<String, Any>? = null) {
        val instance: Tealium? = Tealium[TEALIUM_MAIN]

        // Instance can be remotely destroyed through publish settings
        instance?.track(TealiumView(viewName, data))
    }

    @JvmStatic
    fun trackEvent(eventName: String, data: Map<String, Any>? = null) {
        val instance: Tealium? = Tealium[TEALIUM_MAIN]

        // Instance can be remotely destroyed through publish settings
        instance?.track(TealiumEvent(eventName, data))
    }

    @JvmStatic
    fun startLocationTracking() {
        Tealium[TEALIUM_MAIN]?.location?.startLocationTracking(
            LocationTrackingOptions(
                LocationTrackingAccuracy.BalancedAccuracy,
                10000L,
            )
        )
    }
}