package com.tealium.remotecommands.braze;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import android.app.Activity;
import android.app.Application;

import androidx.test.core.app.ApplicationProvider;

import com.braze.Braze;
import com.tealium.remotecommands.RemoteCommand;
import com.tealium.remotecommands.braze.BrazeConstants.Commands;
import com.tealium.remotecommands.braze.BrazeConstants.Config;
import com.tealium.remotecommands.braze.BrazeConstants.Location;
import com.tealium.remotecommands.braze.BrazeConstants.Purchase;
import com.tealium.remotecommands.braze.BrazeConstants.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockedStatic;
import org.robolectric.RobolectricTestRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@RunWith(RobolectricTestRunner.class)
public class BrazeRemoteCommandTests {

    BrazeRemoteCommand brazeRemoteCommand;
    Application context = ApplicationProvider.getApplicationContext();
    Activity activity;
    BrazeInstance mockBrazeInstance;

    @Before
    public void setup() {
        activity = mock();

        brazeRemoteCommand = new BrazeRemoteCommand(context);

        mockBrazeInstance = mock(BrazeInstance.class);
        brazeRemoteCommand.mBraze = mockBrazeInstance;
    }

    @Test
    public void testInitEventWithApiKey() throws Exception {
        RemoteCommand.Response response = ResponseBuilder.create()
                .addCommand(Commands.INITIALIZE)
                .populatePayload((json) -> {
                    json.put(Config.API_KEY, "api_key");
                })
                .build();

        brazeRemoteCommand.onInvoke(response);

        verify(mockBrazeInstance).initialize(eq("api_key"), any(), any());
    }

    @Test
    public void testInitEventWithOverrides() throws Exception {
        RemoteCommand.Response response = ResponseBuilder.create()
                .addCommand(Commands.INITIALIZE)
                .populatePayload((json) -> {
                    json.put(Config.API_KEY, "api_key");
                })
                .build();

        BrazeRemoteCommand.ConfigOverrider overrides = mock();
        brazeRemoteCommand.registerConfigOverride(overrides);
        brazeRemoteCommand.onInvoke(response);

        verify(mockBrazeInstance).initialize(eq("api_key"), any(), eq(List.of(overrides)));
    }

    @Test
    public void testInitEventWithAllSettings() throws Exception {
        RemoteCommand.Response response = ResponseBuilder.create()
                .addCommand(Commands.INITIALIZE)
                .populatePayload((json) -> {
                    json.put(Config.API_KEY, "api_key");
                    json.put(BrazeConstants.Config.FIREBASE_ENABLED, true);
                    json.put(BrazeConstants.Config.FIREBASE_SENDER_ID, "test-id");
                    json.put(BrazeConstants.Config.ADM_ENABLED, true);
                    json.put(BrazeConstants.Config.AUTO_PUSH_DEEP_LINKS, true);
                    json.put(BrazeConstants.Config.BAD_NETWORK_INTERVAL, 30);
                    json.put(BrazeConstants.Config.GOOD_NETWORK_INTERVAL, 30);
                    json.put(BrazeConstants.Config.GREAT_NETWORK_INTERVAL, 30);
                    json.put(BrazeConstants.Config.CUSTOM_ENDPOINT, "custom-endpoint");
                    json.put(BrazeConstants.Config.DEFAULT_NOTIFICATION_COLOR, 0xFF00FF);
                    json.put(BrazeConstants.Config.DISABLE_LOCATION, true);
                    json.put(BrazeConstants.Config.ENABLE_NEWS_FEED_INDICATOR, true);
                    json.put(BrazeConstants.Config.LARGE_NOTIFICATION_ICON, "large-notification-icon");
                    json.put(BrazeConstants.Config.SMALL_NOTIFICATION_ICON, "small-notification-icon");
                    json.put(BrazeConstants.Config.SESSION_TIMEOUT, 10);
                    json.put(BrazeConstants.Config.TRIGGER_INTERVAL_SECONDS, 10);
                })
                .build();

        brazeRemoteCommand.onInvoke(response);

        verify(mockBrazeInstance).initialize(eq("api_key"), eq(response.getRequestPayload()), any());
    }

