package com.tealium.remotecommands.braze;

import androidx.test.runner.AndroidJUnit4;

import com.appboy.Appboy;
import com.tealium.internal.tagbridge.RemoteCommand;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Requires a working Tag Template and appropriate IQ profile as these tests will test triggering
 * these events through the WebView. As such, they rely on a CompletableFuture in order to make the
 * Validation wait until we can be sure that the tracking has been completed - without doing so,
 * the multi-threaded nature will cause the tests to be evaluated before the WebView has completed
 * what it needs to.
 */
@RunWith(AndroidJUnit4.class)
public class BrazeTrackingTests extends BaseTest {

    public BrazeTrackingTests() {
        super();
    }


    @Test
    public void testInitEventWithApiKey() {
        final Collection<String> expectedMethods = new ArrayList<>();
        expectedMethods.add(TestData.Methods.INITIALIZE);

        final Map<String, Object> event = TestData.Events.initalizeWithApiKeyOnly();
        assertMethodsAndData(expectedMethods, event);
    }

    @Test
    public void testInitEventWithOverrides() {
        final Collection<String> expectedMethods = new ArrayList<>();
        expectedMethods.add(TestData.Methods.INITIALIZE);

        final Map<String, Object> event = TestData.Events.initializeWithAllSettings();
        assertMethodsAndData(expectedMethods, event);
    }

    @Test
    public void testInitEventWithAllSettings() {
        final Collection<String> expectedMethods = new ArrayList<>();
        expectedMethods.add(TestData.Methods.INITIALIZE);

        final Map<String, Object> event = TestData.Events.initializeWithAllSettings();
        assertMethodsAndData(expectedMethods, event);
    }

    @Test
    public void testCustomEventWithProperties() {
        final Collection<String> expectedMethods = new ArrayList<>();
        expectedMethods.add(TestData.Methods.INITIALIZE);
        expectedMethods.add(TestData.Methods.LOG_CUSTOM_EVENT);

        final Map<String, Object> event = TestData.Events.customEventWithProperties();
        assertMethodsAndData(expectedMethods, event);
    }

    @Test
    public void testCustomEvent() {
        final Collection<String> expectedMethods = new ArrayList<>();
        expectedMethods.add(TestData.Methods.INITIALIZE);
        expectedMethods.add(TestData.Methods.LOG_CUSTOM_EVENT);

        final Map<String, Object> event = TestData.Events.customEvent();
        assertMethodsAndData(expectedMethods, event);
    }

    @Test
    public void testPurchaseEvent() {
        final Collection<String> expectedMethods = new ArrayList<>();
        expectedMethods.add(TestData.Methods.INITIALIZE);
        expectedMethods.add(TestData.Methods.LOG_PURCHASE);

        final Map<String, Object> event = TestData.Events.purchaseEvent();
        assertMethodsAndData(expectedMethods, event);
    }

    @Test
    public void testPurchaseEventWithProperties() {
        final Collection<String> expectedMethods = new ArrayList<>();
        expectedMethods.add(TestData.Methods.INITIALIZE);
        expectedMethods.add(TestData.Methods.LOG_PURCHASE);

        final Map<String, Object> event = TestData.Events.purchaseEventWithProperties();
        assertMethodsAndData(expectedMethods, event);
    }

    @Test
    public void testWipeData() {
        final Collection<String> expectedMethods = new ArrayList<>();
        expectedMethods.add(TestData.Methods.WIPE_DATA);

        final Map<String, Object> event = TestData.Events.wipeData();
        assertMethodsAndData(expectedMethods, event);
    }

    @Test
    public void testDisableSdk() {
        final Collection<String> expectedMethods = new ArrayList<>();
        expectedMethods.add(TestData.Methods.ENABLE_SDK);

        final Map<String, Object> event = TestData.Events.disableSdk();
        assertMethodsAndData(expectedMethods, event);
    }

    @Test
    public void testEnableSdk() {
        final Collection<String> expectedMethods = new ArrayList<>();
        expectedMethods.add(TestData.Methods.ENABLE_SDK);

        final Map<String, Object> event = TestData.Events.enableSdk();
        assertMethodsAndData(expectedMethods, event);
    }

    @Test
    public void testUserAlias() {
        final Collection<String> expectedMethods = new ArrayList<>();
        expectedMethods.add(TestData.Methods.INITIALIZE);
        expectedMethods.add(TestData.Methods.SET_USER_ALIAS);

        final Map<String, Object> event = TestData.Events.userAlias();
        assertMethodsAndData(expectedMethods, event);
    }

