package com.tealium.remotecommands.braze;


public final class BrazeConstants {

    private BrazeConstants() {
    }

    public static final String TAG = "Tealium-Braze";
    public static final String SEPARATOR = ",";

    public static final class Commands {
        private Commands() {
        }

        public static final String COMMAND_KEY = "command_name";

        public static final String INITIALIZE = "initialize";
        public static final String ENABLE_SDK = "enablesdk";
        public static final String DISABLE_SDK = "disablesdk";
        public static final String WIPE_DATA = "wipedata";
        public static final String USER_IDENTIFIER = "useridentifier";
        public static final String USER_ALIAS = "useralias";
        public static final String USER_ATTRIBUTE = "userattribute";
        public static final String SET_CUSTOM_ATTRIBUTE = "setcustomattribute";
        public static final String UNSET_CUSTOM_ATTRIBUTE = "unsetcustomattribute";
        public static final String INCREMENT_CUSTOM_ATTRIBUTE = "incrementcustomattribute";
        public static final String SET_CUSTOM_ARRAY_ATTRIBUTE = "setcustomarrayattribute";
        public static final String APPEND_CUSTOM_ARRAY_ATTRIBUTE = "appendcustomarrayattribute";
        public static final String REMOVE_CUSTOM_ARRAY_ATTRIBUTE = "removecustomarrayattribute";
        public static final String EMAIL_NOTIFICATION = "emailnotification";
        public static final String PUSH_NOTIFICATION = "pushnotification";
        public static final String LOG_CUSTOM_EVENT = "logcustomevent";
        public static final String LOG_PURCHASE_EVENT = "logpurchase";
        public static final String LOG_PRODUCT_VIEWED = "logproductviewed";
        public static final String LOG_CART_UPDATED_ADD = "logcartupdatedadd";
        public static final String LOG_CART_UPDATED_REMOVE = "logcartupdatedremove";
        public static final String LOG_CART_UPDATED_REPLACE = "logcartupdatedreplace";
        public static final String LOG_CHECKOUT_STARTED = "logcheckoutstarted";
        public static final String LOG_ORDER_PLACED = "logorderplaced";
        public static final String LOG_ORDER_CANCELLED = "logordercancelled";
        public static final String LOG_ORDER_REFUNDED = "logorderrefunded";
        public static final String FLUSH = "flush";
        public static final String ADD_TO_SUBSCRIPTION_GROUP = "addtosubscriptiongroup";
        public static final String REMOVE_FROM_SUBSCRIPTION_GROUP = "removefromsubscriptiongroup";
        public static final String SET_SDK_AUTH_SIGNATURE = "setsdkauthsignature";
        public static final String SET_LAST_KNOWN_LOCATION = "setlastknownlocation";
        public static final String SET_AD_TRACKING_ENABLED = "setadtrackingenabled";
    }

    public static final class Config {
        private Config() {
        }