    @Test
    public void testCustomEventWithNullProperties() throws Exception {
        RemoteCommand.Response response = ResponseBuilder.create()
                .addCommand(Commands.LOG_CUSTOM_EVENT)
                .populatePayload((json) -> {
                    json.put(BrazeConstants.Event.EVENT_NAME, "event");
                })
                .build();

        brazeRemoteCommand.onInvoke(response);

        verify(mockBrazeInstance).logCustomEvent(eq("event"), eq(null));
    }

    @Test
    public void testCustomEventWithProperties() throws Exception {
        JSONObject eventProps = new JSONObject();
        RemoteCommand.Response response = ResponseBuilder.create()
                .addCommand(Commands.LOG_CUSTOM_EVENT)
                .populatePayload((json) -> {
                    json.put(BrazeConstants.Event.EVENT_NAME, "event");
                    json.put(BrazeConstants.Event.EVENT_PROPERTIES, eventProps);
                })
                .build();

        brazeRemoteCommand.onInvoke(response);

        verify(mockBrazeInstance).logCustomEvent(eq("event"), eq(eventProps));
    }

    @Test
    public void testCustomEventWithShorthandProperties() throws Exception {
        JSONObject eventProps = new JSONObject();
        RemoteCommand.Response response = ResponseBuilder.create()
                .addCommand(Commands.LOG_CUSTOM_EVENT)
                .populatePayload((json) -> {
                    json.put(BrazeConstants.Event.EVENT_NAME, "event");
                    json.put(BrazeConstants.Event.EVENT_PROPERTIES_SHORTHAND, eventProps);
                })
                .build();

        brazeRemoteCommand.onInvoke(response);

        verify(mockBrazeInstance).logCustomEvent(eq("event"), eq(eventProps));
    }

    @Test
    public void testPurchaseEvent() throws Exception {
        RemoteCommand.Response response = ResponseBuilder.create()
                .addCommand(Commands.LOG_PURCHASE_EVENT)
                .populatePayload((json) -> {
                    json.put(Purchase.PRODUCT_ID, "product_id");
                    json.put(Purchase.PRODUCT_QTY, 10);
                    json.put(Purchase.PRODUCT_PRICE, 10.10);
                    json.put(Purchase.PRODUCT_CURRENCY, "GBP");
                })
                .build();

        brazeRemoteCommand.onInvoke(response);

        verify(mockBrazeInstance).logPurchase("product_id", "GBP",  BigDecimal.valueOf(10.10), 10, null);
    }

    @Test
    public void testPurchaseEventWithProperties() throws Exception {
        JSONObject purchaseProps = new JSONObject();
        RemoteCommand.Response response = ResponseBuilder.create()
                .addCommand(Commands.LOG_PURCHASE_EVENT)
                .populatePayload((json) -> {
                    json.put(Purchase.PRODUCT_ID, "product_id");
                    json.put(Purchase.PRODUCT_QTY, 10);
                    json.put(Purchase.PRODUCT_PRICE, 10.10);
                    json.put(Purchase.PRODUCT_CURRENCY, "GBP");
                    json.put(Purchase.PURCHASE_PROPERTIES, purchaseProps);
                })
                .build();

        brazeRemoteCommand.onInvoke(response);

        verify(mockBrazeInstance).logPurchase("product_id", "GBP",  BigDecimal.valueOf(10.10), 10, purchaseProps);
    }

    @Test
    public void testPurchaseEventWithShorthandProperties() throws Exception {
        JSONObject purchaseProps = new JSONObject();
        RemoteCommand.Response response = ResponseBuilder.create()
                .addCommand(Commands.LOG_PURCHASE_EVENT)
                .populatePayload((json) -> {
                    json.put(Purchase.PRODUCT_ID, "product_id");
                    json.put(Purchase.PRODUCT_QTY, 10);
                    json.put(Purchase.PRODUCT_PRICE, 10.10);
                    json.put(Purchase.PRODUCT_CURRENCY, "GBP");
                    json.put(Purchase.PURCHASE_PROPERTIES_SHORTHAND, purchaseProps);
                })
                .build();

        brazeRemoteCommand.onInvoke(response);

        verify(mockBrazeInstance).logPurchase("product_id", "GBP",  BigDecimal.valueOf(10.10), 10, purchaseProps);
    }

    @Test
    public void testWipeData() throws Exception {
        RemoteCommand.Response response = ResponseBuilder.create()
                .addCommand(Commands.WIPE_DATA)
                .build();

        brazeRemoteCommand.onInvoke(response);

        verify(mockBrazeInstance).wipeData();
    }

