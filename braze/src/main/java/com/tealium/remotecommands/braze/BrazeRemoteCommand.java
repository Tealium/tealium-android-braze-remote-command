package com.tealium.remotecommands.braze;

import android.app.Application;
import android.util.Log;

import com.braze.configuration.BrazeConfig;
import com.tealium.remotecommands.RemoteCommand;

import org.json.JSONArray;
import org.json.JSONException;
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
import static com.tealium.remotecommands.braze.BrazeConstants.Ecommerce;
import static com.tealium.remotecommands.braze.BrazeConstants.Location;

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
     *                                    through Braze's lifecycle callbacks.
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
     *                                    through Braze's lifecycle callbacks.
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
     * }],
     * <p>
     * // Ecommerce Events (Braze SDK 42.3.0+), keys match the Braze recommended-event schema 1:1.
     * // Commands: logproductviewed, logcartupdated, logcheckoutstarted, logorderplaced,
     * //           logordercancelled, logorderrefunded
     * // logproductviewed (single product, scalar fields, no products object):
     * "product_id" : "<string>", "product_name" : "<string>", "variant_id" : "<string>",
     * "price" : <double>, "currency" : "<string>", "source" : "<string>",
     * "image_url" : "<string>", // optional
     * "product_url" : "<string>", // optional
     * "type" : ["<string>"], // optional, e.g. "price_drop"/"back_in_stock"
     * "metadata" : { "property_name_1" : "string value" } // optional
     * <p>
     * // "products" (cart/checkout/order): a nested object holding PARALLEL ARRAYS, zipped by
     * // index into line items -- unifying the shape with tealium-android-firebase-remote-command's
     * // items_params. product_id/product_name/variant_id/price/quantity are required and must be
     * // equal length; image_url/product_url/metadata are optional per-index arrays:
     * "products" : {
     * "product_id" : ["<string>"], "product_name" : ["<string>"], "variant_id" : ["<string>"],
     * "price" : [<double>], "quantity" : [<integer>],
     * "image_url" : ["<string>"], "product_url" : ["<string>"], // optional
     * "metadata" : [{ "property_name_1" : "string value" }] // optional, per product
     * },
     * // "discounts" (order_placed/cancelled/refunded): same nested-parallel-arrays shape, all
     * // arrays optional:
     * "discounts" : { "code" : ["<string>"], "amount" : [<double>], "type" : ["<string>"] },
     * <p>
     * // logcartupdated:
     * "cart_id" : "<string>", // required
     * "action" : "<string>", // "add" / "remove" / "replace"; omitted or unrecognized defaults to replace
     * "total_value" : <double>, // required when action is omitted/replace; optional for add/remove
     * "subtotal_value" : <double>, // optional
     * "tax" : <double>, // optional
     * "shipping" : <double>, // optional
     * "currency" : "<string>", "source" : "<string>",
     * "products" : {...}, // required, see above
     * "metadata" : {...} // optional, event-level
     * <p>
     * // logcheckoutstarted:
     * "checkout_id" : "<string>", // required
     * "cart_id" : "<string>", // optional
     * "total_value" : <double>, "currency" : "<string>", "source" : "<string>",
     * "subtotal_value" : <double>, "tax" : <double>, "shipping" : <double>, // all optional
     * "products" : {...}, // required, see above
     * "metadata" : {...} // optional, event-level
     * <p>
     * // logorderplaced:
     * "order_id" : "<string>", // required
     * "cart_id" : "<string>", // optional
     * "total_value" : <double>, "currency" : "<string>", "source" : "<string>",
     * "total_discounts" : <double>, // optional
     * "discounts" : {...}, // optional, see above
     * "products" : {...}, // required, see above
     * "metadata" : {...} // optional, event-level
     * <p>
     * // logordercancelled / logorderrefunded (no typed Braze SDK class; logged via logCustomEvent):
     * "order_id" : "<string>", // required
     * "total_value" : <double>, "currency" : "<string>", "source" : "<string>",
     * "cancel_reason" : "<string>", // logordercancelled only, required
     * "total_discounts" : <double>, "discounts" : {...}, // optional, see above
     * "products" : {...}, // required, see above
     * "metadata" : {...} // optional, event-level
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
        String commandString = payload.optString(Commands.COMMAND_KEY);
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
                        mBraze.enableSdk();
                        break;
                    case Commands.DISABLE_SDK:
                        mBraze.disableSdk();
                        break;
                    case Commands.WIPE_DATA:
                        mBraze.wipeData();
                        break;
                    case Commands.USER_IDENTIFIER:
                        String authSignature = payload.optString(User.SDK_AUTH_SIGNATURE);
                        mBraze.setUserId(
                                payload.optString(User.USER_ID),
                                !authSignature.isEmpty() ? authSignature : null
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
                        mBraze.setUserCountry(
                                payload.optString(User.COUNTRY)
                        );
                        mBraze.setUserPhone(
                                payload.optString(User.PHONE)
                        );
                        mBraze.setUserDateOfBirth(
                                payload.optString(User.DATE_OF_BIRTH)
                        );
                        break;
                    case Commands.SET_CUSTOM_ATTRIBUTE:
                        mBraze.setUserCustomAttributes(
                                payload.getJSONObject(User.SET_CUSTOM_ATTRIBUTE)
                        );
                        break;
                    case Commands.UNSET_CUSTOM_ATTRIBUTE:
                        mBraze.unsetUserCustomAttributes(
                                payload.getJSONArray(User.UNSET_CUSTOM_ATTRIBUTE)
                        );
                        break;
                    case Commands.SET_CUSTOM_ARRAY_ATTRIBUTE:
                        mBraze.setUserCustomAttributeArrays(
                                payload.getJSONObject(User.SET_CUSTOM_ARRAY_ATTRIBUTE)
                        );
                        break;
                    case Commands.REMOVE_CUSTOM_ARRAY_ATTRIBUTE:
                        mBraze.removeFromUserCustomAttributeArrays(
                                payload.getJSONObject(User.REMOVE_CUSTOM_ARRAY_ATTRIBUTE)
                        );
                        break;
                    case Commands.APPEND_CUSTOM_ARRAY_ATTRIBUTE:
                        mBraze.appendUserCustomAttributeArrays(
                                payload.getJSONObject(User.APPEND_CUSTOM_ARRAY_ATTRIBUTE)
                        );
                        break;
                    case Commands.INCREMENT_CUSTOM_ATTRIBUTE:
                        mBraze.incrementUserCustomAttributes(
                                payload.getJSONObject(User.INCREMENT_CUSTOM_ATTRIBUTE)
                        );
                        break;

                    case Commands.LOG_CUSTOM_EVENT:
                        JSONObject eventProps = payload.optJSONObject(Event.EVENT_PROPERTIES);
                        if (eventProps == null) {
                            eventProps = payload.optJSONObject(Event.EVENT_PROPERTIES_SHORTHAND);
                        }
                        mBraze.logCustomEvent(
                                payload.getString(Event.EVENT_NAME),
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
                                    BigDecimal.valueOf(payload.optDouble(Purchase.PRODUCT_PRICE, 0d)),
                                    payload.optInt(Purchase.PRODUCT_QTY),
                                    purchaseProps
                            );
                        }
                        break;
                    case Commands.LOG_PRODUCT_VIEWED:
                        mBraze.logProductViewed(
                                BrazeUtils.requireScalarString(payload, Ecommerce.PRODUCT_ID),
                                BrazeUtils.requireScalarString(payload, Ecommerce.PRODUCT_NAME),
                                BrazeUtils.requireScalarString(payload, Ecommerce.VARIANT_ID),
                                payload.getDouble(Ecommerce.PRICE),
                                BrazeUtils.requireScalarString(payload, Ecommerce.CURRENCY),
                                BrazeUtils.requireScalarString(payload, Ecommerce.SOURCE),
                                BrazeUtils.optionalScalarString(payload, Ecommerce.IMAGE_URL),
                                BrazeUtils.optionalScalarString(payload, Ecommerce.PRODUCT_URL),
                                payload.optJSONObject(Ecommerce.METADATA)
                        );
                        break;
                    case Commands.LOG_CART_UPDATED:
                        double cartTotalValue = payload.optDouble(Ecommerce.TOTAL_VALUE);
                        Ecommerce.Action cartAction = Ecommerce.Action.from(BrazeUtils.optionalScalarString(payload, Ecommerce.ACTION));
                        // total_value is required for a full-cart snapshot (action replace or omitted);
                        // catch it here so the event is skipped client-side rather than dropped at
                        // Braze ingestion. It stays optional for add/remove incremental updates.
                        if (Double.isNaN(cartTotalValue) && cartAction == Ecommerce.Action.REPLACE) {
                            throw new JSONException("total_value is required for cart_updated when action is replace or omitted");
                        }
                        mBraze.logCartUpdated(
                                payload.optString(Ecommerce.CART_ID),
                                BrazeUtils.requireScalarString(payload, Ecommerce.CURRENCY),
                                BrazeUtils.requireScalarString(payload, Ecommerce.SOURCE),
                                Double.isNaN(cartTotalValue) ? null : cartTotalValue,
                                cartAction,
                                payload.getJSONObject(Ecommerce.PRODUCTS),
                                payload.optJSONObject(Ecommerce.METADATA)
                        );
                        break;
                    case Commands.LOG_CHECKOUT_STARTED:
                        mBraze.logCheckoutStarted(
                                BrazeUtils.requireScalarString(payload, Ecommerce.CHECKOUT_ID),
                                BrazeUtils.requireScalarString(payload, Ecommerce.CURRENCY),
                                BrazeUtils.requireScalarString(payload, Ecommerce.SOURCE),
                                payload.getDouble(Ecommerce.TOTAL_VALUE),
                                payload.getJSONObject(Ecommerce.PRODUCTS),
                                BrazeUtils.keyHasValue(payload, Ecommerce.CART_ID) ? payload.optString(Ecommerce.CART_ID) : null,
                                payload.optJSONObject(Ecommerce.METADATA)
                        );
                        break;
                    case Commands.LOG_ORDER_PLACED:
                        double orderPlacedDiscounts = payload.optDouble(Ecommerce.TOTAL_DISCOUNTS);
                        mBraze.logOrderPlaced(
                                BrazeUtils.requireScalarString(payload, Ecommerce.ORDER_ID),
                                BrazeUtils.requireScalarString(payload, Ecommerce.CURRENCY),
                                BrazeUtils.requireScalarString(payload, Ecommerce.SOURCE),
                                payload.getDouble(Ecommerce.TOTAL_VALUE),
                                payload.getJSONObject(Ecommerce.PRODUCTS),
                                BrazeUtils.keyHasValue(payload, Ecommerce.CART_ID) ? payload.optString(Ecommerce.CART_ID) : null,
                                Double.isNaN(orderPlacedDiscounts) ? null : orderPlacedDiscounts,
                                payload.optJSONObject(Ecommerce.DISCOUNTS),
                                payload.optJSONObject(Ecommerce.METADATA)
                        );
                        break;
                    case Commands.LOG_ORDER_CANCELLED:
                        double orderCancelledDiscounts = payload.optDouble(Ecommerce.TOTAL_DISCOUNTS);
                        double orderCancelledSubtotal = payload.optDouble(Ecommerce.SUBTOTAL_VALUE);
                        double orderCancelledTax = payload.optDouble(Ecommerce.TAX);
                        double orderCancelledShipping = payload.optDouble(Ecommerce.SHIPPING);
                        mBraze.logOrderCancelled(
                                payload.getString(Ecommerce.ORDER_ID),
                                BrazeUtils.requireScalarString(payload, Ecommerce.CURRENCY),
                                payload.getString(Ecommerce.SOURCE),
                                payload.getDouble(Ecommerce.TOTAL_VALUE),
                                Double.isNaN(orderCancelledSubtotal) ? null : orderCancelledSubtotal,
                                Double.isNaN(orderCancelledTax) ? null : orderCancelledTax,
                                Double.isNaN(orderCancelledShipping) ? null : orderCancelledShipping,
                                payload.getJSONObject(Ecommerce.PRODUCTS),
                                payload.getString(Ecommerce.CANCEL_REASON),
                                Double.isNaN(orderCancelledDiscounts) ? null : orderCancelledDiscounts,
                                payload.optJSONObject(Ecommerce.DISCOUNTS),
                                payload.optJSONObject(Ecommerce.METADATA)
                        );
                        break;
                    case Commands.LOG_ORDER_REFUNDED:
                        double orderRefundedDiscounts = payload.optDouble(Ecommerce.TOTAL_DISCOUNTS);
                        mBraze.logOrderRefunded(
                                payload.getString(Ecommerce.ORDER_ID),
                                BrazeUtils.requireScalarString(payload, Ecommerce.CURRENCY),
                                payload.getString(Ecommerce.SOURCE),
                                payload.getDouble(Ecommerce.TOTAL_VALUE),
                                payload.getJSONObject(Ecommerce.PRODUCTS),
                                Double.isNaN(orderRefundedDiscounts) ? null : orderRefundedDiscounts,
                                payload.optJSONObject(Ecommerce.DISCOUNTS),
                                payload.optJSONObject(Ecommerce.METADATA)
                        );
                        break;
                    case Commands.EMAIL_NOTIFICATION:
                        mBraze.setEmailSubscriptionType(
                                payload.getString(User.EMAIL_NOTIFICATION)
                        );
                        break;
                    case Commands.PUSH_NOTIFICATION:
                        mBraze.setPushNotificationSubscriptionType(
                                payload.getString(User.PUSH_NOTIFICATION)
                        );
                        break;
                    case Commands.FLUSH:
                        mBraze.requestFlush();
                        break;
                    case Commands.ADD_TO_SUBSCRIPTION_GROUP:
                        mBraze.addToSubscriptionGroup(
                                payload.getString(User.SUBSCRIPTION_GROUP_ID)
                        );
                        break;
                    case Commands.REMOVE_FROM_SUBSCRIPTION_GROUP:
                        mBraze.removeFromSubscriptionGroup(
                                payload.getString(User.SUBSCRIPTION_GROUP_ID)
                        );
                        break;
                    case Commands.SET_SDK_AUTH_SIGNATURE:
                        mBraze.setSdkAuthSignature(
                                payload.getString(User.SDK_AUTH_SIGNATURE)
                        );
                        break;
                    case Commands.SET_LAST_KNOWN_LOCATION:
                        double latitude = payload.getDouble(Location.LOCATION_LATITUDE);
                        double longitude = payload.getDouble(Location.LOCATION_LONGITUDE);
                        double altitude = payload.optDouble(Location.LOCATION_ALTITUDE);
                        double accuracy = payload.optDouble(Location.LOCATION_ACCURACY);

                        mBraze.setLastKnownLocation(
                                latitude,
                                longitude,
                                Double.isNaN(altitude) ? null : altitude,
                                Double.isNaN(accuracy) ? null : accuracy
                        );
                        break;
                    case Commands.SET_AD_TRACKING_ENABLED:
                        String googleAdid = payload.getString(User.GOOGLE_ADID);
                        boolean adTrackingEnabled = payload.getBoolean(User.AD_TRACKING_ENABLED);

                        mBraze.setAdTrackingEnabled(googleAdid, !adTrackingEnabled);
                        break;
                }
            } catch (Exception ex) {
                Log.d(TAG, "Error processing command: " + command + " - " + ex.getMessage());
            }
        }
    }

    /**
     * Registers a ConfigOverrider object. Once all Initialization options have been added to the
     * BrazeConfig.Builder object, the Builder will be exposed in the onOverride method, so you
     * can override any settings necessary, or add any settings without needing to setup mappings
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
     * variables have been added to the BrazeConfig.Builder object, so be aware that this can
     * overwrite any configuration properties that have already been setup.
     */
    @FunctionalInterface
    public interface ConfigOverrider {
        void onOverride(BrazeConfig.Builder b);
    }
}
