package com.tealium.remotecommands.braze;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.app.Application;
import android.app.Application.ActivityLifecycleCallbacks;
import android.os.Bundle;
import android.util.Log;

import com.braze.enums.DeviceKey;
import com.braze.enums.Gender;
import com.braze.enums.NotificationSubscriptionType;
import com.braze.Braze;
import com.braze.BrazeActivityLifecycleCallbackListener;
import com.braze.BrazeUser;
import com.braze.configuration.BrazeConfig;
import com.braze.models.outgoing.BrazeProperties;

import org.json.JSONArray;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import static com.tealium.remotecommands.braze.BrazeConstants.TAG;
import static com.tealium.remotecommands.braze.BrazeConstants.Config;

class BrazeInstance implements BrazeCommand, ActivityLifecycleCallbacks {

    Application mApplication;
    Activity mCurrentActivity;

    boolean mSessionHandlingEnabled;
    Set<Class<?>> mSessionHandlingBlacklist;
    boolean mRegisterInAppMessageManager;
    Set<Class<?>> mInAppMessageBlacklist;

    public BrazeInstance(Application app) {
        this(app, true, null, true, null);
    }

    public BrazeInstance(Application app, boolean sessionHandlingEnabled, Set<Class<?>> sessingHandlingBlacklist, boolean registerInAppMessageManager, Set<Class<?>> inAppMessageBlacklist) {
        mApplication = app;
        mSessionHandlingEnabled = sessionHandlingEnabled;
        mSessionHandlingBlacklist = sessingHandlingBlacklist;
        mRegisterInAppMessageManager = registerInAppMessageManager;
        mInAppMessageBlacklist = inAppMessageBlacklist;

        if (sessionHandlingEnabled) {
            // Init process will be asynchronous; need to register a temporary listener to capture
            // any Activity that may be loaded for session reporting.
            // Cannot register the braze listeners before API key is available, which may be later on.
            mApplication.registerActivityLifecycleCallbacks(this);
        }
    }