    @Test
    public void testDisableSdk() throws Exception {
        RemoteCommand.Response response = ResponseBuilder.create()
                .addCommand(Commands.DISABLE_SDK)
                .build();

        brazeRemoteCommand.onInvoke(response);

        verify(mockBrazeInstance).disableSdk();
    }

    @Test
    public void testEnableSdk() throws Exception {
        RemoteCommand.Response response = ResponseBuilder.create()
                .addCommand(Commands.ENABLE_SDK)
                .build();

        brazeRemoteCommand.onInvoke(response);

        verify(mockBrazeInstance).enableSdk();
    }

    @Test
    public void testUserAlias() throws Exception {
        RemoteCommand.Response response = ResponseBuilder.create()
                .addCommand(Commands.USER_ALIAS)
                .populatePayload((json) -> {
                    json.put(User.ALIAS, "alias");
                    json.put(User.ALIAS_LABEL, "alias_label");
                })
                .build();

        brazeRemoteCommand.onInvoke(response);

        verify(mockBrazeInstance).setUserAlias("alias", "alias_label");
    }

    @Test
    public void testUserIdWithoutSdkAuth() throws Exception {
        RemoteCommand.Response response = ResponseBuilder.create()
                .addCommand(Commands.USER_IDENTIFIER)
                .populatePayload((json) -> {
                    json.put(User.USER_ID, "user_id");
                })
                .build();

        brazeRemoteCommand.onInvoke(response);

        verify(mockBrazeInstance).setUserId("user_id", null);
    }

    @Test
    public void testUserIdWithSdkAuth() throws Exception {
        RemoteCommand.Response response = ResponseBuilder.create()
                .addCommand(Commands.USER_IDENTIFIER)
                .populatePayload((json) -> {
                    json.put(User.USER_ID, "user_id");
                    json.put(User.SDK_AUTH_SIGNATURE, "sdk_signature");
                })
                .build();

        brazeRemoteCommand.onInvoke(response);

        verify(mockBrazeInstance).setUserId("user_id", "sdk_signature");
    }

    @Test
    public void testAllUserAttributes() throws Exception {
        RemoteCommand.Response response = ResponseBuilder.create()
                .addCommand(Commands.USER_ATTRIBUTE)
                .populatePayload((json) -> {
                    json.put(User.FIRST_NAME, "first_name");
                    json.put(User.LAST_NAME, "last_name");
                    json.put(User.EMAIL, "test@test.com");
                    json.put(User.GENDER, "male");
                    json.put(User.LANGUAGE, "en");
                    json.put(User.HOME_CITY, "New York");
                    json.put(User.COUNTRY, "USA");
                    json.put(User.PHONE, "++01234567890");
                    json.put(User.DATE_OF_BIRTH, "2000-01-01");
                })
                .build();

        brazeRemoteCommand.onInvoke(response);

        verify(mockBrazeInstance).setUserFirstName("first_name");
        verify(mockBrazeInstance).setUserLastName("last_name");
        verify(mockBrazeInstance).setUserEmail("test@test.com");
        verify(mockBrazeInstance).setUserGender("male");
        verify(mockBrazeInstance).setUserLanguage("en");
        verify(mockBrazeInstance).setUserCountry("USA");
        verify(mockBrazeInstance).setUserHomeCity("New York");
        verify(mockBrazeInstance).setUserPhone("++01234567890");
        verify(mockBrazeInstance).setUserDateOfBirth("2000-01-01");
    }

    @Test
    public void testSetCustomUserAttribute() throws Exception {
        JSONObject customAttributes = new JSONObject();
        RemoteCommand.Response response = ResponseBuilder.create()
                .addCommand(Commands.SET_CUSTOM_ATTRIBUTE)
                .populatePayload((json) -> {
                    json.put(User.SET_CUSTOM_ATTRIBUTE, customAttributes);
                })
                .build();

        brazeRemoteCommand.onInvoke(response);

        verify(mockBrazeInstance).setUserCustomAttributes(customAttributes);
    }

    @Test
    public void testIncrementCustomUserAttribute() throws Exception {
        JSONObject customAttributes = new JSONObject();
        RemoteCommand.Response response = ResponseBuilder.create()
                .addCommand(Commands.INCREMENT_CUSTOM_ATTRIBUTE)
                .populatePayload((json) -> {
                    json.put(User.INCREMENT_CUSTOM_ATTRIBUTE, customAttributes);
                })
                .build();

        brazeRemoteCommand.onInvoke(response);

        verify(mockBrazeInstance).incrementUserCustomAttributes(customAttributes);
    }

