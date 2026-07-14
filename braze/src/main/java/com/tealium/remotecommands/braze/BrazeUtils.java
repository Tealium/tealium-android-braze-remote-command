package com.tealium.remotecommands.braze;

import android.util.Log;

import com.braze.enums.BrazeDateFormat;
import com.braze.enums.Month;
import com.braze.enums.Gender;
import com.braze.models.outgoing.BrazeProperties;
import com.braze.models.recommended.ecommerce.CartUpdatedAction;
import com.braze.models.recommended.ecommerce.EcommerceProduct;
import com.braze.support.DateTimeUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

class BrazeUtils {

    /**
     * The Format of any Dates that were sent into the WebView as a native java.util.Date, will be
     * returned to the RemoteCommand in the following date format. This is a static property to
     * easily help conversion back into a native Date type.
     */
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("E MMM dd HH:mm:ss z yyyy");

    /**
     * Standard ISO 8601 date format to use when attempting to parse dates.
     */
    public static final SimpleDateFormat ISO_8601_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ROOT);

    /**
     * At the time of writing, the Android SDK will stringify values in a HashMap such that
     * the native type is lost. The method being tested here will attempt to recover that.
     * As a result the expected types should be integer/double/boolean etc despite the value
     * that was sent in the event might actually have been a string.
     * <p>
     * This is a helper method that will take
     *
     * @param key        - name of the custom property to add.
     * @param data       - value to add to the custom property
     * @param properties - an existing  BrazeProperties object to add this key-value pair to. If
     *                   null, then a new  BrazeProperties object will be created to be returned
     * @return The amended BrazeProperties including the additional one supplied
     */
    public static BrazeProperties addCustomProperty(String key, Object data, BrazeProperties properties) {
        return addCustomProperty(key, data, properties, false);
    }

    /**
     * At the time of writing, the Android SDK will stringify values in a HashMap such that
     * the native type is lost. The method being tested here will attempt to recover that.
     * As a result the expected types should be integer/double/boolean, etc. despite the value
     * that was sent in the event might actually have been a string.
     * <p>
     * This is a helper method that will take
     *
     * @param key        - name of the custom property to add.
     * @param data       - value to add to the custom property
     * @param properties - an existing BrazeProperties object to add this key-value pair to. If
     *                   null, then a new BrazeProperties object will be created to be returned
     * @param strictPropertiesEnabled true to add String values as-is without attempting coercion, false to allow coercion
     * @return The amended BrazeProperties including the additional one supplied
     */
    public static BrazeProperties addCustomProperty(String key, Object data, BrazeProperties properties, boolean strictPropertiesEnabled) {
        if (properties == null) {
            Log.d(BrazeConstants.TAG, "Creating new  BrazeProperties");
            properties = new BrazeProperties();
        }

        if (data instanceof String) {
            if (strictPropertiesEnabled) {
                properties.addProperty(key, data);
                return properties;
            }

            try {
                properties = addCustomProperty(key, Integer.parseInt((String) data), properties);
                return properties;
            } catch (NumberFormatException ignored) {

            }
            try {
                properties = addCustomProperty(key, Double.parseDouble((String) data), properties);
                return properties;
            } catch (NumberFormatException ignored) {

            }
            Date date;
            if ((date = BrazeUtils.parseDate((String) data)) != null) {
                properties = addCustomProperty(key, date, properties);
                return properties;
            }
            if (((String) data).equalsIgnoreCase("true") || ((String) data).equalsIgnoreCase("false")) {
                properties = addCustomProperty(key, Boolean.parseBoolean((String) data), properties);
                return properties;
            } else {
                properties.addProperty(key, (String) data);
            }

        } else if (data instanceof Integer || data instanceof Long || data instanceof Double || data instanceof Boolean || data instanceof Date) {
            properties.addProperty(key, data);
        } else if (data instanceof Float) {
            // addProperty seems to drop Float values.
            properties.addProperty(key, ((Float) data).doubleValue());
        }

        return properties;
    }

    /**
     * Short-hand method for calling addCustomProperty, generating a new  BrazeProperties object at
     * the same time.
     *
     * @param key  - name of the custom property to add.
     * @param data - value to add to the custom property
     * @return
     */
    public static BrazeProperties addCustomProperty(String key, Object data) {
        return addCustomProperty(key, data, null);
    }

    /**
     * Helper method to translate a JSONObject of key-value pairs into an  BrazeProperties object.
     * The values in the JSONObject should only be supported types for the  BrazeProperties class,
     * which at the time of writing is only String, Integer, Double, Date and Boolean
     *
     * This method will also attempt to coerce String values into supported primitives.
     *
     * @param customProperties JSONObject of Key-Value pairs.
     * @return  BrazeProperties containing the Key-Value pairs supplied
     */
    public static BrazeProperties extractCustomProperties(JSONObject customProperties) {
        return extractCustomProperties(customProperties, false);
    }

    /**
     * Helper method to translate a JSONObject of key-value pairs into an  BrazeProperties object.
     * The values in the JSONObject should only be supported types for the  BrazeProperties class,
     * which at the time of writing is only String, Integer, Double, Date and Boolean
     *
     * Use the `strictPropertiesEnabled` flag to decide how to handle string values. Set this to `true`
     * if any properties should be passed straight through to the returned BrazeProperties object. Or
     * `false` to allow coercion from string values into some primitives, e.g "123" -> (Int) 123,
     * or "false" -> (Boolean) false
     *
     * @param customProperties JSONObject of Key-Value pairs.
     * @param strictPropertiesEnabled true if property values should be passed on unchanged, else false to coerce string values to other primitives
     * @return  BrazeProperties containing the Key-Value pairs supplied
     */
    public static BrazeProperties extractCustomProperties(JSONObject customProperties, boolean strictPropertiesEnabled) {
        BrazeProperties props = new BrazeProperties();
        if (customProperties != null) {
            try {
                // add the provided custom properties into the new payload object
                Iterator<String> iterator = customProperties.keys();
                while (iterator.hasNext()) {
                    String key = iterator.next();
                    props = BrazeUtils.addCustomProperty(key, customProperties.get(key), props, strictPropertiesEnabled);
                }

            } catch (JSONException jex) {
                Log.w(BrazeConstants.TAG, "Failed to extract custom_attributes from JSON", jex);
            } catch (NullPointerException npe) {
                Log.w(BrazeConstants.TAG, "Object is null.", npe);
            }
        }
        return props;
    }

    /**
     * Helper method to determine whether a particular key in a JSONObject is present AND also is
     * not a null value.
     *
     * @param json - the JSONObject to inspect
     * @param key  - the key in the JSONObject to check existence and value
     * @return
     */
    static boolean keyHasValue(JSONObject json, String key) {
        return (json != null && json.has(key) && !json.isNull(key));
    }

    /**
     * Helper to convert string representation of a Gender into the required.
     * <p>
     * (m)ale = Gender.MALE
     * (f)emale = Gender.FEMALE
     * (o)ther = Gender.OTHER
     * na/not_applicable = Gender.NOT_APPLICABLE
     * no/prefer_not_to_say = Gender.PREFER_NOT_TO_SAY
     * unknown = Gender.UNKNOWN
     * <p>
     * else return null
     *
     * @param genderString
     * @return The Gender enum if found, or null
     */
    public static Gender getGenderEnumFromString(String genderString) {
        if (genderString == null) return null;

        Gender genderEnum;
        switch (genderString.toLowerCase()) {
            case "female":
            case "f":
                genderEnum = Gender.FEMALE;
                break;
            case "male":
            case "m":
                genderEnum = Gender.MALE;
                break;
            case "other":
            case "o":
                genderEnum = Gender.OTHER;
                break;
            case "not_applicable":
            case "na":
                genderEnum = Gender.NOT_APPLICABLE;
                break;
            case "prefer_not_to_say":
            case "no":
                genderEnum = Gender.PREFER_NOT_TO_SAY;
                break;
            case "unknown":
                genderEnum = Gender.UNKNOWN;
                break;
            default:
                genderEnum = null;
                break;
        }

        return genderEnum;
    }

    /**
     * Helper to convert a string representation of a cart action into the Braze CartUpdatedAction
     * enum used by CartUpdatedEvent.
     * <p>
     * add = CartUpdatedAction.ADD
     * remove = CartUpdatedAction.REMOVE
     * replace = CartUpdatedAction.REPLACE
     * <p>
     * else returns CartUpdatedAction.REPLACE, matching Braze's documented wire behavior that
     * omitting the action defaults to a full cart replacement.
     *
     * @param action the cart action as a string
     * @return The CartUpdatedAction enum if found, else CartUpdatedAction.REPLACE
     */
    public static CartUpdatedAction getCartUpdatedActionFromString(String action) {
        if (action == null) return CartUpdatedAction.REPLACE;

        CartUpdatedAction actionEnum;
        switch (action.toLowerCase(Locale.ROOT)) {
            case "add":
                actionEnum = CartUpdatedAction.ADD;
                break;
            case "remove":
                actionEnum = CartUpdatedAction.REMOVE;
                break;
            case "replace":
                actionEnum = CartUpdatedAction.REPLACE;
                break;
            default:
                actionEnum = CartUpdatedAction.REPLACE;
                break;
        }

        return actionEnum;
    }

    /**
     * Helper to build a list of Braze EcommerceProduct objects from a JSONArray of product objects.
     * Each element is expected to be a JSONObject using the keys in BrazeConstants.Ecommerce
     * (product_id, product_name, variant_id, price, quantity and the optional image_url, product_url
     * and properties). Any per-product custom properties are extracted into a BrazeProperties object.
     *
     * @param products                the JSONArray of product objects
     * @param strictPropertiesEnabled whether custom property values should be passed on unchanged
     * @return a list of EcommerceProduct, empty if the array is null or empty
     */
    static List<EcommerceProduct> getEcommerceProductsFromJson(JSONArray products, boolean strictPropertiesEnabled) {
        List<EcommerceProduct> result = new ArrayList<>();
        if (isNullOrEmpty(products)) {
            return result;
        }

        for (int i = 0; i < products.length(); i++) {
            JSONObject product = products.optJSONObject(i);
            if (product == null) {
                continue;
            }

            // price is required per product; skip (rather than silently fabricating $0) any
            // product entry missing it, so one bad line item doesn't corrupt the rest of the event.
            double price;
            try {
                price = product.getDouble(BrazeConstants.Ecommerce.PRICE);
            } catch (JSONException jex) {
                Log.w(BrazeConstants.TAG, "Skipping ecommerce product missing required price", jex);
                continue;
            }

            result.add(new EcommerceProduct(
                    product.optString(BrazeConstants.Ecommerce.PRODUCT_ID),
                    product.optString(BrazeConstants.Ecommerce.PRODUCT_NAME),
                    product.optString(BrazeConstants.Ecommerce.VARIANT_ID),
                    price,
                    product.optLong(BrazeConstants.Ecommerce.QUANTITY, 1L),
                    keyHasValue(product, BrazeConstants.Ecommerce.IMAGE_URL) ? product.optString(BrazeConstants.Ecommerce.IMAGE_URL) : null,
                    keyHasValue(product, BrazeConstants.Ecommerce.PRODUCT_URL) ? product.optString(BrazeConstants.Ecommerce.PRODUCT_URL) : null,
                    extractCustomProperties(product.optJSONObject(BrazeConstants.Ecommerce.PRODUCT_PROPERTIES), strictPropertiesEnabled)
            ));
        }

        return result;
    }

    /**
     * Helper to build a list of discount maps from a JSONArray of discount objects, for use with
     * OrderPlacedEvent's discounts parameter. Each element is expected to be a JSONObject using
     * whatever keys are present among "code" (String), "amount" (Number) and "type" (String);
     * absent or null keys are simply skipped for that entry rather than forcing all three.
     *
     * @param discounts the JSONArray of discount objects
     * @return a list of Map<String, Object>, empty if the array is null or empty
     */
    static List<Map<String, Object>> getDiscountsListFromJson(JSONArray discounts) {
        List<Map<String, Object>> result = new ArrayList<>();
        if (isNullOrEmpty(discounts)) {
            return result;
        }

        for (int i = 0; i < discounts.length(); i++) {
            JSONObject discount = discounts.optJSONObject(i);
            if (discount == null) {
                continue;
            }

            Map<String, Object> entry = new HashMap<>();
            if (keyHasValue(discount, "code")) {
                entry.put("code", discount.optString("code"));
            }
            if (keyHasValue(discount, "amount")) {
                entry.put("amount", discount.optDouble("amount"));
            }
            if (keyHasValue(discount, "type")) {
                entry.put("type", discount.optString("type"));
            }

            result.add(entry);
        }

        return result;
    }

    /**
     * Helper to rebuild a JSONArray of product objects into the wire schema expected by the
     * untyped ecommerce.order_cancelled / ecommerce.order_refunded custom events. Each product's
     * fields are copied straight through, except the internal "properties" key (used by the four
     * typed ecommerce events) is renamed to "metadata" to match the documented wire schema for
     * these two events; the key is omitted entirely when absent.
     *
     * @param products the JSONArray of product objects, using BrazeConstants.Ecommerce keys
     * @return a new JSONArray of plain JSONObjects using the wire schema's "metadata" key
     */
    static JSONArray getEcommerceProductsAsWireJson(JSONArray products) {
        JSONArray result = new JSONArray();
        if (isNullOrEmpty(products)) {
            return result;
        }

        for (int i = 0; i < products.length(); i++) {
            JSONObject product = products.optJSONObject(i);
            if (product == null) {
                continue;
            }

            // price is required per product; skip (rather than silently fabricating $0) any
            // product entry missing it, so one bad line item doesn't corrupt the rest of the event.
            if (!keyHasValue(product, BrazeConstants.Ecommerce.PRICE)) {
                Log.w(BrazeConstants.TAG, "Skipping ecommerce product missing required price");
                continue;
            }

            JSONObject wireProduct = new JSONObject();
            try {
                wireProduct.put(BrazeConstants.Ecommerce.PRODUCT_ID, product.optString(BrazeConstants.Ecommerce.PRODUCT_ID));
                wireProduct.put(BrazeConstants.Ecommerce.PRODUCT_NAME, product.optString(BrazeConstants.Ecommerce.PRODUCT_NAME));
                wireProduct.put(BrazeConstants.Ecommerce.VARIANT_ID, product.optString(BrazeConstants.Ecommerce.VARIANT_ID));
                wireProduct.put(BrazeConstants.Ecommerce.PRICE, product.optDouble(BrazeConstants.Ecommerce.PRICE));
                wireProduct.put(BrazeConstants.Ecommerce.QUANTITY, product.optInt(BrazeConstants.Ecommerce.QUANTITY, 1));
                if (keyHasValue(product, BrazeConstants.Ecommerce.IMAGE_URL)) {
                    wireProduct.put(BrazeConstants.Ecommerce.IMAGE_URL, product.optString(BrazeConstants.Ecommerce.IMAGE_URL));
                }
                if (keyHasValue(product, BrazeConstants.Ecommerce.PRODUCT_URL)) {
                    wireProduct.put(BrazeConstants.Ecommerce.PRODUCT_URL, product.optString(BrazeConstants.Ecommerce.PRODUCT_URL));
                }
                if (keyHasValue(product, BrazeConstants.Ecommerce.PRODUCT_PROPERTIES)) {
                    // The wire schema for order_cancelled/order_refunded calls this key "metadata",
                    // whereas the internal payload shape (shared with the four typed events) uses
                    // "properties" - rename it here since these two events aren't SDK-typed.
                    wireProduct.put("metadata", product.optJSONObject(BrazeConstants.Ecommerce.PRODUCT_PROPERTIES));
                }
            } catch (JSONException jex) {
                // A non-finite price (NaN/Infinity) makes JSONObject.put throw mid-build; skip the
                // partially-built product rather than appending it, matching getEcommerceProductsFromJson.
                Log.w(BrazeConstants.TAG, "Failed to build wire-schema ecommerce product JSON", jex);
                continue;
            }

            result.put(wireProduct);
        }

        return result;
    }

    /**
     * Helper to convert string representation of a Month into the required enum
     *
     * @param month
     * @return The Month enum if found, or null
     */
    public static Month getMonthEnumFromInt(int month) {
        // Try Braze utility first
        Month monthEnum = Month.getMonth(month);
        if (monthEnum != null) return monthEnum;

        // fallback check
        switch (month) {
            case 0:
                monthEnum = Month.JANUARY;
                break;
            case 1:
                monthEnum = Month.FEBRUARY;
                break;
            case 2:
                monthEnum = Month.MARCH;
                break;
            case 3:
                monthEnum = Month.APRIL;
                break;
            case 4:
                monthEnum = Month.MAY;
                break;
            case 5:
                monthEnum = Month.JUNE;
                break;
            case 6:
                monthEnum = Month.JULY;
                break;
            case 7:
                monthEnum = Month.AUGUST;
                break;
            case 8:
                monthEnum = Month.SEPTEMBER;
                break;
            case 9:
                monthEnum = Month.OCTOBER;
                break;
            case 10:
                monthEnum = Month.NOVEMBER;
                break;
            case 11:
                monthEnum = Month.DECEMBER;
                break;
        }

        return monthEnum;
    }

    /**
     * Helper to convert a JSONArray to an array of Strings
     *
     * @param jsonArray
     * @return
     */
    static String[] getStringArrayFromJson(JSONArray jsonArray) {
        String[] returnData;
        if (isNullOrEmpty(jsonArray)) {
            returnData = new String[0];
        } else {
            returnData = new String[jsonArray.length()];
            for (int i = 0; i < jsonArray.length(); i++) {
                returnData[i] = jsonArray.optString(i, "");
            }
        }

        return returnData;
    }

    /**
     * Helper to convert a JSONArray to an array of Integers
     *
     * @param jsonArray
     * @return
     */
    static Integer[] getIntegerArrayFromJson(JSONArray jsonArray) {
        Integer[] returnData;
        if (isNullOrEmpty(jsonArray)) {
            returnData = new Integer[0];
        } else {
            returnData = new Integer[jsonArray.length()];
            for (int i = 0; i < jsonArray.length(); i++) {
                returnData[i] = jsonArray.optInt(i, 1);
            }
        }

        return returnData;
    }

    /**
     * Helper to convert a JSONArray to an array of BigDecimals
     *
     * @param jsonArray
     * @return
     */
    static BigDecimal[] getBigDecimalArrayFromJson(JSONArray jsonArray) {
        BigDecimal[] returnData;
        if (isNullOrEmpty(jsonArray)) {
            returnData = new BigDecimal[0];
        } else {
            returnData = new BigDecimal[jsonArray.length()];
            for (int i = 0; i < jsonArray.length(); i++) {
                returnData[i] = new BigDecimal(jsonArray.optDouble(i, 0));
            }
        }

        return returnData;
    }

    /**
     * Helper to convert a JSONArray to an array of JSONObjects
     *
     * @param jsonArray
     * @return
     */
    static JSONObject[] getJSONObjectArrayFromJson(JSONArray jsonArray) {
        JSONObject[] returnData;
        if (isNullOrEmpty(jsonArray)) {
            returnData = new JSONObject[0];
        } else {
            returnData = new JSONObject[jsonArray.length()];
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    String jsonString = jsonArray.optString(i, "{}");
                    returnData[i] = new JSONObject(jsonString);
                } catch (JSONException jsEx) {
                    returnData[i] = new JSONObject();
                }
            }
        }

        return returnData;
    }

    /**
     * Helper to determine if the JSONArray is null, or has no entries.
     *
     * @param jsonArray
     * @return
     */
    static boolean isNullOrEmpty(JSONArray jsonArray) {
        return jsonArray == null || jsonArray.length() == 0;
    }

    /**
     * Helper to determine if the JSONObject is null, or has no keys.
     *
     * @param jsonObject
     * @return
     */
    static boolean isNullOrEmpty(JSONObject jsonObject) {
        return jsonObject == null || jsonObject.length() == 0;
    }

    /**
     * Helper to determine if the String is null, or is empty.
     *
     * @param string
     * @return
     */
    static boolean isNullOrEmpty(String string) {
        return string == null || string.isEmpty();
    }

    public static Date parseDate(String dateString) {
        Date date = null;
        try {
            // try with simple date format (in case of webview support)
            date = BrazeUtils.DATE_FORMAT.parse(dateString);
            if (date != null) return date;
        } catch (ParseException ignore) {
        }

        try {
            // try with ISO8601 date format
            date = BrazeUtils.ISO_8601_DATE_FORMAT.parse(dateString);
            if (date != null) return date;
        } catch (ParseException ignore) {
        }

        // try Braze date formats
        for (BrazeDateFormat dateFormat : BrazeDateFormat.values()) {
            try {
                // try with ISO8601 date format
                date = DateTimeUtils.parseDate(dateString, dateFormat);
            } catch (Exception ignore) {
                /* Method does not specify ParseException, but does throw during tests. */
            }

            if (date != null) break;
        }

        return date;
    }
}
