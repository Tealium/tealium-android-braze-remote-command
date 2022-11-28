package com.tealium.remotecommands.braze;

import com.tealium.remotecommands.RemoteCommand;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import static com.tealium.remotecommands.braze.BrazeConstants.SEPARATOR;
import static com.tealium.remotecommands.braze.BrazeConstants.Commands;
import static com.tealium.remotecommands.braze.BrazeConstants.Config;
import static com.tealium.remotecommands.braze.BrazeConstants.User;
import static com.tealium.remotecommands.braze.BrazeConstants.Event;
import static com.tealium.remotecommands.braze.BrazeConstants.Purchase;
import static com.tealium.remotecommands.braze.BrazeConstants.Location;

public final class TestData {

    private TestData() {
    }

    public static final class Events {
        private Events() {
        }

        public static Map<String, Object> initalizeWithApiKeyOnly() {
            Map<String, Object> payload = new HashMap<>();
            payload.put(Commands.COMMAND_KEY, Commands.INITIALIZE);
            payload.put(Config.API_KEY, Values.API_KEY);

            return payload;
        }

        public static Map<String, Object> initializeWithAllSettings() {
            Map<String, Object> payload = new HashMap<>();
            payload.put(Commands.COMMAND_KEY, Commands.INITIALIZE);
            payload.put(Config.API_KEY, Values.API_KEY);

            payload.put(Config.FIREBASE_ENABLED, Values.FIREBASE_ENABLED);
            payload.put(Config.FIREBASE_SENDER_ID, Values.FIREBASE_SENDER_ID);
            payload.put(Config.ADM_ENABLED, Values.ADM_ENABLED);
            payload.put(Config.AUTO_PUSH_DEEP_LINKS, Values.AUTO_DEEP_LINKS);
            payload.put(Config.BAD_NETWORK_INTERVAL, Values.BAD_NETWORK_INTERVAL);
            payload.put(Config.GOOD_NETWORK_INTERVAL, Values.GOOD_NETWORK_INTERVAL);
            payload.put(Config.GREAT_NETWORK_INTERVAL, Values.GREAT_NETWORK_INTERVAL);
            payload.put(Config.CUSTOM_ENDPOINT, Values.CUSTOM_ENDPOINT);
            payload.put(Config.DEFAULT_NOTIFICATION_COLOR, Values.DEFAULT_NOTIFICATION_COLOR);
            payload.put(Config.DISABLE_LOCATION, Values.DISABLE_LOCATION);
            payload.put(Config.ENABLE_NEWS_FEED_INDICATOR, Values.ENABLE_NEWS_FEED_INDICATOR);
            payload.put(Config.LARGE_NOTIFICATION_ICON, Values.LARGE_NOTIFICATION_ICON);
            payload.put(Config.SMALL_NOTIFICATION_ICON, Values.SMALL_NOTIFICATION_ICON);
            payload.put(Config.SESSION_TIMEOUT, Values.SESSION_TIMEOUT);
            payload.put(Config.TRIGGER_INTERVAL_SECONDS, Values.TRIGGER_INTERVAL_SECONDS);

            return payload;
        }

        public static Map<String, Object> wipeData() {
            Map<String, Object> payload = new HashMap<>();
            payload.put(Commands.COMMAND_KEY, Commands.WIPE_DATA);

            return payload;
        }

        public static Map<String, Object> enableSdk() {
            Map<String, Object> payload = new HashMap<>();
            payload.put(Commands.COMMAND_KEY, Commands.ENABLE_SDK);

            return payload;
        }

        public static Map<String, Object> disableSdk() {
            Map<String, Object> payload = new HashMap<>();
            payload.put(Commands.COMMAND_KEY, Commands.DISABLE_SDK);

            return payload;
        }

        public static Map<String, Object> customEventWithProperties() {
            Map<String, Object> payload = new HashMap<>();
            payload.put(Commands.COMMAND_KEY, String.join(",",
                    Commands.INITIALIZE,
                    Commands.LOG_CUSTOM_EVENT
            ));

            payload.put(Config.API_KEY, Values.API_KEY);

            payload.put(Event.EVENT_NAME, Values.EVENT_NAME);
            payload.put(Event.EVENT_PROPERTIES, Values.EVENT_PROPERTIES);

            return payload;
        }

