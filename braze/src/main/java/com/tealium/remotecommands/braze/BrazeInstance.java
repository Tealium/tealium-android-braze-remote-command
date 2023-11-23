package com.tealium.remotecommands.braze;

import android.app.Activity;

import androidx.annotation.NonNull;

import android.app.Application;
import android.app.Application.ActivityLifecycleCallbacks;
import android.os.Bundle;
import android.util.Log;

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
import java.util.Iterator;
import java.util.List;
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
    public void initialize(String apiKey) {
        this.initialize(apiKey, null);
    }

    @Override
    public void initialize(String apiKey, JSONObject launchOptions) {
        this.initialize(apiKey, launchOptions, new ArrayList<BrazeRemoteCommand.ConfigOverrider>());
    }

    @Override
    public void initialize(String apiKey, JSONObject launchOptions, List<BrazeRemoteCommand.ConfigOverrider> overrides) {
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

            if (BrazeUtils.keyHasValue(launchOptions, Config.CUSTOM_ENDPOINT)) {
                builder.setCustomEndpoint(launchOptions.optString(Config.CUSTOM_ENDPOINT));
            }

            if (BrazeUtils.keyHasValue(launchOptions, Config.SMALL_NOTIFICATION_ICON)) {
                builder.setSmallNotificationIcon(launchOptions.optString(Config.SMALL_NOTIFICATION_ICON));
            }

            if (BrazeUtils.keyHasValue(launchOptions, Config.LARGE_NOTIFICATION_ICON)) {
                builder.setLargeNotificationIcon(launchOptions.optString(Config.LARGE_NOTIFICATION_ICON));
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

            if (BrazeUtils.keyHasValue(launchOptions, Config.ADM_ENABLED)) {
                builder.setAdmMessagingRegistrationEnabled(launchOptions.optBoolean(Config.ADM_ENABLED));
            }

            if (BrazeUtils.keyHasValue(launchOptions, Config.AUTO_PUSH_DEEP_LINKS)) {
                builder.setHandlePushDeepLinksAutomatically(launchOptions.optBoolean(Config.AUTO_PUSH_DEEP_LINKS));
            }

            if (BrazeUtils.keyHasValue(launchOptions, Config.DISABLE_LOCATION)) {
                builder.setIsLocationCollectionEnabled(!launchOptions.optBoolean(Config.DISABLE_LOCATION));
            }

            if (BrazeUtils.keyHasValue(launchOptions, Config.ENABLE_NEWS_FEED_INDICATOR)) {
                builder.setNewsfeedVisualIndicatorOn(launchOptions.optBoolean(Config.ENABLE_NEWS_FEED_INDICATOR));
            }

            if (BrazeUtils.keyHasValue(launchOptions, Config.ENABLE_GEOFENCES)) {
                builder.setGeofencesEnabled(launchOptions.optBoolean(Config.ENABLE_GEOFENCES));
            }

            if (BrazeUtils.keyHasValue(launchOptions, Config.BACKSTACK_ACTIVITY_ENABLED)) {
                builder.setPushDeepLinkBackStackActivityEnabled(launchOptions.optBoolean(Config.BACKSTACK_ACTIVITY_ENABLED));
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

            if (BrazeUtils.keyHasValue(launchOptions, Config.GOOD_NETWORK_INTERVAL)) {
                builder.setGoodNetworkDataFlushInterval(launchOptions.optInt(Config.GOOD_NETWORK_INTERVAL));
            }

            if (BrazeUtils.keyHasValue(launchOptions, Config.GREAT_NETWORK_INTERVAL)) {
                builder.setGreatNetworkDataFlushInterval(launchOptions.optInt(Config.GREAT_NETWORK_INTERVAL));
            }

            if (BrazeUtils.keyHasValue(launchOptions, Config.IS_SDK_AUTHENTICATION_ENABLED)) {
                builder.setIsSdkAuthenticationEnabled(launchOptions.optBoolean(Config.IS_SDK_AUTHENTICATION_ENABLED));
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
    public void enableSdk(Boolean enabled) {
        if (enabled) {
            Braze.enableSdk(mApplication.getApplicationContext());
        } else {
            Braze.disableSdk(mApplication.getApplicationContext());
        }
    }

    @Override
    public void wipeData() {
        Braze.wipeData(mApplication.getApplicationContext());
    }

    @Override
    public void setUserId(String userId) {
        if (userId != null) {
            getBrazeInstance().changeUser(userId);
        }
    }

    @Override
    public void setUserAlias(String userAlias, String aliasLabel) {
        if (BrazeUtils.isNullOrEmpty(userAlias) ||
                BrazeUtils.isNullOrEmpty(aliasLabel) ||
                getBrazeUser() == null) {
            return;
        }

        getBrazeUser().addAlias(userAlias, aliasLabel);
    }

    @Override
    public void setUserFirstName(String firstName) {
        if (BrazeUtils.isNullOrEmpty(firstName) || getBrazeUser() == null) {
            return;
        }

        getBrazeUser().setFirstName(firstName);
    }

    @Override
    public void setUserLastName(String lastName) {
        if (BrazeUtils.isNullOrEmpty(lastName) || getBrazeUser() == null) {
            return;
        }

        getBrazeUser().setLastName(lastName);
    }

    @Override
    public void setUserEmail(String email) {
        if (BrazeUtils.isNullOrEmpty(email) || getBrazeUser() == null) {
            return;
        }

        getBrazeUser().setEmail(email);
    }

    @Override
    public void setUserLanguage(String language) {
        if (BrazeUtils.isNullOrEmpty(language) || getBrazeUser() == null) {
            return;
        }

        getBrazeUser().setLanguage(language);
    }

    @Override
    public void setUserGender(String gender) {
        if (BrazeUtils.isNullOrEmpty(gender) || getBrazeUser() == null) {
            return;
        }

        getBrazeUser().setGender(
                BrazeUtils.getGenderEnumFromString(gender)
        );
    }

    @Override
    public void setUserHomeCity(String city) {
        if (BrazeUtils.isNullOrEmpty(city) || getBrazeUser() == null) {
            return;
        }

        getBrazeUser().setHomeCity(city);
    }

    @Override
    public void setUserCustomAttribute(String key, String value) {
        if (BrazeUtils.isNullOrEmpty(key)) {
            return;
        }

        getBrazeUser().setCustomUserAttribute(key, value);
    }

    @Override
    public void setUserCustomAttribute(String key, Integer value) {
        if (BrazeUtils.isNullOrEmpty(key)) {
            return;
        }

        getBrazeUser().setCustomUserAttribute(key, value);
    }

    @Override
    public void setUserCustomAttribute(String key, Double value) {
        if (BrazeUtils.isNullOrEmpty(key)) {
            return;
        }

        getBrazeUser().setCustomUserAttribute(key, value);
    }

    @Override
    public void setUserCustomAttribute(String key, Boolean value) {
        if (BrazeUtils.isNullOrEmpty(key)) {
            return;
        }

        getBrazeUser().setCustomUserAttribute(key, value);
    }

    @Override
    public void setUserCustomAttribute(String key, Long value) {
        if (BrazeUtils.isNullOrEmpty(key)) {
            return;
        }

        getBrazeUser().setCustomUserAttribute(key, value);
    }

    @Override
    public void setUserCustomAttribute(String key, Float value) {
        if (BrazeUtils.isNullOrEmpty(key)) {
            return;
        }

        getBrazeUser().setCustomUserAttribute(key, value);
    }

    @Override
    public void setUserCustomAttributes(JSONObject attributes) {

        Iterator<String> iter = attributes.keys();
        while (iter.hasNext()) {
            String key = iter.next();
            Object value = attributes.opt(key);

            if (value != null) {
                if (value instanceof Long) {
                    setUserCustomAttribute(key, (Long) value);
                } else if (value instanceof Integer) {
                    setUserCustomAttribute(key, (Integer) value);
                } else if (value instanceof Double) {
                    setUserCustomAttribute(key, (Double) value);
                } else if (value instanceof Float) {
                    setUserCustomAttribute(key, (Float) value);
                } else if (value instanceof Boolean) {
                    setUserCustomAttribute(key, (Boolean) value);
                } else {
                    // default to String.
                    setUserCustomAttribute(key, (String) value);
                }
            }
        }
    }

    @Override
    public void unsetUserCustomAttribute(String key) {
        if (BrazeUtils.isNullOrEmpty(key)) {
            return;
        }

        getBrazeUser().unsetCustomUserAttribute(key);
    }

    @Override
    public void unsetUserCustomAttributes(JSONArray keys) {
        if (BrazeUtils.isNullOrEmpty(keys)) {
            return;
        }

        for (int i = 0; i < keys.length(); i++) {
            String key = keys.optString(i);
            if (key != null) {
                unsetUserCustomAttribute(key);
            }
        }
    }

    @Override
    public void incrementUserCustomAttribute(String key) {
        incrementUserCustomAttribute(key, 1);
    }

    @Override
    public void incrementUserCustomAttribute(String key, Integer increment) {
        if (BrazeUtils.isNullOrEmpty(key)) {
            return;
        }

        getBrazeUser().incrementCustomUserAttribute(key, increment);
    }

    @Override
    public void incrementUserCustomAttributes(JSONObject attributes) {
        if (BrazeUtils.isNullOrEmpty(attributes)) {
            return;
        }

        Iterator<String> iter = attributes.keys();
        while (iter.hasNext()) {
            String key = iter.next();
            Integer value = attributes.optInt(key, 1);

            incrementUserCustomAttribute(key, value);
        }
    }

    @Override
    public void setUserCustomAttributeArray(String key, String[] attributeArray) {
        if (BrazeUtils.isNullOrEmpty(key)) {
            return;
        }

        getBrazeUser().setCustomAttributeArray(key, attributeArray);
    }

    @Override
    public void setUserCustomAttributeArrays(JSONObject attributes) {
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
    public void appendUserCustomAttributeArray(String key, String value) {
        if (BrazeUtils.isNullOrEmpty(key)) {
            return;
        }

        getBrazeUser().addToCustomAttributeArray(key, value);
    }

    @Override
    public void appendUserCustomAttributeArrays(JSONObject attributes) {
        if (BrazeUtils.isNullOrEmpty(attributes)) {
            return;
        }

        Iterator<String> iter = attributes.keys();
        while (iter.hasNext()) {
            String key = iter.next();
            String value = attributes.optString(key);

            if (value != null) {
                appendUserCustomAttributeArray(key, value);
            }
        }
    }

    @Override
    public void removeFromUserCustomAttributeArray(String key, String value) {
        if (BrazeUtils.isNullOrEmpty(key)) {
            return;
        }

        getBrazeUser().removeFromCustomAttributeArray(key, value);
    }

    @Override
    public void removeFromUserCustomAttributeArrays(JSONObject attributes) {
        if (BrazeUtils.isNullOrEmpty(attributes)) {
            return;
        }

        Iterator<String> iter = attributes.keys();
        while (iter.hasNext()) {
            String key = iter.next();
            String value = attributes.optString(key);

            if (value != null) {
                removeFromUserCustomAttributeArray(key, value);
            }
        }
    }

    @Override
    public void setPushNotificationSubscriptionType(String notificationType) {
        if (BrazeUtils.isNullOrEmpty(notificationType)) {
            return;
        }

        getBrazeUser().setPushNotificationSubscriptionType(
                NotificationSubscriptionType.valueOf(notificationType)
        );
    }

    @Override
    public void setEmailSubscriptionType(String notificationType) {
        if (BrazeUtils.isNullOrEmpty(notificationType)) {
            return;
        }

        getBrazeUser().setEmailNotificationSubscriptionType(
                NotificationSubscriptionType.valueOf(notificationType)
        );
    }

    @Override
    public void setFacebookData(String facebookId,
                                String firstName,
                                String lastName,
                                String email,
                                String bio,
                                String cityName,
                                String gender,
                                Integer numberOfFriends,
                                JSONArray listOfLikes,
                                String birthday) {
        // TODO - remove
    }

    @Override
    public void setTwitterData(Integer twitterUserId,
                               String twitterHandle,
                               String name,
                               String description,
                               Integer followerCount,
                               Integer followingCount,
                               Integer tweetCount,
                               String profileImageUrl) {
        // TODO - remove
     }

    @Override
    public void logCustomEvent(@NonNull String eventName) {
        logCustomEvent(eventName, null);
    }

    @Override
    public void logCustomEvent(@NonNull String eventName, JSONObject eventProperties) {
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
    public void logPurchase(@NonNull String productId, String currency, @NonNull BigDecimal unitPrice) {
        logPurchase(productId, currency, unitPrice, 1, null);
    }

    @Override
    public void logPurchase(@NonNull String productId, String currency, @NonNull BigDecimal unitPrice, Integer quantity) {
        logPurchase(productId, currency, unitPrice, quantity, null);
    }

    @Override
    public void logPurchase(@NonNull String productId, String currency, @NonNull BigDecimal unitPrice, Integer quantity, JSONObject purchaseProperties) {
        if (BrazeUtils.isNullOrEmpty(currency)) {
            currency = "USD";// braze default.
        }

        getBrazeInstance().logPurchase(productId, currency, unitPrice, quantity > 0 ? quantity : 1, BrazeUtils.extractCustomProperties(purchaseProperties));
    }

    @Override
    public void logPurchase(@NonNull String[] productIds, String[] currencies, @NonNull BigDecimal[] unitPrices, Integer[] quantities, JSONObject[] purchaseProperties) {
        if (productIds == null) {
            Log.i(TAG, "Missing productIds array in purchase event. No purchase will be logged.");
            return;
        }
        for (int i = 0; i < productIds.length; i++) {
            logPurchase(
                    productIds[i],
                    currencies != null && currencies.length > i ? currencies[i] : null,
                    unitPrices != null && unitPrices.length > i ? unitPrices[i] : new BigDecimal(0),
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
    public void registerToken(String token) {
        if (token == null) return;

        getBrazeInstance().setRegisteredPushToken(token);
    }

    @Override
    public void addToSubscriptionGroup(String groupId) {
        if (groupId == null) return;

        getBrazeUser().addToSubscriptionGroup(groupId);
    }

    @Override
    public void removeFromSubscriptionGroup(String groupId) {
        if (groupId == null) return;

        getBrazeUser().removeFromSubscriptionGroup(groupId);
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
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }
}
