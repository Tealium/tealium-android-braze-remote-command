package com.tealium.remotecommands.braze;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.app.Activity;
import android.app.Application;

import androidx.test.core.app.ApplicationProvider;

import com.braze.Braze;
import com.braze.BrazeUser;
import com.braze.configuration.BrazeConfig;
import com.braze.enums.DeviceKey;
import com.braze.enums.Gender;
import com.braze.enums.Month;
import com.braze.enums.NotificationSubscriptionType;
import com.braze.models.outgoing.BrazeProperties;
import com.braze.models.recommended.ecommerce.CartUpdatedAction;
import com.braze.models.recommended.ecommerce.CartUpdatedEvent;
import com.braze.models.recommended.ecommerce.CheckoutStartedEvent;
import com.braze.models.recommended.ecommerce.OrderPlacedEvent;
import com.braze.models.recommended.ecommerce.ProductViewedEvent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.robolectric.RobolectricTestRunner;

import java.math.BigDecimal;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

@RunWith(RobolectricTestRunner.class)
public class BrazeInstanceTests {

    Application context = ApplicationProvider.getApplicationContext();
    Activity activity;

    MockedStatic<Braze> mockedBrazeStatic;
    Braze mockBraze;
    BrazeUser mockBrazeUser;
    BrazeInstance brazeInstance;

    @Before
    public void setup() {
        activity = mock();

        mockBraze = mock(Braze.class);
        mockBrazeUser = mock(BrazeUser.class);
        mockedBrazeStatic = mockStatic(Braze.class);
        mockedBrazeStatic.when(() -> Braze.getInstance(context)).thenReturn(mockBraze);
        when(mockBraze.getCurrentUser()).thenReturn(mockBrazeUser);

        brazeInstance = new BrazeInstance(context);
    }

    @After
    public void tearDown() {
        mockedBrazeStatic.close();
    }

    @Test
    public void initialize_InitializesWithNoOptions() {
        brazeInstance.initialize(null, null, null);

        mockedBrazeStatic.verify(() -> {
            Braze.configure(eq(context), any());
        });
    }

    @Test
    public void initialize_InitializesWithProvidedApiKey() {
        ArgumentCaptor<BrazeConfig> config = ArgumentCaptor.forClass(BrazeConfig.class);
        brazeInstance.initialize("api_key", null, null);

        mockedBrazeStatic.verify(() -> {
            Braze.configure(eq(context), config.capture());
        });
        assertEquals("api_key", config.getValue().apiKey);
    }

    @Test
    public void initialize_InitializesWithProvidedOptions() throws JSONException {
        ArgumentCaptor<BrazeConfig> config = ArgumentCaptor.forClass(BrazeConfig.class);
        JSONObject options = new JSONObject();
        options.put(BrazeConstants.Config.FIREBASE_ENABLED, true);
        options.put(BrazeConstants.Config.FIREBASE_SENDER_ID, "test-id");
        options.put(BrazeConstants.Config.FIREBASE_FALLBACK_MESSAGING_SERVICE_CLASSPATH, "test-messaging-classpath");
        options.put(BrazeConstants.Config.ADM_ENABLED, true);
        options.put(BrazeConstants.Config.AUTO_PUSH_DEEP_LINKS, true);
        options.put(BrazeConstants.Config.BAD_NETWORK_INTERVAL, 30);
        options.put(BrazeConstants.Config.GOOD_NETWORK_INTERVAL, 30);
        options.put(BrazeConstants.Config.GREAT_NETWORK_INTERVAL, 30);
        options.put(BrazeConstants.Config.CUSTOM_ENDPOINT, "custom-endpoint");
        options.put(BrazeConstants.Config.DEFAULT_NOTIFICATION_COLOR, 0xFF00FF);
        options.put(BrazeConstants.Config.ENABLE_AUTOMATIC_LOCATION, true);
        options.put(BrazeConstants.Config.FIREBASE_FALLBACK_MESSAGING_SERVICE_ENABLED, true);
        options.put(BrazeConstants.Config.FIREBASE_NEW_TOKEN_ENABLED, true);
        options.put(BrazeConstants.Config.ENABLE_AUTOMATIC_GEOFENCE_REQUESTS, true);
        options.put(BrazeConstants.Config.IS_SDK_AUTHENTICATION_ENABLED, true);
        options.put(BrazeConstants.Config.LARGE_NOTIFICATION_ICON, "large-notification-icon");
        options.put(BrazeConstants.Config.SMALL_NOTIFICATION_ICON, "small-notification-icon");
        options.put(BrazeConstants.Config.DEFAULT_NOTIFICATION_CHANNEL_DESCRIPTION, "notification-channel-description");
        options.put(BrazeConstants.Config.DEFAULT_NOTIFICATION_CHANNEL_NAME, "notification-channel-name");
        options.put(BrazeConstants.Config.SESSION_TIMEOUT, 10);
        options.put(BrazeConstants.Config.TRIGGER_INTERVAL_SECONDS, 10);
        JSONArray deviceOptions = new JSONArray();
        deviceOptions.put("model");
        deviceOptions.put("android_version");
        options.put(BrazeConstants.Config.DEVICE_OPTIONS, deviceOptions);

        brazeInstance.initialize(null, options, null);

        mockedBrazeStatic.verify(() -> {
            Braze.configure(eq(context), config.capture());
        });
        assertTrue(config.getValue().isFirebaseCloudMessagingRegistrationEnabled);
        assertEquals("test-id", config.getValue().firebaseCloudMessagingSenderIdKey);
        assertEquals("test-messaging-classpath", config.getValue().fallbackFirebaseMessagingServiceClasspath);
        assertTrue(config.getValue().isAdmMessagingRegistrationEnabled);
        assertTrue(config.getValue().willHandlePushDeepLinksAutomatically);
        assertEquals(30, config.getValue().badNetworkInterval.intValue());
        assertEquals(30, config.getValue().goodNetworkInterval.intValue());
        assertEquals(30, config.getValue().greatNetworkInterval.intValue());
        assertEquals("custom-endpoint", config.getValue().customEndpoint);
        assertEquals(0xFF00FF, config.getValue().defaultNotificationAccentColor.intValue());
        assertEquals(true, config.getValue().isAutomaticLocationCollectionEnabled);
        assertEquals(true, config.getValue().isFallbackFirebaseMessagingServiceEnabled);
        assertEquals(true, config.getValue().isFirebaseMessagingServiceOnNewTokenRegistrationEnabled);
        assertEquals(true, config.getValue().areAutomaticGeofenceRequestsEnabled);
        assertEquals(true, config.getValue().isSdkAuthEnabled);
        assertEquals("large-notification-icon", config.getValue().largeNotificationIcon);
        assertEquals("small-notification-icon", config.getValue().smallNotificationIcon);
        assertEquals("notification-channel-description", config.getValue().defaultNotificationChannelDescription);
        assertEquals("notification-channel-name", config.getValue().defaultNotificationChannelName);
        assertEquals(10, config.getValue().sessionTimeout.intValue());
        assertEquals(10, config.getValue().triggerActionMinimumTimeIntervalSeconds.intValue());

        EnumSet<DeviceKey> deviceAllowList = config.getValue().deviceObjectAllowlist;
        assertTrue(deviceAllowList.contains(DeviceKey.MODEL));
        assertTrue(deviceAllowList.contains(DeviceKey.ANDROID_VERSION));
        assertEquals(2, deviceAllowList.size());
    }