        public static Map<String, Object> customEvent() {
            Map<String, Object> payload = new HashMap<>();
            payload.put(Commands.COMMAND_KEY, String.join(",",
                    Commands.INITIALIZE,
                    Commands.LOG_CUSTOM_EVENT
            ));

            payload.put(Config.API_KEY, Values.API_KEY);

            payload.put(Event.EVENT_NAME, Values.EVENT_NAME);

            return payload;
        }

        public static Map<String, Object> purchaseEventWithProperties() {
            Map<String, Object> payload = new HashMap<>();
            payload.put(Commands.COMMAND_KEY, String.join(",",
                    Commands.INITIALIZE,
                    Commands.LOG_PURCHASE_EVENT
            ));

            payload.put(Config.API_KEY, Values.API_KEY);

            payload.put(Purchase.PRODUCT_ID, Values.PRODUCT_ID);
            payload.put(Purchase.PRODUCT_QTY, Values.PRODUCT_QTY);
            payload.put(Purchase.PRODUCT_PRICE, Values.PRODUCT_PRICE);
            payload.put(Purchase.PURCHASE_PROPERTIES, Values.PRODUCT_PROPERTIES);
            payload.put(Purchase.PRODUCT_CURRENCY, Values.PRODUCT_CURRENCY);

            return payload;
        }

        public static Map<String, Object> purchaseEvent() {
            Map<String, Object> payload = new HashMap<>();
            payload.put(Commands.COMMAND_KEY, String.join(",",
                    Commands.INITIALIZE,
                    Commands.LOG_PURCHASE_EVENT
            ));

            payload.put(Config.API_KEY, Values.API_KEY);

            payload.put(Purchase.PRODUCT_ID, Values.PRODUCT_ID);
            payload.put(Purchase.PRODUCT_QTY, Values.PRODUCT_QTY);
            payload.put(Purchase.PRODUCT_PRICE, Values.PRODUCT_PRICE);
            payload.put(Purchase.PRODUCT_CURRENCY, Values.PRODUCT_CURRENCY);

            return payload;
        }

        public static Map<String, Object> userAlias() {
            Map<String, Object> payload = new HashMap<>();
            payload.put(Commands.COMMAND_KEY, String.join(",",
                    Commands.INITIALIZE,
                    Commands.USER_ALIAS
            ));

            payload.put(Config.API_KEY, Values.API_KEY);

            payload.put(User.ALIAS, Values.USER_ALIAS);
            payload.put(User.ALIAS_LABEL, Values.USER_ALIAS_LABEL);

            return payload;
        }

        public static Map<String, Object> userId() {
            Map<String, Object> payload = new HashMap<>();
            payload.put(Commands.COMMAND_KEY, String.join(",",
                    Commands.INITIALIZE,
                    Commands.USER_IDENTIFIER
            ));

            payload.put(Config.API_KEY, Values.API_KEY);

            payload.put(User.USER_ID, Values.USER_ID);
            payload.put(User.ALIAS_LABEL, Values.USER_ALIAS_LABEL);

            return payload;
        }

        public static Map<String, Object> userAllAttributes() {
            Map<String, Object> payload = new HashMap<>();

            payload.put(Commands.COMMAND_KEY, String.join(SEPARATOR,
                    Commands.INITIALIZE,
                    Commands.USER_IDENTIFIER,
                    Commands.USER_ATTRIBUTE,
                    Commands.USER_ALIAS
            ));

            payload.put(Config.API_KEY, Values.API_KEY);
            payload.put(User.USER_ID, Values.USER_ID);

            payload.put(User.ALIAS, Values.USER_ALIAS);
            payload.put(User.ALIAS_LABEL, Values.USER_ALIAS_LABEL);

            payload.put(User.FIRST_NAME, Values.USER_FIRST_NAME);
            payload.put(User.LAST_NAME, Values.USER_LAST_NAME);
            payload.put(User.EMAIL, Values.USER_EMAIL);
            payload.put(User.GENDER, Values.USER_GENDER);
            payload.put(User.LANGUAGE, Values.USER_LANGUAGE);
            payload.put(User.HOME_CITY, Values.USER_HOME_CITY);

            return payload;
        }

