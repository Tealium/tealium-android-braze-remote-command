package com.tealium.remotecommands.braze;

import android.app.Application;
import android.util.Log;

import com.braze.configuration.BrazeConfig;
import com.tealium.remotecommands.RemoteCommand;

import org.json.JSONArray;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static com.tealium.remotecommands.braze.BrazeConstants.TAG;
import static com.tealium.remotecommands.braze.BrazeConstants.Commands;
import static com.tealium.remotecommands.braze.BrazeConstants.Config;
import static com.tealium.remotecommands.braze.BrazeConstants.User;
import static com.tealium.remotecommands.braze.BrazeConstants.Event;
import static com.tealium.remotecommands.braze.BrazeConstants.Purchase;

/**
 * Created by jameskeith on 23/10/2018.
 */

public class BrazeRemoteCommand extends RemoteCommand {

    public static final String DEFAULT_COMMAND_ID = "braze";
    public static final String DEFAULT_COMMAND_DESCRIPTION = "Tealium-Braze Remote Command";

    BrazeCommand mBraze;
    List<ConfigOverrider> configOverriders = new LinkedList<>();

    /**
     * Constructs a RemoteCommand that integrates with the Braze SDK to allow Braze API calls to be
     * implemented through Tealium.
     *
     * @param app                         - The Application instance
     */
    public BrazeRemoteCommand(Application app) {
        this(app, true, null, true, null, DEFAULT_COMMAND_ID, DEFAULT_COMMAND_DESCRIPTION);
    }

    /**
     * Constructs a RemoteCommand that integrates with the Braze SDK to allow Braze API calls to be
     * implemented through Tealium.
     *
     * @param app                         - The Application instance
     * @param sessionHandlingEnabled      - Whether session handling should be automatically handled by Braze
     * @param sessionHandlingBlacklist    - Set of classes not to open/close sessions on.
     * @param registerInAppMessageManager - Automatically registers the Braze InAppMessageManager
     *                                    through Braze's lifecycle callabacks.
     * @param inAppMessageBlacklist       - Set of classes that should not show in app messages
     */
    public BrazeRemoteCommand(Application app, boolean sessionHandlingEnabled, Set<Class<?>> sessionHandlingBlacklist, boolean registerInAppMessageManager, Set<Class<?>> inAppMessageBlacklist) {
        this(app, sessionHandlingEnabled, sessionHandlingBlacklist, registerInAppMessageManager, inAppMessageBlacklist, DEFAULT_COMMAND_ID, DEFAULT_COMMAND_DESCRIPTION);
    }

    /**
     * Constructs a RemoteCommand that integrates with the Braze SDK to allow Braze API calls to be
     * implemented through Tealium.
     *
     * @param app                         - The Application instance
     * @param sessionHandlingEnabled      - Whether session handling should be automatically handled by Braze
     * @param sessionHandlingBlacklist    - Set of classes not to open/close sessions on.
     * @param registerInAppMessageManager - Automatically registers the Braze InAppMessageManager
     *                                    through Braze's lifecycle callabacks.
     * @param inAppMessageBlacklist       - Set of classes that should not show in app messages
     * @param commandId                   - Override for the default command id as set on your TagBridge Custom
     *                                    Command tag in Tealium IQ.
     *                                    Default - "braze"
     * @param description                 - Override description for this Remote Command
     */
    public BrazeRemoteCommand(Application app, boolean sessionHandlingEnabled, Set<Class<?>> sessionHandlingBlacklist, boolean registerInAppMessageManager, Set<Class<?>> inAppMessageBlacklist, String commandId, String description) {
        super(
                !BrazeUtils.isNullOrEmpty(commandId) ? commandId : DEFAULT_COMMAND_ID,
                !BrazeUtils.isNullOrEmpty(description) ? description : DEFAULT_COMMAND_DESCRIPTION,
                BuildConfig.TEALIUM_BRAZE_VERSION);
        mBraze = new BrazeInstance(app, sessionHandlingEnabled, sessionHandlingBlacklist, registerInAppMessageManager, inAppMessageBlacklist);
    }