    @Test
    public void initialize_InitializesWithProvidedOptions_ButAllowsOverrides() throws JSONException {
        ArgumentCaptor<BrazeConfig> config = ArgumentCaptor.forClass(BrazeConfig.class);
        JSONObject options = new JSONObject();
        options.put(BrazeConstants.Config.FIREBASE_ENABLED, true);

        brazeInstance.initialize(null, options, List.of((builder) -> {
            builder.setIsFirebaseCloudMessagingRegistrationEnabled(false);
        }));

        mockedBrazeStatic.verify(() -> {
            Braze.configure(eq(context), config.capture());
        });
        assertFalse(config.getValue().isFirebaseCloudMessagingRegistrationEnabled);
    }

    @Test
    public void initialize_SetsStrictPropertiesEnabled_When_Configured() throws JSONException {
        JSONObject options = new JSONObject();

        // enabled
        options.put(BrazeConstants.Config.STRICT_PROPERTIES_ENABLED, true);
        brazeInstance.initialize(null, options, null);
        assertTrue(brazeInstance.mStrictPropertiesEnabled);

        // disabled
        options.put(BrazeConstants.Config.STRICT_PROPERTIES_ENABLED, false);
        brazeInstance.initialize(null, options, null);
        assertFalse(brazeInstance.mStrictPropertiesEnabled);
    }

    @Test
    public void initialize_SetsStrictPropertiesEnabled_False_When_NotConfigured() throws JSONException {
        JSONObject options = new JSONObject(); // not configured

        brazeInstance.initialize(null, options, null);
        assertFalse(brazeInstance.mStrictPropertiesEnabled);
    }

    @Test
    public void enableSdk_EnablesSdk() {
        brazeInstance.enableSdk();

        mockedBrazeStatic.verify(() -> {
            Braze.enableSdk(context);
        });
    }

    @Test
    public void disableSdk_DisablesSdk() {
        brazeInstance.disableSdk();

        mockedBrazeStatic.verify(() -> {
            Braze.disableSdk(context);
        });
    }

    @Test
    public void wipeData_WipesData() {
        brazeInstance.wipeData();

        mockedBrazeStatic.verify(() -> {
            Braze.wipeData(context);
        });
    }

    @Test
    public void setUserId_ChangesUserIdOnly() {
        brazeInstance.setUserId("user_id", null);

        verify(mockBraze).changeUser("user_id");
    }

    @Test
    public void setUserId_ChangesUserAndSetsSdkAuth() {
        brazeInstance.setUserId("user_id", "auth");

        verify(mockBraze).changeUser("user_id", "auth");
    }

    @Test
    public void setAdTrackingEnabled_ChangesUserIdOnly() {
        brazeInstance.setAdTrackingEnabled("ad_id", true);

        verify(mockBraze).setGoogleAdvertisingId("ad_id", true);
    }