        public static final String API_KEY = "api_key";
        public static final String FIREBASE_ENABLED = "firebase_enabled";
        public static final String FIREBASE_NEW_TOKEN_ENABLED = "firebase_new_token_enabled";
        public static final String FIREBASE_SENDER_ID = "firebase_sender_id";
        public static final String FIREBASE_FALLBACK_MESSAGING_SERVICE_ENABLED = "firebase_fallback_messaging_service_enabled";
        public static final String FIREBASE_FALLBACK_MESSAGING_SERVICE_CLASSPATH = "firebase_fallback_messaging_service_classpath";
        public static final String SESSION_TIMEOUT = "session_timeout";
        public static final String CUSTOM_ENDPOINT = "custom_endpoint";
        public static final String ADM_ENABLED = "adm_enabled";
        public static final String AUTO_PUSH_DEEP_LINKS = "auto_push_deep_links";
        public static final String SMALL_NOTIFICATION_ICON = "small_notification_icon";
        public static final String LARGE_NOTIFICATION_ICON = "large_notification_icon";
        public static final String ENABLE_AUTOMATIC_LOCATION = "enable_automatic_location";
        public static final String DEFAULT_NOTIFICATION_COLOR = "default_notification_color";
        public static final String DEFAULT_NOTIFICATION_CHANNEL_DESCRIPTION = "default_notification_channel_description";
        public static final String DEFAULT_NOTIFICATION_CHANNEL_NAME = "default_notification_channel_name";
        public static final String BAD_NETWORK_INTERVAL = "bad_network_interval";
        public static final String GOOD_NETWORK_INTERVAL = "good_network_interval";
        public static final String GREAT_NETWORK_INTERVAL = "great_network_interval";
        public static final String TRIGGER_INTERVAL_SECONDS = "trigger_interval_seconds";
        public static final String ENABLE_GEOFENCES = "enable_geofences";
        public static final String ENABLE_AUTOMATIC_GEOFENCE_REQUESTS = "enable_automatic_geofence_requests";
        public static final String BACKSTACK_ACTIVITY_ENABLED = "backstack_activity_enabled";
        public static final String BACKSTACK_ACTIVITY_CLASS = "backstack_activity_class";
        public static final String IS_SDK_AUTHENTICATION_ENABLED = "is_sdk_authentication_enabled";
        public static final String DEVICE_OPTIONS = "device_options";
        public static final String STRICT_PROPERTIES_ENABLED = "strict_properties_enabled";
    }

    public static final class User {

        private User() {
        }

        public static final String USER_ID = "user_id";
        public static final String FIRST_NAME = "first_name";
        public static final String LAST_NAME = "last_name";
        public static final String GENDER = "gender";
        public static final String LANGUAGE = "language";
        public static final String EMAIL = "email";
        public static final String HOME_CITY = "home_city";
        public static final String DATE_OF_BIRTH = "date_of_birth";
        public static final String COUNTRY = "country";
        public static final String PHONE = "phone";
        public static final String ALIAS = "user_alias";
        public static final String ALIAS_LABEL = "user_alias_label";
        public static final String SET_CUSTOM_ATTRIBUTE = "set_custom_attribute";
        public static final String UNSET_CUSTOM_ATTRIBUTE = "unset_custom_attribute";
        public static final String INCREMENT_CUSTOM_ATTRIBUTE = "increment_custom_attribute";
        public static final String SET_CUSTOM_ARRAY_ATTRIBUTE = "set_custom_array_attribute";
        public static final String APPEND_CUSTOM_ARRAY_ATTRIBUTE = "append_custom_array_attribute";
        public static final String REMOVE_CUSTOM_ARRAY_ATTRIBUTE = "remove_custom_array_attribute";
        public static final String EMAIL_NOTIFICATION = "email_notification";
        public static final String PUSH_NOTIFICATION = "push_notification";
        public static final String SUBSCRIPTION_GROUP_ID = "subscription_group_id";
        public static final String GOOGLE_ADID = "google_adid";
        public static final String AD_TRACKING_ENABLED = "ad_tracking_enabled";
        public static final String SDK_AUTH_SIGNATURE = "sdk_authentication_signature";
    }

    public static final class Event {
        private Event() {
        }

        public static final String EVENT_NAME = "event_name";
        public static final String EVENT_PROPERTIES = "event_properties";
        public static final String EVENT_PROPERTIES_SHORTHAND = "event";
    }

    public static final class Purchase {
        private Purchase() {
        }

        public static final String PRODUCT_ID = "product_id";
        public static final String PRODUCT_QTY = "product_qty";
        public static final String PRODUCT_PRICE = "product_unit_price";
        public static final String PRODUCT_CURRENCY = "product_currency";
        public static final String PURCHASE_PROPERTIES = "purchase_properties";
        public static final String PURCHASE_PROPERTIES_SHORTHAND = "purchase";
    }

    public static final class Ecommerce {
        private Ecommerce() {
        }

