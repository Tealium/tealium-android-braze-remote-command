package com.tealium.remotecommands.braze;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.util.Log;

import com.appboy.Appboy;
import com.appboy.AppboyLifecycleCallbackListener;
import com.appboy.AppboyUser;
import com.appboy.configuration.AppboyConfig;
import com.appboy.enums.NotificationSubscriptionType;
import com.appboy.models.outgoing.AppboyProperties;
import com.appboy.models.outgoing.FacebookUser;
import com.appboy.models.outgoing.TwitterUser;

import com.tealium.library.Tealium;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import static com.tealium.remotecommands.braze.BrazeConstants.TAG;
import static com.tealium.remotecommands.braze.BrazeConstants.Config;


class BrazeWrapperImpl implements BrazeWrapper {

    Tealium.Config mTealiumConfig;
    Activity mCurrentActivity;

    public BrazeWrapperImpl(Tealium.Config config) {
        this(config, true);
    }

    public BrazeWrapperImpl(Tealium.Config config, boolean registerInAppMessageManager) {
        this.mTealiumConfig = config;

        mTealiumConfig.getApplication().registerActivityLifecycleCallbacks(new AppboyLifecycleCallbackListener(true, registerInAppMessageManager));
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
        AppboyConfig.Builder builder = new AppboyConfig.Builder();

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
                builder.setDisableLocationCollection(launchOptions.optBoolean(Config.DISABLE_LOCATION));
            }

