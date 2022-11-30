package com.tealium.remotecommands.braze;

import android.app.Activity;
import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class MockBrazeInstance extends BrazeInstance {

    public MockBrazeInstance(Application app, Activity activity) {
        super(app);
        // allow for overriding of activity for QA purposes
        if (mCurrentActivity == null) {
            mCurrentActivity = activity;
        }
    }

    public List<String> methodsCalled = new ArrayList<>();

    private void addCallerName() {
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        for (int i = 0; i < stackTraceElements.length; i++) {
            if (stackTraceElements[i].getClassName().contains("com.tealium") && !stackTraceElements[i].getMethodName().contains("addCallerName")) {
                methodsCalled.add(stackTraceElements[i].getMethodName());
                break;
            }
        }
    }

    @Override
    public void initialize(@NonNull String apiKey, JSONObject launchOptions, List<BrazeRemoteCommand.ConfigOverrider> overrides) {
        super.initialize(apiKey, launchOptions, overrides);
        addCallerName();
    }

    @Override
    public void enableSdk() {
        super.enableSdk();
        addCallerName();
    }

    @Override
    public void disableSdk() {
        super.disableSdk();
        addCallerName();
    }

    @Override
    public void wipeData() {
        super.wipeData();
        addCallerName();
    }

    @Override
    public void setUserId(String userId, @Nullable String sdkAuthSignature) {
        super.setUserId(userId, sdkAuthSignature);
        addCallerName();
    }

    @Override
    public void setUserAlias(String userAlias, String aliasLabel) {
        super.setUserAlias(userAlias, aliasLabel);
        addCallerName();
    }

    @Override
    public void setUserFirstName(String firstName) {
        super.setUserFirstName(firstName);
        addCallerName();
    }

    @Override
    public void setUserLastName(String lastName) {
        super.setUserLastName(lastName);
        addCallerName();
    }

    @Override
    public void setUserEmail(String email) {
        super.setUserEmail(email);
        addCallerName();
    }

    @Override
    public void setUserLanguage(String language) {
        super.setUserLanguage(language);
        addCallerName();
    }

    @Override
    public void setUserGender(String gender) {
        super.setUserGender(gender);
        addCallerName();
    }

    @Override
    public void setUserHomeCity(String city) {
        super.setUserHomeCity(city);
        addCallerName();
    }

    @Override
    public void setUserCustomAttribute(String key, String value) {
        super.setUserCustomAttribute(key, value);
        addCallerName();
    }

    @Override
    public void setUserCustomAttribute(String key, Integer value) {
        super.setUserCustomAttribute(key, value);
        addCallerName();
    }

    @Override
    public void setUserCustomAttribute(String key, Double value) {
        super.setUserCustomAttribute(key, value);
        addCallerName();
    }

    @Override
    public void setUserCustomAttribute(String key, Boolean value) {
        super.setUserCustomAttribute(key, value);
        addCallerName();
    }

    @Override
    public void setUserCustomAttribute(String key, Long value) {
        super.setUserCustomAttribute(key, value);
        addCallerName();
    }

    @Override
    public void setUserCustomAttribute(String key, Float value) {
        super.setUserCustomAttribute(key, value);
        addCallerName();
    }

    @Override
    public void setUserCustomAttributes(JSONObject attributes) {
        super.setUserCustomAttributes(attributes);
        addCallerName();
    }

    @Override
    public void unsetUserCustomAttribute(String key) {
        super.unsetUserCustomAttribute(key);
        addCallerName();
    }

    @Override
    public void unsetUserCustomAttributes(JSONArray keys) {
        super.unsetUserCustomAttributes(keys);
        addCallerName();
    }

    @Override
    public void incrementUserCustomAttribute(String key) {
        super.incrementUserCustomAttribute(key);
        addCallerName();
    }

    @Override
    public void incrementUserCustomAttribute(String key, Integer increment) {
        super.incrementUserCustomAttribute(key, increment);
        addCallerName();
    }

    @Override
    public void incrementUserCustomAttributes(JSONObject attributes) {
        super.incrementUserCustomAttributes(attributes);
        addCallerName();
    }

    @Override
    public void setUserCustomAttributeArray(String key, String[] attributeArray) {
        super.setUserCustomAttributeArray(key, attributeArray);
        addCallerName();
    }

    @Override
    public void setUserCustomAttributeArrays(JSONObject attributes) {
        super.setUserCustomAttributeArrays(attributes);
        addCallerName();
    }

    @Override
    public void appendUserCustomAttributeArray(String key, String value) {
        super.appendUserCustomAttributeArray(key, value);
        addCallerName();
    }

    @Override
    public void appendUserCustomAttributeArrays(JSONObject attributes) {
        super.appendUserCustomAttributeArrays(attributes);
        addCallerName();
    }

    @Override
    public void removeFromUserCustomAttributeArray(String key, String value) {
        super.removeFromUserCustomAttributeArray(key, value);
        addCallerName();
    }

    @Override
    public void removeFromUserCustomAttributeArrays(JSONObject attributes) {
        super.removeFromUserCustomAttributeArrays(attributes);
        addCallerName();
    }

    @Override
    public void setPushNotificationSubscriptionType(String notificationType) {
        super.setPushNotificationSubscriptionType(notificationType);
        addCallerName();
    }

    @Override
    public void setEmailSubscriptionType(String notificationType) {
        super.setEmailSubscriptionType(notificationType);
        addCallerName();
    }

    @Override
    public void logCustomEvent(@NonNull String eventName) {
        super.logCustomEvent(eventName);
        addCallerName();
    }

    @Override
    public void logCustomEvent(@NonNull String eventName, JSONObject eventProperties) {
        super.logCustomEvent(eventName, eventProperties);
        addCallerName();
    }

    @Override
    public void logPurchase(@NonNull String productId, String currency, @NonNull BigDecimal unitPrice) {
        super.logPurchase(productId, currency, unitPrice);
        addCallerName();
    }

    @Override
    public void logPurchase(@NonNull String productId, String currency, @NonNull BigDecimal unitPrice, Integer quantity) {
        super.logPurchase(productId, currency, unitPrice, quantity);
        addCallerName();
    }

    @Override
    public void logPurchase(@NonNull String productId, String currency, @NonNull BigDecimal unitPrice, Integer quantity, JSONObject purchaseProerties) {
        super.logPurchase(productId, currency, unitPrice, quantity, purchaseProerties);
        addCallerName();
    }

    @Override
    public void logPurchase(@NonNull String[] productIds, String[] currencies, @NonNull BigDecimal[] unitPrices, Integer[] quantities, JSONObject[] purchaseProerties) {
        super.logPurchase(productIds, currencies, unitPrices, quantities, purchaseProerties);
        addCallerName();
    }

    @Override
    public void requestFlush() {
        super.requestFlush();
        addCallerName();
    }

    @Override
    public void addToSubscriptionGroup(String groupId) {
        super.addToSubscriptionGroup(groupId);
        addCallerName();
    }

    @Override
    public void removeFromSubscriptionGroup(String groupId) {
        super.removeFromSubscriptionGroup(groupId);
        addCallerName();
    }

    @Override
    public void setUserCountry(String country) {
        super.setUserCountry(country);
        addCallerName();
    }

    @Override
    public void setUserPhone(String phone) {
        super.setUserPhone(phone);
        addCallerName();
    }

    @Override
    public void setUserDateOfBirth(String dob) {
        super.setUserDateOfBirth(dob);
        addCallerName();
    }

    @Override
    public void setLastKnownLocation(@NonNull Double latitude, @NonNull Double longitude, @Nullable Double altitude, @Nullable Double accuracy) {
        super.setLastKnownLocation(latitude, longitude, altitude, accuracy);
        addCallerName();
    }

    @Override
    public void setSdkAuthSignature(String signature) {
        super.setSdkAuthSignature(signature);
        addCallerName();
    }

    @Override
    public void setAdTrackingEnabled(String googleAdid, boolean limitAdTracking) {
        super.setAdTrackingEnabled(googleAdid, limitAdTracking);
        addCallerName();
    }
}
