package com.tealium.remotecommands.braze;

import androidx.annotation.NonNull;
import androidx.test.runner.AndroidJUnit4;

import com.appboy.Appboy;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import org.junit.runner.RunWith;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class BrazeDirectTests extends BaseTest {

    public BrazeDirectTests() {
        super();
    }

    @Override
    public void setup() {
        super.setup();
    }

    @Test
    public void testInitEventWithApiKey() {
        Collection<String> expectedMethods = new ArrayList<>();
        expectedMethods.add(TestData.Methods.INITIALIZE);

        try {
            MockBrazeRemoteCommand brc = newMockRemoteCommand();
            MockBrazeWrapperImpl mockBrazeWrapper = new MockBrazeWrapperImpl(TestUtils.getDefaultConfig(), QAActivity.getActivity()) {
                @Override
                public void initialize(String apiKey, JSONObject launchOptions) {
                    Assert.assertEquals("apiKey does not match.", TestData.Values.API_KEY, apiKey);
                    Assert.assertTrue("launchOptions should be empty", BrazeUtils.isNullOrEmpty(launchOptions));
                    super.initialize(apiKey, launchOptions);
                }
            };

            brc.setBrazeWrapper(mockBrazeWrapper);
            setupInitTestWithApiKey(brc);

            brc.onInvoke(TestData.Responses.initalizeWithApiKeyOnly());
            TestUtils.assertContainsAllAndOnly(mockBrazeWrapper.methodsCalled, expectedMethods);

        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testInitEventWithOverrides() {
        Collection<String> expectedMethods = new ArrayList<>();
        expectedMethods.add(TestData.Methods.INITIALIZE);

        try {
            MockBrazeRemoteCommand brc = newMockRemoteCommand();
            MockBrazeWrapperImpl mockBrazeWrapper = new MockBrazeWrapperImpl(TestUtils.getDefaultConfig(), QAActivity.getActivity()) {
                @Override
                public void initialize(@NonNull String apiKey, JSONObject launchOptions, List<BrazeRemoteCommand.ConfigOverrider> overrides) {
                    Assert.assertEquals("apiKey does not match.", TestData.Values.API_KEY, apiKey);
                    Assert.assertTrue("launchOptions should only contain api_key and command", launchOptions.has(BrazeConstants.Config.API_KEY) && launchOptions.has(BrazeConstants.Commands.COMMAND_KEY) && launchOptions.length() <= 2);
                    Assert.assertTrue("overrides should have two entries", overrides.size() == 2);

                    super.initialize(apiKey, launchOptions, overrides);
                }
            };
            brc.setBrazeWrapper(mockBrazeWrapper);
            setupInitTestWithOverrides(brc);

            brc.onInvoke(TestData.Responses.initalizeWithApiKeyOnly());
            TestUtils.assertContainsAllAndOnly(mockBrazeWrapper.methodsCalled, expectedMethods);

        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testInitEventWithAllSettings() {
        Collection<String> expectedMethods = new ArrayList<>();
        expectedMethods.add(TestData.Methods.INITIALIZE);

        try {
            MockBrazeRemoteCommand brc = newMockRemoteCommand();
            MockBrazeWrapperImpl mockBrazeWrapper = new MockBrazeWrapperImpl(TestUtils.getDefaultConfig(), QAActivity.getActivity()) {
                // all settings are verified in an override created by setupInitTestWithAllSettings(..)
            };
            brc.setBrazeWrapper(mockBrazeWrapper);
            setupInitTestWithAllSettings(brc);

            brc.onInvoke(TestData.Responses.initializeWithAllSettings());
            TestUtils.assertContainsAllAndOnly(mockBrazeWrapper.methodsCalled, expectedMethods);

        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testCustomEventWithProperties() {
        Collection<String> expectedMethods = new ArrayList<>();
        expectedMethods.add(TestData.Methods.LOG_CUSTOM_EVENT);

        try {
            MockBrazeRemoteCommand brazeRemoteCommand = newMockRemoteCommand();
            MockBrazeWrapperImpl mockBrazeWrapper = new MockBrazeWrapperImpl(TestUtils.getDefaultConfig(), QAActivity.getActivity()) {
                @Override
                public void logCustomEvent(@NonNull String eventName, JSONObject eventProperties) {
                    Assert.assertTrue("eventName parameter does not match what was sent", eventName.equals(TestData.Values.EVENT_NAME));
                    Assert.assertTrue("eventroperties parameter does not match what was sent", eventProperties.toString().equals(TestData.Values.EVENT_PROPERTIES.toString()));
                    super.logCustomEvent(eventName, eventProperties);
                }
            };
            brazeRemoteCommand.setBrazeWrapper(mockBrazeWrapper);

            brazeRemoteCommand.onInvoke(TestData.Responses.customEventWithProperties());
            TestUtils.assertContainsAllAndOnly(mockBrazeWrapper.methodsCalled, expectedMethods);

        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testCustomEvent() {
        Collection<String> expectedMethods = new ArrayList<>();
        expectedMethods.add(TestData.Methods.LOG_CUSTOM_EVENT);

        try {
            MockBrazeRemoteCommand brazeRemoteCommand = newMockRemoteCommand();
            MockBrazeWrapperImpl mockBrazeWrapper = new MockBrazeWrapperImpl(TestUtils.getDefaultConfig(), QAActivity.getActivity()) {
                @Override
                public void logCustomEvent(@NonNull String eventName, JSONObject eventProperties) {
                    Assert.assertTrue("eventName parameter does not match what was sent", eventName.equals(TestData.Values.EVENT_NAME));
                    Assert.assertTrue("eventProperties param should be null or empty", BrazeUtils.isNullOrEmpty(eventProperties));
                    super.logCustomEvent(eventName, eventProperties);
                }
            };
            brazeRemoteCommand.setBrazeWrapper(mockBrazeWrapper);

            brazeRemoteCommand.onInvoke(TestData.Responses.customEvent());
            TestUtils.assertContainsAllAndOnly(mockBrazeWrapper.methodsCalled, expectedMethods);

        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testPurchaseEvent() {
        Collection<String> expectedMethods = new ArrayList<>();
        expectedMethods.add(TestData.Methods.LOG_PURCHASE);

        try {
            MockBrazeRemoteCommand brazeRemoteCommand = newMockRemoteCommand();
            MockBrazeWrapperImpl mockBrazeWrapper = new MockBrazeWrapperImpl(TestUtils.getDefaultConfig(), QAActivity.getActivity()) {
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
            brazeRemoteCommand.setBrazeWrapper(mockBrazeWrapper);

            brazeRemoteCommand.onInvoke(TestData.Responses.purchaseEvent());
            TestUtils.assertContainsAllAndOnly(mockBrazeWrapper.methodsCalled, expectedMethods);

        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testPurchaseEventWithProperties() {
        Collection<String> expectedMethods = new ArrayList<>();
        expectedMethods.add(TestData.Methods.LOG_PURCHASE);

        try {
            MockBrazeRemoteCommand brazeRemoteCommand = newMockRemoteCommand();
            MockBrazeWrapperImpl mockBrazeWrapper = new MockBrazeWrapperImpl(TestUtils.getDefaultConfig(), QAActivity.getActivity()) {
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
            brazeRemoteCommand.setBrazeWrapper(mockBrazeWrapper);

            brazeRemoteCommand.onInvoke(TestData.Responses.purchaseEventWithProperties());
            TestUtils.assertContainsAllAndOnly(mockBrazeWrapper.methodsCalled, expectedMethods);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testWipeData() {
        Collection<String> expectedMethods = new ArrayList<>();
        expectedMethods.add(TestData.Methods.WIPE_DATA);

        try {
            MockBrazeRemoteCommand brazeRemoteCommand = newMockRemoteCommand();
            MockBrazeWrapperImpl mockBrazeWrapper = new MockBrazeWrapperImpl(TestUtils.getDefaultConfig(), QAActivity.getActivity()) {
                @Override
                public void wipeData() {
                    // nothing to test here - just need to verify it's been called
                    super.wipeData();
                }
            };
            brazeRemoteCommand.setBrazeWrapper(mockBrazeWrapper);

            brazeRemoteCommand.onInvoke(TestData.Responses.wipeData());
            TestUtils.assertContainsAllAndOnly(mockBrazeWrapper.methodsCalled, expectedMethods);

        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testDisableSdk() {
        Collection<String> expectedMethods = new ArrayList<>();
        expectedMethods.add(TestData.Methods.ENABLE_SDK);

        try {
            MockBrazeRemoteCommand brazeRemoteCommand = newMockRemoteCommand();
            MockBrazeWrapperImpl mockBrazeWrapper = new MockBrazeWrapperImpl(TestUtils.getDefaultConfig(), QAActivity.getActivity()) {
                @Override
                public void enableSdk(Boolean enabled) {
                    Assert.assertFalse("enabled should be false", enabled);
                    super.enableSdk(enabled);
                }
            };
            brazeRemoteCommand.setBrazeWrapper(mockBrazeWrapper);

            brazeRemoteCommand.onInvoke(TestData.Responses.disableSdk());
            TestUtils.assertContainsAllAndOnly(mockBrazeWrapper.methodsCalled, expectedMethods);

        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testEnableSdk() {
        Collection<String> expectedMethods = new ArrayList<>();
        expectedMethods.add(TestData.Methods.ENABLE_SDK);

        try {
            MockBrazeRemoteCommand brazeRemoteCommand = newMockRemoteCommand();
            MockBrazeWrapperImpl mockBrazeWrapper = new MockBrazeWrapperImpl(TestUtils.getDefaultConfig(), QAActivity.getActivity()) {
                @Override
                public void enableSdk(Boolean enabled) {
                    Assert.assertTrue("enabled should be true", enabled);
                    super.enableSdk(enabled);
                }
            };
            brazeRemoteCommand.setBrazeWrapper(mockBrazeWrapper);

            brazeRemoteCommand.onInvoke(TestData.Responses.enableSdk());
            TestUtils.assertContainsAllAndOnly(mockBrazeWrapper.methodsCalled, expectedMethods);

        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testUserAlias() {
        Collection<String> expectedMethods = new ArrayList<>();
        expectedMethods.add(TestData.Methods.SET_USER_ALIAS);

        try {
            MockBrazeRemoteCommand brazeRemoteCommand = newMockRemoteCommand();
            MockBrazeWrapperImpl mockBrazeWrapper = new MockBrazeWrapperImpl(TestUtils.getDefaultConfig(), QAActivity.getActivity()) {
                @Override
                public void setUserAlias(String userAlias, String aliasLabel) {
                    Assert.assertEquals("userAlias does not match what was sent", TestData.Values.USER_ALIAS, userAlias);
                    Assert.assertEquals("aliasLabel does not match what was sent", TestData.Values.USER_ALIAS_LABEL, aliasLabel);
                    super.setUserAlias(userAlias, aliasLabel);
                }
            };
            brazeRemoteCommand.setBrazeWrapper(mockBrazeWrapper);

            brazeRemoteCommand.onInvoke(TestData.Responses.userAlias());
            TestUtils.assertContainsAllAndOnly(mockBrazeWrapper.methodsCalled, expectedMethods);

        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testUserId() {
        Collection<String> expectedMethods = new ArrayList<>();
        expectedMethods.add(TestData.Methods.SET_USER_ID);

        try {
            MockBrazeRemoteCommand brazeRemoteCommand = newMockRemoteCommand();
            MockBrazeWrapperImpl mockBrazeWrapper = new MockBrazeWrapperImpl(TestUtils.getDefaultConfig(), QAActivity.getActivity()) {
                @Override
                public void setUserId(String userId) {
                    Assert.assertEquals("userId does not match what was sent", TestData.Values.USER_ID, userId);
                    super.setUserId(userId);
                }
            };
            brazeRemoteCommand.setBrazeWrapper(mockBrazeWrapper);

            brazeRemoteCommand.onInvoke(TestData.Responses.userId());
            TestUtils.assertContainsAllAndOnly(mockBrazeWrapper.methodsCalled, expectedMethods);
            Assert.assertEquals("testUserId: User ID's do not match", TestData.Values.USER_ID, Appboy.getInstance(TestUtils.getDefaultConfig().getApplication().getApplicationContext()).getCurrentUser().getUserId());

        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
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

        try {
            MockBrazeRemoteCommand brazeRemoteCommand = newMockRemoteCommand();
            MockBrazeWrapperImpl mockBrazeWrapper = new MockBrazeWrapperImpl(TestUtils.getDefaultConfig(), QAActivity.getActivity()) {
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
            brazeRemoteCommand.setBrazeWrapper(mockBrazeWrapper);

            brazeRemoteCommand.onInvoke(TestData.Responses.userAllAttributes());
            TestUtils.assertContainsAllAndOnly(mockBrazeWrapper.methodsCalled, expectedMethods);

        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
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

        try {
            MockBrazeRemoteCommand brazeRemoteCommand = newMockRemoteCommand();
            MockBrazeWrapperImpl mockBrazeWrapper = new MockBrazeWrapperImpl(TestUtils.getDefaultConfig(), QAActivity.getActivity()) {
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
            brazeRemoteCommand.setBrazeWrapper(mockBrazeWrapper);

            brazeRemoteCommand.onInvoke(TestData.Responses.userAllCustomAttributes());
            TestUtils.assertContainsAllAndOnly(mockBrazeWrapper.methodsCalled, expectedMethods);

        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
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

        try {
            MockBrazeRemoteCommand brazeRemoteCommand = newMockRemoteCommand();
            MockBrazeWrapperImpl mockBrazeWrapper = new MockBrazeWrapperImpl(TestUtils.getDefaultConfig(), QAActivity.getActivity()) {
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
            brazeRemoteCommand.setBrazeWrapper(mockBrazeWrapper);

            brazeRemoteCommand.onInvoke(TestData.Responses.userAllCustomArrayAttributes());
            TestUtils.assertContainsAllAndOnly(mockBrazeWrapper.methodsCalled, expectedMethods);

        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testSocialData() {
        Collection<String> expectedMethods = new ArrayList<>();
        expectedMethods.add(TestData.Methods.INITIALIZE);
        expectedMethods.add(TestData.Methods.SET_FACEBOOK_DATA);
        expectedMethods.add(TestData.Methods.SET_TWITTER_DATA);

        try {
            MockBrazeRemoteCommand brazeRemoteCommand = newMockRemoteCommand();
            MockBrazeWrapperImpl mockBrazeWrapper = new MockBrazeWrapperImpl(TestUtils.getDefaultConfig(), QAActivity.getActivity()) {
            };
            brazeRemoteCommand.setBrazeWrapper(mockBrazeWrapper);

            brazeRemoteCommand.onInvoke(TestData.Responses.socialData());
            TestUtils.assertContainsAllAndOnly(mockBrazeWrapper.methodsCalled, expectedMethods);

        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }
}
