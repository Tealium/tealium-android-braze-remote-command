package com.tealium.remotecommands.braze;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.tealium.remotecommands.braze.BrazeRemoteCommand.ConfigOverrider;

import org.json.JSONArray;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.List;


/**
 * This interface represents the tasks that are expected to be used by our Tealium Integration via
 * Remote Commands. Many parameters in the methods take non-type-safe classes like
 * JSONObject and JSONArray, these are what will be provided by the RemoteCommand.Response
 * in the onInvoke method.
 * <p>
 * As a result the key-value pairings are important, and all supported keys are documented in
 * com.tealium.remotecommands.braze.BrazeConstants and its contained public classes.
 * Where a key-value pair is expected in a JSONObject,  this should be documented in the method's
 * Javadoc and whether there is any specific type expected for the values.
 */
interface BrazeCommand {

    /**
     * Configures a new Braze instance, setting the Braze API Key and any other configuration items
     * passed in the launchOptions parameters. This should be key-value pairs, it will look for the
     * keys as specified in the static class BrazeConstants.Config.
     * After building any launchOptions it will execute any overrides before creating the  Braze
     * instance.
     *
     * @param apiKey The Braze API Key
     * @param launchOptions An optional JSONObject of mapped key-value options
     * @param overrides An optional list of programmatic config overrides
     */
    void initialize(@Nullable String apiKey, @Nullable JSONObject launchOptions, @Nullable List<ConfigOverrider> overrides);

    /**
     * Sets the Braze SDK to enabled.
     */
    void enableSdk();

    /**
     * Sets the Braze SDK to disabled.
     */
    void disableSdk();

    /**
     * Executes Braze's wipeData function to clear any user data stored on the device.
     */
    void wipeData();

    /**
     * Calls the changeUser method to switch which Braze User any subsequent events are related to.
     *
     * @param userId The new user identifier
     * @param sdkAuthSignature Optional sdk authentication signature
     */
    void setUserId(@NonNull String userId, @Nullable String sdkAuthSignature);

    /**
     * Sets the Google ADID and sets whether or not to limit tracking
     *
     * @param googleAdid The Google Ad Identifier
     * @param limitAdTracking Whether ad tracking is limited or not
     */
    void setAdTrackingEnabled(@NonNull String googleAdid, boolean limitAdTracking);

    /**
     * Registers an Alias and Alias Label for the current user. Neither parameter should be blank
     * or null else the method will return without doing anything.
     *
     * @param userAlias The alias to set for the current user
     * @param aliasLabel The label for this alias
     */
    void setUserAlias(@NonNull String userAlias, @NonNull String aliasLabel);

    /**
     * Sets the First Name attribute for the current user in Braze.
     *
     * @param firstName The current user's first name
     */
    void setUserFirstName(@NonNull String firstName);

    /**
     * Sets the Last Name attribute for the current user in Braze.
     *
     * @param lastName The current user's last name
     */
    void setUserLastName(@NonNull String lastName);

    /**
     * Sets the Email Address attribute for the current user in Braze.
     *
     * @param email The current user's email
     */
    void setUserEmail(@NonNull String email);

    /**
     * Sets the Language attribute for the current user in Braze.
     *
     * @param language The current user's language
     */
    void setUserLanguage(@NonNull String language);

    /**
     * Sets the Gender attribute for the current user in Braze.
     * Will attempt to convert the string representation of the Gender into the Braze Enum before
     * doing anything.
     * <p>
     * (m)ale = Gender.MALE
     * (f)emale = Gender.FEMALE
     * (o)ther = Gender.OTHER
     * na/not_applicable = Gender.NOT_APPLICABLE
     * no/prefer_not_to_say = Gender.PREFER_NOT_TO_SAY
     * unknown = Gender.UNKNOWN
     * <p>
     * else returns null
     *
     * @param gender The current user's gender as a string.
     */
    void setUserGender(@NonNull String gender);

    /**
     * Sets the Home City attribute for the current user in Braze.
     *
     * @param city The current user's home city
     */
    void setUserHomeCity(@NonNull String city);

    /**
     * Sets the Country attribute for the current user in Braze.
     *
     * @param country The current user's country
     */
    void setUserCountry(@NonNull String country);

    /**
     * Sets the Phone attribute for the current user in Braze.
     *
     * @param phone The current user's phone
     */
    void setUserPhone(@NonNull String phone);

    /**
     * Sets the Date of Birth for the current user in Braze.
     *
     * @param dob The current user's date of birth
     */
    void setUserDateOfBirth(@NonNull String dob);

    /**
     * Sets the Push Notification Subscription Type for the current user in Braze.
     * Uses Braze's helper method to convert from String to Enum:
     * NotificationSubscriptionType.valueOf(String notificationType)
     *
     * @param notificationType The new push notification subscription type
     */
    void setPushNotificationSubscriptionType(@NonNull String notificationType);

    /**
     * Sets the Email Notification Subscription Type for the current user in Braze.
     * Uses Braze's helper method to convert from String to Enum:
     * NotificationSubscriptionType.valueOf(String notificationType)
     *
     * @param notificationType  The new email notification subscription type
     */
    void setEmailSubscriptionType(@NonNull String notificationType);