        public static Map<String, Object> userAllCustomAttributes() {
            Map<String, Object> payload = new HashMap<>();

            payload.put(Commands.COMMAND_KEY, String.join(",",
                    Commands.INITIALIZE,
                    Commands.SET_CUSTOM_ATTRIBUTE,
                    Commands.INCREMENT_CUSTOM_ATTRIBUTE,
                    Commands.UNSET_CUSTOM_ATTRIBUTE
            ));

            payload.put(Config.API_KEY, Values.API_KEY);

            payload.put(User.SET_CUSTOM_ATTRIBUTE, Values.SET_CUSTOM_USER_ATTRIBUTES);
            payload.put(User.INCREMENT_CUSTOM_ATTRIBUTE, Values.INCREMENT_CUSTOM_USER_ATTRIBUTES);
            payload.put(User.UNSET_CUSTOM_ATTRIBUTE, Values.UNSET_CUSTOM_USER_ATTRIBUTES);

            return payload;
        }

        public static Map<String, Object> userAllCustomArrayAttributes() {
            Map<String, Object> payload = new HashMap<>();

            payload.put(Commands.COMMAND_KEY, String.join(SEPARATOR,
                    Commands.INITIALIZE,
                    Commands.SET_CUSTOM_ARRAY_ATTRIBUTE,
                    Commands.APPEND_CUSTOM_ARRAY_ATTRIBUTE,
                    Commands.REMOVE_CUSTOM_ARRAY_ATTRIBUTE
            ));

            payload.put(Config.API_KEY, Values.API_KEY);

            payload.put(User.SET_CUSTOM_ARRAY_ATTRIBUTE, Values.SET_CUSTOM_USER_ATTRIBUTES_ARRAY);
            payload.put(User.APPEND_CUSTOM_ARRAY_ATTRIBUTE, Values.APPEND_CUSTOM_USER_ATTRIBUTES_ARRAY);
            payload.put(User.REMOVE_CUSTOM_ARRAY_ATTRIBUTE, Values.REMOVE_CUSTOM_USER_ATTRIBUTES_ARRAY);

            return payload;
        }
    }

    public static final class Responses {
        private static final String DEFAULT_RESPONSE_ID = "mock";

        private Responses() {
        }

        public static RemoteCommand.Response create(String commandId, String responseId, JSONObject payload) {
            return new RemoteCommand.Response(null, commandId, responseId, payload);
        }

        public static RemoteCommand.Response create(String responseId, JSONObject payload) {
            return create(BrazeRemoteCommand.DEFAULT_COMMAND_ID, responseId, payload);
        }

        public static RemoteCommand.Response create(JSONObject payload) {
            return create(BrazeRemoteCommand.DEFAULT_COMMAND_ID, DEFAULT_RESPONSE_ID, payload);
        }

        public static RemoteCommand.Response initalizeWithApiKeyOnly() throws JSONException {
            JSONObject payload = new JSONObject();
            payload.put(Commands.COMMAND_KEY, Commands.INITIALIZE);
            payload.put(Config.API_KEY, Values.API_KEY);

            return create(payload);
        }

        public static RemoteCommand.Response initializeWithAllSettings() throws JSONException {
            JSONObject payload = new JSONObject();
            payload.put(Commands.COMMAND_KEY, Commands.INITIALIZE);

            payload.put(Config.API_KEY, Values.API_KEY);

            payload.put(Config.FIREBASE_ENABLED, Values.FIREBASE_ENABLED);
            payload.put(Config.FIREBASE_SENDER_ID, Values.FIREBASE_SENDER_ID);
            payload.put(Config.ADM_ENABLED, Values.ADM_ENABLED);
            payload.put(Config.AUTO_PUSH_DEEP_LINKS, Values.AUTO_DEEP_LINKS);
            payload.put(Config.BAD_NETWORK_INTERVAL, Values.BAD_NETWORK_INTERVAL);
            payload.put(Config.GOOD_NETWORK_INTERVAL, Values.GOOD_NETWORK_INTERVAL);
            payload.put(Config.GREAT_NETWORK_INTERVAL, Values.GREAT_NETWORK_INTERVAL);
            payload.put(Config.CUSTOM_ENDPOINT, Values.CUSTOM_ENDPOINT);
            payload.put(Config.DEFAULT_NOTIFICATION_COLOR, Values.DEFAULT_NOTIFICATION_COLOR);
            payload.put(Config.DISABLE_LOCATION, Values.DISABLE_LOCATION);
            payload.put(Config.ENABLE_NEWS_FEED_INDICATOR, Values.ENABLE_NEWS_FEED_INDICATOR);
            payload.put(Config.LARGE_NOTIFICATION_ICON, Values.LARGE_NOTIFICATION_ICON);
            payload.put(Config.SMALL_NOTIFICATION_ICON, Values.SMALL_NOTIFICATION_ICON);
            payload.put(Config.SESSION_TIMEOUT, Values.SESSION_TIMEOUT);
            payload.put(Config.TRIGGER_INTERVAL_SECONDS, Values.TRIGGER_INTERVAL_SECONDS);

            return create(payload);
        }