    @Override
    public void initialize(@Nullable String apiKey, @Nullable JSONObject launchOptions, @Nullable List<BrazeRemoteCommand.ConfigOverrider> overrides) {
        BrazeConfig.Builder builder = new BrazeConfig.Builder();

        // API Key can be setup in a local resx file,
        // So no need to make this absolutely mandatory.
        if (apiKey != null) {
            builder.setApiKey(apiKey);
        }

        // check for any populated launch options
        if (launchOptions != null) {
            // Strings
            if (BrazeUtils.keyHasValue(launchOptions, Config.FIREBASE_SENDER_ID)) {
                builder.setFirebaseCloudMessagingSenderIdKey(launchOptions.optString(Config.FIREBASE_SENDER_ID));
            }

            if (BrazeUtils.keyHasValue(launchOptions, Config.FIREBASE_MESSAGING_SERVICE_CLASSPATH)) {
                builder.setFallbackFirebaseMessagingServiceClasspath(launchOptions.optString(Config.FIREBASE_MESSAGING_SERVICE_CLASSPATH));
            }

            if (BrazeUtils.keyHasValue(launchOptions, Config.CUSTOM_ENDPOINT)) {
                builder.setCustomEndpoint(launchOptions.optString(Config.CUSTOM_ENDPOINT));
            }

            if (BrazeUtils.keyHasValue(launchOptions, Config.SMALL_NOTIFICATION_ICON)) {
                builder.setSmallNotificationIcon(launchOptions.optString(Config.SMALL_NOTIFICATION_ICON));
            }

            if (BrazeUtils.keyHasValue(launchOptions, Config.LARGE_NOTIFICATION_ICON)) {
                builder.setLargeNotificationIcon(launchOptions.optString(Config.LARGE_NOTIFICATION_ICON));
            }

            if(BrazeUtils.keyHasValue(launchOptions, Config.DEFAULT_NOTIFICATION_CHANNEL_DESCRIPTION)) {
                builder.setDefaultNotificationChannelDescription(launchOptions.optString(Config.DEFAULT_NOTIFICATION_CHANNEL_DESCRIPTION));
            }

            if(BrazeUtils.keyHasValue(launchOptions, Config.DEFAULT_NOTIFICATION_CHANNEL_NAME)) {
                builder.setDefaultNotificationChannelName(launchOptions.optString(Config.DEFAULT_NOTIFICATION_CHANNEL_NAME));
            }

            if (BrazeUtils.keyHasValue(launchOptions, Config.BACKSTACK_ACTIVITY_CLASS)) {
                try {
                    builder.setPushDeepLinkBackStackActivityClass(Class.forName(launchOptions.optString(Config.BACKSTACK_ACTIVITY_CLASS)));
                } catch (ClassNotFoundException cnf) {
                    Log.w(TAG, "Backstack Class not found.", cnf);
                }
            }

            // Booleans
            if (BrazeUtils.keyHasValue(launchOptions, Config.FIREBASE_ENABLED)) {
                builder.setIsFirebaseCloudMessagingRegistrationEnabled(launchOptions.optBoolean(Config.FIREBASE_ENABLED));
            }

            if (BrazeUtils.keyHasValue(launchOptions, Config.FIREBASE_FALLBACK_MESSAGE_SERVICE_ENABLED)) {
                builder.setIsFirebaseCloudMessagingRegistrationEnabled(launchOptions.optBoolean(Config.FIREBASE_FALLBACK_MESSAGE_SERVICE_ENABLED));
            }

            if (BrazeUtils.keyHasValue(launchOptions, Config.FIREBASE_NEW_TOKEN_ENABLED)) {
                builder.setIsFirebaseMessagingServiceOnNewTokenRegistrationEnabled(launchOptions.optBoolean(Config.FIREBASE_NEW_TOKEN_ENABLED));
            }

            if (BrazeUtils.keyHasValue(launchOptions, Config.ADM_ENABLED)) {
                builder.setAdmMessagingRegistrationEnabled(launchOptions.optBoolean(Config.ADM_ENABLED));
            }

            if (BrazeUtils.keyHasValue(launchOptions, Config.AUTO_PUSH_DEEP_LINKS)) {
                builder.setHandlePushDeepLinksAutomatically(launchOptions.optBoolean(Config.AUTO_PUSH_DEEP_LINKS));
            }

            if (BrazeUtils.keyHasValue(launchOptions, Config.ENABLE_AUTOMATIC_LOCATION)) {
                builder.setIsLocationCollectionEnabled(launchOptions.optBoolean(Config.ENABLE_AUTOMATIC_LOCATION));
            }

            if (BrazeUtils.keyHasValue(launchOptions, Config.ENABLE_NEWS_FEED_INDICATOR)) {
                builder.setNewsfeedVisualIndicatorOn(launchOptions.optBoolean(Config.ENABLE_NEWS_FEED_INDICATOR));
            }

            if (BrazeUtils.keyHasValue(launchOptions, Config.ENABLE_GEOFENCES)) {
                builder.setGeofencesEnabled(launchOptions.optBoolean(Config.ENABLE_GEOFENCES));
            }

            if (BrazeUtils.keyHasValue(launchOptions, Config.ENABLE_AUTOMATIC_GEOFENCE_REQUESTS))  {
                builder.setAutomaticGeofenceRequestsEnabled(launchOptions.optBoolean(Config.ENABLE_AUTOMATIC_GEOFENCE_REQUESTS));
            }

            if (BrazeUtils.keyHasValue(launchOptions, Config.BACKSTACK_ACTIVITY_ENABLED)) {
                builder.setPushDeepLinkBackStackActivityEnabled(launchOptions.optBoolean(Config.BACKSTACK_ACTIVITY_ENABLED));
            }

            if (BrazeUtils.keyHasValue(launchOptions, Config.IS_SDK_AUTHENTICATION_ENABLED)) {
                builder.setIsSdkAuthenticationEnabled(launchOptions.optBoolean(Config.IS_SDK_AUTHENTICATION_ENABLED));
            }

            // Integers
            if (BrazeUtils.keyHasValue(launchOptions, Config.SESSION_TIMEOUT)) {
                builder.setSessionTimeout(launchOptions.optInt(Config.SESSION_TIMEOUT));
            }

            if (BrazeUtils.keyHasValue(launchOptions, Config.TRIGGER_INTERVAL_SECONDS)) {
                builder.setTriggerActionMinimumTimeIntervalSeconds(launchOptions.optInt(Config.TRIGGER_INTERVAL_SECONDS));
            }

            if (BrazeUtils.keyHasValue(launchOptions, Config.DEFAULT_NOTIFICATION_COLOR)) {
                builder.setDefaultNotificationAccentColor(launchOptions.optInt(Config.DEFAULT_NOTIFICATION_COLOR));
            }

            if (BrazeUtils.keyHasValue(launchOptions, Config.BAD_NETWORK_INTERVAL)) {
                builder.setBadNetworkDataFlushInterval(launchOptions.optInt(Config.BAD_NETWORK_INTERVAL));
            }

            if (BrazeUtils.keyHasValue(launchOptions, Config.BAD_NETWORK_INTERVAL)) {
                builder.setBadNetworkDataFlushInterval(launchOptions.optInt(Config.BAD_NETWORK_INTERVAL));
            }

            if (BrazeUtils.keyHasValue(launchOptions, Config.GOOD_NETWORK_INTERVAL)) {
                builder.setGoodNetworkDataFlushInterval(launchOptions.optInt(Config.GOOD_NETWORK_INTERVAL));
            }

            if (BrazeUtils.keyHasValue(launchOptions, Config.GREAT_NETWORK_INTERVAL)) {
                builder.setGreatNetworkDataFlushInterval(launchOptions.optInt(Config.GREAT_NETWORK_INTERVAL));
            }

            if (BrazeUtils.keyHasValue(launchOptions, Config.DEVICE_OPTIONS)) {
                JSONArray options = launchOptions.optJSONArray(Config.DEVICE_OPTIONS);
                if (options != null && options.length() > 0) {

                    List<DeviceKey> deviceOptions = new ArrayList<>();
                    for (int i = 0; i < options.length(); i++) {
                        try {
                            DeviceKey key = DeviceKey.valueOf(options.getString(i).toUpperCase(Locale.ROOT));
                            deviceOptions.add(key);
                        } catch (Exception ignore) {
                        }
                    }

                    builder.setDeviceObjectAllowlist(EnumSet.copyOf(deviceOptions));
                }
            }
        }

        // Go through all the config overrides..
        if (overrides != null) {
            for (BrazeRemoteCommand.ConfigOverrider c : overrides) {
                c.onOverride(builder);
            }
        }

        // configure the instance.
        Braze.configure(mApplication.getApplicationContext(), builder.build());

        if (mSessionHandlingEnabled) {
            if (mCurrentActivity != null
                    && (mSessionHandlingBlacklist == null || !mSessionHandlingBlacklist.contains(mCurrentActivity.getClass()))) {
                // Current activity found.
                // No longer need a temporary listener.
                getBrazeInstance().openSession(mCurrentActivity);
            }
            mApplication.unregisterActivityLifecycleCallbacks(this);
            // register Braze listeners so they can take over the session handling.
            mApplication.registerActivityLifecycleCallbacks((ActivityLifecycleCallbacks) new BrazeActivityLifecycleCallbackListener(mSessionHandlingEnabled,
                    mRegisterInAppMessageManager,
                    mSessionHandlingBlacklist,
                    mInAppMessageBlacklist));
        }
    }