    @Test
    public void testUnsetCustomUserAttribute() throws Exception {
        JSONArray customAttributes = new JSONArray();
        RemoteCommand.Response response = ResponseBuilder.create()
                .addCommand(Commands.UNSET_CUSTOM_ATTRIBUTE)
                .populatePayload((json) -> {
                    json.put(User.UNSET_CUSTOM_ATTRIBUTE, customAttributes);
                })
                .build();

        brazeRemoteCommand.onInvoke(response);

        verify(mockBrazeInstance).unsetUserCustomAttributes(customAttributes);
    }

    @Test
    public void testSetCustomUserArrayAttribute() throws Exception {
        JSONObject customAttributes = new JSONObject();

        RemoteCommand.Response response = ResponseBuilder.create()
                .addCommand(Commands.SET_CUSTOM_ARRAY_ATTRIBUTE)
                .populatePayload((json) -> {
                    json.put(User.SET_CUSTOM_ARRAY_ATTRIBUTE, customAttributes);
                })
                .build();

        brazeRemoteCommand.onInvoke(response);

        verify(mockBrazeInstance).setUserCustomAttributeArrays(customAttributes);
    }

    @Test
    public void testRemoveCustomUserArrayAttribute() throws Exception {
        JSONObject customAttributes = new JSONObject();

        RemoteCommand.Response response = ResponseBuilder.create()
                .addCommand(Commands.REMOVE_CUSTOM_ARRAY_ATTRIBUTE)
                .populatePayload((json) -> {
                    json.put(User.REMOVE_CUSTOM_ARRAY_ATTRIBUTE, customAttributes);
                })
                .build();

        brazeRemoteCommand.onInvoke(response);

        verify(mockBrazeInstance).removeFromUserCustomAttributeArrays(customAttributes);
    }

    @Test
    public void testAppendCustomUserArrayAttribute() throws Exception {
        JSONObject customAttributes = new JSONObject();

        RemoteCommand.Response response = ResponseBuilder.create()
                .addCommand(Commands.APPEND_CUSTOM_ARRAY_ATTRIBUTE)
                .populatePayload((json) -> {
                    json.put(User.APPEND_CUSTOM_ARRAY_ATTRIBUTE, customAttributes);
                })
                .build();

        brazeRemoteCommand.onInvoke(response);

        verify(mockBrazeInstance).appendUserCustomAttributeArrays(customAttributes);
    }


    @Test
    public void testRequestFlush() throws Exception {
        RemoteCommand.Response response = ResponseBuilder.create()
                .addCommand(Commands.FLUSH)
                .build();

        brazeRemoteCommand.onInvoke(response);

        verify(mockBrazeInstance).requestFlush();
    }

    @Test
    public void testAddToSubscriptionId() throws Exception {
        RemoteCommand.Response response = ResponseBuilder.create()
                .addCommand(Commands.ADD_TO_SUBSCRIPTION_GROUP)
                .populatePayload((json) -> {
                    json.put(User.SUBSCRIPTION_GROUP_ID, "12345");
                })
                .build();

        brazeRemoteCommand.onInvoke(response);

        verify(mockBrazeInstance).addToSubscriptionGroup("12345");
    }

    @Test
    public void testRemoveFromSubscriptionId() throws Exception {
        RemoteCommand.Response response = ResponseBuilder.create()
                .addCommand(Commands.REMOVE_FROM_SUBSCRIPTION_GROUP)
                .populatePayload((json) -> {
                    json.put(User.SUBSCRIPTION_GROUP_ID, "12345");
                })
                .build();

        brazeRemoteCommand.onInvoke(response);

        verify(mockBrazeInstance).removeFromSubscriptionGroup("12345");
    }

    @Test
    public void testSetSdkAuthSignature() throws Exception {
        RemoteCommand.Response response = ResponseBuilder.create()
                .addCommand(Commands.SET_SDK_AUTH_SIGNATURE)
                .populatePayload((json) -> {
                    json.put(User.SDK_AUTH_SIGNATURE, "12345");
                })
                .build();

        brazeRemoteCommand.onInvoke(response);

        verify(mockBrazeInstance).setSdkAuthSignature("12345");
    }