    /**
     * Helper method that will take each key-value pair in the attributes parameter and attempt to
     * set a Custom Attribute for each based on their type.
     * e.g. {
     * "attribute_key_name_1" : "some string value",
     * "attribute_key_name_2" : 10 // ...etc
     * }
     *
     * @param attributes The custom attributes to assign to the current user
     */
    void setUserCustomAttributes(@NonNull JSONObject attributes);

    /**
     * Helper method that will take each entry in the Array and attempt to unset the attribute with
     * the key name based on the value in the array.
     * e.g. [
     * key_1,
     * key_2
     * ]
     *
     * @param keys - keys is expected to be an array of strings
     */
    void unsetUserCustomAttributes(@NonNull JSONArray keys);

    /**
     * Helper method that will take each key-value pair and attempt to increment the attributes
     * named by the keys, by the given value.
     * e.g. {
     * "attribute_key_name_1" : 5,
     * "attribute_key_name_2" : 1
     * }
     *
     * @param attributes - values for each key should be an Integer.
     */
    void incrementUserCustomAttributes(@NonNull JSONObject attributes);

    /**
     * Helper method that will take each key value pair and attempt to set a Custom Array Attribute
     * for each one, where the key will be the attribute name, and the value will be the array of
     * Strings to set.
     * e.g. {
     * "array_attribute_name_1" : [
     * "String_1",
     * "String_2"
     * ],
     * "array_attribute_name_2" : [
     * "String_1",
     * "String_2"
     * ]
     * }
     *
     * @param attributes - values are expected to be JSONArrays of strings
     */
    void setUserCustomAttributeArrays(@NonNull JSONObject attributes);

    /**
     * Helper method that will take each key-value pair and attempt to append the value to the array
     * attribute named by key.
     * e.g. {
     * "array_attribute_name_1" : "string value",
     * "array_attribute_name_1" : "other value",
     * }
     *
     * @param attributes - Keys and Values are expected to be strings.
     */
    void appendUserCustomAttributeArrays(@NonNull JSONObject attributes);

    /**
     * Helper method that will take each key-value pair and attempt to remove teh value from the
     * attribute named by the keys.
     * e.g. {
     * "array_attribute_name_1" : "string value to remove",
     * "array_attribute_name_1" : "other string value to remove"
     * }
     *
     * @param attributes A list of attribute names and their values to remove from custom user array attributes
     */
    void removeFromUserCustomAttributeArrays(@NonNull JSONObject attributes);

    /**
     * Logs a custom event with the given event name, and the custom properties as described by the
     * key-value pairs in the eventProperties parameter.
     * e.g. {
     * "custom_prop_1" : "custom string value",
     * "custom_prop_2" : 10 // ...etc
     * }
     *
     * @param eventName The event name
     * @param eventProperties The optional properties that accompany this custom event
     */
    void logCustomEvent(@NonNull String eventName, @Nullable JSONObject eventProperties);

    /**
     * Logs a purchase event with the provided productId, currency, unitPrice, quantity and any
     * custom purchase properties described by key-value pairs in the purchaseProperties param
     *
     * @param productId          - productId of the product being purchased
     * @param currency           - currency used by this purchase; default = "USD"
     * @param unitPrice          - unit price of the product.
     * @param quantity           - number of units purchased
     * @param purchaseProperties - key-value pairs describing the custom purchase properties
     *                           e.g. {
     *                           "custom_property_1" : "some string value",
     *                           "custom_property_2" : 10,
     *                           "custom_property_3" : false
     *                           }
     */
    void logPurchase(@NonNull String productId, @Nullable String currency, @NonNull BigDecimal unitPrice, Integer quantity, @Nullable JSONObject purchaseProperties);

    /**
     * Helper method that will take each bit of purchase information and attempt to log multiple
     * purchases. Each array should be matched in length.
     *
     * @param productIds An array of product ids for this purchase
     * @param currencies An array of currencies for this purchase
     * @param unitPrices An array of unit prices for this purchase
     * @param quantities An array of quantities for this purchase
     * @param purchaseProperties Any optional properties to accomapny this purchase event
     */
    void logPurchase(@NonNull String[] productIds, @Nullable String[] currencies, @NonNull BigDecimal[] unitPrices, Integer[] quantities, @Nullable JSONObject[] purchaseProperties);

    /**
     * Requests an immediate flush of any queued up events within the Braze SDK.
     */
    void requestFlush();

    /**
     * Adds the current BrazeUser to a subscription group
     *
     * @param groupId The id of the group to be subscribed to
     */
    void addToSubscriptionGroup(@NonNull String groupId);

    /**
     * Removes the current BrazeUser from a subscription group
     *
     * @param groupId The id of the group to be unsubscribed to
     */
    void removeFromSubscriptionGroup(@NonNull String groupId);

    /**
     * Sets the SDK Auth Signature
     *
     * @param signature The SDK authentication signature to set
     */
    void setSdkAuthSignature(@NonNull String signature);

    /**
     * Sets the last known location of the BrazeUser
     *
     * @param latitude The latitude of the users last known location
     * @param longitude The longitude of the users last known location
     * @param altitude Optional altitude of the users last known location
     * @param accuracy Optional accuracy of the users last known location
     * @param accuracy Optional accuracy of the users last known location
     */
    void setLastKnownLocation(@NonNull Double latitude, @NonNull Double longitude, @Nullable Double altitude, @Nullable Double accuracy);
}