            if (BrazeUtils.keyHasValue(launchOptions, Config.ENABLE_NEWS_FEED_INDICATOR)) {
                builder.setNewsfeedVisualIndicatorOn(launchOptions.optBoolean(Config.ENABLE_NEWS_FEED_INDICATOR));
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

            if (BrazeUtils.keyHasValue(launchOptions, Config.LOCALE_MAPPING)) {
                try {
                    List<String> localeMapping = null;

                    Object obj = launchOptions.get(Config.LOCALE_MAPPING);
                    if (obj instanceof JSONArray) {
                        JSONArray jsonArray = (JSONArray) obj;
                        List<String> temp = new LinkedList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            temp.add(jsonArray.getString(i));
                        }
                        localeMapping = temp;
                    } else if (obj instanceof List) {
                        localeMapping = (List<String>) obj;
                    }

                    if (localeMapping != null && !localeMapping.isEmpty()) {
                        builder.setLocaleToApiMapping(localeMapping);
                    }

                } catch (JSONException jsEx) {
                    Log.w(TAG, "Error retrieving locale mapping list from JSON. " + jsEx.getMessage());
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
        Appboy.configure(mTealiumConfig.getApplication().getApplicationContext(), builder.build());
    }

    @Override
    public void enableSdk(Boolean enabled) {
        if (enabled) {
            Appboy.enableSdk(mTealiumConfig.getApplication().getApplicationContext());
        } else {
            Appboy.disableSdk(mTealiumConfig.getApplication().getApplicationContext());
        }
    }

    @Override
    public void wipeData() {
        Appboy.wipeData(mTealiumConfig.getApplication().getApplicationContext());
    }

    @Override
    public void setUserId(String userId) {
        if (userId != null && !getAppboyUser().getUserId().equalsIgnoreCase(userId)) {
            getAppboyInstance().changeUser(userId);
        }
    }

    @Override
    public void setUserAlias(String userAlias, String aliasLabel) {
        if (BrazeUtils.isNullOrEmpty(userAlias) || BrazeUtils.isNullOrEmpty(aliasLabel)) {
            return;
        }

        getAppboyUser().addAlias(userAlias, aliasLabel);
    }

    @Override
    public void setUserFirstName(String firstName) {
        if (BrazeUtils.isNullOrEmpty(firstName)) {
            return;
        }

        getAppboyUser().setFirstName(firstName);
    }

    @Override
    public void setUserLastName(String lastName) {
        if (BrazeUtils.isNullOrEmpty(lastName)) {
            return;
        }

        getAppboyUser().setLastName(lastName);
    }

    @Override
    public void setUserEmail(String email) {
        if (BrazeUtils.isNullOrEmpty(email)) {
            return;
        }

        getAppboyUser().setEmail(email);
    }

    @Override
    public void setUserLanguage(String language) {
        if (BrazeUtils.isNullOrEmpty(language)) {
            return;
        }

        getAppboyUser().setLanguage(language);
    }

    @Override
    public void setUserGender(String gender) {
        if (BrazeUtils.isNullOrEmpty(gender)) {
            return;
        }

        getAppboyUser().setGender(
                BrazeUtils.getGenderEnumFromString(gender)
        );
    }

    @Override
    public void setUserHomeCity(String city) {
        if (BrazeUtils.isNullOrEmpty(city)) {
            return;
        }

        getAppboyUser().setHomeCity(city);
    }

    @Override
    public void setUserCustomAttribute(String key, String value) {
        if (BrazeUtils.isNullOrEmpty(key)) {
            return;
        }

        getAppboyUser().setCustomUserAttribute(key, value);
    }

    @Override
    public void setUserCustomAttribute(String key, Integer value) {
        if (BrazeUtils.isNullOrEmpty(key)) {
            return;
        }

        getAppboyUser().setCustomUserAttribute(key, value);
    }

    @Override
    public void setUserCustomAttribute(String key, Double value) {
        if (BrazeUtils.isNullOrEmpty(key)) {
            return;
        }

        getAppboyUser().setCustomUserAttribute(key, value);
    }

    @Override
    public void setUserCustomAttribute(String key, Boolean value) {
        if (BrazeUtils.isNullOrEmpty(key)) {
            return;
        }

        getAppboyUser().setCustomUserAttribute(key, value);
    }

    @Override
    public void setUserCustomAttribute(String key, Long value) {
        if (BrazeUtils.isNullOrEmpty(key)) {
            return;
        }

        getAppboyUser().setCustomUserAttribute(key, value);
    }

    @Override
    public void setUserCustomAttribute(String key, Float value) {
        if (BrazeUtils.isNullOrEmpty(key)) {
            return;
        }

        getAppboyUser().setCustomUserAttribute(key, value);
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

        getAppboyUser().unsetCustomUserAttribute(key);
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

        getAppboyUser().incrementCustomUserAttribute(key, increment);
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

        getAppboyUser().setCustomAttributeArray(key, attributeArray);
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

        getAppboyUser().addToCustomAttributeArray(key, value);
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

        getAppboyUser().removeFromCustomAttributeArray(key, value);
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

        getAppboyUser().setPushNotificationSubscriptionType(
                NotificationSubscriptionType.valueOf(notificationType)
        );
    }

    @Override
    public void setEmailSubscriptionType(String notificationType) {
        if (BrazeUtils.isNullOrEmpty(notificationType)) {
            return;
        }

        getAppboyUser().setEmailNotificationSubscriptionType(
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
        List<String> likes = new ArrayList<>();
        if (listOfLikes != null && listOfLikes.length() > 0) {
            for (int i = 0; i < listOfLikes.length(); i++) {
                String s = listOfLikes.optString(i, null);
                if (s != null) {
                    likes.add(s);
                }
            }
        }

        FacebookUser facebookUser = new FacebookUser(
                facebookId,
                firstName,
                lastName,
                email,
                bio,
                cityName,
                BrazeUtils.getGenderEnumFromString(gender),
                numberOfFriends != -1 ? numberOfFriends : null,
                likes,
                birthday
        );
        getAppboyUser().setFacebookData(facebookUser);

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

        TwitterUser twitterUser = new TwitterUser(twitterUserId,
                twitterHandle,
                name,
                description,
                followerCount != -1 ? followerCount : null,
                followingCount != -1 ? followingCount : null,
                tweetCount != -1 ? tweetCount : null,
                profileImageUrl);
        getAppboyUser().setTwitterData(twitterUser);
    }


    @Override
    public void logCustomEvent(@NonNull String eventName) {
        logCustomEvent(eventName, null);
    }

    @Override
    public void logCustomEvent(@NonNull String eventName, JSONObject eventProperties) {
        AppboyProperties appboyProperties;
        if (eventProperties == null) {
            appboyProperties = null;
        } else {
            appboyProperties = BrazeUtils.extractCustomProperties(eventProperties);
        }

        // appboy api handles an empty AppboyProperties object, so no need to wrap both calls.
        getAppboyInstance().logCustomEvent(eventName, appboyProperties);
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
    public void logPurchase(@NonNull String productId, String currency, @NonNull BigDecimal unitPrice, Integer quantity, JSONObject purchaseProerties) {
        if (BrazeUtils.isNullOrEmpty(currency)) {
            currency = "USD";// braze default.
        }

        getAppboyInstance().logPurchase(productId, currency, unitPrice, quantity, BrazeUtils.extractCustomProperties(purchaseProerties));
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
                    unitPrices != null && unitPrices.length > i ? unitPrices[i] : null,
                    quantities != null && quantities.length > i ? quantities[i] : 1,
                    purchaseProperties != null && purchaseProperties.length > i ? purchaseProperties[i] : null
            );
        }
    }

    @Override
    public void requestFlush() {
        getAppboyInstance().requestImmediateDataFlush();
    }

    /**
     * Helper method to always fetch the current Appboy User rather than storing a local variable
     *
     * @return
     */
    private AppboyUser getAppboyUser() {
        return getAppboyInstance().getCurrentUser();
    }

    /**
     * Helper method to always fetch the current Appboy Instance rather than storing a local variable
     *
     * @return
     */
    private Appboy getAppboyInstance() {
        return Appboy.getInstance(mTealiumConfig.getApplication().getApplicationContext());
    }
}