    @Test
    public void testSetLastLocation_MissingRequiredParams() throws Exception {
        RemoteCommand.Response response = ResponseBuilder.create()
                .addCommand(Commands.SET_LAST_KNOWN_LOCATION)
                .populatePayload((json) -> {
                    json.put(Location.LOCATION_LATITUDE, 0.0);
                })
                .build();

        brazeRemoteCommand.onInvoke(response);

        verify(mockBrazeInstance, never()).setLastKnownLocation(any(), any(),any(), any());
    }

    @Test
    public void testSetLastLocation_RequiredParams() throws Exception {
        RemoteCommand.Response response = ResponseBuilder.create()
                .addCommand(Commands.SET_LAST_KNOWN_LOCATION)
                .populatePayload((json) -> {
                    json.put(Location.LOCATION_LATITUDE, 0.0);
                    json.put(Location.LOCATION_LONGITUDE, 1.1);
                })
                .build();

        brazeRemoteCommand.onInvoke(response);

        verify(mockBrazeInstance).setLastKnownLocation(0.0, 1.1, null, null);
    }

    @Test
    public void testSetLastLocation_AllParams() throws Exception {
        RemoteCommand.Response response = ResponseBuilder.create()
                .addCommand(Commands.SET_LAST_KNOWN_LOCATION)
                .populatePayload((json) -> {
                    json.put(Location.LOCATION_LATITUDE, 0.0);
                    json.put(Location.LOCATION_LONGITUDE, 1.1);
                    json.put(Location.LOCATION_ALTITUDE, 2.2);
                    json.put(Location.LOCATION_ACCURACY, 3.3);
                })
                .build();

        brazeRemoteCommand.onInvoke(response);

        verify(mockBrazeInstance).setLastKnownLocation(0.0, 1.1, 2.2, 3.3);
    }

    @Test
    public void testSetAdTrackingEnabled() throws Exception {
        RemoteCommand.Response response = ResponseBuilder.create()
                .addCommand(Commands.SET_AD_TRACKING_ENABLED)
                .populatePayload((json) -> {
                    json.put(User.GOOGLE_ADID, "google_adid");
                    json.put(User.AD_TRACKING_ENABLED, true);
                })
                .build();

        brazeRemoteCommand.onInvoke(response);

        verify(mockBrazeInstance).setAdTrackingEnabled("google_adid", false);
    }

    @Test
    public void testSetAdTrackingEnabled_MissingParams() throws Exception {
        RemoteCommand.Response response = ResponseBuilder.create()
                .addCommand(Commands.SET_AD_TRACKING_ENABLED)
                .build();

        brazeRemoteCommand.onInvoke(response);

        verify(mockBrazeInstance, never()).setAdTrackingEnabled(null, false);
    }

    private static class ResponseBuilder {

        private final List<String> commands;
        private final JSONObject payload;
        private String commandId;
        private String responseId;

        private ResponseBuilder() {
            commands = new ArrayList<>();
            payload = new JSONObject();
            commandId = null;
            responseId = null;
        }

        static ResponseBuilder create() {
            return new ResponseBuilder();
        }

        public ResponseBuilder addCommand(String commandName) {
            commands.add(commandName);
            return this;
        }

        public ResponseBuilder setCommandId(String commandId) {
            this.commandId = commandId;
            return this;
        }

        public ResponseBuilder setResponseId(String responseId) {
            this.responseId = responseId;
            return this;
        }

        public ResponseBuilder populatePayload(JsonBuilder jsonBuilder) {
            try {
                jsonBuilder.populateJson(payload);
            } catch (JSONException jex) {
                Assert.fail(jex.getMessage());
                jex.printStackTrace();
            }
            return this;
        }

        public RemoteCommand.Response build() {
            try {
                this.payload.put(Commands.COMMAND_KEY, String.join(BrazeConstants.SEPARATOR, commands));
            } catch (JSONException jex) {
                Assert.fail();
            }
            return new RemoteCommand.Response(
                    null,
                    this.commandId != null ? this.commandId : "",
                    this.responseId != null ? this.responseId : "",
                    this.payload
            );
        }
    }

    @FunctionalInterface
    private interface JsonBuilder {
        void populateJson(JSONObject json) throws JSONException;
    }
}