    @Test
    public void setUserAlias_AddsUserAlias() {
        brazeInstance.setUserAlias("user_alias", "alias_label");

        verify(mockBrazeUser).addAlias("user_alias", "alias_label");
    }

    @Test
    public void setUserAlias_DoesNothingWhenEitherParamIsEmpty() {
        brazeInstance.setUserAlias("user_alias", "");
        brazeInstance.setUserAlias("", "alias_label");

        verify(mockBrazeUser, never()).addAlias(any(), any());
    }

    @Test
    public void setUserFirstName_SetsFirstName() {
        brazeInstance.setUserFirstName("name");

        verify(mockBrazeUser).setFirstName("name");
    }

    @Test
    public void setUserLastName_SetsLastName() {
        brazeInstance.setUserLastName("name");

        verify(mockBrazeUser).setLastName("name");
    }

    @Test
    public void setUserEmail_SetsEmail() {
        brazeInstance.setUserEmail("email");

        verify(mockBrazeUser).setEmail("email");
    }

    @Test
    public void setUserLanguage_SetsLanguage() {
        brazeInstance.setUserLanguage("en");

        verify(mockBrazeUser).setLanguage("en");
    }

    @Test
    public void setUserGender_SetsGender() {
        // string conversions tested elsewhere
        brazeInstance.setUserGender("male");

        verify(mockBrazeUser).setGender(Gender.MALE);
    }

    @Test
    public void setUserGender_DoesNotSetGenderWhenInvalidString() {
        brazeInstance.setUserGender("xyz");

        verify(mockBrazeUser, never()).setGender(any());
    }

    @Test
    public void setUserHomeCity_SetsCity() {
        brazeInstance.setUserHomeCity("New York");

        verify(mockBrazeUser).setHomeCity("New York");
    }

    @Test
    public void setUserCountry_SetsCountry() {
        brazeInstance.setUserCountry("USA");

        verify(mockBrazeUser).setCountry("USA");
    }

    @Test
    public void setUserPhone_SetsPhone() {
        brazeInstance.setUserPhone("01234567890");

        verify(mockBrazeUser).setPhoneNumber("01234567890");
    }

    @Test
    public void setUserDateOfBirth_SetsDateOfBirth() {
        TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
        // String conversions tested elsewhere
        brazeInstance.setUserDateOfBirth("2000-01-01");

        verify(mockBrazeUser).setDateOfBirth(2000, Month.JANUARY, 1);
    }

    @Test
    public void setUserCustomAttribute_SetsCustomAttributes() throws Exception {
        JSONObject attributes = new JSONObject();
        attributes.put("int", 100);
        attributes.put("long", 100L);
        attributes.put("double", 100.1);
        attributes.put("boolean", true);

        JSONArray array = new JSONArray();
        array.put(1);
        array.put(2);
        array.put(3);
        attributes.put("array", array);

        JSONObject json = new JSONObject();
        json.put("child_1", 1);
        json.put("child_2", 2);
        json.put("child_3", 3);
        attributes.put("json", json);

        brazeInstance.setUserCustomAttributes(attributes);

        verify(mockBrazeUser).setCustomUserAttribute("int", 100);
        verify(mockBrazeUser).setCustomUserAttribute("long", 100L);
        verify(mockBrazeUser).setCustomUserAttribute("double", 100.1);
        verify(mockBrazeUser).setCustomUserAttribute("boolean", true);
        verify(mockBrazeUser).setCustomUserAttribute("array", array);
        verify(mockBrazeUser).setCustomUserAttribute("json", json);
    }

    @Test
    public void unsetUserCustomAttribute_UnsetsAttributes() {
        JSONArray array = new JSONArray();
        array.put("attr1");
        array.put("attr2");
        array.put("attr3");

        brazeInstance.unsetUserCustomAttributes(array);

        verify(mockBrazeUser).unsetCustomUserAttribute("attr1");
        verify(mockBrazeUser).unsetCustomUserAttribute("attr2");
        verify(mockBrazeUser).unsetCustomUserAttribute("attr3");
    }

    @Test
    public void incrementUserCustomAttribute_IncrementsAttributes() throws Exception {
        JSONObject attributes = new JSONObject();
        attributes.put("attr1", 1);
        attributes.put("attr2", 2);
        attributes.put("attr3", 3);

        brazeInstance.incrementUserCustomAttributes(attributes);

        verify(mockBrazeUser).incrementCustomUserAttribute("attr1", 1);
        verify(mockBrazeUser).incrementCustomUserAttribute("attr2", 2);
        verify(mockBrazeUser).incrementCustomUserAttribute("attr3", 3);
    }

    @Test
    public void removeUserCustomAttribute_RemovesAttributes() throws Exception {
        JSONObject attributes = new JSONObject();
        attributes.put("attr1", "value1");
        attributes.put("attr2", "value2");
        attributes.put("attr3", "value3");

        brazeInstance.removeFromUserCustomAttributeArrays(attributes);

        verify(mockBrazeUser).removeFromCustomAttributeArray("attr1", "value1");
        verify(mockBrazeUser).removeFromCustomAttributeArray("attr2", "value2");
        verify(mockBrazeUser).removeFromCustomAttributeArray("attr3", "value3");
    }

