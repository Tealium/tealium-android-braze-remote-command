package com.tealium.remotecommands.braze;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.runner.AndroidJUnit4;

import com.braze.Braze;
import com.braze.configuration.BrazeConfig;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.junit.runner.RunWith;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class BrazeRemoteCommandTests {

    MockBrazeRemoteCommand brazeRemoteCommand;
    Application application;

    public BrazeRemoteCommandTests() {
        super();
    }

    @Rule
    public ActivityScenarioRule<com.tealium.remotecommands.braze.QAActivity> QAActivity = new ActivityScenarioRule<>(com.tealium.remotecommands.braze.QAActivity.class);

    @Before
    public void setup() {
        application = (Application) ApplicationProvider.getApplicationContext();

        Braze.enableSdk(application);
        Braze.enableMockNetworkRequestsAndDropEventsMode();

        brazeRemoteCommand = newMockRemoteCommand();
    }

    @Test
    public void testInitEventWithApiKey() {
        Collection<String> expectedMethods = new ArrayList<>();
        expectedMethods.add(TestData.Methods.INITIALIZE);
        QAActivity.getScenario().onActivity(activity -> {
            try {
                MockBrazeInstance mockBrazeInstance = new MockBrazeInstance(application, activity) {
                    @Override
                    public void initialize(String apiKey, JSONObject launchOptions) {
                        Assert.assertEquals("apiKey does not match.", TestData.Values.API_KEY, apiKey);
                        Assert.assertTrue("launchOptions should be empty", BrazeUtils.isNullOrEmpty(launchOptions));
                        super.initialize(apiKey, launchOptions);
                    }
                };

                brazeRemoteCommand.setBrazeCommand(mockBrazeInstance);
                setupInitTestWithApiKey(brazeRemoteCommand);

                brazeRemoteCommand.onInvoke(TestData.Responses.initalizeWithApiKeyOnly());
                TestUtils.assertContainsAllAndOnly(mockBrazeInstance.methodsCalled, expectedMethods);

            } catch (Exception e) {
                e.printStackTrace();
                Assert.fail();
            }
        });
    }

    @Test
    public void testInitEventWithOverrides() {
        Collection<String> expectedMethods = new ArrayList<>();
        expectedMethods.add(TestData.Methods.INITIALIZE);
        QAActivity.getScenario().onActivity(activity -> {
            try {
                MockBrazeInstance mockBrazeInstance = new MockBrazeInstance(application, activity) {
                    @Override
                    public void initialize(@NonNull String apiKey, JSONObject launchOptions, List<BrazeRemoteCommand.ConfigOverrider> overrides) {
                        Assert.assertEquals("apiKey does not match.", TestData.Values.API_KEY, apiKey);
                        Assert.assertTrue("launchOptions should only contain api_key and command", launchOptions.has(BrazeConstants.Config.API_KEY) && launchOptions.has(BrazeConstants.Commands.COMMAND_KEY) && launchOptions.length() <= 2);
                        Assert.assertEquals("overrides should have two entries", 2, overrides.size());

                        super.initialize(apiKey, launchOptions, overrides);
                    }
                };
                brazeRemoteCommand.setBrazeCommand(mockBrazeInstance);
                setupInitTestWithOverrides(brazeRemoteCommand);

                brazeRemoteCommand.onInvoke(TestData.Responses.initalizeWithApiKeyOnly());
                TestUtils.assertContainsAllAndOnly(mockBrazeInstance.methodsCalled, expectedMethods);

            } catch (Exception e) {
                e.printStackTrace();
                Assert.fail();
            }
        });
    }

    @Test
    public void testInitEventWithAllSettings() {
        Collection<String> expectedMethods = new ArrayList<>();
        expectedMethods.add(TestData.Methods.INITIALIZE);
        QAActivity.getScenario().onActivity(activity -> {
            try {
                MockBrazeInstance mockBrazeInstance = new MockBrazeInstance(application, activity) {
                    // all settings are verified in an override created by setupInitTestWithAllSettings(..)
                };
                brazeRemoteCommand.setBrazeCommand(mockBrazeInstance);
                setupInitTestWithAllSettings(brazeRemoteCommand);

                brazeRemoteCommand.onInvoke(TestData.Responses.initializeWithAllSettings());
                TestUtils.assertContainsAllAndOnly(mockBrazeInstance.methodsCalled, expectedMethods);

            } catch (Exception e) {
                e.printStackTrace();
                Assert.fail();
            }
        });
    }

    @Test
    public void testCustomEventWithProperties() {
        Collection<String> expectedMethods = new ArrayList<>();
        expectedMethods.add(TestData.Methods.LOG_CUSTOM_EVENT);
        QAActivity.getScenario().onActivity(activity -> {
            try {
                MockBrazeInstance mockBrazeInstance = new MockBrazeInstance(application, activity) {
                    @Override
                    public void logCustomEvent(@NonNull String eventName, JSONObject eventProperties) {
                        Assert.assertEquals("eventName parameter does not match what was sent", TestData.Values.EVENT_NAME, eventName);
                        Assert.assertEquals("eventroperties parameter does not match what was sent", eventProperties.toString(), TestData.Values.EVENT_PROPERTIES.toString());
                        super.logCustomEvent(eventName, eventProperties);
                    }
                };
                brazeRemoteCommand.setBrazeCommand(mockBrazeInstance);

                brazeRemoteCommand.onInvoke(TestData.Responses.customEventWithProperties());
                TestUtils.assertContainsAllAndOnly(mockBrazeInstance.methodsCalled, expectedMethods);

            } catch (Exception e) {
                e.printStackTrace();
                Assert.fail();
            }
        });
    }

    @Test
    public void testCustomEventWithShorthandProperties() {
        Collection<String> expectedMethods = new ArrayList<>();
        expectedMethods.add(TestData.Methods.LOG_CUSTOM_EVENT);
        QAActivity.getScenario().onActivity(activity -> {
            try {
                MockBrazeInstance mockBrazeInstance = new MockBrazeInstance(application, activity) {
                    @Override
                    public void logCustomEvent(@NonNull String eventName, JSONObject eventProperties) {
                        Assert.assertEquals("eventName parameter does not match what was sent", TestData.Values.EVENT_NAME, eventName);
                        Assert.assertEquals("eventroperties parameter does not match what was sent", eventProperties.toString(), TestData.Values.EVENT_PROPERTIES.toString());
                        super.logCustomEvent(eventName, eventProperties);
                    }
                };
                brazeRemoteCommand.setBrazeCommand(mockBrazeInstance);

                brazeRemoteCommand.onInvoke(TestData.Responses.customEventWithShorthandProperties());
                TestUtils.assertContainsAllAndOnly(mockBrazeInstance.methodsCalled, expectedMethods);

            } catch (Exception e) {
                e.printStackTrace();
                Assert.fail();
            }
        });
    }

    @Test
    public void testCustomEvent() {
        Collection<String> expectedMethods = new ArrayList<>();
        expectedMethods.add(TestData.Methods.LOG_CUSTOM_EVENT);
        QAActivity.getScenario().onActivity(activity -> {
            try {
                MockBrazeInstance mockBrazeInstance = new MockBrazeInstance(application, activity) {
                    @Override
                    public void logCustomEvent(@NonNull String eventName, JSONObject eventProperties) {
                        Assert.assertEquals("eventName parameter does not match what was sent", TestData.Values.EVENT_NAME, eventName);
                        Assert.assertTrue("eventProperties param should be null or empty", BrazeUtils.isNullOrEmpty(eventProperties));
                        super.logCustomEvent(eventName, eventProperties);
                    }
                };
                brazeRemoteCommand.setBrazeCommand(mockBrazeInstance);

                brazeRemoteCommand.onInvoke(TestData.Responses.customEvent());
                TestUtils.assertContainsAllAndOnly(mockBrazeInstance.methodsCalled, expectedMethods);

            } catch (Exception e) {
                e.printStackTrace();
                Assert.fail();
            }
        });
    }

    @Test
    public void testPurchaseEvent() {
        Collection<String> expectedMethods = new ArrayList<>();
        expectedMethods.add(TestData.Methods.LOG_PURCHASE);
        QAActivity.getScenario().onActivity(activity -> {
            try {
                MockBrazeInstance mockBrazeInstance = new MockBrazeInstance(application, activity) {
                    @Override
                    public void logPurchase(@NonNull String productId, String currency, @NonNull BigDecimal unitPrice, Integer quantity, JSONObject purchaseProerties) {
                        Assert.assertEquals("productId does not match what was sent.", TestData.Values.PRODUCT_ID, productId);
                        Assert.assertEquals("currency does not match what was sent.", TestData.Values.PRODUCT_CURRENCY, currency);
                        Assert.assertEquals("unitPrice does not match what was sent.", (int) TestData.Values.PRODUCT_PRICE, unitPrice.intValue());
                        Assert.assertEquals("quantity does not match what was sent.", TestData.Values.PRODUCT_QTY, quantity);

                        Assert.assertTrue("purchaseProperties is supposed to be null or empty.", BrazeUtils.isNullOrEmpty(purchaseProerties));
                        super.logPurchase(productId, currency, unitPrice, quantity, purchaseProerties);
                    }
                };
                brazeRemoteCommand.setBrazeCommand(mockBrazeInstance);

                brazeRemoteCommand.onInvoke(TestData.Responses.purchaseEvent());
                TestUtils.assertContainsAllAndOnly(mockBrazeInstance.methodsCalled, expectedMethods);

            } catch (Exception e) {
                e.printStackTrace();
                Assert.fail();
            }
        });
    }

    @Test
    public void testPurchaseEventWithProperties() {
        Collection<String> expectedMethods = new ArrayList<>();
        expectedMethods.add(TestData.Methods.LOG_PURCHASE);
        QAActivity.getScenario().onActivity(activity -> {
            try {
                MockBrazeInstance mockBrazeInstance = new MockBrazeInstance(application, activity) {
                    @Override
                    public void logPurchase(@NonNull String productId, String currency, @NonNull BigDecimal unitPrice, Integer quantity, JSONObject purchaseProerties) {
                        Assert.assertEquals("productId does not match what was sent.", TestData.Values.PRODUCT_ID, productId);
                        Assert.assertEquals("currency does not match what was sent.", TestData.Values.PRODUCT_CURRENCY, currency);
                        Assert.assertEquals("unitPrice does not match what was sent.", (int) TestData.Values.PRODUCT_PRICE, unitPrice.intValue());
                        Assert.assertEquals("quantity does not match what was sent.", TestData.Values.PRODUCT_QTY, quantity);

                        Assert.assertEquals("purchaseProperties does not match what was sent.", TestData.Values.PRODUCT_PROPERTIES.toString(), purchaseProerties.toString());
                        super.logPurchase(productId, currency, unitPrice, quantity, purchaseProerties);
                    }
                };
                brazeRemoteCommand.setBrazeCommand(mockBrazeInstance);

                brazeRemoteCommand.onInvoke(TestData.Responses.purchaseEventWithProperties());
                TestUtils.assertContainsAllAndOnly(mockBrazeInstance.methodsCalled, expectedMethods);
            } catch (Exception e) {
                e.printStackTrace();
                Assert.fail();
            }
        });
    }

    @Test
    public void testPurchaseEventWithShorthandProperties() {
        Collection<String> expectedMethods = new ArrayList<>();
        expectedMethods.add(TestData.Methods.LOG_PURCHASE);
        QAActivity.getScenario().onActivity(activity -> {
            try {
                MockBrazeInstance mockBrazeInstance = new MockBrazeInstance(application, activity) {
                    @Override
                    public void logPurchase(@NonNull String productId, String currency, @NonNull BigDecimal unitPrice, Integer quantity, JSONObject purchaseProerties) {
                        Assert.assertEquals("productId does not match what was sent.", TestData.Values.PRODUCT_ID, productId);
                        Assert.assertEquals("currency does not match what was sent.", TestData.Values.PRODUCT_CURRENCY, currency);
                        Assert.assertEquals("unitPrice does not match what was sent.", (int) TestData.Values.PRODUCT_PRICE, unitPrice.intValue());
                        Assert.assertEquals("quantity does not match what was sent.", TestData.Values.PRODUCT_QTY, quantity);

                        Assert.assertEquals("purchaseProperties does not match what was sent.", TestData.Values.PRODUCT_PROPERTIES.toString(), purchaseProerties.toString());
                        super.logPurchase(productId, currency, unitPrice, quantity, purchaseProerties);
                    }
                };
                brazeRemoteCommand.setBrazeCommand(mockBrazeInstance);

                brazeRemoteCommand.onInvoke(TestData.Responses.purchaseEventWithShorthandProperties());
                TestUtils.assertContainsAllAndOnly(mockBrazeInstance.methodsCalled, expectedMethods);
            } catch (Exception e) {
                e.printStackTrace();
                Assert.fail();
            }
        });
    }

    @Test
    public void testWipeData() {
        Collection<String> expectedMethods = new ArrayList<>();
        expectedMethods.add(TestData.Methods.WIPE_DATA);
        QAActivity.getScenario().onActivity(activity -> {
            try {
                MockBrazeInstance mockBrazeInstance = new MockBrazeInstance(application, activity) {
                    @Override
                    public void wipeData() {
                        // nothing to test here - just need to verify it's been called
                        super.wipeData();
                    }
                };
                brazeRemoteCommand.setBrazeCommand(mockBrazeInstance);

                brazeRemoteCommand.onInvoke(TestData.Responses.wipeData());
                TestUtils.assertContainsAllAndOnly(mockBrazeInstance.methodsCalled, expectedMethods);

            } catch (Exception e) {
                e.printStackTrace();
                Assert.fail();
            }
        });
    }

    @Test
    public void testDisableSdk() {
        Collection<String> expectedMethods = new ArrayList<>();
        expectedMethods.add(TestData.Methods.ENABLE_SDK);
        QAActivity.getScenario().onActivity(activity -> {
            try {
                MockBrazeInstance mockBrazeInstance = new MockBrazeInstance(application, activity) {
                    @Override
                    public void enableSdk(Boolean enabled) {
                        Assert.assertFalse("enabled should be false", enabled);
                        super.enableSdk(enabled);
                    }
                };
                brazeRemoteCommand.setBrazeCommand(mockBrazeInstance);

                brazeRemoteCommand.onInvoke(TestData.Responses.disableSdk());
                TestUtils.assertContainsAllAndOnly(mockBrazeInstance.methodsCalled, expectedMethods);

            } catch (Exception e) {
                e.printStackTrace();
                Assert.fail();
            }
        });
    }

    @Test
    public void testEnableSdk() {
        Collection<String> expectedMethods = new ArrayList<>();
        expectedMethods.add(TestData.Methods.ENABLE_SDK);
        QAActivity.getScenario().onActivity(activity -> {
            try {
                MockBrazeInstance mockBrazeInstance = new MockBrazeInstance(application, activity) {
                    @Override
                    public void enableSdk(Boolean enabled) {
                        Assert.assertTrue("enabled should be true", enabled);
                        super.enableSdk(enabled);
                    }
                };
                brazeRemoteCommand.setBrazeCommand(mockBrazeInstance);

                brazeRemoteCommand.onInvoke(TestData.Responses.enableSdk());
                TestUtils.assertContainsAllAndOnly(mockBrazeInstance.methodsCalled, expectedMethods);

            } catch (Exception e) {
                e.printStackTrace();
                Assert.fail();
            }
        });
    }

    @Test
    public void testUserAlias() {
        Collection<String> expectedMethods = new ArrayList<>();
        expectedMethods.add(TestData.Methods.SET_USER_ALIAS);
        QAActivity.getScenario().onActivity(activity -> {
            try {
                MockBrazeInstance mockBrazeInstance = new MockBrazeInstance(application, activity) {
                    @Override
                    public void setUserAlias(String userAlias, String aliasLabel) {
                        Assert.assertEquals("userAlias does not match what was sent", TestData.Values.USER_ALIAS, userAlias);
                        Assert.assertEquals("aliasLabel does not match what was sent", TestData.Values.USER_ALIAS_LABEL, aliasLabel);
                        super.setUserAlias(userAlias, aliasLabel);
                    }
                };
                brazeRemoteCommand.setBrazeCommand(mockBrazeInstance);

                brazeRemoteCommand.onInvoke(TestData.Responses.userAlias());
                TestUtils.assertContainsAllAndOnly(mockBrazeInstance.methodsCalled, expectedMethods);

            } catch (Exception e) {
                e.printStackTrace();
                Assert.fail();
            }
        });
    }

    @Test
    public void testUserId() {
        Collection<String> expectedMethods = new ArrayList<>();
        expectedMethods.add(TestData.Methods.SET_USER_ID);
        QAActivity.getScenario().onActivity(activity -> {
            try {
                MockBrazeInstance mockBrazeInstance = new MockBrazeInstance(application, activity) {
                    @Override
                    public void setUserId(String userId) {
                        Assert.assertEquals("userId does not match what was sent", TestData.Values.USER_ID, userId);
                        super.setUserId(userId);
                    }
                };
                brazeRemoteCommand.setBrazeCommand(mockBrazeInstance);

                brazeRemoteCommand.onInvoke(TestData.Responses.userId());
                TestUtils.assertContainsAllAndOnly(mockBrazeInstance.methodsCalled, expectedMethods);

            } catch (Exception e) {
                e.printStackTrace();
                Assert.fail();
            }
        });
    }

    @Test
    public void testAllUserAttributes() {
        Collection<String> expectedMethods = new ArrayList<>();
        expectedMethods.add(TestData.Methods.INITIALIZE);
        expectedMethods.add(TestData.Methods.SET_USER_ID);
        expectedMethods.add(TestData.Methods.SET_USER_ALIAS);
        expectedMethods.add(TestData.Methods.SET_EMAIL);
        expectedMethods.add(TestData.Methods.SET_FIRST_NAME);
        expectedMethods.add(TestData.Methods.SET_LAST_NAME);
        expectedMethods.add(TestData.Methods.SET_HOME_CITY);
        expectedMethods.add(TestData.Methods.SET_GENDER);
        expectedMethods.add(TestData.Methods.SET_LANGUAGE);
        QAActivity.getScenario().onActivity(activity -> {
            try {
                MockBrazeInstance mockBrazeInstance = new MockBrazeInstance(application, activity) {
                    @Override
                    public void setUserId(String userId) {
                        Assert.assertEquals("userId does not match what was sent", TestData.Values.USER_ID, userId);
                        super.setUserId(userId);
                    }

                    @Override
                    public void setUserAlias(String userAlias, String aliasLabel) {
                        Assert.assertEquals("userAlias does not match what was sent", TestData.Values.USER_ALIAS, userAlias);
                        Assert.assertEquals("aliasLabel does not match what was sent", TestData.Values.USER_ALIAS_LABEL, aliasLabel);
                        super.setUserAlias(userAlias, aliasLabel);
                    }

                    @Override
                    public void setUserFirstName(String firstName) {
                        Assert.assertEquals("firstName does not match what was sent", TestData.Values.USER_FIRST_NAME, firstName);
                        super.setUserFirstName(firstName);
                    }

                    @Override
                    public void setUserLastName(String lastName) {
                        Assert.assertEquals("lastName does not match what was sent", TestData.Values.USER_LAST_NAME, lastName);
                        super.setUserLastName(lastName);
                    }

                    @Override
                    public void setUserGender(String gender) {
                        Assert.assertEquals("gender does not match what was sent", TestData.Values.USER_GENDER, gender);
                        super.setUserGender(gender);
                    }

                    @Override
                    public void setUserEmail(String email) {
                        Assert.assertEquals("email does not match what was sent", TestData.Values.USER_EMAIL, email);
                        super.setUserEmail(email);
                    }

                    @Override
                    public void setUserHomeCity(String city) {
                        Assert.assertEquals("city does not match what was sent", TestData.Values.USER_HOME_CITY, city);
                        super.setUserHomeCity(city);
                    }

                    @Override
                    public void setUserLanguage(String language) {
                        Assert.assertEquals("language does not match what was sent", TestData.Values.USER_LANGUAGE, language);
                        super.setUserLanguage(language);
                    }
                };
                brazeRemoteCommand.setBrazeCommand(mockBrazeInstance);

                brazeRemoteCommand.onInvoke(TestData.Responses.userAllAttributes());
                TestUtils.assertContainsAllAndOnly(mockBrazeInstance.methodsCalled, expectedMethods);

            } catch (Exception e) {
                e.printStackTrace();
                Assert.fail();
            }
        });
    }

    @Test
    public void testAllCustomUserAttributes() {
        Collection<String> expectedMethods = new ArrayList<>();
        expectedMethods.add(TestData.Methods.INITIALIZE);
        expectedMethods.add(TestData.Methods.SET_USER_CUSTOM_ATTRIBUTES);// plural
        expectedMethods.add(TestData.Methods.SET_USER_CUSTOM_ATTRIBUTE);// singular
        expectedMethods.add(TestData.Methods.UNSET_USER_CUSTOM_ATTRIBUTES);
        expectedMethods.add(TestData.Methods.UNSET_USER_CUSTOM_ATTRIBUTE);
        expectedMethods.add(TestData.Methods.INCREMENT_USER_CUSTOM_ATTRIBUTES);
        expectedMethods.add(TestData.Methods.INCREMENT_USER_CUSTOM_ATTRIBUTE);
        QAActivity.getScenario().onActivity(activity -> {
            try {
                MockBrazeInstance mockBrazeInstance = new MockBrazeInstance(application, activity) {
                    @Override
                    public void setUserCustomAttributeArray(String key, String[] attributeArray) {
                        JSONArray jsonArray = new JSONArray();
                        for (String value : attributeArray) {
                            jsonArray.put(value);
                        }

                        Assert.assertEquals(TestData.Values.SET_CUSTOM_USER_ATTRIBUTES.optJSONArray(key).toString(), jsonArray.toString());
                        super.setUserCustomAttributeArray(key, attributeArray);
                    }
                };
                brazeRemoteCommand.setBrazeCommand(mockBrazeInstance);

                brazeRemoteCommand.onInvoke(TestData.Responses.userAllCustomAttributes());
                TestUtils.assertContainsAllAndOnly(mockBrazeInstance.methodsCalled, expectedMethods);

            } catch (Exception e) {
                e.printStackTrace();
                Assert.fail();
            }
        });
    }

    @Test
    public void testAllCustomUserArrayAttributes() {
        Collection<String> expectedMethods = new ArrayList<>();
        expectedMethods.add(TestData.Methods.INITIALIZE);
        expectedMethods.add(TestData.Methods.SET_USER_CUSTOM_ATTRIBUTES_ARRAY);// plural
        expectedMethods.add(TestData.Methods.APPEND_USER_CUSTOM_ATTRIBUTES_ARRAY);
        expectedMethods.add(TestData.Methods.REMOVE_USER_CUSTOM_ATTRIBUTES_ARRAY);
        expectedMethods.add(TestData.Methods.SET_USER_CUSTOM_ATTRIBUTE_ARRAY);// singular
        expectedMethods.add(TestData.Methods.APPEND_USER_CUSTOM_ATTRIBUTE_ARRAY);
        expectedMethods.add(TestData.Methods.REMOVE_USER_CUSTOM_ATTRIBUTE_ARRAY);
        QAActivity.getScenario().onActivity(activity -> {
            try {
                MockBrazeInstance mockBrazeInstance = new MockBrazeInstance(application, activity) {
                    @Override
                    public void setUserCustomAttributeArray(String key, String[] attributeArray) {
                        JSONArray jsonArray = new JSONArray();
                        for (String value : attributeArray) {
                            jsonArray.put(value);
                        }

                        Assert.assertEquals(TestData.Values.SET_CUSTOM_USER_ATTRIBUTES_ARRAY.optJSONArray(key).toString(), jsonArray.toString());
                        super.setUserCustomAttributeArray(key, attributeArray);
                    }
                };
                brazeRemoteCommand.setBrazeCommand(mockBrazeInstance);

                brazeRemoteCommand.onInvoke(TestData.Responses.userAllCustomArrayAttributes());
                TestUtils.assertContainsAllAndOnly(mockBrazeInstance.methodsCalled, expectedMethods);

            } catch (Exception e) {
                e.printStackTrace();
                Assert.fail();
            }
        });
    }

    @Test
    public void testRequestFlush() {
        Collection<String> expectedMethods = new ArrayList<>();
        expectedMethods.add(TestData.Methods.REQUEST_FLUSH);
        QAActivity.getScenario().onActivity(activity -> {
            try {
                MockBrazeInstance mockBrazeInstance = new MockBrazeInstance(application, activity) {
                };
                brazeRemoteCommand.setBrazeCommand(mockBrazeInstance);

                brazeRemoteCommand.onInvoke(TestData.Responses.requestFlush());
                TestUtils.assertContainsAllAndOnly(mockBrazeInstance.methodsCalled, expectedMethods);

            } catch (Exception e) {
                e.printStackTrace();
                Assert.fail();
            }
        });
    }

    @Test
    public void testRegisterPush() {
        Collection<String> expectedMethods = new ArrayList<>();
        expectedMethods.add(TestData.Methods.REGISTER_PUSH);
        QAActivity.getScenario().onActivity(activity -> {
            try {
                MockBrazeInstance mockBrazeInstance = new MockBrazeInstance(application, activity) {
                };
                brazeRemoteCommand.setBrazeCommand(mockBrazeInstance);

                brazeRemoteCommand.onInvoke(TestData.Responses.registerPush());
                TestUtils.assertContainsAllAndOnly(mockBrazeInstance.methodsCalled, expectedMethods);

            } catch (Exception e) {
                e.printStackTrace();
                Assert.fail();
            }
        });
    }

    @Test
    public void testAddToSubscriptionId() {
        Collection<String> expectedMethods = new ArrayList<>();
        expectedMethods.add(TestData.Methods.ADD_TO_SUBSCRIPTION);
        QAActivity.getScenario().onActivity(activity -> {
            try {
                MockBrazeInstance mockBrazeInstance = new MockBrazeInstance(application, activity) {
                };
                brazeRemoteCommand.setBrazeCommand(mockBrazeInstance);

                brazeRemoteCommand.onInvoke(TestData.Responses.addToSubscription());
                TestUtils.assertContainsAllAndOnly(mockBrazeInstance.methodsCalled, expectedMethods);

            } catch (Exception e) {
                e.printStackTrace();
                Assert.fail();
            }
        });
    }

    @Test
    public void testRemoveFromSubscriptionId() {
        Collection<String> expectedMethods = new ArrayList<>();
        expectedMethods.add(TestData.Methods.REMOVE_FROM_SUBSCRIPTION);
        QAActivity.getScenario().onActivity(activity -> {
            try {
                MockBrazeInstance mockBrazeInstance = new MockBrazeInstance(application, activity) {
                };
                brazeRemoteCommand.setBrazeCommand(mockBrazeInstance);

                brazeRemoteCommand.onInvoke(TestData.Responses.removeFromSubscription());
                TestUtils.assertContainsAllAndOnly(mockBrazeInstance.methodsCalled, expectedMethods);

            } catch (Exception e) {
                e.printStackTrace();
                Assert.fail();
            }
        });
    }

    public MockBrazeRemoteCommand newMockRemoteCommand() {
        return new MockBrazeRemoteCommand(application, false, null, false, null);
    }

    public void setupInitTestWithApiKey(BrazeRemoteCommand brc) {

        brc.registerConfigOverride(b -> {
            BrazeConfig brazeConfig = b.build();

            Assert.assertEquals(brazeConfig.apiKey, TestData.Values.API_KEY);
        });
    }

    public void setupInitTestWithOverrides(BrazeRemoteCommand brc) {
        brc.registerConfigOverride(b -> {
            b.setFirebaseCloudMessagingSenderIdKey(TestData.Values.FIREBASE_SENDER_ID);
            b.setIsFirebaseCloudMessagingRegistrationEnabled(TestData.Values.FIREBASE_ENABLED);
        });
        brc.registerConfigOverride(b -> {
            BrazeConfig brazeConfig = b.build();

            Assert.assertEquals(brazeConfig.apiKey, TestData.Values.API_KEY);
            Assert.assertEquals(brazeConfig.firebaseCloudMessagingSenderIdKey, TestData.Values.FIREBASE_SENDER_ID);
            Assert.assertEquals(brazeConfig.isFirebaseCloudMessagingRegistrationEnabled, TestData.Values.FIREBASE_ENABLED);
        });
    }

    public void setupInitTestWithAllSettings(BrazeRemoteCommand brc) {
        brc.registerConfigOverride(b -> {
            BrazeConfig brazeConfig = b.build();

            Assert.assertEquals(brazeConfig.apiKey, TestData.Values.API_KEY);
            Assert.assertEquals(brazeConfig.firebaseCloudMessagingSenderIdKey, TestData.Values.FIREBASE_SENDER_ID);
            Assert.assertEquals(brazeConfig.isFirebaseCloudMessagingRegistrationEnabled, TestData.Values.FIREBASE_ENABLED);
            Assert.assertEquals(brazeConfig.isAdmMessagingRegistrationEnabled, TestData.Values.ADM_ENABLED);
            Assert.assertEquals(brazeConfig.isLocationCollectionEnabled, !TestData.Values.DISABLE_LOCATION);
            Assert.assertEquals(brazeConfig.willHandlePushDeepLinksAutomatically, TestData.Values.AUTO_DEEP_LINKS);
            Assert.assertEquals(brazeConfig.badNetworkInterval, TestData.Values.BAD_NETWORK_INTERVAL);
            Assert.assertEquals(brazeConfig.goodNetworkInterval, TestData.Values.GOOD_NETWORK_INTERVAL);
            Assert.assertEquals(brazeConfig.greatNetworkInterval, TestData.Values.GREAT_NETWORK_INTERVAL);
            Assert.assertEquals(brazeConfig.largeNotificationIcon, TestData.Values.LARGE_NOTIFICATION_ICON);
            Assert.assertEquals(brazeConfig.smallNotificationIcon, TestData.Values.SMALL_NOTIFICATION_ICON);
            Assert.assertEquals(brazeConfig.sessionTimeout, TestData.Values.SESSION_TIMEOUT);
            Assert.assertEquals(brazeConfig.customEndpoint, TestData.Values.CUSTOM_ENDPOINT);
            Assert.assertEquals(brazeConfig.triggerActionMinimumTimeIntervalSeconds, TestData.Values.TRIGGER_INTERVAL_SECONDS);
            Assert.assertEquals(brazeConfig.defaultNotificationAccentColor, TestData.Values.DEFAULT_NOTIFICATION_COLOR);
            Assert.assertEquals(brazeConfig.isNewsFeedVisualIndicatorOn, TestData.Values.ENABLE_NEWS_FEED_INDICATOR);
        });
    }
}