    /**
     * Handles the RemoteCommand response data. Any command names listed in the Commands.COMMAND_KEY key
     * in the response json, will be split using the BrazeConstants.SEPARATOR char and each command
     * will be executed in turn.
     * Payload JSON is expected like so:
     * {
     * // Commands
     * "command" : "<string>", // comma-separated string, e.g. initialize,wipeData
     * <p>
     * // Initialization
     * "api_key" : "<string>",
     * "enable_sdk" : <boolean>, // true/false
     * <p>
     * "firebase_enabled" : <boolean>, // true/false
     * "adm_enabled" : <boolean>, // true/false
     * "auto_push_deep_links" : <boolean>, // true/false
     * "disable_location" : <boolean>,
     * "enable_news_feed_indicator" : <boolean>,
     * <p>
     * "firebase_sender_id" : "<string>",
     * "small_notification_icon" : "<string>",
     * "large_notification_icon" : "<string>",
     * "custom_endpoint" : "<string>", // custom endpoint URL
     * <p>
     * "session_timeout" : <integer>,
     * "default_notification_color" : <integer>,
     * "bad_network_interval" : <integer>,
     * "good_network_interval" : <integer>,
     * "great_network_interval" : <integer>,
     * "trigger_interval_seconds" : <integer>
     * <p>
     * // User data
     * "user_id" : "<string>", // e.g. test@test.com
     * "user_alias" : "<string>",
     * "user_alias_label" : "<string>",
     * "user_name" : "<string>",
     * "first_name" : "<string>",
     * "last_name" : "<string>",
     * "gender" : "<string>",
     * "language" : "<string>",
     * "email" : "<string>",
     * "home_city" : "<string>",
     * "date_of_birth" : "<date>"
     * <p>
     * // User Custom Attributes
     * "set_custom_attribute" : {
     * "attr_name_1" : "attr_value_1",
     * "attr_name_2" : "attr_value_2"
     * },
     * "unset_custom_attribute" : [
     * "attr_name_1"
     * ],
     * "increment_custom_attribute" : {
     * "attr_name_1" : 1, // increment default is 1
     * "attr_name_2" : 10 // custom increment
     * },
     * "set_custom_array_attribute" {
     * "attr_array_id_1" : [
     * "string 1",
     * "string 2"
     * // ...
     * ]
     * // etc
     * },
     * "append_custom_array_attribute" : {
     * "attr_array_id_1" : "string_value_to_add"
     * },
     * "remove_custom_array_attribute : {
     * "attr_array_id_1" : "string_value_to_remove"
     * },
     * "facebook_id" : "<string>",
     * "friends_count" : <integer>,
     * "likes" : [
     * "likes"
     * ],
     * <p>
     * // Social
     * "description" : "<string>",
     * "twitter_id" : <integer>,
     * "twitter_name" : "<string>",
     * "profile_image_url" : "<string>",
     * "screen_name" : "<string>",
     * "followers_count" : <integer>,
     * "statuses_count" : <integer>,
     * <p>
     * // Notifications
     * "email_notification" : "<string>", // "unsubscribed", "subscribed", "opted_in"
     * "push_notification" : "<string>", // "unsubscribed", "subscribed", "opted_in"
     * <p>
     * // Custom Events
     * "event_name" : "<string>",
     * "event_properties" : {
     * "property_name_1" : "string value",
     * "property_name_2" : <boolean>, // true/false
     * "property_name_3" : <integer>, // 10
     * "property_name_4" : <double>, // 10.15
     * "property_name_5" : <date>, // format "E MMM dd HH:mm:ss z yyyy"
     * },
     * <p>
     * // Purchases
     * "product_id" : [
     * "<string>"
     * ],
     * "product_qty" : [
     * <integer>
     * ],
     * "product_price" : [
     * <double>
     * ],
     * "product_currency" : [
     * "<string>" // e.g. "USD"
     * ],
     * "purchase_properties" : [{
     * "property_name_1" : "string value",
     * "property_name_2" : <boolean>, // true/false
     * "property_name_3" : <integer>, // 10
     * "property_name_4" : <double>, // 10.15
     * "property_name_5" : <date>, // format "E MMM dd HH:mm:ss z yyyy"
     * }]
     * }
     *
     * @param response
     * @throws Exception
     */
    @Override
    protected void onInvoke(Response response) throws Exception {
        JSONObject payload = response.getRequestPayload();
        String[] commands = splitCommands(payload);
        parseCommands(commands, payload);
        response.send();
    }

    private String[] splitCommands(JSONObject payload) {
        String commandString = payload.optString(Commands.COMMAND_KEY, "");
        return commandString.split(BrazeConstants.SEPARATOR);
    }