    @Test
    public void setPushNotificationType_SetsNotificationType() throws Exception {
        // other conversions tested elsewhere
        brazeInstance.setPushNotificationSubscriptionType("opted_in");
        brazeInstance.setPushNotificationSubscriptionType("subscribed");
        brazeInstance.setPushNotificationSubscriptionType("unsubscribed");

        verify(mockBrazeUser).setPushNotificationSubscriptionType(NotificationSubscriptionType.OPTED_IN);
        verify(mockBrazeUser).setPushNotificationSubscriptionType(NotificationSubscriptionType.SUBSCRIBED);
        verify(mockBrazeUser).setPushNotificationSubscriptionType(NotificationSubscriptionType.UNSUBSCRIBED);
    }

    @Test
    public void setEmailSubscriptionType_SetsSubscriptionType() throws Exception {
        // other conversions tested elsewhere
        brazeInstance.setEmailSubscriptionType("opted_in");
        brazeInstance.setEmailSubscriptionType("subscribed");
        brazeInstance.setEmailSubscriptionType("unsubscribed");

        verify(mockBrazeUser).setEmailNotificationSubscriptionType(NotificationSubscriptionType.OPTED_IN);
        verify(mockBrazeUser).setEmailNotificationSubscriptionType(NotificationSubscriptionType.SUBSCRIBED);
        verify(mockBrazeUser).setEmailNotificationSubscriptionType(NotificationSubscriptionType.UNSUBSCRIBED);
    }

    @Test
    public void logCustomEvent_LogsEvent_WithoutProperties() throws Exception {
        brazeInstance.logCustomEvent("event", null);

        verify(mockBraze).logCustomEvent("event", null);
    }

    @Test
    public void logCustomEvent_LogsEvent_WithProperties() throws JSONException {
        ArgumentCaptor<BrazeProperties> brazeProps = ArgumentCaptor.forClass(BrazeProperties.class);
        Map<String, Object> data = Map.of(
                "string-prop", "value",
                "int-prop", 10,
                "float-prop", 10.5f,
                "double-prop", 10.5d,
                "long-prop", 100L,
                "false-prop", false,
                "true-prop", true
        );
        JSONObject properties = new JSONObject(data);

        brazeInstance.logCustomEvent("event", properties);

        verify(mockBraze).logCustomEvent(eq("event"), brazeProps.capture());
        assertEquals("value", brazeProps.getValue().get("string-prop"));
        assertEquals(10, brazeProps.getValue().get("int-prop"));
        assertEquals(10.5d, brazeProps.getValue().get("float-prop"));
        assertEquals(10.5d, brazeProps.getValue().get("double-prop"));
        assertEquals(100L, brazeProps.getValue().get("long-prop"));
        assertEquals(false, brazeProps.getValue().get("false-prop"));
        assertEquals(true, brazeProps.getValue().get("true-prop"));
    }

    @Test
    public void logCustomEvent_LogsEvent_WithStrictProperties() throws JSONException {
        brazeInstance.mStrictPropertiesEnabled = true;
        ArgumentCaptor<BrazeProperties> brazeProps = ArgumentCaptor.forClass(BrazeProperties.class);
        Map<String, Object> data = Map.of(
                "string-prop", "value",
                "int-prop", 10,
                "float-prop", 10.5f,
                "double-prop", 10.5d,
                "long-prop", 100L,
                "false-prop", false,
                "true-prop", true
        );
        JSONObject properties = new JSONObject(data);

        brazeInstance.logCustomEvent("event", properties);

        verify(mockBraze).logCustomEvent(eq("event"), brazeProps.capture());
        assertEquals("value", brazeProps.getValue().get("string-prop"));
        assertEquals(10, brazeProps.getValue().get("int-prop"));
        assertEquals(10.5d, brazeProps.getValue().get("float-prop"));
        assertEquals(10.5d, brazeProps.getValue().get("double-prop"));
        assertEquals(100L, brazeProps.getValue().get("long-prop"));
        assertEquals(false, brazeProps.getValue().get("false-prop"));
        assertEquals(true, brazeProps.getValue().get("true-prop"));
    }

    @Test
    public void logCustomEvent_LogsEvent_WithParsedValues_WhenStrictPropertiesDisabled() throws JSONException {
        brazeInstance.mStrictPropertiesEnabled = false;
        ArgumentCaptor<BrazeProperties> brazeProps = ArgumentCaptor.forClass(BrazeProperties.class);
        Map<String, Object> data = Map.of(
                "string-prop", "value",
                "int-prop", "10",
                "float-prop", "10.5",
                "long-prop", "100000000000000",
                "false-prop", "false",
                "true-prop", "true"
        );
        JSONObject properties = new JSONObject(data);

        brazeInstance.logCustomEvent("event", properties);

        verify(mockBraze).logCustomEvent(eq("event"), brazeProps.capture());
        assertEquals("value", brazeProps.getValue().get("string-prop"));
        assertEquals(10, brazeProps.getValue().get("int-prop"));
        assertEquals(10.5d, brazeProps.getValue().get("float-prop"));
        assertEquals(100_000_000_000_000.0, brazeProps.getValue().get("long-prop"));
        assertEquals(false, brazeProps.getValue().get("false-prop"));
        assertEquals(true, brazeProps.getValue().get("true-prop"));
    }