        // Event-level fields.
        public static final String CURRENCY = "ecommerce_currency";
        public static final String SOURCE = "ecommerce_source";
        public static final String TOTAL_VALUE = "ecommerce_total_value";
        public static final String SUBTOTAL_VALUE = "ecommerce_subtotal_value"; // optional, order_cancelled
        public static final String TAX = "ecommerce_tax"; // optional, order_cancelled
        public static final String SHIPPING = "ecommerce_shipping"; // optional, order_cancelled
        public static final String TOTAL_DISCOUNTS = "ecommerce_total_discounts";
        public static final String DISCOUNTS = "discounts"; // JSONArray of discount objects, logorderplaced/order_cancelled/order_refunded
        public static final String PROPERTIES = "ecommerce_properties"; // event-level metadata
        public static final String CHECKOUT_ID = "checkout_id";
        public static final String CART_ID = "cart_id";
        public static final String ORDER_ID = "order_id";
        public static final String CANCEL_REASON = "cancel_reason"; // logordercancelled only, required

        // Product-level fields. For cart/checkout/order events these are PARALLEL TOP-LEVEL ARRAYS
        // (product_id: [...], product_unit_price: [...], ...) zipped by index; for a
        // ProductViewedEvent (no products array) they are read as top-level scalars. Key names are
        // unified with the iOS remote command.
        public static final String PRODUCT_ID = "product_id";
        public static final String PRODUCT_NAME = "product_name";
        public static final String VARIANT_ID = "variant_id";
        public static final String PRICE = "product_unit_price";
        public static final String QUANTITY = "product_qty";
        public static final String QUANTITY_FALLBACK = "quantity"; // legacy alias for product_qty
        public static final String IMAGE_URL = "image_url";
        public static final String PRODUCT_URL = "product_url";
        public static final String PRODUCT_PROPERTIES = "product_metadata"; // per-product metadata
    }

    public static final class Location {
        private Location() {
        }

        public static final String LOCATION_LATITUDE = "location_latitude";
        public static final String LOCATION_LONGITUDE = "location_longitude";
        public static final String LOCATION_ALTITUDE = "location_altitude";
        public static final String LOCATION_ACCURACY = "location_accuracy";
    }

    /**
     * Braze {@code ecommerce.order_cancelled} / {@code ecommerce.order_refunded} OUTPUT wire
     * property names, as opposed to {@link Ecommerce}, which holds INPUT payload keys read from
     * the remote command call. These are fixed by the Braze schema and have no safe equivalent in
     * {@link Ecommerce} (its constants carry an "ecommerce_" prefix matching the remote command
     * payload's own key names, e.g. {@code Ecommerce.TOTAL_VALUE} is "ecommerce_total_value", not
     * "total_value"). Do not assume a {@code WireOutputKeys.X} and {@code Ecommerce.X} pair share a
     * value just because they share a name.
     */
    public static final class WireOutputKeys {
        private WireOutputKeys() {
        }

        public static final String TOTAL_VALUE = "total_value";
        public static final String SUBTOTAL_VALUE = "subtotal_value";
        public static final String TAX = "tax";
        public static final String SHIPPING = "shipping";
        public static final String CURRENCY = "currency";
        public static final String PRODUCTS = "products";
        public static final String SOURCE = "source";
        public static final String TOTAL_DISCOUNTS = "total_discounts";
        public static final String METADATA = "metadata";

        // Product-object OUTPUT wire keys, nested inside each element of PRODUCTS. Braze's schema
        // uses bare "price"/"quantity" here, distinct from the "product_unit_price"/"product_qty"
        // INPUT keys in Ecommerce.
        public static final String PRODUCT_ID = "product_id";
        public static final String PRODUCT_NAME = "product_name";
        public static final String VARIANT_ID = "variant_id";
        public static final String PRICE = "price";
        public static final String QUANTITY = "quantity";
        public static final String IMAGE_URL = "image_url";
        public static final String PRODUCT_URL = "product_url";
    }
}