        public static RemoteCommand.Response wipeData() throws JSONException {
            JSONObject payload = new JSONObject();
            payload.put(Commands.COMMAND_KEY, Commands.WIPE_DATA);

            return create(payload);
        }

        public static RemoteCommand.Response enableSdk() throws JSONException {
            JSONObject payload = new JSONObject();
            payload.put(Commands.COMMAND_KEY, Commands.ENABLE_SDK);

            return create(payload);
        }

        public static RemoteCommand.Response disableSdk() throws JSONException {
            JSONObject payload = new JSONObject();
            payload.put(Commands.COMMAND_KEY, Commands.DISABLE_SDK);

            return create(payload);
        }

        public static RemoteCommand.Response customEventWithProperties() throws JSONException {
            JSONObject payload = new JSONObject();
            payload.put(Commands.COMMAND_KEY, Commands.LOG_CUSTOM_EVENT);
            payload.put(Event.EVENT_NAME, Values.EVENT_NAME);
            payload.put(Event.EVENT_PROPERTIES, Values.EVENT_PROPERTIES);

            return create(payload);
        }

        public static RemoteCommand.Response customEventWithShorthandProperties() throws JSONException {
            JSONObject payload = new JSONObject();
            payload.put(Commands.COMMAND_KEY, Commands.LOG_CUSTOM_EVENT);
            payload.put(Event.EVENT_NAME, Values.EVENT_NAME);
            payload.put(Event.EVENT_PROPERTIES_SHORTHAND, Values.EVENT_PROPERTIES);

            return create(payload);
        }

        public static RemoteCommand.Response customEvent() throws JSONException {
            JSONObject payload = new JSONObject();
            payload.put(Commands.COMMAND_KEY, Commands.LOG_CUSTOM_EVENT);
            payload.put(Event.EVENT_NAME, Values.EVENT_NAME);

            return create(payload);
        }

        public static RemoteCommand.Response purchaseEventWithProperties() throws JSONException {
            JSONObject payload = new JSONObject();
            payload.put(Commands.COMMAND_KEY, Commands.LOG_PURCHASE_EVENT);
            payload.put(Purchase.PRODUCT_ID, Values.PRODUCT_ID);
            payload.put(Purchase.PRODUCT_QTY, Values.PRODUCT_QTY);
            payload.put(Purchase.PRODUCT_PRICE, Values.PRODUCT_PRICE);
            payload.put(Purchase.PURCHASE_PROPERTIES, Values.PRODUCT_PROPERTIES);
            payload.put(Purchase.PRODUCT_CURRENCY, Values.PRODUCT_CURRENCY);

            return create(payload);
        }

        public static RemoteCommand.Response purchaseEventWithShorthandProperties() throws JSONException {
            JSONObject payload = new JSONObject();
            payload.put(Commands.COMMAND_KEY, Commands.LOG_PURCHASE_EVENT);
            payload.put(Purchase.PRODUCT_ID, Values.PRODUCT_ID);
            payload.put(Purchase.PRODUCT_QTY, Values.PRODUCT_QTY);
            payload.put(Purchase.PRODUCT_PRICE, Values.PRODUCT_PRICE);
            payload.put(Purchase.PURCHASE_PROPERTIES_SHORTHAND, Values.PRODUCT_PROPERTIES);
            payload.put(Purchase.PRODUCT_CURRENCY, Values.PRODUCT_CURRENCY);

            return create(payload);
        }

        public static RemoteCommand.Response purchaseEvent() throws JSONException {
            JSONObject payload = new JSONObject();
            payload.put(Commands.COMMAND_KEY, Commands.LOG_PURCHASE_EVENT);
            payload.put(Purchase.PRODUCT_ID, Values.PRODUCT_ID);
            payload.put(Purchase.PRODUCT_QTY, Values.PRODUCT_QTY);
            payload.put(Purchase.PRODUCT_PRICE, Values.PRODUCT_PRICE);
            payload.put(Purchase.PRODUCT_CURRENCY, Values.PRODUCT_CURRENCY);

            return create(payload);
        }

