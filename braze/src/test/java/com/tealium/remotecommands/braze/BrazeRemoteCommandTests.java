package com.tealium.remotecommands.braze;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import android.app.Activity;
import android.app.Application;

import androidx.test.core.app.ApplicationProvider;

import com.tealium.remotecommands.RemoteCommand;
import com.tealium.remotecommands.braze.BrazeConstants.Commands;
import com.tealium.remotecommands.braze.BrazeConstants.Config;
import com.tealium.remotecommands.braze.BrazeConstants.Ecommerce;
import com.tealium.remotecommands.braze.BrazeConstants.Location;
import com.tealium.remotecommands.braze.BrazeConstants.Purchase;
import com.tealium.remotecommands.braze.BrazeConstants.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
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
                    json.put(BrazeConstants.Config.ENABLE_AUTOMATIC_LOCATION, true);
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

    private JSONArray singleProductArray() throws JSONException {
        JSONObject product = new JSONObject();
        product.put(Ecommerce.PRODUCT_ID, "sku123");
        product.put(Ecommerce.PRODUCT_NAME, "Widget");
        product.put(Ecommerce.VARIANT_ID, "widget_blue");
        product.put(Ecommerce.PRICE, 49.99);
        product.put(Ecommerce.QUANTITY, 1);
        JSONArray products = new JSONArray();
        products.put(product);
        return products;
    }

    @Test
    public void testProductViewedEvent() throws Exception {
        RemoteCommand.Response response = ResponseBuilder.create()
                .addCommand(Commands.LOG_PRODUCT_VIEWED)
                .populatePayload((json) -> {
                    json.put(Ecommerce.PRODUCT_ID, "sku123");
                    json.put(Ecommerce.PRODUCT_NAME, "Widget");
                    json.put(Ecommerce.VARIANT_ID, "widget_blue");
                    json.put(Ecommerce.PRICE, 49.99);
                    json.put(Ecommerce.CURRENCY, "USD");
                    json.put(Ecommerce.SOURCE, "test-source");
                })
                .build();

        brazeRemoteCommand.onInvoke(response);

        verify(mockBrazeInstance).logProductViewed(eq("sku123"), eq("Widget"), eq("widget_blue"), eq(49.99), eq("USD"), eq("test-source"), eq(null), eq(null), eq(null));
    }

    @Test
    public void testProductViewedEvent_LogsMultipleEvents_WhenPayloadIsArrayShaped() throws Exception {
        RemoteCommand.Response response = ResponseBuilder.create()
                .addCommand(Commands.LOG_PRODUCT_VIEWED)
                .populatePayload((json) -> {
                    json.put(Ecommerce.PRODUCT_ID, new JSONArray(new String[]{"sku123", "sku456"}));
                    json.put(Ecommerce.PRODUCT_NAME, new JSONArray(new String[]{"Widget", "Gadget"}));
                    json.put(Ecommerce.VARIANT_ID, new JSONArray(new String[]{"widget_blue", "gadget_red"}));
                    json.put(Ecommerce.PRICE, new JSONArray(new double[]{49.99, 19.99}));
                    json.put(Ecommerce.CURRENCY, "USD");
                    json.put(Ecommerce.SOURCE, "test-source");
                })
                .build();

        brazeRemoteCommand.onInvoke(response);

        ArgumentCaptor<String[]> productIdsCaptor = ArgumentCaptor.forClass(String[].class);
        ArgumentCaptor<BigDecimal[]> pricesCaptor = ArgumentCaptor.forClass(BigDecimal[].class);
        verify(mockBrazeInstance).logProductViewed(productIdsCaptor.capture(), any(), any(), pricesCaptor.capture(), eq("USD"), eq("test-source"), any(), any(), any());
        assertArrayEquals(new String[]{"sku123", "sku456"}, productIdsCaptor.getValue());
        assertEquals(49.99, pricesCaptor.getValue()[0].doubleValue(), 0.001);
        assertEquals(19.99, pricesCaptor.getValue()[1].doubleValue(), 0.001);
    }

    @Test
    public void testCartUpdatedEvent() throws Exception {
        JSONArray products = singleProductArray();
        RemoteCommand.Response response = ResponseBuilder.create()
                .addCommand(Commands.LOG_CART_UPDATED)
                .populatePayload((json) -> {
                    json.put(Ecommerce.CART_ID, "cart-1");
                    json.put(Ecommerce.CURRENCY, "USD");
                    json.put(Ecommerce.SOURCE, "test-source");
                    json.put(Ecommerce.TOTAL_VALUE, 49.99);
                    json.put(Ecommerce.CART_ACTION, "add");
                    json.put(Ecommerce.PRODUCTS, products);
                })
                .build();

        brazeRemoteCommand.onInvoke(response);

        verify(mockBrazeInstance).logCartUpdated(eq("cart-1"), eq("USD"), eq("test-source"), eq(49.99), eq(products), eq("add"), eq(null));
    }

    @Test
    public void testCartUpdatedEvent_NullTotalValue_WhenMissing() throws Exception {
        JSONArray products = singleProductArray();
        RemoteCommand.Response response = ResponseBuilder.create()
                .addCommand(Commands.LOG_CART_UPDATED)
                .populatePayload((json) -> {
                    json.put(Ecommerce.CART_ID, "cart-1");
                    json.put(Ecommerce.CURRENCY, "USD");
                    json.put(Ecommerce.SOURCE, "test-source");
                    json.put(Ecommerce.CART_ACTION, "remove");
                    json.put(Ecommerce.PRODUCTS, products);
                })
                .build();

        brazeRemoteCommand.onInvoke(response);

        verify(mockBrazeInstance).logCartUpdated(eq("cart-1"), eq("USD"), eq("test-source"), eq((Double) null), eq(products), eq("remove"), eq(null));
    }

    @Test
    public void testCheckoutStartedEvent() throws Exception {
        JSONArray products = singleProductArray();
        RemoteCommand.Response response = ResponseBuilder.create()
                .addCommand(Commands.LOG_CHECKOUT_STARTED)
                .populatePayload((json) -> {
                    json.put(Ecommerce.CHECKOUT_ID, "checkout-1");
                    json.put(Ecommerce.CURRENCY, "USD");
                    json.put(Ecommerce.SOURCE, "test-source");
                    json.put(Ecommerce.TOTAL_VALUE, 49.99);
                    json.put(Ecommerce.CART_ID, "cart-1");
                    json.put(Ecommerce.PRODUCTS, products);
                })
                .build();

        brazeRemoteCommand.onInvoke(response);

        verify(mockBrazeInstance).logCheckoutStarted(eq("checkout-1"), eq("USD"), eq("test-source"), eq(49.99), eq(products), eq("cart-1"), eq(null));
    }

    @Test
    public void testCheckoutStartedEvent_WithProperties() throws Exception {
        JSONArray products = singleProductArray();
        JSONObject eventProperties = new JSONObject();
        eventProperties.put("promo_code", "SUMMER10");
        RemoteCommand.Response response = ResponseBuilder.create()
                .addCommand(Commands.LOG_CHECKOUT_STARTED)
                .populatePayload((json) -> {
                    json.put(Ecommerce.CHECKOUT_ID, "checkout-1");
                    json.put(Ecommerce.CURRENCY, "USD");
                    json.put(Ecommerce.SOURCE, "test-source");
                    json.put(Ecommerce.TOTAL_VALUE, 49.99);
                    json.put(Ecommerce.CART_ID, "cart-1");
                    json.put(Ecommerce.PRODUCTS, products);
                    json.put(Ecommerce.PROPERTIES, eventProperties);
                })
                .build();

        brazeRemoteCommand.onInvoke(response);

        verify(mockBrazeInstance).logCheckoutStarted(eq("checkout-1"), eq("USD"), eq("test-source"), eq(49.99), eq(products), eq("cart-1"), eq(eventProperties));
    }

    @Test
    public void testOrderPlacedEvent() throws Exception {
        JSONArray products = singleProductArray();
        RemoteCommand.Response response = ResponseBuilder.create()
                .addCommand(Commands.LOG_ORDER_PLACED)
                .populatePayload((json) -> {
                    json.put(Ecommerce.ORDER_ID, "order-1");
                    json.put(Ecommerce.CURRENCY, "USD");
                    json.put(Ecommerce.SOURCE, "test-source");
                    json.put(Ecommerce.TOTAL_VALUE, 49.99);
                    json.put(Ecommerce.CART_ID, "cart-1");
                    json.put(Ecommerce.TOTAL_DISCOUNTS, 5.0);
                    json.put(Ecommerce.PRODUCTS, products);
                })
                .build();

        brazeRemoteCommand.onInvoke(response);

        verify(mockBrazeInstance).logOrderPlaced(eq("order-1"), eq("USD"), eq("test-source"), eq(49.99), eq(products), eq("cart-1"), eq(5.0), eq(null), eq(null));
    }

    @Test
    public void testOrderPlacedEvent_WithDiscounts() throws Exception {
        JSONArray products = singleProductArray();
        JSONObject discount = new JSONObject();
        discount.put("code", "SUMMER10");
        discount.put("amount", 5.0);
        JSONArray discounts = new JSONArray();
        discounts.put(discount);

        RemoteCommand.Response response = ResponseBuilder.create()
                .addCommand(Commands.LOG_ORDER_PLACED)
                .populatePayload((json) -> {
                    json.put(Ecommerce.ORDER_ID, "order-1");
                    json.put(Ecommerce.CURRENCY, "USD");
                    json.put(Ecommerce.SOURCE, "test-source");
                    json.put(Ecommerce.TOTAL_VALUE, 49.99);
                    json.put(Ecommerce.PRODUCTS, products);
                    json.put(Ecommerce.DISCOUNTS, discounts);
                })
                .build();

        brazeRemoteCommand.onInvoke(response);

        ArgumentCaptor<JSONArray> discountsCaptor = ArgumentCaptor.forClass(JSONArray.class);
        verify(mockBrazeInstance).logOrderPlaced(eq("order-1"), eq("USD"), eq("test-source"), eq(49.99), eq(products), eq(null), eq(null), discountsCaptor.capture(), eq(null));
        assertNotNull(discountsCaptor.getValue());
        assertEquals(discounts.toString(), discountsCaptor.getValue().toString());
    }

    @Test
    public void testCheckoutStartedEvent_NotDispatched_WhenTotalValueMissing() throws Exception {
        JSONArray products = singleProductArray();
        RemoteCommand.Response response = ResponseBuilder.create()
                .addCommand(Commands.LOG_CHECKOUT_STARTED)
                .populatePayload((json) -> {
                    json.put(Ecommerce.CHECKOUT_ID, "checkout-1");
                    json.put(Ecommerce.CURRENCY, "USD");
                    json.put(Ecommerce.SOURCE, "test-source");
                    json.put(Ecommerce.PRODUCTS, products);
                    // ecommerce_total_value intentionally omitted.
                })
                .build();

        brazeRemoteCommand.onInvoke(response);

        verify(mockBrazeInstance, never()).logCheckoutStarted(any(), any(), any(), anyDouble(), any(), any(), any());
    }

    @Test
    public void testEcommerceEvent_DoesNotDispatch_OnInvalidPayload() throws Exception {
        // Missing every ecommerce field, including the now-required total_value; dispatch must not
        // propagate an exception, but must also not fabricate a $0 order.
        RemoteCommand.Response response = ResponseBuilder.create()
                .addCommand(Commands.LOG_ORDER_PLACED)
                .build();

        brazeRemoteCommand.onInvoke(response);

        // Reached here without throwing, but the missing required total_value means the event was
        // never dispatched to the instance.
        verify(mockBrazeInstance, never()).logOrderPlaced(anyString(), anyString(), anyString(), anyDouble(), any(), any(), any(), any(), any());
    }

    @Test
    public void testOrderCancelledEvent() throws Exception {
        JSONArray products = singleProductArray();
        RemoteCommand.Response response = ResponseBuilder.create()
                .addCommand(Commands.LOG_ORDER_CANCELLED)
                .populatePayload((json) -> {
                    json.put(Ecommerce.ORDER_ID, "order-1");
                    json.put(Ecommerce.CURRENCY, "USD");
                    json.put(Ecommerce.SOURCE, "test-source");
                    json.put(Ecommerce.TOTAL_VALUE, 49.99);
                    json.put(Ecommerce.CANCEL_REASON, "customer_request");
                    json.put(Ecommerce.PRODUCTS, products);
                })
                .build();

        brazeRemoteCommand.onInvoke(response);

        verify(mockBrazeInstance).logOrderCancelled(eq("order-1"), eq("USD"), eq("test-source"), eq(49.99), eq(null), eq(null), eq(null), eq(products), eq("customer_request"), eq(null), eq(null), eq(null));
    }

    @Test
    public void testOrderCancelledEvent_NotDispatched_WhenTotalValueMissing() throws Exception {
        JSONArray products = singleProductArray();
        RemoteCommand.Response response = ResponseBuilder.create()
                .addCommand(Commands.LOG_ORDER_CANCELLED)
                .populatePayload((json) -> {
                    json.put(Ecommerce.ORDER_ID, "order-1");
                    json.put(Ecommerce.CURRENCY, "USD");
                    json.put(Ecommerce.SOURCE, "test-source");
                    json.put(Ecommerce.CANCEL_REASON, "customer_request");
                    json.put(Ecommerce.PRODUCTS, products);
                    // ecommerce_total_value intentionally omitted.
                })
                .build();

        brazeRemoteCommand.onInvoke(response);

        verify(mockBrazeInstance, never()).logOrderCancelled(any(), any(), any(), anyDouble(), any(), any(), any(), any(), any(), any(), any(), any());
    }

    @Test
    public void testOrderCancelledEvent_NotDispatched_WhenCancelReasonMissing() throws Exception {
        JSONArray products = singleProductArray();
        RemoteCommand.Response response = ResponseBuilder.create()
                .addCommand(Commands.LOG_ORDER_CANCELLED)
                .populatePayload((json) -> {
                    json.put(Ecommerce.ORDER_ID, "order-1");
                    json.put(Ecommerce.CURRENCY, "USD");
                    json.put(Ecommerce.SOURCE, "test-source");
                    json.put(Ecommerce.TOTAL_VALUE, 49.99);
                    json.put(Ecommerce.PRODUCTS, products);
                    // cancel_reason intentionally omitted; required per the wire schema.
                })
                .build();

        brazeRemoteCommand.onInvoke(response);

        verify(mockBrazeInstance, never()).logOrderCancelled(any(), any(), any(), anyDouble(), any(), any(), any(), any(), any(), any(), any(), any());
    }

    @Test
    public void testOrderCancelledEvent_ForwardsSubtotalTaxShipping() throws Exception {
        JSONArray products = singleProductArray();
        RemoteCommand.Response response = ResponseBuilder.create()
                .addCommand(Commands.LOG_ORDER_CANCELLED)
                .populatePayload((json) -> {
                    json.put(Ecommerce.ORDER_ID, "order-1");
                    json.put(Ecommerce.CURRENCY, "USD");
                    json.put(Ecommerce.SOURCE, "test-source");
                    json.put(Ecommerce.TOTAL_VALUE, 49.99);
                    json.put(Ecommerce.SUBTOTAL_VALUE, 44.99);
                    json.put(Ecommerce.TAX, 3.0);
                    json.put(Ecommerce.SHIPPING, 2.0);
                    json.put(Ecommerce.CANCEL_REASON, "customer_request");
                    json.put(Ecommerce.PRODUCTS, products);
                })
                .build();

        brazeRemoteCommand.onInvoke(response);

        verify(mockBrazeInstance).logOrderCancelled(eq("order-1"), eq("USD"), eq("test-source"), eq(49.99), eq(44.99), eq(3.0), eq(2.0), eq(products), eq("customer_request"), eq(null), eq(null), eq(null));
    }

    @Test
    public void testOrderCancelledEvent_NotDispatched_WhenOrderIdMissing() throws Exception {
        JSONArray products = singleProductArray();
        RemoteCommand.Response response = ResponseBuilder.create()
                .addCommand(Commands.LOG_ORDER_CANCELLED)
                .populatePayload((json) -> {
                    json.put(Ecommerce.CURRENCY, "USD");
                    json.put(Ecommerce.SOURCE, "test-source");
                    json.put(Ecommerce.TOTAL_VALUE, 49.99);
                    json.put(Ecommerce.CANCEL_REASON, "customer_request");
                    json.put(Ecommerce.PRODUCTS, products);
                    // order_id intentionally omitted; required per the wire schema.
                })
                .build();

        brazeRemoteCommand.onInvoke(response);

        verify(mockBrazeInstance, never()).logOrderCancelled(any(), any(), any(), anyDouble(), any(), any(), any(), any(), any(), any(), any(), any());
    }

    @Test
    public void testOrderCancelledEvent_NotDispatched_WhenSourceMissing() throws Exception {
        JSONArray products = singleProductArray();
        RemoteCommand.Response response = ResponseBuilder.create()
                .addCommand(Commands.LOG_ORDER_CANCELLED)
                .populatePayload((json) -> {
                    json.put(Ecommerce.ORDER_ID, "order-1");
                    json.put(Ecommerce.CURRENCY, "USD");
                    json.put(Ecommerce.TOTAL_VALUE, 49.99);
                    json.put(Ecommerce.CANCEL_REASON, "customer_request");
                    json.put(Ecommerce.PRODUCTS, products);
                    // source intentionally omitted; required per the wire schema.
                })
                .build();

        brazeRemoteCommand.onInvoke(response);

        verify(mockBrazeInstance, never()).logOrderCancelled(any(), any(), any(), anyDouble(), any(), any(), any(), any(), any(), any(), any(), any());
    }

    @Test
    public void testOrderRefundedEvent() throws Exception {
        JSONArray products = singleProductArray();
        RemoteCommand.Response response = ResponseBuilder.create()
                .addCommand(Commands.LOG_ORDER_REFUNDED)
                .populatePayload((json) -> {
                    json.put(Ecommerce.ORDER_ID, "order-1");
                    json.put(Ecommerce.CURRENCY, "USD");
                    json.put(Ecommerce.SOURCE, "test-source");
                    json.put(Ecommerce.TOTAL_VALUE, 49.99);
                    json.put(Ecommerce.PRODUCTS, products);
                })
                .build();

        brazeRemoteCommand.onInvoke(response);

        verify(mockBrazeInstance).logOrderRefunded(eq("order-1"), eq("USD"), eq("test-source"), eq(49.99), eq(products), eq(null), eq(null), eq(null));
    }

    @Test
    public void testOrderRefundedEvent_NotDispatched_WhenTotalValueMissing() throws Exception {
        JSONArray products = singleProductArray();
        RemoteCommand.Response response = ResponseBuilder.create()
                .addCommand(Commands.LOG_ORDER_REFUNDED)
                .populatePayload((json) -> {
                    json.put(Ecommerce.ORDER_ID, "order-1");
                    json.put(Ecommerce.CURRENCY, "USD");
                    json.put(Ecommerce.SOURCE, "test-source");
                    json.put(Ecommerce.PRODUCTS, products);
                    // ecommerce_total_value intentionally omitted.
                })
                .build();

        brazeRemoteCommand.onInvoke(response);

        verify(mockBrazeInstance, never()).logOrderRefunded(any(), any(), any(), anyDouble(), any(), any(), any(), any());
    }

    @Test
    public void testOrderCancelledEvent_NotDispatched_WhenProductsMissing() throws Exception {
        RemoteCommand.Response response = ResponseBuilder.create()
                .addCommand(Commands.LOG_ORDER_CANCELLED)
                .populatePayload((json) -> {
                    json.put(Ecommerce.ORDER_ID, "order-1");
                    json.put(Ecommerce.CURRENCY, "USD");
                    json.put(Ecommerce.SOURCE, "test-source");
                    json.put(Ecommerce.TOTAL_VALUE, 49.99);
                    json.put(Ecommerce.CANCEL_REASON, "customer_request");
                    // products intentionally omitted; required per the wire schema.
                })
                .build();

        brazeRemoteCommand.onInvoke(response);

        verify(mockBrazeInstance, never()).logOrderCancelled(any(), any(), any(), anyDouble(), any(), any(), any(), any(), any(), any(), any(), any());
    }

    @Test
    public void testOrderRefundedEvent_NotDispatched_WhenProductsMissing() throws Exception {
        RemoteCommand.Response response = ResponseBuilder.create()
                .addCommand(Commands.LOG_ORDER_REFUNDED)
                .populatePayload((json) -> {
                    json.put(Ecommerce.ORDER_ID, "order-1");
                    json.put(Ecommerce.CURRENCY, "USD");
                    json.put(Ecommerce.SOURCE, "test-source");
                    json.put(Ecommerce.TOTAL_VALUE, 49.99);
                    // products intentionally omitted; required per the wire schema.
                })
                .build();

        brazeRemoteCommand.onInvoke(response);

        verify(mockBrazeInstance, never()).logOrderRefunded(any(), any(), any(), anyDouble(), any(), any(), any(), any());
    }

    @Test
    public void testOrderRefundedEvent_NotDispatched_WhenOrderIdMissing() throws Exception {
        JSONArray products = singleProductArray();
        RemoteCommand.Response response = ResponseBuilder.create()
                .addCommand(Commands.LOG_ORDER_REFUNDED)
                .populatePayload((json) -> {
                    json.put(Ecommerce.CURRENCY, "USD");
                    json.put(Ecommerce.SOURCE, "test-source");
                    json.put(Ecommerce.TOTAL_VALUE, 49.99);
                    json.put(Ecommerce.PRODUCTS, products);
                    // order_id intentionally omitted; required per the wire schema.
                })
                .build();

        brazeRemoteCommand.onInvoke(response);

        verify(mockBrazeInstance, never()).logOrderRefunded(any(), any(), any(), anyDouble(), any(), any(), any(), any());
    }

    @Test
    public void testOrderRefundedEvent_NotDispatched_WhenSourceMissing() throws Exception {
        JSONArray products = singleProductArray();
        RemoteCommand.Response response = ResponseBuilder.create()
                .addCommand(Commands.LOG_ORDER_REFUNDED)
                .populatePayload((json) -> {
                    json.put(Ecommerce.ORDER_ID, "order-1");
                    json.put(Ecommerce.CURRENCY, "USD");
                    json.put(Ecommerce.TOTAL_VALUE, 49.99);
                    json.put(Ecommerce.PRODUCTS, products);
                    // source intentionally omitted; required per the wire schema.
                })
                .build();

        brazeRemoteCommand.onInvoke(response);

        verify(mockBrazeInstance, never()).logOrderRefunded(any(), any(), any(), anyDouble(), any(), any(), any(), any());
    }

    @Test
    public void testCartUpdatedEvent_NotDispatched_WhenTotalValueMissingAndActionDefaultsToReplace() throws Exception {
        JSONArray products = singleProductArray();
        RemoteCommand.Response response = ResponseBuilder.create()
                .addCommand(Commands.LOG_CART_UPDATED)
                .populatePayload((json) -> {
                    json.put(Ecommerce.CART_ID, "cart-1");
                    json.put(Ecommerce.CURRENCY, "USD");
                    json.put(Ecommerce.SOURCE, "test-source");
                    json.put(Ecommerce.PRODUCTS, products);
                    // ecommerce_cart_action and ecommerce_total_value both intentionally omitted;
                    // action defaults to "replace", which requires total_value.
                })
                .build();

        brazeRemoteCommand.onInvoke(response);

        verify(mockBrazeInstance, never()).logCartUpdated(any(), any(), any(), any(), any(), any(), any());
    }

    private JSONArray multiProductArray() throws JSONException {
        JSONObject productOne = new JSONObject();
        productOne.put(Ecommerce.PRODUCT_ID, "sku123");
        productOne.put(Ecommerce.PRODUCT_NAME, "Widget");
        productOne.put(Ecommerce.VARIANT_ID, "widget_blue");
        productOne.put(Ecommerce.PRICE, 49.99);
        productOne.put(Ecommerce.QUANTITY, 1);

        JSONObject productTwo = new JSONObject();
        productTwo.put(Ecommerce.PRODUCT_ID, "sku456");
        productTwo.put(Ecommerce.PRODUCT_NAME, "Gadget");
        productTwo.put(Ecommerce.VARIANT_ID, "gadget_red");
        productTwo.put(Ecommerce.PRICE, 19.99);
        productTwo.put(Ecommerce.QUANTITY, 3);

        JSONArray products = new JSONArray();
        products.put(productOne);
        products.put(productTwo);
        return products;
    }

    @Test
    public void testCartUpdatedEvent_MultipleProducts() throws Exception {
        JSONArray products = multiProductArray();
        RemoteCommand.Response response = ResponseBuilder.create()
                .addCommand(Commands.LOG_CART_UPDATED)
                .populatePayload((json) -> {
                    json.put(Ecommerce.CART_ID, "cart-1");
                    json.put(Ecommerce.CURRENCY, "USD");
                    json.put(Ecommerce.SOURCE, "test-source");
                    json.put(Ecommerce.TOTAL_VALUE, 109.96);
                    json.put(Ecommerce.CART_ACTION, "replace");
                    json.put(Ecommerce.PRODUCTS, products);
                })
                .build();

        brazeRemoteCommand.onInvoke(response);

        verify(mockBrazeInstance).logCartUpdated(eq("cart-1"), eq("USD"), eq("test-source"), eq(109.96), eq(products), eq("replace"), eq(null));
    }

    @Test
    public void testOrderPlacedEvent_MultipleProducts() throws Exception {
        JSONArray products = multiProductArray();
        RemoteCommand.Response response = ResponseBuilder.create()
                .addCommand(Commands.LOG_ORDER_PLACED)
                .populatePayload((json) -> {
                    json.put(Ecommerce.ORDER_ID, "order-1");
                    json.put(Ecommerce.CURRENCY, "USD");
                    json.put(Ecommerce.SOURCE, "test-source");
                    json.put(Ecommerce.TOTAL_VALUE, 109.96);
                    json.put(Ecommerce.PRODUCTS, products);
                })
                .build();

        brazeRemoteCommand.onInvoke(response);

        verify(mockBrazeInstance).logOrderPlaced(eq("order-1"), eq("USD"), eq("test-source"), eq(109.96), eq(products), eq(null), eq(null), eq(null), eq(null));
    }

    @Test
    public void testOrderCancelledEvent_MultipleProducts() throws Exception {
        JSONArray products = multiProductArray();
        RemoteCommand.Response response = ResponseBuilder.create()
                .addCommand(Commands.LOG_ORDER_CANCELLED)
                .populatePayload((json) -> {
                    json.put(Ecommerce.ORDER_ID, "order-1");
                    json.put(Ecommerce.CURRENCY, "USD");
                    json.put(Ecommerce.SOURCE, "test-source");
                    json.put(Ecommerce.TOTAL_VALUE, 109.96);
                    json.put(Ecommerce.CANCEL_REASON, "customer_request");
                    json.put(Ecommerce.PRODUCTS, products);
                })
                .build();

        brazeRemoteCommand.onInvoke(response);

        verify(mockBrazeInstance).logOrderCancelled(eq("order-1"), eq("USD"), eq("test-source"), eq(109.96), eq(null), eq(null), eq(null), eq(products), eq("customer_request"), eq(null), eq(null), eq(null));
    }

    @Test
    public void testOrderRefundedEvent_MultipleProducts() throws Exception {
        JSONArray products = multiProductArray();
        RemoteCommand.Response response = ResponseBuilder.create()
                .addCommand(Commands.LOG_ORDER_REFUNDED)
                .populatePayload((json) -> {
                    json.put(Ecommerce.ORDER_ID, "order-1");
                    json.put(Ecommerce.CURRENCY, "USD");
                    json.put(Ecommerce.SOURCE, "test-source");
                    json.put(Ecommerce.TOTAL_VALUE, 109.96);
                    json.put(Ecommerce.PRODUCTS, products);
                })
                .build();

        brazeRemoteCommand.onInvoke(response);

        verify(mockBrazeInstance).logOrderRefunded(eq("order-1"), eq("USD"), eq("test-source"), eq(109.96), eq(products), eq(null), eq(null), eq(null));
    }

    @Test
    public void testProductViewedEvent_NotDispatched_WhenPriceMissing() throws Exception {
        RemoteCommand.Response response = ResponseBuilder.create()
                .addCommand(Commands.LOG_PRODUCT_VIEWED)
                .populatePayload((json) -> {
                    json.put(Ecommerce.PRODUCT_ID, "sku123");
                    json.put(Ecommerce.PRODUCT_NAME, "Widget");
                    json.put(Ecommerce.VARIANT_ID, "widget_blue");
                    json.put(Ecommerce.CURRENCY, "USD");
                    json.put(Ecommerce.SOURCE, "test-source");
                    // ecommerce_price intentionally omitted; required per the wire schema and must
                    // not silently dispatch a fabricated $0 event.
                })
                .build();

        brazeRemoteCommand.onInvoke(response);

        verify(mockBrazeInstance, never()).logProductViewed(any(), any(), any(), anyDouble(), any(), any(), any(), any(), any());
    }

    @Test
    public void testProductViewedEvent_NullImageAndProductUrl_WhenJsonNull() throws Exception {
        RemoteCommand.Response response = ResponseBuilder.create()
                .addCommand(Commands.LOG_PRODUCT_VIEWED)
                .populatePayload((json) -> {
                    json.put(Ecommerce.PRODUCT_ID, "sku123");
                    json.put(Ecommerce.PRODUCT_NAME, "Widget");
                    json.put(Ecommerce.VARIANT_ID, "widget_blue");
                    json.put(Ecommerce.PRICE, 49.99);
                    json.put(Ecommerce.CURRENCY, "USD");
                    json.put(Ecommerce.SOURCE, "test-source");
                    json.put(Ecommerce.IMAGE_URL, JSONObject.NULL);
                    json.put(Ecommerce.PRODUCT_URL, JSONObject.NULL);
                })
                .build();

        brazeRemoteCommand.onInvoke(response);

        verify(mockBrazeInstance).logProductViewed(eq("sku123"), eq("Widget"), eq("widget_blue"), eq(49.99), eq("USD"), eq("test-source"), eq(null), eq(null), eq(null));
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

        verify(mockBrazeInstance, never()).setAdTrackingEnabled(anyString(), anyBoolean());
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