    @Override
    public void enableSdk() {
        Braze.enableSdk(mApplication.getApplicationContext());
    }

    @Override
    public void disableSdk() {
        Braze.disableSdk(mApplication.getApplicationContext());
    }

    @Override
    public void wipeData() {
        Braze.wipeData(mApplication.getApplicationContext());
    }

    @Override
    public void setUserId(@NonNull String userId, @Nullable String sdkAuthSignature) {
        if (BrazeUtils.isNullOrEmpty(userId)) return;

        if (sdkAuthSignature != null) {
            getBrazeInstance().changeUser(userId, sdkAuthSignature);
        } else {
            getBrazeInstance().changeUser(userId);
        }
    }

    @Override
    public void setAdTrackingEnabled(@NonNull String googleAdid, boolean limitAdTracking) {
        getBrazeInstance().setGoogleAdvertisingId(googleAdid, limitAdTracking);
    }

    @Override
    public void setUserAlias(@NonNull String userAlias, @NonNull String aliasLabel) {
        if (BrazeUtils.isNullOrEmpty(userAlias) ||
                BrazeUtils.isNullOrEmpty(aliasLabel) ||
                getBrazeUser() == null) {
            return;
        }

        getBrazeUser().addAlias(userAlias, aliasLabel);
    }

    @Override
    public void setUserFirstName(@NonNull String firstName) {
        if (BrazeUtils.isNullOrEmpty(firstName) || getBrazeUser() == null) {
            return;
        }

        getBrazeUser().setFirstName(firstName);
    }