    @Test
    public void logCustomEvent_LogsEvent_WithStringValues_WhenStrictPropertiesEnabled() throws JSONException {
        brazeInstance.mStrictPropertiesEnabled = true;
        ArgumentCaptor<BrazeProperties> brazeProps = ArgumentCaptor.forClass(BrazeProperties.class);
        Map<String, Object> data = Map.of(
                "string-prop", "value",
                "int-prop", "10",
                "float-prop", "10.5",
                "long-prop", "100000000000000",
                "false-prop", "false",
                "true-prop", "true"
        );
        JSONObject properties = new JSONObject(data);

        brazeInstance.logCustomEvent("event", properties);

        verify(mockBraze).logCustomEvent(eq("event"), brazeProps.capture());
        assertEquals("value", brazeProps.getValue().get("string-prop"));
        assertEquals("10", brazeProps.getValue().get("int-prop"));
        assertEquals("10.5", brazeProps.getValue().get("float-prop"));
        assertEquals("100000000000000", brazeProps.getValue().get("long-prop"));
        assertEquals("false", brazeProps.getValue().get("false-prop"));
        assertEquals("true", brazeProps.getValue().get("true-prop"));
    }

    @Test
    public void logPurchase_LogsPurchase_WithoutProperties() throws Exception {
        ArgumentCaptor<BrazeProperties> brazeProps = ArgumentCaptor.forClass(BrazeProperties.class);

        brazeInstance.logPurchase("product1", "GBP", BigDecimal.ONE, 1, null);
        verify(mockBraze).logPurchase(eq("product1"), eq("GBP"), eq(BigDecimal.ONE), eq(1), brazeProps.capture());
        assertEquals(0, brazeProps.getValue().getSize());

        brazeInstance.logPurchase("product1", null, BigDecimal.TEN, 10, null);
        verify(mockBraze).logPurchase(eq("product1"), eq("USD"), eq(BigDecimal.TEN), eq(10), brazeProps.capture());
        assertEquals(0, brazeProps.getValue().getSize());
    }

    @Test
    public void logPurchase_LogsPurchase_WithProperties() throws Exception {
        ArgumentCaptor<BrazeProperties> brazeProps = ArgumentCaptor.forClass(BrazeProperties.class);
        JSONObject properties = new JSONObject();
        properties.put("string-prop", "value");

        brazeInstance.logPurchase("product1", "GBP", BigDecimal.ONE, 1, properties);

        verify(mockBraze).logPurchase(eq("product1"), eq("GBP"), eq(BigDecimal.ONE), eq(1), brazeProps.capture());
        assertEquals("value", brazeProps.getValue().get("string-prop"));
    }

    @Test
    public void logPurchase_LogsMultiplePurchases_WithProperties() throws JSONException {
        ArgumentCaptor<BrazeProperties> brazeProps = ArgumentCaptor.forClass(BrazeProperties.class);
        JSONObject properties = new JSONObject();
        properties.put("string-prop", "value");

        brazeInstance.logPurchase(
                new String[]{"product1", "product2"},
                new String[]{"GBP", null},
                new BigDecimal[]{BigDecimal.ONE, BigDecimal.TEN},
                new Integer[]{10},
                new JSONObject[]{properties});

        verify(mockBraze).logPurchase(eq("product1"), eq("GBP"), eq(BigDecimal.ONE), eq(10), brazeProps.capture());
        assertEquals("value", brazeProps.getValue().get("string-prop"));
        verify(mockBraze).logPurchase(eq("product2"), eq("USD"), eq(BigDecimal.TEN), eq(1), brazeProps.capture());
        assertEquals(0, brazeProps.getValue().getSize());
    }

    /**
     * Builds the nested Ecommerce.PRODUCTS object -- parallel arrays zipped by index -- from a
     * list of flat product field maps. Mirrors the shape BrazeRemoteCommand reads from a real
     * payload (product_id/product_name/variant_id/price/quantity required per index).
     */
    private JSONObject productsObject(JSONObject... products) throws JSONException {
        JSONArray ids = new JSONArray();
        JSONArray names = new JSONArray();
        JSONArray variants = new JSONArray();
        JSONArray prices = new JSONArray();
        JSONArray quantities = new JSONArray();
        for (JSONObject product : products) {
            ids.put(product.opt(BrazeConstants.Ecommerce.PRODUCT_ID));
            names.put(product.opt(BrazeConstants.Ecommerce.PRODUCT_NAME));
            variants.put(product.opt(BrazeConstants.Ecommerce.VARIANT_ID));
            prices.put(product.opt(BrazeConstants.Ecommerce.PRICE));
            quantities.put(product.opt(BrazeConstants.Ecommerce.QUANTITY));
        }
        JSONObject result = new JSONObject();
        result.put(BrazeConstants.Ecommerce.PRODUCT_ID, ids);
        result.put(BrazeConstants.Ecommerce.PRODUCT_NAME, names);
        result.put(BrazeConstants.Ecommerce.VARIANT_ID, variants);
        result.put(BrazeConstants.Ecommerce.PRICE, prices);
        result.put(BrazeConstants.Ecommerce.QUANTITY, quantities);
        return result;
    }

