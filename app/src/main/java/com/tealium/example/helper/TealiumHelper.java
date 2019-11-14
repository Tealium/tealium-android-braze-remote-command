package com.tealium.example.helper;

import android.app.Application;
import android.os.Build;
import android.util.Log;
import android.webkit.WebView;

import com.appboy.configuration.AppboyConfig;
import com.tealium.example.BuildConfig;
import com.tealium.example.MainActivity;
import com.tealium.example.UserActivity;
import com.tealium.library.Tealium;
import com.tealium.remotecommands.braze.BrazeRemoteCommand;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * This class abstracts interaction with the Tealium library and simplifies comprehension
 */
public final class TealiumHelper {

    private final static String TAG = "TealiumHelper";

    // Identifier for the main Tealium instance
    public static final String TEALIUM_MAIN = "main";

    // Not instantiatable.
    private TealiumHelper() {
    }

    public static void initialize(Application application) {

        Log.i(TAG, "initialize(" + application.getClass().getSimpleName() + ")");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && BuildConfig.DEBUG) {
            WebView.setWebContentsDebuggingEnabled(true);
        }

        final Tealium.Config config = Tealium.Config.create(application, "tealiummobile", "braze-tag", "qa");
        config.setForceOverrideLogLevel("dev");

        final Tealium instance = Tealium.createInstance(TEALIUM_MAIN, config);

        // Optional: Setup Braze blacklists.
        Set<Class> sessionHandlingBlacklist = new HashSet<>();
        Set<Class> inAppMessageBlacklist = new HashSet<>();
        // sessionHandlingBlacklist.add(MainActivity.class);
        // inAppMessageBlacklist.add(UserActivity.class);

        BrazeRemoteCommand brc = new BrazeRemoteCommand(config,
                true,
                sessionHandlingBlacklist,
                true,
                inAppMessageBlacklist);

        // Optional: Set config options that may not be supported yet by the Tag in Tealium IQ
        //              or simply to override settings locally.
        // brc.registerConfigOverride(new BrazeRemoteCommand.ConfigOverrider() {
        //     @Override
        //     public void onOverride(AppboyConfig.Builder b) {
        //         // b.setIsLocationCollectionEnabled(true);
        //         // b.setGeofencesEnabled(true);
        //
        //         // b.setPushDeepLinkBackStackActivityEnabled(true);
        //         // b.setPushDeepLinkBackStackActivityClass(UserActivity.class);
        //     }
        // });

        // Required: Add BrazeRemoteCommand object to your Tealium Instance.
        instance.addRemoteCommand(brc);

        TealiumHelper.trackEvent("initialization", new HashMap<String, Object>());
    }

    public static void trackView(String viewName, Map<String, ?> data) {
        final Tealium instance = Tealium.getInstance(TEALIUM_MAIN);

        // Instance can be remotely destroyed through publish settings
        if (instance != null) {
            instance.trackView(viewName, data);
        }
    }

    public static void trackEvent(String eventName, Map<String, ?> data) {
        final Tealium instance = Tealium.getInstance(TEALIUM_MAIN);

        // Instance can be remotely destroyed through publish settings
        if (instance != null) {
            instance.trackEvent(eventName, data);
        }
    }
}