    @Override
    public void setUserLastName(@NonNull String lastName) {
        if (BrazeUtils.isNullOrEmpty(lastName) || getBrazeUser() == null) {
            return;
        }

        getBrazeUser().setLastName(lastName);
    }

    @Override
    public void setUserEmail(@NonNull String email) {
        if (BrazeUtils.isNullOrEmpty(email) || getBrazeUser() == null) {
            return;
        }

        getBrazeUser().setEmail(email);
    }

    @Override
    public void setUserLanguage(@NonNull String language) {
        if (BrazeUtils.isNullOrEmpty(language) || getBrazeUser() == null) {
            return;
        }

        getBrazeUser().setLanguage(language);
    }

    @Override
    public void setUserGender(@NonNull String genderString) {
        if (BrazeUtils.isNullOrEmpty(genderString) || getBrazeUser() == null) {
            return;
        }

        Gender gender = BrazeUtils.getGenderEnumFromString(genderString);
        if (gender == null) return;

        getBrazeUser().setGender(gender);
    }

    @Override
    public void setUserHomeCity(@NonNull String city) {
        if (BrazeUtils.isNullOrEmpty(city) || getBrazeUser() == null) {
            return;
        }

        getBrazeUser().setHomeCity(city);
    }

    @Override
    public void setUserCountry(@NonNull String country) {
        if (BrazeUtils.isNullOrEmpty(country) || getBrazeUser() == null) {
            return;
        }

        getBrazeUser().setCountry(country);
    }

    @Override
    public void setUserPhone(@NonNull String phone) {
        if (BrazeUtils.isNullOrEmpty(phone) || getBrazeUser() == null) {
            return;
        }

        getBrazeUser().setPhoneNumber(phone);
    }

    @Override
    public void setUserDateOfBirth(@NonNull String dob) {
        if (BrazeUtils.isNullOrEmpty(dob) || getBrazeUser() == null) {
            return;
        }

        Date dateOfBirth = BrazeUtils.parseDate(dob);
        if (dateOfBirth == null) return;

        getBrazeUser().setDateOfBirth(
                dateOfBirth.getYear() + 1900,
                BrazeUtils.getMonthEnumFromInt(dateOfBirth.getMonth()),
                dateOfBirth.getDate()
        );
    }