        public static RemoteCommand.Response userAlias() throws JSONException {
            JSONObject payload = new JSONObject();
            payload.put(Commands.COMMAND_KEY, Commands.USER_ALIAS);
            payload.put(User.ALIAS, Values.USER_ALIAS);
            payload.put(User.ALIAS_LABEL, Values.USER_ALIAS_LABEL);

            return create(payload);
        }

        public static RemoteCommand.Response userId() throws JSONException {
            JSONObject payload = new JSONObject();
            payload.put(Commands.COMMAND_KEY, Commands.USER_IDENTIFIER);
            payload.put(User.USER_ID, Values.USER_ID);
            payload.put(User.ALIAS_LABEL, Values.USER_ALIAS_LABEL);

            return create(payload);
        }

        public static RemoteCommand.Response userIdWithAuthSignature() throws JSONException {
            JSONObject payload = new JSONObject();
            payload.put(Commands.COMMAND_KEY, Commands.USER_IDENTIFIER);
            payload.put(User.USER_ID, Values.USER_ID);
            payload.put(User.SDK_AUTH_SIGNATURE, Values.SDK_AUTH_SIGNATURE);

            return create(payload);
        }

        public static RemoteCommand.Response userAllAttributes() throws JSONException {
            JSONObject payload = new JSONObject();
            Collection<String> commandList = new LinkedList<>();
            commandList.add(Commands.INITIALIZE);
            commandList.add(Commands.USER_IDENTIFIER);
            commandList.add(Commands.USER_ATTRIBUTE);
            commandList.add(Commands.USER_ALIAS);

            payload.put(Commands.COMMAND_KEY, String.join(SEPARATOR, commandList));

            payload.put(Config.API_KEY, Values.API_KEY);

            payload.put(User.USER_ID, Values.USER_ID);
            payload.put(User.ALIAS, Values.USER_ALIAS);
            payload.put(User.ALIAS_LABEL, Values.USER_ALIAS_LABEL);
            payload.put(User.SDK_AUTH_SIGNATURE, Values.SDK_AUTH_SIGNATURE);

            payload.put(User.FIRST_NAME, Values.USER_FIRST_NAME);
            payload.put(User.LAST_NAME, Values.USER_LAST_NAME);
            payload.put(User.EMAIL, Values.USER_EMAIL);
            payload.put(User.GENDER, Values.USER_GENDER);
            payload.put(User.LANGUAGE, Values.USER_LANGUAGE);
            payload.put(User.HOME_CITY, Values.USER_HOME_CITY);
            payload.put(User.COUNTRY, Values.USER_COUNTRY);
            payload.put(User.PHONE, Values.USER_PHONE);
            payload.put(User.DATE_OF_BIRTH, Values.DOB_BRAZE_SHORT);

            return create(payload);
        }

        public static RemoteCommand.Response userAllCustomAttributes() throws JSONException {
            JSONObject payload = new JSONObject();
            Collection<String> commandList = new LinkedList<>();
            commandList.add(Commands.INITIALIZE);
            commandList.add(Commands.SET_CUSTOM_ATTRIBUTE);
            commandList.add(Commands.INCREMENT_CUSTOM_ATTRIBUTE);
            commandList.add(Commands.UNSET_CUSTOM_ATTRIBUTE);

            payload.put(Commands.COMMAND_KEY, String.join(SEPARATOR, commandList));
            payload.put(Config.API_KEY, Values.API_KEY);

            payload.put(User.SET_CUSTOM_ATTRIBUTE, Values.SET_CUSTOM_USER_ATTRIBUTES);
            payload.put(User.INCREMENT_CUSTOM_ATTRIBUTE, Values.INCREMENT_CUSTOM_USER_ATTRIBUTES);
            payload.put(User.UNSET_CUSTOM_ATTRIBUTE, Values.UNSET_CUSTOM_USER_ATTRIBUTES);

            return create(payload);
        }

