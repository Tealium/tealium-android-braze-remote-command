package com.tealium.remotecommands.braze;

import androidx.test.rule.ActivityTestRule;

import com.appboy.Appboy;
import com.appboy.configuration.AppboyConfig;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;

public class BaseTest extends ActivityTestRule<QAActivity> {

    public BaseTest() {
        super(QAActivity.class);
    }

    @Rule
    public ActivityTestRule<QAActivity> QAActivity = new ActivityTestRule<>(com.tealium.remotecommands.braze.QAActivity.class);

    @Before
    public void setup() {
        TestUtils.setupInstance(QAActivity.getActivity().getApplication());

        Appboy.enableSdk(TestUtils.getDefaultConfig().getApplication().getApplicationContext());
        Appboy.enableMockAppboyNetworkRequestsAndDropEventsMode();
    }

    public MockBrazeRemoteCommand newMockRemoteCommand() {
        return new MockBrazeRemoteCommand(TestUtils.getDefaultConfig(), true);
    }

    public MockBrazeWrapperImpl newMockBrazeWrapper() {
        return new MockBrazeWrapperImpl(TestUtils.getDefaultConfig(), QAActivity.getActivity());
    }

    public void setupInitTestWithApiKey(BrazeRemoteCommand brc) {

        brc.registerConfigOverride(new BrazeRemoteCommand.ConfigOverrider() {
            @Override
            public void onOverride(AppboyConfig.Builder b) {
                AppboyConfig appboyConfig = b.build();

                Assert.assertEquals(appboyConfig.getApiKey(), TestData.Values.API_KEY);
            }
        });
    }

    public void setupInitTestWithOverrides(BrazeRemoteCommand brc) {
        brc.registerConfigOverride(new BrazeRemoteCommand.ConfigOverrider() {
            @Override
            public void onOverride(AppboyConfig.Builder b) {
                b.setFirebaseCloudMessagingSenderIdKey(TestData.Values.FIREBASE_SENDER_ID);
                b.setIsFirebaseCloudMessagingRegistrationEnabled(TestData.Values.FIREBASE_ENABLED);
            }
        });
        brc.registerConfigOverride(new BrazeRemoteCommand.ConfigOverrider() {
            @Override
            public void onOverride(AppboyConfig.Builder b) {
                AppboyConfig appboyConfig = b.build();

                Assert.assertEquals(appboyConfig.getApiKey(), TestData.Values.API_KEY);
                Assert.assertEquals(appboyConfig.getFirebaseCloudMessagingSenderIdKey(), TestData.Values.FIREBASE_SENDER_ID);
                Assert.assertEquals(appboyConfig.getIsFirebaseCloudMessagingRegistrationEnabled(), TestData.Values.FIREBASE_ENABLED);
            }
        });
    }

    public void setupInitTestWithAllSettings(BrazeRemoteCommand brc) {
        brc.registerConfigOverride(new BrazeRemoteCommand.ConfigOverrider() {
            @Override
            public void onOverride(AppboyConfig.Builder b) {
                AppboyConfig appboyConfig = b.build();

                Assert.assertEquals(appboyConfig.getApiKey(), TestData.Values.API_KEY);
                Assert.assertEquals(appboyConfig.getFirebaseCloudMessagingSenderIdKey(), TestData.Values.FIREBASE_SENDER_ID);
                Assert.assertEquals(appboyConfig.getIsFirebaseCloudMessagingRegistrationEnabled(), TestData.Values.FIREBASE_ENABLED);
                Assert.assertEquals(appboyConfig.getAdmMessagingRegistrationEnabled(), TestData.Values.ADM_ENABLED);
                Assert.assertEquals(appboyConfig.getDisableLocationCollection(), TestData.Values.DISABLE_LOCATION);
                Assert.assertEquals(appboyConfig.getHandlePushDeepLinksAutomatically(), TestData.Values.AUTO_DEEP_LINKS);
                Assert.assertEquals(appboyConfig.getBadNetworkDataFlushInterval(), TestData.Values.BAD_NETWORK_INTERVAL);
                Assert.assertEquals(appboyConfig.getGoodNetworkDataFlushInterval(), TestData.Values.GOOD_NETWORK_INTERVAL);
                Assert.assertEquals(appboyConfig.getGreatNetworkDataFlushInterval(), TestData.Values.GREAT_NETWORK_INTERVAL);
                Assert.assertEquals(appboyConfig.getLargeNotificationIcon(), TestData.Values.LARGE_NOTIFICATION_ICON);
                Assert.assertEquals(appboyConfig.getSmallNotificationIcon(), TestData.Values.SMALL_NOTIFICATION_ICON);
                Assert.assertEquals(appboyConfig.getLocaleToApiMapping(), TestData.Values.LOCALE_MAPPING);
                Assert.assertEquals(appboyConfig.getSessionTimeout(), TestData.Values.SESSION_TIMEOUT);
                Assert.assertEquals(appboyConfig.getCustomEndpoint(), TestData.Values.CUSTOM_ENDPOINT);
                Assert.assertEquals(appboyConfig.getTriggerActionMinimumTimeIntervalSeconds(), TestData.Values.TRIGGER_INTERVAL_SECONDS);
                Assert.assertEquals(appboyConfig.getDefaultNotificationAccentColor(), TestData.Values.DEFAULT_NOTIFICATION_COLOR);
                Assert.assertEquals(appboyConfig.getIsNewsFeedVisualIndicatorOn(), TestData.Values.ENABLE_NEWS_FEED_INDICATOR);
            }
        });
    }
}