    private void parseCommands(String[] commandList, JSONObject payload) {
        for (String command : commandList) {
            command = command.trim();
            Log.d(TAG, "Executing command: " + command);
            try {
                switch (command) {
                    case Commands.INITIALIZE:
                        String apiKey = payload.optString(Config.API_KEY);
                        mBraze.initialize(
                                apiKey,
                                payload,
                                configOverriders
                        );
                        break;
                    case Commands.ENABLE_SDK:
                        if (BrazeUtils.keyHasValue(payload, Config.ENABLE_SDK)) {
                            mBraze.enableSdk(
                                    payload.optBoolean(Config.ENABLE_SDK)
                            );
                        }
                        break;
                    case Commands.WIPE_DATA:
                        mBraze.wipeData();
                        break;
                    case Commands.USER_IDENTIFIER:
                        mBraze.setUserId(
                                payload.optString(User.USER_ID)
                        );
                        break;
                    case Commands.USER_ALIAS:
                        mBraze.setUserAlias(
                                payload.optString(User.ALIAS),
                                payload.optString(User.ALIAS_LABEL)
                        );
                        break;
                    case Commands.USER_ATTRIBUTE:
                        mBraze.setUserFirstName(
                                payload.optString(User.FIRST_NAME)
                        );
                        mBraze.setUserLastName(
                                payload.optString(User.LAST_NAME)
                        );
                        mBraze.setUserEmail(
                                payload.optString(User.EMAIL)
                        );
                        mBraze.setUserLanguage(
                                payload.optString(User.LANGUAGE)
                        );
                        mBraze.setUserGender(
                                payload.optString(User.GENDER)
                        );
                        mBraze.setUserHomeCity(
                                payload.optString(User.HOME_CITY)
                        );

                        break;
                    case Commands.SET_CUSTOM_ATTRIBUTE:
                        mBraze.setUserCustomAttributes(
                                payload.optJSONObject(User.SET_CUSTOM_ATTRIBUTE)
                        );
                        break;
                    case Commands.UNSET_CUSTOM_ATTRIBUTE:
                        mBraze.unsetUserCustomAttributes(
                                payload.optJSONArray(User.UNSET_CUSTOM_ATTRIBUTE)
                        );
                        break;
                    case Commands.SET_CUSTOM_ARRAY_ATTRIBUTE:
                        mBraze.setUserCustomAttributeArrays(
                                payload.optJSONObject(User.SET_CUSTOM_ARRAY_ATTRIBUTE)
                        );
                        break;
                    case Commands.REMOVE_CUSTOM_ARRAY_ATTRIBUTE:
                        mBraze.removeFromUserCustomAttributeArrays(
                                payload.optJSONObject(User.REMOVE_CUSTOM_ARRAY_ATTRIBUTE)
                        );
                        break;
                    case Commands.APPEND_CUSTOM_ARRAY_ATTRIBUTE:
                        mBraze.appendUserCustomAttributeArrays(
                                payload.optJSONObject(User.APPEND_CUSTOM_ARRAY_ATTRIBUTE)
                        );
                        break;
                    case Commands.INCREMENT_CUSTOM_ATTRIBUTE:
                        mBraze.incrementUserCustomAttributes(
                                payload.optJSONObject(User.INCREMENT_CUSTOM_ATTRIBUTE)
                        );
                        break;

                    case Commands.LOG_CUSTOM_EVENT:
                        JSONObject eventProps = payload.optJSONObject(Event.EVENT_PROPERTIES);
                        if (eventProps == null) {
                            eventProps = payload.optJSONObject(Event.EVENT_PROPERTIES_SHORTHAND);
                        }
                        mBraze.logCustomEvent(
                                payload.optString(Event.EVENT_NAME, null),
                                eventProps
                        );
                        break;
                    case Commands.LOG_PURCHASE_EVENT:
                        Object productId = payload.get(Purchase.PRODUCT_ID);
                        if (productId instanceof JSONArray) {
                            JSONArray purchaseProps = payload.optJSONArray(Purchase.PURCHASE_PROPERTIES);
                            if (purchaseProps == null) {
                                purchaseProps = payload.optJSONArray(Purchase.PURCHASE_PROPERTIES_SHORTHAND);
                            }
                            mBraze.logPurchase(
                                    BrazeUtils.getStringArrayFromJson(payload.optJSONArray(Purchase.PRODUCT_ID)),
                                    BrazeUtils.getStringArrayFromJson(payload.optJSONArray(Purchase.PRODUCT_CURRENCY)),
                                    BrazeUtils.getBigDecimalArrayFromJson(payload.optJSONArray(Purchase.PRODUCT_PRICE)),
                                    BrazeUtils.getIntegerArrayFromJson(payload.optJSONArray(Purchase.PRODUCT_QTY)),
                                    BrazeUtils.getJSONObjectArrayFromJson(purchaseProps)
                            );
                        } else {
                            // assume a single purchase
                            JSONObject purchaseProps = payload.optJSONObject(Purchase.PURCHASE_PROPERTIES);
                            if (purchaseProps == null) {
                                purchaseProps = payload.optJSONObject(Purchase.PURCHASE_PROPERTIES_SHORTHAND);
                            }
                            mBraze.logPurchase(
                                    payload.optString(Purchase.PRODUCT_ID),
                                    payload.optString(Purchase.PRODUCT_CURRENCY),
                                    new BigDecimal(payload.optDouble(Purchase.PRODUCT_PRICE, 0d)),
                                    payload.optInt(Purchase.PRODUCT_QTY),
                                    purchaseProps
                            );
                        }
                        break;
                    case Commands.FACEBOOK_USER:
                        mBraze.setFacebookData(
                                payload.optString(User.FACEBOOK_ID, null),
                                payload.optString(User.FIRST_NAME, null),
                                payload.optString(User.LAST_NAME, null),
                                payload.optString(User.EMAIL, null),
                                payload.optString(User.DESCRIPTION, null),
                                payload.optString(User.HOME_CITY, null),
                                payload.optString(User.GENDER, null),
                                payload.optInt(User.FRIENDS_COUNT, -1),
                                payload.optJSONArray(User.LIKES),
                                payload.optString(User.DATE_OF_BIRTH)
                        );
                        break;
                    case Commands.TWITTER_USER:
                        mBraze.setTwitterData(
                                payload.optInt(User.TWITTER_ID, -1),
                                payload.optString(User.TWITTER_NAME, null),
                                payload.optString(User.SCREEN_NAME, null),
                                payload.optString(User.DESCRIPTION, null),
                                payload.optInt(User.FOLLOWERS_COUNT, -1),
                                payload.optInt(User.FRIENDS_COUNT, -1),
                                payload.optInt(User.STATUSES_COUNT, -1),
                                payload.optString(User.PROFILE_IMAGE_URL, null)
                        );
                        break;
                    case Commands.EMAIL_NOTIFICATION:
                        mBraze.setEmailSubscriptionType(
                                payload.optString(User.EMAIL_NOTIFICATION, null)
                        );
                        break;
                    case Commands.PUSH_NOTIFICATION:
                        mBraze.setPushNotificationSubscriptionType(
                                payload.optString(User.PUSH_NOTIFICATION, null)
                        );
                        break;
                    case Commands.FLUSH:
                        mBraze.requestFlush();
                        break;
                    case Commands.REGISTER_TOKEN:
                        mBraze.registerToken(
                                payload.optString(User.PUSH_TOKEN, null)
                        );
                        break;
                    case Commands.ADD_TO_SUBSCRIPTION_GROUP:
                        mBraze.addToSubscriptionGroup(
                                payload.optString(User.SUBSCRIPTION_GROUP_ID, null)
                        );
                        break;
                    case Commands.REMOVE_FROM_SUBSCRIPTION_GROUP:
                        mBraze.removeFromSubscriptionGroup(
                                payload.optString(User.SUBSCRIPTION_GROUP_ID, null)
                        );
                        break;
                }
            } catch (Exception ex) {
                Log.w(TAG, "Error processing command: " + command, ex);
            }
        }
    }

    /**
     * Registers a ConfigOverrider object. Once all Initialization options have been added to the
     * AppboyConfig.Builder object, the Builder will be exposed in the onOverride method, so you
     * can override any settings necessary, or add any settings without needing to setup mappiings
     * in Tealium IQ.
     *
     * @param overrider
     */
    public void registerConfigOverride(ConfigOverrider overrider) {
        configOverriders.add(overrider);
    }

    /**
     * Interface to allow users to inject additional configuration items that may not be present
     * in the data supplied back from the RemoteCommand. This method is called after all LaunchOption
     * variables have been added to the AppboyConfig.Builder object, so be aware that this can
     * overwrite any configuration properties that have already been setup.
     */
    public interface ConfigOverrider {
        void onOverride(BrazeConfig.Builder b);
    }
}