        public static RemoteCommand.Response userAllCustomArrayAttributes() throws JSONException {
            JSONObject payload = new JSONObject();
            Collection<String> commandList = new LinkedList<>();
            commandList.add(Commands.INITIALIZE);
            commandList.add(Commands.SET_CUSTOM_ARRAY_ATTRIBUTE);
            commandList.add(Commands.APPEND_CUSTOM_ARRAY_ATTRIBUTE);
            commandList.add(Commands.REMOVE_CUSTOM_ARRAY_ATTRIBUTE);

            payload.put(Commands.COMMAND_KEY, String.join(SEPARATOR, commandList));
            payload.put(Config.API_KEY, Values.API_KEY);

            payload.put(User.SET_CUSTOM_ARRAY_ATTRIBUTE, Values.SET_CUSTOM_USER_ATTRIBUTES_ARRAY);
            payload.put(User.APPEND_CUSTOM_ARRAY_ATTRIBUTE, Values.APPEND_CUSTOM_USER_ATTRIBUTES_ARRAY);
            payload.put(User.REMOVE_CUSTOM_ARRAY_ATTRIBUTE, Values.REMOVE_CUSTOM_USER_ATTRIBUTES_ARRAY);

            return create(payload);
        }

        public static RemoteCommand.Response requestFlush() throws JSONException {
            JSONObject payload = new JSONObject();
            Collection<String> commandList = new LinkedList<>();
            commandList.add(Commands.FLUSH);

            payload.put(Commands.COMMAND_KEY, String.join(SEPARATOR, commandList));

            return create(payload);
        }

        public static RemoteCommand.Response addToSubscription() throws JSONException {
            JSONObject payload = new JSONObject();
            Collection<String> commandList = new LinkedList<>();
            commandList.add(Commands.ADD_TO_SUBSCRIPTION_GROUP);

            payload.put(Commands.COMMAND_KEY, String.join(SEPARATOR, commandList));
            payload.put(User.SUBSCRIPTION_GROUP_ID, "12345");

            return create(payload);
        }

        public static RemoteCommand.Response removeFromSubscription() throws JSONException {
            JSONObject payload = new JSONObject();
            Collection<String> commandList = new LinkedList<>();
            commandList.add(Commands.REMOVE_FROM_SUBSCRIPTION_GROUP);

            payload.put(Commands.COMMAND_KEY, String.join(SEPARATOR, commandList));
            payload.put(User.SUBSCRIPTION_GROUP_ID, "12345");

            return create(payload);
        }

        public static RemoteCommand.Response setLastKnownLocation_RequiredValues() throws JSONException {
            JSONObject payload = new JSONObject();
            Collection<String> commandList = new LinkedList<>();
            commandList.add(Commands.SET_LAST_KNOWN_LOCATION);

            payload.put(Commands.COMMAND_KEY, String.join(SEPARATOR, commandList));
            payload.put(Location.LOCATION_LATITUDE, 0.0);
            payload.put(Location.LOCATION_LONGITUDE, 1.1);

            return create(payload);
        }

        public static RemoteCommand.Response setLastKnownLocation_AllValues() throws JSONException {
            JSONObject payload = new JSONObject();
            Collection<String> commandList = new LinkedList<>();
            commandList.add(Commands.SET_LAST_KNOWN_LOCATION);

            payload.put(Commands.COMMAND_KEY, String.join(SEPARATOR, commandList));
            payload.put(Location.LOCATION_LATITUDE, 0.0);
            payload.put(Location.LOCATION_LONGITUDE, 1.1);
            payload.put(Location.LOCATION_ALTITUDE, 2.2);
            payload.put(Location.LOCATION_ACCURACY, 3.3);

            return create(payload);
        }

        public static RemoteCommand.Response setSdkAuthSignature() throws JSONException {
            JSONObject payload = new JSONObject();
            Collection<String> commandList = new LinkedList<>();
            commandList.add(Commands.SET_SDK_AUTH_SIGNATURE);

            payload.put(Commands.COMMAND_KEY, String.join(SEPARATOR, commandList));
            payload.put(User.SDK_AUTH_SIGNATURE, Values.SDK_AUTH_SIGNATURE);

            return create(payload);
        }

        public static RemoteCommand.Response setAdTrackingEnabled() throws JSONException {
            JSONObject payload = new JSONObject();
            Collection<String> commandList = new LinkedList<>();
            commandList.add(Commands.SET_AD_TRACKING_ENABLED);

            payload.put(Commands.COMMAND_KEY, String.join(SEPARATOR, commandList));
            payload.put(User.GOOGLE_ADID, Values.GOOGLE_ADID);
            payload.put(User.AD_TRACKING_ENABLED, Values.AD_TRACKING_ENABLED);

            return create(payload);
        }