    private JSONObject singleProduct() throws JSONException {
        JSONObject product = new JSONObject();
        product.put(BrazeConstants.Ecommerce.PRODUCT_ID, "sku123");
        product.put(BrazeConstants.Ecommerce.PRODUCT_NAME, "Widget");
        product.put(BrazeConstants.Ecommerce.VARIANT_ID, "widget_blue");
        product.put(BrazeConstants.Ecommerce.PRICE, 49.99);
        product.put(BrazeConstants.Ecommerce.QUANTITY, 2);
        return product;
    }

    private JSONObject secondProduct() throws JSONException {
        JSONObject product = new JSONObject();
        product.put(BrazeConstants.Ecommerce.PRODUCT_ID, "sku456");
        product.put(BrazeConstants.Ecommerce.PRODUCT_NAME, "Gadget");
        product.put(BrazeConstants.Ecommerce.VARIANT_ID, "gadget_red");
        product.put(BrazeConstants.Ecommerce.PRICE, 19.99);
        product.put(BrazeConstants.Ecommerce.QUANTITY, 1);
        return product;
    }

    @Test
    public void logProductViewed_LogsProductViewedEvent() throws JSONException {
        ArgumentCaptor<ProductViewedEvent> event = ArgumentCaptor.forClass(ProductViewedEvent.class);
        JSONObject properties = new JSONObject();
        properties.put("string-prop", "value");

        brazeInstance.logProductViewed("sku123", "Widget", "widget_blue", 49.99, "GBP", "test-source", null, null, properties);

        verify(mockBraze).logEcommerceEvent(event.capture());
        assertEquals("GBP", event.getValue().getCurrency());
        assertEquals("test-source", event.getValue().getSource());
        // ProductViewedEvent wraps its flat product fields (and the custom properties) into a
        // single EcommerceProduct rather than exposing them on the event itself.
        assertEquals(1, event.getValue().getProducts().size());
        assertEquals("sku123", event.getValue().getProducts().get(0).getProductId());
        assertEquals("value", event.getValue().getProducts().get(0).getMetadata().get("string-prop"));
    }

    @Test
    public void logCartUpdated_LogsCartUpdatedEvent_ForEachAction() throws JSONException {
        ArgumentCaptor<CartUpdatedEvent> event = ArgumentCaptor.forClass(CartUpdatedEvent.class);
        JSONObject products = productsObject(singleProduct());

        brazeInstance.logCartUpdated("cart-1", "USD", "test-source", 49.99, BrazeConstants.Ecommerce.Action.ADD, products, null);
        brazeInstance.logCartUpdated("cart-1", "USD", "test-source", null, BrazeConstants.Ecommerce.Action.REMOVE, products, null);
        brazeInstance.logCartUpdated("cart-1", "USD", "test-source", 49.99, BrazeConstants.Ecommerce.Action.REPLACE, products, null);

        verify(mockBraze, org.mockito.Mockito.times(3)).logEcommerceEvent(event.capture());
        assertEquals(CartUpdatedAction.ADD, event.getAllValues().get(0).getAction());
        assertEquals(CartUpdatedAction.REMOVE, event.getAllValues().get(1).getAction());
        assertNull(event.getAllValues().get(1).getTotalValue());
        assertEquals(CartUpdatedAction.REPLACE, event.getAllValues().get(2).getAction());
        assertEquals("cart-1", event.getAllValues().get(0).getCartId());
        assertEquals(1, event.getAllValues().get(0).getProducts().size());
    }

    @Test
    public void logCartUpdated_LogsCartUpdatedEvent_WithMultipleProducts() throws JSONException {
        ArgumentCaptor<CartUpdatedEvent> event = ArgumentCaptor.forClass(CartUpdatedEvent.class);

        brazeInstance.logCartUpdated("cart-1", "USD", "test-source", 69.98, BrazeConstants.Ecommerce.Action.ADD, productsObject(singleProduct(), secondProduct()), null);

        verify(mockBraze).logEcommerceEvent(event.capture());
        assertEquals(2, event.getValue().getProducts().size());
        assertEquals("sku123", event.getValue().getProducts().get(0).getProductId());
        assertEquals("sku456", event.getValue().getProducts().get(1).getProductId());
    }

    @Test
    public void logCheckoutStarted_LogsCheckoutStartedEvent() throws JSONException {
        ArgumentCaptor<CheckoutStartedEvent> event = ArgumentCaptor.forClass(CheckoutStartedEvent.class);

        brazeInstance.logCheckoutStarted("checkout-1", "USD", "test-source", 49.99, productsObject(singleProduct()), null, null);

        verify(mockBraze).logEcommerceEvent(event.capture());
        assertEquals("checkout-1", event.getValue().getCheckoutId());
        assertEquals(49.99, event.getValue().getTotalValue(), 0.0001);
        assertEquals(1, event.getValue().getProducts().size());
        assertNull(event.getValue().getCartId());
    }