    @Override
    public void setUserCustomAttributes(@NonNull JSONObject attributes) {
        BrazeUser user = getBrazeUser();
        if (user == null || BrazeUtils.isNullOrEmpty(attributes)) {
            return;
        }

        Iterator<String> iter = attributes.keys();
        while (iter.hasNext()) {
            String key = iter.next();
            Object value = attributes.opt(key);

            if (BrazeUtils.isNullOrEmpty(key) || value == null) {
                continue;
            }

            if (value instanceof Long) {
                user.setCustomUserAttribute(key, (Long) value);
            } else if (value instanceof Integer) {
                user.setCustomUserAttribute(key, (Integer) value);
            } else if (value instanceof Double) {
                user.setCustomUserAttribute(key, (Double) value);
            } else if (value instanceof Float) {
                user.setCustomUserAttribute(key, (Float) value);
            } else if (value instanceof Boolean) {
                user.setCustomUserAttribute(key, (Boolean) value);
            } else if (value instanceof JSONArray) {
                user.setCustomUserAttribute(key, (JSONArray) value);
            } else if (value instanceof JSONObject) {
                user.setCustomUserAttribute(key, (JSONObject) value);
            } else {
                // default to String.
                user.setCustomUserAttribute(key, (String) value);
            }
        }
    }

    @Override
    public void unsetUserCustomAttributes(@NonNull JSONArray keys) {
        if (BrazeUtils.isNullOrEmpty(keys)) {
            return;
        }

        for (int i = 0; i < keys.length(); i++) {
            String key = keys.optString(i);
            if (key != null) {
                getBrazeUser().unsetCustomUserAttribute(key);
            }
        }
    }

    @Override
    public void incrementUserCustomAttributes(@NonNull JSONObject attributes) {
        if (BrazeUtils.isNullOrEmpty(attributes)) {
            return;
        }

        Iterator<String> iter = attributes.keys();
        while (iter.hasNext()) {
            String key = iter.next();
            if (BrazeUtils.isNullOrEmpty(key)) {
                continue;
            }

            int increment = attributes.optInt(key, 1);
            getBrazeUser().incrementCustomUserAttribute(key, increment);
        }
    }

    private void setUserCustomAttributeArray(String key, String[] attributeArray) {
        if (BrazeUtils.isNullOrEmpty(key)) {
            return;
        }

        getBrazeUser().setCustomAttributeArray(key, attributeArray);
    }

    @Override
    public void setUserCustomAttributeArrays(@NonNull JSONObject attributes) {
        if (BrazeUtils.isNullOrEmpty(attributes)) {
            return;
        }

        Iterator<String> iter = attributes.keys();
        while (iter.hasNext()) {
            String key = iter.next();
            JSONArray value = attributes.optJSONArray(key);
            if (value != null && value.length() > 0) {
                List<String> arrList = new ArrayList<>();
                for (int i = 0; i < value.length(); i++) {
                    String str = value.optString(i, null);
                    if (str != null) {
                        arrList.add(str);
                    }
                }

                setUserCustomAttributeArray(key, arrList.toArray(new String[0]));
            }
        }
    }

    @Override
    public void appendUserCustomAttributeArrays(@NonNull JSONObject attributes) {
        if (BrazeUtils.isNullOrEmpty(attributes)) {
            return;
        }

        Iterator<String> iter = attributes.keys();
        while (iter.hasNext()) {
            String key = iter.next();
            if (BrazeUtils.isNullOrEmpty(key)) {
                continue;
            }

            String value = attributes.optString(key);
            if (!BrazeUtils.isNullOrEmpty(value)) {
                getBrazeUser().addToCustomAttributeArray(key, value);
            }
        }
    }

    @Override
    public void removeFromUserCustomAttributeArrays(@NonNull JSONObject attributes) {
        if (BrazeUtils.isNullOrEmpty(attributes)) {
            return;
        }

        Iterator<String> iter = attributes.keys();
        while (iter.hasNext()) {
            String key = iter.next();
            if (BrazeUtils.isNullOrEmpty(key)) {
                continue;
            }

            String value = attributes.optString(key);
            if (!BrazeUtils.isNullOrEmpty(value)) {
                getBrazeUser().removeFromCustomAttributeArray(key, value);
            }
        }
    }

