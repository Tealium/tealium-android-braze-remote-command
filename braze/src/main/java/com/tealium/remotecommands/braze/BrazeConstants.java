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
        public static final String ENABLE_SDK = "enable_sdk";
        public static final String WIPE_DATA = "wipe_data";
        public static final String REGISTER_TOKEN = "registertoken";
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
        public static final String FACEBOOK_USER = "facebookuser";
        public static final String TWITTER_USER = "twitteruser";
        public static final String FLUSH = "flush";
        public static final String ADD_TO_SUBSCRIPTION_GROUP = "addtosubscriptiongroup";
        public static final String REMOVE_FROM_SUBSCRIPTION_GROUP = "removefromsubscriptiongroup";
    }

    public static final class Config {
        private Config() {
        }

        public static final String API_KEY = "api_key";
        public static final String ENABLE_SDK = "enable_sdk";
        public static final String FIREBASE_ENABLED = "firebase_enabled";
        public static final String FIREBASE_SENDER_ID = "firebase_sender_id";
        public static final String SESSION_TIMEOUT = "session_timeout";
        public static final String CUSTOM_ENDPOINT = "custom_endpoint";
        public static final String ADM_ENABLED = "adm_enabled";
        public static final String AUTO_PUSH_DEEP_LINKS = "auto_push_deep_links";
        public static final String SMALL_NOTIFICATION_ICON = "small_notification_icon";
        public static final String LARGE_NOTIFICATION_ICON = "large_notification_icon";
        public static final String DISABLE_LOCATION = "disable_location";
        public static final String ENABLE_NEWS_FEED_INDICATOR = "enable_news_feed_indicator";
        public static final String DEFAULT_NOTIFICATION_COLOR = "default_notification_color";
        @Deprecated
        public static final String LOCALE_MAPPING = "locale_mapping";
        public static final String BAD_NETWORK_INTERVAL = "bad_network_interval";
        public static final String GOOD_NETWORK_INTERVAL = "good_network_interval";
        public static final String GREAT_NETWORK_INTERVAL = "great_network_interval";
        public static final String TRIGGER_INTERVAL_SECONDS = "trigger_interval_seconds";
        public static final String ENABLE_GEOFENCES = "enable_geofences";
        public static final String BACKSTACK_ACTIVITY_ENABLED = "backstack_activity_enabled";
        public static final String BACKSTACK_ACTIVITY_CLASS = "backstack_activity_class";
        public static final String IS_SDK_AUTHENTICATION_ENABLED = "is_sdk_authentication_enabled";
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
        public static final String ALIAS = "user_alias";
        public static final String ALIAS_LABEL = "user_alias_label";
        public static final String SET_CUSTOM_ATTRIBUTE = "set_custom_attribute";
        public static final String UNSET_CUSTOM_ATTRIBUTE = "unset_custom_attribute";
        public static final String INCREMENT_CUSTOM_ATTRIBUTE = "increment_custom_attribute";
        public static final String SET_CUSTOM_ARRAY_ATTRIBUTE = "set_custom_array_attribute";
        public static final String APPEND_CUSTOM_ARRAY_ATTRIBUTE = "append_custom_array_attribute";
        public static final String REMOVE_CUSTOM_ARRAY_ATTRIBUTE = "remove_custom_array_attribute";
        public static final String FACEBOOK_ID = "facebook_id";
        public static final String FRIENDS_COUNT = "friends_count";
        public static final String LIKES = "likes";
        public static final String DESCRIPTION = "description";
        public static final String TWITTER_ID = "twitter_id";
        public static final String TWITTER_NAME = "twitter_name";
        public static final String PROFILE_IMAGE_URL = "profile_image_url";
        public static final String SCREEN_NAME = "screen_name";
        public static final String FOLLOWERS_COUNT = "followers_count";
        public static final String STATUSES_COUNT = "statuses_count";
        public static final String EMAIL_NOTIFICATION = "email_notification";
        public static final String PUSH_NOTIFICATION = "push_notification";
        public static final String PUSH_TOKEN = "push_token";
        public static final String SUBSCRIPTION_GROUP_ID = "subscription_group_id";
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

}