    @Test
    public void logCheckoutStarted_LogsCheckoutStartedEvent_WithMultipleProducts() throws JSONException {
        ArgumentCaptor<CheckoutStartedEvent> event = ArgumentCaptor.forClass(CheckoutStartedEvent.class);

        brazeInstance.logCheckoutStarted("checkout-1", "USD", "test-source", 69.98, productsObject(singleProduct(), secondProduct()), null, null);

        verify(mockBraze).logEcommerceEvent(event.capture());
        assertEquals(2, event.getValue().getProducts().size());
        assertEquals("sku123", event.getValue().getProducts().get(0).getProductId());
        assertEquals("sku456", event.getValue().getProducts().get(1).getProductId());
    }

    @Test
    public void logOrderPlaced_LogsOrderPlacedEvent() throws JSONException {
        ArgumentCaptor<OrderPlacedEvent> event = ArgumentCaptor.forClass(OrderPlacedEvent.class);

        brazeInstance.logOrderPlaced("order-1", "USD", "test-source", 49.99, productsObject(singleProduct()), "cart-1", 5.0, null, null);

        verify(mockBraze).logEcommerceEvent(event.capture());
        assertEquals("order-1", event.getValue().getOrderId());
        assertEquals("cart-1", event.getValue().getCartId());
        assertEquals(5.0, event.getValue().getTotalDiscounts(), 0.0001);
        assertEquals(1, event.getValue().getProducts().size());
    }

    @Test
    public void logOrderPlaced_AllowsNullOptionalFields() throws JSONException {
        ArgumentCaptor<OrderPlacedEvent> event = ArgumentCaptor.forClass(OrderPlacedEvent.class);

        brazeInstance.logOrderPlaced("order-1", "USD", "test-source", 49.99, productsObject(singleProduct()), null, null, null, null);

        verify(mockBraze).logEcommerceEvent(event.capture());
        assertNull(event.getValue().getCartId());
        assertNull(event.getValue().getTotalDiscounts());
    }

    @Test
    public void logOrderPlaced_PopulatesDiscounts() throws JSONException {
        ArgumentCaptor<OrderPlacedEvent> event = ArgumentCaptor.forClass(OrderPlacedEvent.class);

        JSONObject discounts = new JSONObject();
        discounts.put(BrazeConstants.Ecommerce.DISCOUNT_CODE, new JSONArray(new String[]{"SUMMER10"}));
        discounts.put(BrazeConstants.Ecommerce.DISCOUNT_AMOUNT, new JSONArray(new double[]{5.0}));
        discounts.put(BrazeConstants.Ecommerce.DISCOUNT_TYPE, new JSONArray(new String[]{"percentage"}));

        brazeInstance.logOrderPlaced("order-1", "USD", "test-source", 49.99, productsObject(singleProduct()), "cart-1", 5.0, discounts, null);

        verify(mockBraze).logEcommerceEvent(event.capture());
        List<Object> resultDiscounts = event.getValue().getDiscounts();
        assertEquals(1, resultDiscounts.size());
        Map<?, ?> resultDiscount = (Map<?, ?>) resultDiscounts.get(0);
        assertEquals("SUMMER10", resultDiscount.get("code"));
        assertEquals(5.0, (Double) resultDiscount.get("amount"), 0.0001);
        assertEquals("percentage", resultDiscount.get("type"));
    }

    @Test
    public void logOrderPlaced_AllowsNullDiscounts() throws JSONException {
        ArgumentCaptor<OrderPlacedEvent> event = ArgumentCaptor.forClass(OrderPlacedEvent.class);

        brazeInstance.logOrderPlaced("order-1", "USD", "test-source", 49.99, productsObject(singleProduct()), "cart-1", 5.0, null, null);

        verify(mockBraze).logEcommerceEvent(event.capture());
        assertTrue(event.getValue().getDiscounts().isEmpty());
    }

    @Test
    public void logOrderPlaced_LogsOrderPlacedEvent_WithMultipleProducts() throws JSONException {
        ArgumentCaptor<OrderPlacedEvent> event = ArgumentCaptor.forClass(OrderPlacedEvent.class);

        brazeInstance.logOrderPlaced("order-1", "USD", "test-source", 69.98, productsObject(singleProduct(), secondProduct()), "cart-1", null, null, null);

        verify(mockBraze).logEcommerceEvent(event.capture());
        assertEquals(2, event.getValue().getProducts().size());
        assertEquals("sku123", event.getValue().getProducts().get(0).getProductId());
        assertEquals("sku456", event.getValue().getProducts().get(1).getProductId());
    }

    @Test
    public void logOrderCancelled_LogsCustomEvent_WithExpectedShape() throws JSONException {
        ArgumentCaptor<BrazeProperties> props = ArgumentCaptor.forClass(BrazeProperties.class);

        JSONObject discounts = new JSONObject();
        discounts.put(BrazeConstants.Ecommerce.DISCOUNT_CODE, new JSONArray(new String[]{"SUMMER10"}));

        JSONObject metadata = new JSONObject();
        metadata.put("note", "vip-customer");

        brazeInstance.logOrderCancelled("order-1", "USD", "test-source", 49.99, 44.99, 3.0, 2.0, productsObject(singleProduct()), "customer_request", 5.0, discounts, metadata);

        verify(mockBraze).logCustomEvent(eq("ecommerce.order_cancelled"), props.capture());
        JSONObject payload = props.getValue().forJsonPut();
        assertEquals("order-1", payload.getString("order_id"));
        assertEquals(49.99, payload.getDouble("total_value"), 0.0001);
        assertEquals(44.99, payload.getDouble("subtotal_value"), 0.0001);
        assertEquals(3.0, payload.getDouble("tax"), 0.0001);
        assertEquals(2.0, payload.getDouble("shipping"), 0.0001);
        assertEquals("USD", payload.getString("currency"));
        assertEquals("customer_request", payload.getString("cancel_reason"));
        assertEquals("test-source", payload.getString("source"));
        assertEquals(5.0, payload.getDouble("total_discounts"), 0.0001);
        assertEquals(1, payload.getJSONArray("discounts").length());
        assertEquals(1, payload.getJSONArray("products").length());
        assertEquals("sku123", payload.getJSONArray("products").getJSONObject(0).getString("product_id"));
        assertEquals("vip-customer", payload.getJSONObject("metadata").getString("note"));
    }