    @Override
    public void setPushNotificationSubscriptionType(@NonNull String notificationType) {
        if (BrazeUtils.isNullOrEmpty(notificationType)) {
            return;
        }

        getBrazeUser().setPushNotificationSubscriptionType(
                NotificationSubscriptionType.fromValue(notificationType)
        );
    }

    @Override
    public void setEmailSubscriptionType(@NonNull String notificationType) {
        if (BrazeUtils.isNullOrEmpty(notificationType)) {
            return;
        }

        getBrazeUser().setEmailNotificationSubscriptionType(
                NotificationSubscriptionType.fromValue(notificationType)
        );
    }

    @Override
    public void logCustomEvent(@NonNull String eventName, @Nullable JSONObject eventProperties) {
        BrazeProperties brazeProperties;
        if (eventProperties == null) {
            brazeProperties = null;
        } else {
            brazeProperties = BrazeUtils.extractCustomProperties(eventProperties);
        }

        // Braze api handles an empty BrazeProperties object, so no need to wrap both calls.
        getBrazeInstance().logCustomEvent(eventName, brazeProperties);
    }

    @Override
    public void logPurchase(@NonNull String productId, @Nullable String currency, @NonNull BigDecimal unitPrice, Integer quantity, JSONObject purchaseProperties) {
        if (BrazeUtils.isNullOrEmpty(currency)) {
            currency = "USD";// braze default.
        }

        getBrazeInstance().logPurchase(productId, currency, unitPrice, quantity > 0 ? quantity : 1, BrazeUtils.extractCustomProperties(purchaseProperties));
    }

    @Override
    public void logPurchase(@NonNull String[] productIds, String[] currencies, @NonNull BigDecimal[] unitPrices, Integer[] quantities, JSONObject[] purchaseProperties) {
        for (int i = 0; i < productIds.length; i++) {
            logPurchase(
                    productIds[i],
                    currencies != null && currencies.length > i ? currencies[i] : null,
                    unitPrices != null && unitPrices.length > i ? unitPrices[i] : BigDecimal.ZERO,
                    quantities != null && quantities.length > i ? quantities[i] : 1,
                    purchaseProperties != null && purchaseProperties.length > i ? purchaseProperties[i] : null
            );
        }
    }

    @Override
    public void requestFlush() {
        getBrazeInstance().requestImmediateDataFlush();
    }

    @Override
    public void setLastKnownLocation(@NonNull Double latitude, @NonNull Double longitude, @Nullable Double altitude, @Nullable Double accuracy) {
        getBrazeUser().setLastKnownLocation(
                latitude,
                longitude,
                altitude,
                accuracy
        );
    }

    @Override
    public void addToSubscriptionGroup(@NonNull String groupId) {
        getBrazeUser().addToSubscriptionGroup(groupId);
    }

    @Override
    public void removeFromSubscriptionGroup(@NonNull String groupId) {
        getBrazeUser().removeFromSubscriptionGroup(groupId);
    }

    @Override
    public void setSdkAuthSignature(@NonNull String signature) {
        getBrazeInstance().setSdkAuthenticationSignature(signature);
    }

    /**
     * Helper method to always fetch the current Braze User rather than storing a local variable
     *
     * @return The current Braze User
     */
    private BrazeUser getBrazeUser() {
        return getBrazeInstance().getCurrentUser();
    }

    /**
     * Helper method to always fetch the current Braze Instance rather than storing a local variable
     *
     * @return The shared Braze instance
     */
    private Braze getBrazeInstance() {
        return Braze.getInstance(mApplication.getApplicationContext());
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, Bundle savedInstanceState) {
        mCurrentActivity = activity;
    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {
        mCurrentActivity = activity;
    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
        mCurrentActivity = activity;
    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {

    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {

    }
}
