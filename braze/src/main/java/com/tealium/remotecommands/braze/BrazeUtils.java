package com.tealium.remotecommands.braze;

import android.util.Log;

import com.braze.enums.BrazeDateFormat;
import com.braze.enums.Month;
import com.braze.enums.Gender;
import com.braze.models.outgoing.BrazeProperties;
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
     * Helper to determine whether the element at {@code index} of a JSONArray is present and not a
     * JSON null. Used for optional per-product arrays (image_url, product_url) where an individual
     * element may be null for a product that lacks that field.
     *
     * @param array - the JSONArray to inspect
     * @param index - the element index to check
     * @return true when the element exists and is not null
     */
    static boolean keyHasValue(JSONArray array, int index) {
        return (array != null && !array.isNull(index));
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
     * Normalizes the parallel top-level product arrays of an ecommerce payload into a JSONArray of
     * product objects. Products are supplied on the wire as columnar arrays (product_id: [...],
     * product_unit_price: [...], product_qty: [...], ...) zipped by index -- unifying the shape with
     * the iOS remote command and the existing logPurchase path -- and this reshapes them into the
     * array-of-objects form the downstream builders ({@link #getEcommerceProductsFromJson},
     * {@link #getEcommerceProductsAsWireJson}) consume. product_id, product_name, variant_id,
     * product_unit_price and product_qty (fallback quantity) are required and length-matched;
     * image_url, product_url and product_metadata are optional per-index arrays. Returns an empty
     * array when the required arrays are missing or mismatched, so the caller skips the whole event.
     *
     * @param payload the full command payload holding the parallel product arrays
     * @return a JSONArray of product objects keyed by the BrazeConstants.Ecommerce input keys
     * @throws JSONException when a required product array is missing or the arrays' lengths don't
     *                       match -- so the caller skips the whole event (mirroring the previous
     *                       {@code getJSONArray} behavior and the iOS remote command)
     */
    static JSONArray normalizeProductArrays(JSONObject payload) throws JSONException {
        JSONArray result = new JSONArray();
        ProductArrays arrays = ProductArrays.from(payload);
        if (arrays == null) {
            throw new JSONException("Missing or mismatched required ecommerce product arrays");
        }

        for (int i = 0; i < arrays.count; i++) {
            JSONObject product = new JSONObject();
            try {
                product.put(BrazeConstants.Ecommerce.PRODUCT_ID, arrays.productIds.opt(i));
                product.put(BrazeConstants.Ecommerce.PRODUCT_NAME, arrays.productNames.opt(i));
                product.put(BrazeConstants.Ecommerce.VARIANT_ID, arrays.variantIds.opt(i));
                product.put(BrazeConstants.Ecommerce.PRICE, arrays.prices.opt(i));
                product.put(BrazeConstants.Ecommerce.QUANTITY, arrays.quantities.opt(i));
                if (arrays.imageUrls != null && keyHasValue(arrays.imageUrls, i)) {
                    product.put(BrazeConstants.Ecommerce.IMAGE_URL, arrays.imageUrls.optString(i));
                }
                if (arrays.productUrls != null && keyHasValue(arrays.productUrls, i)) {
                    product.put(BrazeConstants.Ecommerce.PRODUCT_URL, arrays.productUrls.optString(i));
                }
                if (arrays.metadatas != null && arrays.metadatas.optJSONObject(i) != null) {
                    product.put(BrazeConstants.Ecommerce.PRODUCT_PROPERTIES, arrays.metadatas.optJSONObject(i));
                }
            } catch (JSONException jex) {
                Log.w(BrazeConstants.TAG, "Skipping ecommerce product at index " + i, jex);
                continue;
            }
            result.put(product);
        }

        return result;
    }

    /**
     * Helper to build a list of Braze EcommerceProduct objects from a JSONArray of product objects
     * (as produced by {@link #normalizeProductArrays}). Each element is a JSONObject using the keys
     * in BrazeConstants.Ecommerce (product_id, product_name, variant_id, product_unit_price,
     * product_qty and the optional image_url, product_url and product_metadata). Any per-product
     * custom properties are extracted into a BrazeProperties object.
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
     * Rebuilds a JSONArray of product objects (as produced by {@link #normalizeProductArrays}) into
     * the wire schema expected by the untyped ecommerce.order_cancelled / ecommerce.order_refunded
     * custom events. Input product-level keys (product_unit_price, product_qty, product_metadata) are
     * mapped to the Braze wire names (price, quantity, metadata); optional fields are omitted when
     * absent. A product missing its required price is logged and skipped rather than corrupting the
     * rest of the event.
     *
     * @param products the JSONArray of product objects, using BrazeConstants.Ecommerce input keys
     * @return a new JSONArray of plain JSONObjects using the Braze wire schema
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
                wireProduct.put(BrazeConstants.WireOutputKeys.PRODUCT_ID, product.optString(BrazeConstants.Ecommerce.PRODUCT_ID));
                wireProduct.put(BrazeConstants.WireOutputKeys.PRODUCT_NAME, product.optString(BrazeConstants.Ecommerce.PRODUCT_NAME));
                wireProduct.put(BrazeConstants.WireOutputKeys.VARIANT_ID, product.optString(BrazeConstants.Ecommerce.VARIANT_ID));
                wireProduct.put(BrazeConstants.WireOutputKeys.PRICE, product.optDouble(BrazeConstants.Ecommerce.PRICE));
                wireProduct.put(BrazeConstants.WireOutputKeys.QUANTITY, product.optInt(BrazeConstants.Ecommerce.QUANTITY, 1));
                if (keyHasValue(product, BrazeConstants.Ecommerce.IMAGE_URL)) {
                    wireProduct.put(BrazeConstants.WireOutputKeys.IMAGE_URL, product.optString(BrazeConstants.Ecommerce.IMAGE_URL));
                }
                if (keyHasValue(product, BrazeConstants.Ecommerce.PRODUCT_URL)) {
                    wireProduct.put(BrazeConstants.WireOutputKeys.PRODUCT_URL, product.optString(BrazeConstants.Ecommerce.PRODUCT_URL));
                }
                if (keyHasValue(product, BrazeConstants.Ecommerce.PRODUCT_PROPERTIES)) {
                    // The wire schema for order_cancelled/order_refunded calls this key "metadata",
                    // whereas the input payload uses "product_metadata".
                    wireProduct.put(BrazeConstants.WireOutputKeys.METADATA, product.optJSONObject(BrazeConstants.Ecommerce.PRODUCT_PROPERTIES));
                }
            } catch (JSONException jex) {
                // A non-finite price (NaN/Infinity) makes JSONObject.put throw mid-build; skip the
                // partially-built product rather than appending it.
                Log.w(BrazeConstants.TAG, "Failed to build wire-schema ecommerce product JSON", jex);
                continue;
            }

            result.put(wireProduct);
        }

        return result;
    }

    /**
     * The parallel top-level product arrays shared by every ecommerce event that carries products
     * (cart/checkout/order). Required arrays are length-matched; a mismatch or a missing required
     * array yields null so the caller skips the whole event. Optional arrays are kept only when
     * their length matches the required count (a misaligned optional array can't be safely indexed).
     */
    private static final class ProductArrays {
        final JSONArray productIds;
        final JSONArray productNames;
        final JSONArray variantIds;
        final JSONArray quantities;
        final JSONArray prices;
        final JSONArray imageUrls;
        final JSONArray productUrls;
        final JSONArray metadatas;
        final int count;

        private ProductArrays(JSONArray productIds, JSONArray productNames, JSONArray variantIds,
                              JSONArray quantities, JSONArray prices, JSONArray imageUrls,
                              JSONArray productUrls, JSONArray metadatas, int count) {
            this.productIds = productIds;
            this.productNames = productNames;
            this.variantIds = variantIds;
            this.quantities = quantities;
            this.prices = prices;
            this.imageUrls = imageUrls;
            this.productUrls = productUrls;
            this.metadatas = metadatas;
            this.count = count;
        }

        static ProductArrays from(JSONObject payload) {
            JSONArray productIds = payload.optJSONArray(BrazeConstants.Ecommerce.PRODUCT_ID);
            JSONArray productNames = payload.optJSONArray(BrazeConstants.Ecommerce.PRODUCT_NAME);
            JSONArray variantIds = payload.optJSONArray(BrazeConstants.Ecommerce.VARIANT_ID);
            JSONArray quantities = payload.optJSONArray(BrazeConstants.Ecommerce.QUANTITY);
            if (quantities == null) {
                quantities = payload.optJSONArray(BrazeConstants.Ecommerce.QUANTITY_FALLBACK);
            }
            JSONArray prices = payload.optJSONArray(BrazeConstants.Ecommerce.PRICE);

            if (productIds == null || productNames == null || variantIds == null
                    || quantities == null || prices == null) {
                return null;
            }
            int count = productIds.length();
            if (productNames.length() != count || variantIds.length() != count
                    || quantities.length() != count || prices.length() != count) {
                Log.w(BrazeConstants.TAG, "Skipping ecommerce event: mismatched product array lengths");
                return null;
            }

            return new ProductArrays(productIds, productNames, variantIds, quantities, prices,
                    optionalMatchedArray(payload, BrazeConstants.Ecommerce.IMAGE_URL, count),
                    optionalMatchedArray(payload, BrazeConstants.Ecommerce.PRODUCT_URL, count),
                    optionalMatchedArray(payload, BrazeConstants.Ecommerce.PRODUCT_PROPERTIES, count),
                    count);
        }

        /**
         * Returns the optional array at {@code key} only when present and length-matched to
         * {@code count}; otherwise null, so a misaligned optional array is dropped whole rather than
         * indexed out of step with the required arrays.
         */
        private static JSONArray optionalMatchedArray(JSONObject payload, String key, int count) {
            JSONArray array = payload.optJSONArray(key);
            return (array != null && array.length() == count) ? array : null;
        }
    }

    // Scalar-string readers for logProductViewed, which is scalar-only (it carries no products
    // array, unlike cart/checkout/order). These reject a JSONArray value explicitly because
    // Android's org.json coerces a JSONArray to its literal string form (e.g. "[\"sku\"]") via
    // getString()/optString() rather than throwing -- unlike getDouble()/getJSONArray(), and
    // unlike the reference org.json used in off-device tests. Without this guard an array value
    // would be logged as a corrupted scalar instead of skipping the event, and would diverge from
    // the iOS remote command (whose `as? String` cast cleanly rejects an array).

    /**
     * Reads a required scalar String. Rejects a missing key, a JSON null, an array, or any
     * non-String value (matching the iOS `as? String` strictness).
     *
     * @param json the payload
     * @param key  the key to read
     * @return the scalar String value
     * @throws JSONException if the value is absent or is not a String
     */
    static String requireScalarString(JSONObject json, String key) throws JSONException {
        Object raw = json.opt(key);
        if (raw instanceof String) {
            return (String) raw;
        }
        throw new JSONException("Expected a scalar String for '" + key + "'");
    }

    /**
     * Reads an optional scalar String. Returns null when the key is absent, is a JSON null, is an
     * array, or is any non-String value (matching the iOS `as? String` cast).
     *
     * @param json the payload
     * @param key  the key to read
     * @return the scalar String value, or null
     */
    static String optionalScalarString(JSONObject json, String key) {
        Object raw = json.opt(key);
        return raw instanceof String ? (String) raw : null;
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