    @Test
    public void logOrderCancelled_OmitsSubtotalTaxShipping_WhenNull() throws JSONException {
        ArgumentCaptor<BrazeProperties> props = ArgumentCaptor.forClass(BrazeProperties.class);

        brazeInstance.logOrderCancelled("order-1", "USD", "test-source", 49.99, null, null, null, productsObject(singleProduct()), "customer_request", null, null, null);

        verify(mockBraze).logCustomEvent(eq("ecommerce.order_cancelled"), props.capture());
        JSONObject payload = props.getValue().forJsonPut();
        assertFalse(payload.has("subtotal_value"));
        assertFalse(payload.has("tax"));
        assertFalse(payload.has("shipping"));
    }

    @Test
    public void logOrderCancelled_RenamesMetadataOnEachProduct() throws JSONException {
        ArgumentCaptor<BrazeProperties> props = ArgumentCaptor.forClass(BrazeProperties.class);
        JSONObject product = singleProduct();
        JSONObject productProps = new JSONObject();
        productProps.put("rewards_member", true);
        product.put(BrazeConstants.Ecommerce.METADATA, productProps);
        JSONObject products = productsObject(product);
        products.put(BrazeConstants.Ecommerce.METADATA, new JSONArray().put(productProps));

        brazeInstance.logOrderCancelled("order-1", "USD", "test-source", 49.99, null, null, null, products, "customer_request", null, null, null);

        verify(mockBraze).logCustomEvent(eq("ecommerce.order_cancelled"), props.capture());
        JSONObject wireProduct = props.getValue().forJsonPut().getJSONArray("products").getJSONObject(0);
        assertTrue(wireProduct.getJSONObject("metadata").getBoolean("rewards_member"));
    }

    @Test
    public void logOrderRefunded_LogsCustomEvent_WithExpectedShape() throws JSONException {
        ArgumentCaptor<BrazeProperties> props = ArgumentCaptor.forClass(BrazeProperties.class);

        JSONObject discounts = new JSONObject();
        discounts.put(BrazeConstants.Ecommerce.DISCOUNT_CODE, new JSONArray(new String[]{"SUMMER10"}));

        brazeInstance.logOrderRefunded("order-1", "USD", "test-source", 49.99, productsObject(singleProduct()), 5.0, discounts, null);

        verify(mockBraze).logCustomEvent(eq("ecommerce.order_refunded"), props.capture());
        JSONObject payload = props.getValue().forJsonPut();
        assertEquals("order-1", payload.getString("order_id"));
        assertEquals(49.99, payload.getDouble("total_value"), 0.0001);
        assertEquals("USD", payload.getString("currency"));
        assertEquals("test-source", payload.getString("source"));
        assertEquals(5.0, payload.getDouble("total_discounts"), 0.0001);
        assertEquals(1, payload.getJSONArray("discounts").length());
        assertEquals(1, payload.getJSONArray("products").length());
        assertFalse(payload.has("cancel_reason"));
    }

    @Test
    public void requestFlush_FlushesSdk() {
        brazeInstance.requestFlush();

        verify(mockBraze).requestImmediateDataFlush();
    }

    @Test
    public void setLastKnownLocation_SetsLastKnownLocation_WhenLatAndLong() {
        brazeInstance.setLastKnownLocation(0.0, 1.1, null, null);

        verify(mockBrazeUser).setLastKnownLocation(0.0, 1.1, null, null);
    }

    @Test
    public void setLastKnownLocation_SetsLastKnownLocation() {
        brazeInstance.setLastKnownLocation(0.0, 1.1, 2.2, 3.3);

        verify(mockBrazeUser).setLastKnownLocation(0.0, 1.1, 2.2, 3.3);
    }

    @Test
    public void addToSubscriptionGroup_AddsToSubscriptionGroup() {
        brazeInstance.addToSubscriptionGroup("group_id");

        verify(mockBrazeUser).addToSubscriptionGroup("group_id");
    }

    @Test
    public void removeFromSubscriptionGroup_RemovesFromSubscriptionGroup() {
        brazeInstance.removeFromSubscriptionGroup("group_id");

        verify(mockBrazeUser).removeFromSubscriptionGroup("group_id");
    }

    @Test
    public void setSdkSignature_SetsSdkSignature() {
        brazeInstance.setSdkAuthSignature("signature");

        verify(mockBraze).setSdkAuthenticationSignature("signature");
    }
}