        public static RemoteCommand.Response setAdTrackingEnabled_MissingAdid() throws JSONException {
            JSONObject payload = new JSONObject();
            Collection<String> commandList = new LinkedList<>();
            commandList.add(Commands.SET_AD_TRACKING_ENABLED);

            payload.put(Commands.COMMAND_KEY, String.join(SEPARATOR, commandList));
            payload.put(User.AD_TRACKING_ENABLED, Values.AD_TRACKING_ENABLED);

            return create(payload);
        }
    }

    public static final class Values {
        private Values() {
        }

        // Config
        public static final String API_KEY = "2abb6f86-dfc6-42bc-9ffb-8a8ff5714c96";
        public static final String FIREBASE_SENDER_ID = "test-id";
        public static final String CUSTOM_ENDPOINT = "custom-endpoint";
        public static final String LARGE_NOTIFICATION_ICON = "large-custom-icon";
        public static final String SMALL_NOTIFICATION_ICON = "small-custom-icon";
        public static final boolean FIREBASE_ENABLED = true;
        public static final boolean ADM_ENABLED = true;
        public static final boolean AUTO_DEEP_LINKS = true;
        public static final boolean DISABLE_LOCATION = true;
        public static final boolean ENABLE_NEWS_FEED_INDICATOR = true;
        public static final Integer BAD_NETWORK_INTERVAL = 30;
        public static final Integer GOOD_NETWORK_INTERVAL = 30;
        public static final Integer GREAT_NETWORK_INTERVAL = 30;
        public static final Integer DEFAULT_NOTIFICATION_COLOR = 0xFF00FF;
        public static final Integer SESSION_TIMEOUT = 10;
        public static final Integer TRIGGER_INTERVAL_SECONDS = 10;
        public static final String SDK_AUTH_SIGNATURE = "signature";
        public static final String DOB_ISO_8601 = "2000-01-01T01:01:01Z";
        public static final String DOB_BRAZE_SHORT = "2000-01-01";
        public static final String DOB_BRAZE_LONG = "2000-01-01 01:01:01";
        public static final String GOOGLE_ADID = "my_adid";
        public static final boolean AD_TRACKING_ENABLED = false;

        // Events
        public static final String EVENT_NAME = "add_to_cart";
        public static final JSONObject EVENT_PROPERTIES = new JSONObject() {{
            try {
                put("custom_string", "String Value");
                put("custom_integer", 2);
                put("custom_double", 2.2);
                put("custom_boolean", true);

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date date = sdf.parse("1970-01-01");

                put("custom_date", date);

            } catch (JSONException jex) {

            } catch (ParseException pex) {

            }
        }};

        // Purchases
        public static final String PRODUCT_ID = "product_id1";
        public static final String PRODUCT_CURRENCY = "GBP";
        public static final Integer PRODUCT_PRICE = 10;
        public static final Integer PRODUCT_QTY = 1;
        public static final JSONObject PRODUCT_PROPERTIES = EVENT_PROPERTIES;

        // Users
        public static final String USER_ID = "test-account@test.com";
        public static final String USER_ALIAS = "test-alias";
        public static final String USER_ALIAS_LABEL = "test-alias-label";
        public static final String USER_FIRST_NAME = "james";
        public static final String USER_LAST_NAME = "keith";
        public static final String USER_GENDER = "male";
        public static final String USER_LANGUAGE = "english";
        public static final String USER_HOME_CITY = "Reading";
        public static final String USER_EMAIL = "email@test.com";
        public static final String USER_PHONE = "++441234567890";
        public static final String USER_COUNTRY = "USA";
        public static final JSONObject SET_CUSTOM_USER_ATTRIBUTES = new JSONObject() {{
            try {
                put("custom_string", null);
                put("custom_integer", 2);
                put("custom_double", 2.2);

            } catch (JSONException jex) {
            }
        }};
        public static final JSONObject INCREMENT_CUSTOM_USER_ATTRIBUTES = new JSONObject() {{
            try {
                put("custom_string", null);
                put("custom_integer", 2);
                put("custom_double", 2.2);

            } catch (JSONException jex) {
            }
        }};
        public static final JSONArray UNSET_CUSTOM_USER_ATTRIBUTES = new JSONArray() {{
            put("custom_string");
            put("custom_integer");
            put("custom_double");
        }};
        public static final JSONObject SET_CUSTOM_USER_ATTRIBUTES_ARRAY = new JSONObject() {{
            try {
                put("custom_array_1", UNSET_CUSTOM_USER_ATTRIBUTES);
                put("custom_array_2", UNSET_CUSTOM_USER_ATTRIBUTES);
            } catch (JSONException jex) {
            }
        }};
        public static final JSONObject APPEND_CUSTOM_USER_ATTRIBUTES_ARRAY = new JSONObject() {{
            try {
                put("custom_array_1", "additional_value_1");
                put("custom_array_2", "additional_value_2");
            } catch (JSONException jex) {
            }
        }};
        public static final JSONObject REMOVE_CUSTOM_USER_ATTRIBUTES_ARRAY = new JSONObject() {{
            try {
                put("custom_array_1", "additional_value_1");
                put("custom_array_2", "additional_value_2");
            } catch (JSONException jex) {
            }
        }};

