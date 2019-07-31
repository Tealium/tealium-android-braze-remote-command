package com.tealium.remotecommands.braze;

import androidx.annotation.NonNull;

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
interface BrazeWrapper {

    /**
     * Configures a new Braze instance, setting just the Braze API Key and no other configuration
     *
     * @param apiKey
     */
    void initialize(String apiKey);

    /**
     * Configures a new Braze instance, setting the Braze API Key and any other configuration items
     * passed in the launchOptions parameters. This should be key-value pairs, it will look for the
     * keys as specified in the static class BrazeConstants.Config.
     *
     * @param apiKey
     */
    void initialize(String apiKey, JSONObject launchOptions);

    /**
     * Configures a new Braze instance, setting the Braze API Key and any other configuration items
     * passed in the launchOptions parameters. This should be key-value pairs, it will look for the
     * keys as specified in the static class BrazeConstants.Config.
     * After building any launchOptions it will execute any overrides before creating the Appboy
     * instance.
     *
     * @param apiKey
     */
    void initialize(String apiKey, JSONObject launchOptions, List<ConfigOverrider> overrides);

    /**
     * Sets whether or not the Braze SDK is enabled or not.
     *
     * @param enabled - a value of false will disable the SDK, a value of true will enable it.
     */
    void enableSdk(Boolean enabled);

    /**
     * Executes Braze's wipeData function to clear any user data stored on the device.
     */
    void wipeData();

    /**
     * Calls the changeUser method to switch which Braze User any subsequent events are related to.
     *
     * @param userId
     */
    void setUserId(String userId);

    /**
     * Registers an Alias and Alias Label for the current user. Neither parameter should be blank
     * or null else the method will return without doing anything.
     *
     * @param userAlias
     * @param aliasLabel
     */
    void setUserAlias(String userAlias, String aliasLabel);

    /**
     * Sets the First Name attribute for the current user in Braze.
     *
     * @param firstName
     */
    void setUserFirstName(String firstName);

    /**
     * Sets the Last Name attribute for the current user in Braze.
     *
     * @param lastName
     */
    void setUserLastName(String lastName);

    /**
     * Sets the Email Address attribute for the current user in Braze.
     *
     * @param email
     */
    void setUserEmail(String email);

    /**
     * Sets the Language attribute for the current user in Braze.
     *
     * @param language
     */
    void setUserLanguage(String language);

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
     * @param gender
     */
    void setUserGender(String gender);

    /**
     * Sets the Home City attribute for the current user in Braze.
     *
     * @param city
     */
    void setUserHomeCity(String city);

    /**
     * Sets the Push Notification Subscription Type for the current user in Braze.
     * Uses Braze's helper method to convert from String to Enum:
     * NotificationSubscriptionType.valueOf(String notificationType)
     *
     * @param notificationType
     */
    void setPushNotificationSubscriptionType(String notificationType);

    /**
     * Sets the Email Notification Subscription Type for the current user in Braze.
     * Uses Braze's helper method to convert from String to Enum:
     * NotificationSubscriptionType.valueOf(String notificationType)
     *
     * @param notificationType
     */
    void setEmailSubscriptionType(String notificationType);

    /**
     * Sets the Facebook Account Data for this current user in Braze
     *
     * @param facebookId
     * @param firstName
     * @param lastName
     * @param email
     * @param bio
     * @param cityName
     * @param gender
     * @param numberOfFriends
     * @param listOfLikes
     * @param birthday
     */
    void setFacebookData(String facebookId,
                         String firstName,
                         String lastName,
                         String email,
                         String bio,
                         String cityName,
                         String gender,
                         Integer numberOfFriends,
                         JSONArray listOfLikes,
                         String birthday);

    /**
     * Sets the Twitter Account Data for this current user in Braze
     *
     * @param twitterUserId
     * @param twitterHandle
     * @param name
     * @param description
     * @param followerCount
     * @param followingCount
     * @param tweetCount
     * @param profileImageUrl
     */
    void setTwitterData(Integer twitterUserId,
                        String twitterHandle,
                        String name,
                        String description,
                        Integer followerCount,
                        Integer followingCount,
                        Integer tweetCount,
                        String profileImageUrl);

    /**
     * Sets a Custom String Attribute for the current user in Braze
     *
     * @param key   - should not be null or empty
     * @param value
     */
    void setUserCustomAttribute(String key, String value);

    /**
     * Sets a Custom Integer Attribute for the current user in Braze
     *
     * @param key   - should not be null or empty
     * @param value
     */
    void setUserCustomAttribute(String key, Integer value);

    /**
     * Sets a Custom Double Attribute for the current user in Braze
     *
     * @param key   - should not be null or empty
     * @param value
     */
    void setUserCustomAttribute(String key, Double value);