    @Test
    public void testUserId() {
        final Collection<String> expectedMethods = new ArrayList<>();
        expectedMethods.add(TestData.Methods.INITIALIZE);
        expectedMethods.add(TestData.Methods.SET_USER_ID);

        final Map<String, Object> event = TestData.Events.userId();
        assertMethodsAndData(expectedMethods, event, new MockBrazeRemoteCommand.Validator() {
            @Override
            public Boolean onValidate(RemoteCommand.Response response) {
                boolean isTrue = TestData.Values.USER_ID.equals(Appboy.getInstance(TestUtils.getDefaultConfig().getApplication().getApplicationContext()).getCurrentUser().getUserId());
                Assert.assertTrue("testUserId: User ID's do not match", isTrue);
                return isTrue;
            }
        });
    }

    @Test
    public void testAllUserAttributes() {
        final Collection<String> expectedMethods = new ArrayList<>();
        expectedMethods.add(TestData.Methods.INITIALIZE);
        expectedMethods.add(TestData.Methods.SET_USER_ID);
        expectedMethods.add(TestData.Methods.SET_USER_ALIAS);
        expectedMethods.add(TestData.Methods.SET_EMAIL);
        expectedMethods.add(TestData.Methods.SET_FIRST_NAME);
        expectedMethods.add(TestData.Methods.SET_LAST_NAME);
        expectedMethods.add(TestData.Methods.SET_HOME_CITY);
        expectedMethods.add(TestData.Methods.SET_GENDER);
        expectedMethods.add(TestData.Methods.SET_LANGUAGE);

        final Map<String, Object> event = TestData.Events.userAllAttributes();
        assertMethodsAndData(expectedMethods, event);
    }

    @Test
    public void testAllCustomUserAttributes() {
        final Collection<String> expectedMethods = new ArrayList<>();
        expectedMethods.add(TestData.Methods.INITIALIZE);
        expectedMethods.add(TestData.Methods.SET_USER_CUSTOM_ATTRIBUTES_ARRAY);// plural
        expectedMethods.add(TestData.Methods.APPEND_USER_CUSTOM_ATTRIBUTES_ARRAY);
        expectedMethods.add(TestData.Methods.REMOVE_USER_CUSTOM_ATTRIBUTES_ARRAY);
        expectedMethods.add(TestData.Methods.SET_USER_CUSTOM_ATTRIBUTE_ARRAY);// singular
        expectedMethods.add(TestData.Methods.APPEND_USER_CUSTOM_ATTRIBUTE_ARRAY);
        expectedMethods.add(TestData.Methods.REMOVE_USER_CUSTOM_ATTRIBUTE_ARRAY);

        final Map<String, Object> event = TestData.Events.userAllCustomArrayAttributes();
        assertMethodsAndData(expectedMethods, event);
    }

    @Test
    public void testSocialData() {
        final Collection<String> expectedMethods = new ArrayList<>();
        expectedMethods.add(TestData.Methods.INITIALIZE);
        expectedMethods.add(TestData.Methods.SET_FACEBOOK_DATA);
        expectedMethods.add(TestData.Methods.SET_TWITTER_DATA);

        final Map<String, Object> event = TestData.Events.socialData();
        assertMethodsAndData(expectedMethods, event);
    }

    private void assertMethodsAndData(final Collection<String> methods, final Map<String, Object> event) {
        assertMethodsAndData(methods, event, null);
    }

    /**
     * Will add a new BrazeRemoteCommand to the default Tealium instance, and then track an event
     * with the provided HashMap. It will do a basic comparison on the data from the original event
     * and the JSON payload returned by the Remote Command tag.
     *
     * @param methods         - List of Method names expected to be called by the event.
     * @param event           - The original set of Event Data to compare against the returned JSONObject
     * @param customValidator - Any custom validation over and above the default comparisons.
     */
    private void assertMethodsAndData(final Collection<String> methods, final Map<String, Object> event, MockBrazeRemoteCommand.Validator customValidator) {

        final MockBrazeRemoteCommand brazeRemoteCommand = newMockRemoteCommand();
        final MockBrazeWrapperImpl mockBrazeWrapper = newMockBrazeWrapper();
        try {

            brazeRemoteCommand.setBrazeWrapper(mockBrazeWrapper);

            if (customValidator != null) {
                brazeRemoteCommand.addValidator(customValidator);
            }

            brazeRemoteCommand.addValidator(new MockBrazeRemoteCommand.Validator() {
                @Override
                public Boolean onValidate(RemoteCommand.Response response) {
                    return TestUtils.assertContainsAllAndOnly(mockBrazeWrapper.methodsCalled, methods)
                            && TestUtils.compareMapToJson(event, response.getRequestPayload());
                }
            });

            TestUtils.getDefaultInstance().addRemoteCommand(brazeRemoteCommand);
            TestUtils.getDefaultInstance().trackEvent("Event", event);

            Assert.assertTrue("Validation Failed, or Timeout for completion Reached", brazeRemoteCommand.future.get(10000L, TimeUnit.MILLISECONDS));

        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        } finally {
            TestUtils.getDefaultInstance().removeRemoteCommand(brazeRemoteCommand);
        }
    }
}