        public static final double LATITUDE = 0.0;
        public static final double LONGITUDE = 1.1;
        public static final double ALTITUDE = 2.2;
        public static final double ACCURACY = 3.3;
    }

    public static final class Methods {
        private Methods() {
        }

        public static final String INITIALIZE = "initialize";
        public static final String LOG_PURCHASE = "logPurchase";
        public static final String LOG_CUSTOM_EVENT = "logCustomEvent";
        public static final String WIPE_DATA = "wipeData";
        public static final String ENABLE_SDK = "enableSdk";
        public static final String DISABLE_SDK = "disableSdk";
        public static final String SET_USER_ALIAS = "setUserAlias";
        public static final String SET_USER_ID = "setUserId";
        public static final String SET_FIRST_NAME = "setUserFirstName";
        public static final String SET_LAST_NAME = "setUserLastName";
        public static final String SET_EMAIL = "setUserEmail";
        public static final String SET_LANGUAGE = "setUserLanguage";
        public static final String SET_GENDER = "setUserGender";
        public static final String SET_HOME_CITY = "setUserHomeCity";
        public static final String SET_COUNTRY = "setUserCountry";
        public static final String SET_PHONE = "setUserPhone";
        public static final String SET_DATE_OF_BIRTH = "setUserDateOfBirth";
        public static final String SET_USER_CUSTOM_ATTRIBUTE = "setUserCustomAttribute";
        public static final String SET_USER_CUSTOM_ATTRIBUTES = "setUserCustomAttributes";
        public static final String INCREMENT_USER_CUSTOM_ATTRIBUTE = "incrementUserCustomAttribute";
        public static final String INCREMENT_USER_CUSTOM_ATTRIBUTES = "incrementUserCustomAttributes";
        public static final String UNSET_USER_CUSTOM_ATTRIBUTE = "unsetUserCustomAttribute";
        public static final String UNSET_USER_CUSTOM_ATTRIBUTES = "unsetUserCustomAttributes";
        public static final String SET_USER_CUSTOM_ATTRIBUTE_ARRAY = "setUserCustomAttributeArray";
        public static final String SET_USER_CUSTOM_ATTRIBUTES_ARRAY = "setUserCustomAttributeArrays";
        public static final String APPEND_USER_CUSTOM_ATTRIBUTE_ARRAY = "appendUserCustomAttributeArray";
        public static final String APPEND_USER_CUSTOM_ATTRIBUTES_ARRAY = "appendUserCustomAttributeArrays";
        public static final String REMOVE_USER_CUSTOM_ATTRIBUTE_ARRAY = "removeFromUserCustomAttributeArray";
        public static final String REMOVE_USER_CUSTOM_ATTRIBUTES_ARRAY = "removeFromUserCustomAttributeArrays";
        public static final String REQUEST_FLUSH = "requestFlush";
        public static final String ADD_TO_SUBSCRIPTION = "addToSubscriptionGroup";
        public static final String REMOVE_FROM_SUBSCRIPTION = "removeFromSubscriptionGroup";
        public static final String SET_LAST_KNOWN_LOCATION = "setLastKnownLocation";
        public static final String SET_SDK_AUTH_SIGNATURE = "setSdkAuthSignature";
        public static final String SET_AD_TRACKING_ENABLED = "setAdTrackingEnabled";
    }
}