    /**
     * Sets a Custom Boolean Attribute for the current user in Braze
     *
     * @param key   - should not be null or empty
     * @param value
     */
    void setUserCustomAttribute(String key, Boolean value);

    /**
     * Sets a Custom Long Attribute for the current user in Braze
     *
     * @param key   - should not be null or empty
     * @param value
     */
    void setUserCustomAttribute(String key, Long value);

    /**
     * Sets a Custom Float Attribute for the current user in Braze
     *
     * @param key   - should not be null or empty
     * @param value
     */
    void setUserCustomAttribute(String key, Float value);

    /**
     * Helper method that will take each key-value pair in the attributes parameter and attempt to
     * set a Custom Attribute for each based on their type.
     * e.g. {
     * "attribute_key_name_1" : "some string value",
     * "attribute_key_name_2" : 10 // ...etc
     * }
     *
     * @param attributes
     */
    void setUserCustomAttributes(JSONObject attributes);

    /**
     * Unsets a Custom Attribute for the given key name.
     *
     * @param key
     */
    void unsetUserCustomAttribute(String key);

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
    void unsetUserCustomAttributes(JSONArray keys);

    /**
     * Increments a custom attribute by 1.
     *
     * @param key
     */
    void incrementUserCustomAttribute(String key);

    /**
     * Increments the custom attribute by the given increment, named by the given key.
     *
     * @param key       - key name of the attribute to to be incremented.
     * @param increment - value to increment by; default = 1
     */
    void incrementUserCustomAttribute(String key, Integer increment);

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
    void incrementUserCustomAttributes(JSONObject attributes);

    /**
     * Sets a Custom Array Attribute. Array Attributes always contain Strings according to Braze.
     *
     * @param key            - The attribute name to set.
     * @param attributeArray - The array of strings to set in this custom attribute
     */
    void setUserCustomAttributeArray(String key, String[] attributeArray);

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
    void setUserCustomAttributeArrays(JSONObject attributes);

    /**
     * Appends the given value onto the Custom Attribute named by the given key.
     *
     * @param key   - name of the attribute to append the value to
     * @param value - value to append to the array
     */
    void appendUserCustomAttributeArray(String key, String value);

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
    void appendUserCustomAttributeArrays(JSONObject attributes);

    /**
     * Removes the value from the array attribute named by the given key.
     *
     * @param key   - key of the attribute to attempt to remove from.
     * @param value - value to remove from the array.
     */
    void removeFromUserCustomAttributeArray(String key, String value);

    /**
     * Helper method that will take each key-value pair and attempt to remove teh value from the
     * attribute named by the keys.
     * e.g. {
     * "array_attribute_name_1" : "string value to remove",
     * "array_attribute_name_1" : "other string value to remove"
     * }
     *
     * @param attributes
     */
    void removeFromUserCustomAttributeArrays(JSONObject attributes);

    /**
     * Logs a custom event with the given event name, and no custom properties.
     *
     * @param eventName - event name to send
     */
    void logCustomEvent(@NonNull String eventName);

    /**
     * Logs a custom event with the given event name, and the custom properties as described by the
     * key-value pairs in the eventProperties parameter.
     * e.g. {
     * "custom_prop_1" : "custom string value",
     * "custom_prop_2" : 10 // ...etc
     * }
     *
     * @param eventName
     * @param eventProperties
     */
    void logCustomEvent(@NonNull String eventName, JSONObject eventProperties);

    /**
     * Logs a purchase event with the provided productId, currency and unitPrices
     *
     * @param productId - productId of the product being purchased
     * @param currency  - currency used by this purchase; default = "USD"
     * @param unitPrice - unit price of the product.
     */
    void logPurchase(@NonNull String productId, String currency, @NonNull BigDecimal unitPrice);

    /**
     * Logs a purchase event with the provided productId, currency, unitPrice and quantity
     *
     * @param productId - productId of the product being purchased
     * @param currency  - currency used by this purchase; default = "USD"
     * @param unitPrice - unit price of the product.
     * @param quantity  - number of units purchased
     */
    void logPurchase(@NonNull String productId, String currency, @NonNull BigDecimal unitPrice, Integer quantity);

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
    void logPurchase(@NonNull String productId, String currency, @NonNull BigDecimal unitPrice, Integer quantity, JSONObject purchaseProperties);

    /**
     * Helper method that will take each bit of purchase information and attempt to log multiple
     * purchases. Each array should be matched in length.
     *
     * @param productIds
     * @param currencies
     * @param unitPrices
     * @param quantities
     * @param purchaseProerties
     */
    void logPurchase(@NonNull String[] productIds, String[] currencies, @NonNull BigDecimal[] unitPrices, Integer[] quantities, JSONObject[] purchaseProerties);

    /**
     * Requests an immediate flush of any queued up events within the Braze SDK.
     */
    void requestFlush();
}
