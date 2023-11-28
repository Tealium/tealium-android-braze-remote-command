package com.tealium.remotecommands.braze;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyFloat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
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
        options.put(BrazeConstants.Config.ADM_ENABLED, true);
        options.put(BrazeConstants.Config.AUTO_PUSH_DEEP_LINKS, true);
        options.put(BrazeConstants.Config.BAD_NETWORK_INTERVAL, 30);
        options.put(BrazeConstants.Config.GOOD_NETWORK_INTERVAL, 30);
        options.put(BrazeConstants.Config.GREAT_NETWORK_INTERVAL, 30);
        options.put(BrazeConstants.Config.CUSTOM_ENDPOINT, "custom-endpoint");
        options.put(BrazeConstants.Config.DEFAULT_NOTIFICATION_COLOR, 0xFF00FF);
        options.put(BrazeConstants.Config.ENABLE_AUTOMATIC_LOCATION, true);
        options.put(BrazeConstants.Config.ENABLE_NEWS_FEED_INDICATOR, true);
        options.put(BrazeConstants.Config.LARGE_NOTIFICATION_ICON, "large-notification-icon");
        options.put(BrazeConstants.Config.SMALL_NOTIFICATION_ICON, "small-notification-icon");
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
        assertTrue(config.getValue().isAdmMessagingRegistrationEnabled);
        assertTrue(config.getValue().willHandlePushDeepLinksAutomatically);
        assertEquals(30, config.getValue().badNetworkInterval.intValue());
        assertEquals(30, config.getValue().goodNetworkInterval.intValue());
        assertEquals(30, config.getValue().greatNetworkInterval.intValue());
        assertEquals("custom-endpoint", config.getValue().customEndpoint);
        assertEquals(0xFF00FF, config.getValue().defaultNotificationAccentColor.intValue());
        assertEquals(true, config.getValue().isLocationCollectionEnabled);
        assertEquals(true, config.getValue().isNewsFeedVisualIndicatorOn);
        assertEquals("large-notification-icon", config.getValue().largeNotificationIcon);
        assertEquals("small-notification-icon", config.getValue().smallNotificationIcon);
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
    public void setUserId_DoesNothing_WhenUserIdIsNull() {
        brazeInstance.setUserId(null, null);

        verify(mockBraze, never()).changeUser(any());
        verify(mockBraze, never()).changeUser(any(), any());
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
    public void setAdTrackingEnabled_DoesNothing_WhenGoogleAdIdIsNull() {
        brazeInstance.setAdTrackingEnabled(null, false);

        verify(mockBraze, never()).setGoogleAdvertisingId(null, false);
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
    public void setUserAlias_DoesNothingWhenEitherParamIsNull() {
        brazeInstance.setUserAlias("user_alias", null);
        brazeInstance.setUserAlias(null, "alias_label");

        verify(mockBrazeUser, never()).addAlias(any(), any());
    }

    @Test
    public void setUserFirstName_SetsFirstName() {
        brazeInstance.setUserFirstName("name");

        verify(mockBrazeUser).setFirstName("name");
    }

    @Test
    public void setUserFirstName_DoesNothingWhenParamIsNull() {
        brazeInstance.setUserFirstName(null);

        verify(mockBrazeUser, never()).setFirstName(any());
    }

    @Test
    public void setUserLastName_SetsLastName() {
        brazeInstance.setUserLastName("name");

        verify(mockBrazeUser).setLastName("name");
    }

    @Test
    public void setUserLastName_DoesNothingWhenParamIsNull() {
        brazeInstance.setUserLastName(null);

        verify(mockBrazeUser, never()).setLastName("name");
    }

    @Test
    public void setUserEmail_SetsEmail() {
        brazeInstance.setUserEmail("email");

        verify(mockBrazeUser).setEmail("email");
    }

    @Test
    public void setUserEmail_DoesNothingWhenParamIsNull() {
        brazeInstance.setUserEmail(null);

        verify(mockBrazeUser, never()).setEmail(any());
    }

    @Test
    public void setUserLanguage_SetsLanguage() {
        brazeInstance.setUserLanguage("en");

        verify(mockBrazeUser).setLanguage("en");
    }

    @Test
    public void setUserLanguage_DoesNothingWhenParamIsNull() {
        brazeInstance.setUserLanguage(null);

        verify(mockBrazeUser, never()).setLanguage(any());
    }

    @Test
    public void setUserGender_SetsGender() {
        // string conversions tested elsewhere
        brazeInstance.setUserGender("male");

        verify(mockBrazeUser).setGender(Gender.MALE);
    }

    @Test
    public void setUserGender_DoesNothingWhenParamIsNull() {
        brazeInstance.setUserGender(null);

        verify(mockBrazeUser, never()).setGender(any());
    }

    @Test
    public void setUserHomeCity_SetsCity() {
        brazeInstance.setUserHomeCity("New York");

        verify(mockBrazeUser).setHomeCity("New York");
    }

    @Test
    public void setUserHomeCity_DoesNothingWhenParamIsNull() {
        brazeInstance.setUserHomeCity(null);

        verify(mockBrazeUser, never()).setHomeCity(any());
    }

    @Test
    public void setUserCountry_SetsCountry() {
        brazeInstance.setUserCountry("USA");

        verify(mockBrazeUser).setCountry("USA");
    }

    @Test
    public void setUserCountry_DoesNothingWhenParamIsNull() {
        brazeInstance.setUserCountry(null);

        verify(mockBrazeUser, never()).setCountry(any());
    }

    @Test
    public void setUserPhone_SetsPhone() {
        brazeInstance.setUserPhone("01234567890");

        verify(mockBrazeUser).setPhoneNumber("01234567890");
    }

    @Test
    public void setUserPhone_DoesNothingWhenParamIsNull() {
        brazeInstance.setUserPhone(null);

        verify(mockBrazeUser, never()).setPhoneNumber(any());
    }

    @Test
    public void setUserDateOfBirth_SetsDateOfBirth() {
        // String conversions tested elsewhere
        brazeInstance.setUserDateOfBirth("2000-01-01");

        verify(mockBrazeUser).setDateOfBirth(2000, Month.JANUARY, 1);
    }

    @Test
    public void setUserDateOfBirth_DoesNothingWhenParamIsNull() {
        brazeInstance.setUserDateOfBirth(null);

        verify(mockBrazeUser, never()).setDateOfBirth(anyInt(), any(), anyInt());
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
    public void setUserCustomAttribute_DoesNothingWhenParamIsNull() {
        brazeInstance.setUserCustomAttributes(null);

        verify(mockBrazeUser, never()).setCustomUserAttribute(any(), anyInt());
        verify(mockBrazeUser, never()).setCustomUserAttribute(any(), anyLong());
        verify(mockBrazeUser, never()).setCustomUserAttribute(any(), anyDouble());
        verify(mockBrazeUser, never()).setCustomUserAttribute(any(), anyFloat());
        verify(mockBrazeUser, never()).setCustomUserAttribute(any(), anyBoolean());
        verify(mockBrazeUser, never()).setCustomUserAttribute(any(), any(JSONArray.class));
        verify(mockBrazeUser, never()).setCustomUserAttribute(any(), any(JSONObject.class));
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
    public void unsetUserCustomAttribute_DoesNothingWhenParamIsNull() {
        brazeInstance.unsetUserCustomAttributes(null);

        verify(mockBrazeUser, never()).unsetCustomUserAttribute(any());
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
    public void incrementUserCustomAttribute_DoesNothingWhenParamIsNull() {
        brazeInstance.incrementUserCustomAttributes(null);

        verify(mockBrazeUser, never()).incrementCustomUserAttribute(any());
        verify(mockBrazeUser, never()).incrementCustomUserAttribute(any(), anyInt());
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
    public void removeUserCustomAttribute_DoesNothingWhenParamIsNull() {
        brazeInstance.removeFromUserCustomAttributeArrays(null);

        verify(mockBrazeUser, never()).removeFromCustomAttributeArray(any(), any());
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
    public void setPushNotificationType_DoesNothingWhenParamIsNull() {
        brazeInstance.setPushNotificationSubscriptionType(null);

        verify(mockBrazeUser, never()).setPushNotificationSubscriptionType(any());
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
    public void setEmailSubscriptionType_DoesNothingWhenParamIsNull() {
        brazeInstance.setEmailSubscriptionType(null);

        verify(mockBrazeUser, never()).setEmailNotificationSubscriptionType(any());
    }

    @Test
    public void logCustomEvent_LogsEvent_WithoutProperties() throws Exception {
        brazeInstance.logCustomEvent("event", null);

        verify(mockBraze).logCustomEvent("event", null);
    }

    @Test
    public void logCustomEvent_LogsEvent_WithProperties() throws JSONException {
        ArgumentCaptor<BrazeProperties> brazeProps = ArgumentCaptor.forClass(BrazeProperties.class);
        JSONObject properties = new JSONObject();
        properties.put("string-prop", "value");

        brazeInstance.logCustomEvent("event", properties);

        verify(mockBraze).logCustomEvent(eq("event"), brazeProps.capture());
        assertEquals("value", brazeProps.getValue().get("string-prop"));
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

    @Test
    public void requestFlush_FlushsSdk() {
        brazeInstance.requestFlush();

        verify(mockBraze).requestImmediateDataFlush();
    }

    @Test
    public void setLastKnownLocation_DoesNothing_WhenMissingRequiredValues() {
        brazeInstance.setLastKnownLocation(null, null, null, null);

        verify(mockBrazeUser, never()).setLastKnownLocation(anyDouble(), anyDouble(), anyDouble(), anyDouble());
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
    public void addToSubscriptionGroup_DoesNothingWhenParamIsNull() {
        brazeInstance.addToSubscriptionGroup(null);

        verify(mockBrazeUser, never()).addToSubscriptionGroup(any());
    }

    @Test
    public void removeFromSubscriptionGroup_RemovesFromSubscriptionGroup() {
        brazeInstance.removeFromSubscriptionGroup("group_id");

        verify(mockBrazeUser).removeFromSubscriptionGroup("group_id");
    }

    @Test
    public void removeFromSubscriptionGroup_DoesNothingWhenParamIsNull() {
        brazeInstance.removeFromSubscriptionGroup(null);

        verify(mockBrazeUser, never()).removeFromSubscriptionGroup(any());
    }

    @Test
    public void setSdkSignature_SetsSdkSignature() {
        brazeInstance.setSdkAuthSignature("signature");

        verify(mockBraze).setSdkAuthenticationSignature("signature");
    }

    @Test
    public void setSdkAuthSignature_DoesNothingWhenParamIsNull() {
        brazeInstance.setSdkAuthSignature(null);

        verify(mockBraze, never()).setSdkAuthenticationSignature(any());
    }
}
