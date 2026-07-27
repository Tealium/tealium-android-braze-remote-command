package com.tealium.remotecommands.braze;

import android.util.Log;

import androidx.annotation.Nullable;

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
     * Builds a list of Braze EcommerceProduct objects from the nested Ecommerce.PRODUCTS object,
     * whose values are parallel arrays keyed by PRODUCT_ID/PRODUCT_NAME/VARIANT_ID/PRICE/QUANTITY
     * (required, equal length) and optional per-index IMAGE_URL/PRODUCT_URL/METADATA, zipped by
     * index -- mirroring the tealium-android-firebase-remote-command items_params convention.
     * A product missing its required price is skipped rather than fabricating a $0 line item.
     *
     * @param products                the nested Ecommerce.PRODUCTS object (must not be null -- callers read it via
     *                                 the required {@code payload.getJSONObject(Ecommerce.PRODUCTS)})
     * @param strictPropertiesEnabled whether per-product metadata values should be passed on unchanged
     * @return a list of EcommerceProduct
     * @throws JSONException when a required nested array (product_id/product_name/variant_id/price/quantity)
     *                        is missing or their lengths don't match, so the caller skips the whole event.
     *                        Individual products with an invalid price/quantity are skipped per-index;
     *                        if that leaves no valid products, this throws too so the whole event is
     *                        skipped client-side (parity with iOS, which throws emptyProductsArray)
     *                        rather than dispatching an empty products list.
     */
    static List<EcommerceProduct> getProductsFromNestedArrays(@Nullable JSONObject products, boolean strictPropertiesEnabled) throws JSONException {
        if (products == null) {
            throw new JSONException("Missing required ecommerce products object");
        }

        JSONArray productIds = products.optJSONArray(BrazeConstants.Ecommerce.PRODUCT_ID);
        JSONArray productNames = products.optJSONArray(BrazeConstants.Ecommerce.PRODUCT_NAME);
        JSONArray variantIds = products.optJSONArray(BrazeConstants.Ecommerce.VARIANT_ID);
        JSONArray prices = products.optJSONArray(BrazeConstants.Ecommerce.PRICE);
        JSONArray quantities = products.optJSONArray(BrazeConstants.Ecommerce.QUANTITY);
        if (productIds == null || productNames == null || variantIds == null || prices == null || quantities == null) {
            throw new JSONException("Missing required ecommerce product arrays");
        }

        int count = productIds.length();
        if (productNames.length() != count || variantIds.length() != count
                || prices.length() != count || quantities.length() != count) {
            throw new JSONException("Mismatched ecommerce product array lengths");
        }

        List<EcommerceProduct> result = new ArrayList<>();

        JSONArray imageUrls = optionalMatchedArray(products, BrazeConstants.Ecommerce.IMAGE_URL, count);
        JSONArray productUrls = optionalMatchedArray(products, BrazeConstants.Ecommerce.PRODUCT_URL, count);
        JSONArray metadatas = optionalMatchedArray(products, BrazeConstants.Ecommerce.METADATA, count);

        for (int i = 0; i < count; i++) {
            // price and quantity are required per product. A missing/non-numeric value, or a value
            // the Braze EcommerceProduct constructor rejects (negative price, blank/>255-char string,
            // negative quantity), skips only this line item -- one bad product must not drop the whole
            // event, and quantity is not silently defaulted (matches the iOS remote command's strict
            // per-item behaviour). getDouble/getLong throw JSONException on missing/non-numeric;
            // the constructor throws IllegalArgumentException on invalid values.
            try {
                double price = prices.getDouble(i);
                long quantity = quantities.getLong(i);
                result.add(new EcommerceProduct(
                        productIds.optString(i),
                        productNames.optString(i),
                        variantIds.optString(i),
                        price,
                        quantity,
                        imageUrls != null && keyHasValue(imageUrls, i) ? imageUrls.optString(i) : null,
                        productUrls != null && keyHasValue(productUrls, i) ? productUrls.optString(i) : null,
                        extractCustomProperties(metadatas != null ? metadatas.optJSONObject(i) : null, strictPropertiesEnabled)
                ));
            } catch (JSONException | IllegalArgumentException ex) {
                Log.w(BrazeConstants.TAG, "Skipping invalid ecommerce product at index " + i, ex);
            }
        }

        if (result.isEmpty()) {
            // Every product was invalid; throw so the caller skips the whole event client-side
            // rather than dispatching an event with an empty products list (parity with iOS, which
            // throws emptyProductsArray).
            throw new JSONException("No valid ecommerce products");
        }

        return result;
    }

    /**
     * Builds a list of discount maps from the nested Ecommerce.DISCOUNTS object, whose values are
     * parallel arrays keyed by DISCOUNT_CODE/DISCOUNT_AMOUNT/DISCOUNT_TYPE, zipped by index -- same
     * nested-parallel-arrays convention as {@link #getProductsFromNestedArrays}. All three arrays
     * are optional; absent or null entries are simply skipped for that discount rather than forcing
     * all three fields.
     *
     * @param discounts the nested Ecommerce.DISCOUNTS object (may be null)
     * @return a list of Map<String, Object>, empty if the nested object is absent or empty
     */
    static List<Map<String, Object>> getDiscountsFromNestedArrays(@Nullable JSONObject discounts) {
        List<Map<String, Object>> result = new ArrayList<>();
        if (discounts == null) {
            return result;
        }

        JSONArray codes = discounts.optJSONArray(BrazeConstants.Ecommerce.DISCOUNT_CODE);
        JSONArray amounts = discounts.optJSONArray(BrazeConstants.Ecommerce.DISCOUNT_AMOUNT);
        JSONArray types = discounts.optJSONArray(BrazeConstants.Ecommerce.DISCOUNT_TYPE);

        int count = Math.max(codes != null ? codes.length() : 0,
                Math.max(amounts != null ? amounts.length() : 0, types != null ? types.length() : 0));

        for (int i = 0; i < count; i++) {
            Map<String, Object> entry = new HashMap<>();
            if (codes != null && keyHasValue(codes, i)) {
                entry.put(BrazeConstants.Ecommerce.DISCOUNT_CODE, codes.optString(i));
            }
            if (amounts != null && keyHasValue(amounts, i)) {
                // A non-numeric amount coerces to NaN via optDouble; only carry the amount when it is
                // a real number so we never box NaN into the discount map.
                double amt = amounts.optDouble(i, Double.NaN);
                if (!Double.isNaN(amt)) {
                    entry.put(BrazeConstants.Ecommerce.DISCOUNT_AMOUNT, amt);
                }
            }
            if (types != null && keyHasValue(types, i)) {
                entry.put(BrazeConstants.Ecommerce.DISCOUNT_TYPE, types.optString(i));
            }
            result.add(entry);
        }

        return result;
    }

    /**
     * Returns the optional array at {@code key} within {@code json} only when present and
     * length-matched to {@code count}; otherwise null, so a misaligned optional array is dropped
     * whole rather than indexed out of step with the required arrays.
     */
    private static JSONArray optionalMatchedArray(JSONObject json, String key, int count) {
        JSONArray array = json.optJSONArray(key);
        return (array != null && array.length() == count) ? array : null;
    }

    /**
     * Builds a JSONArray of plain product objects from the nested Ecommerce.PRODUCTS object, for
     * use as the "products" value in the ecommerce.order_cancelled / ecommerce.order_refunded
     * logCustomEvent wire payload. Same nested-parallel-arrays convention as
     * {@link #getProductsFromNestedArrays}; the wire schema uses the same key names (price,
     * quantity, metadata) as the input, so no key remapping is needed.
     *
     * @param products the nested Ecommerce.PRODUCTS object (must not be null -- callers read it via
     *                 the required {@code payload.getJSONObject(Ecommerce.PRODUCTS)})
     * @return a JSONArray of plain JSONObjects
     * @throws JSONException when a required nested array (product_id/product_name/variant_id/price/quantity)
     *                        is missing or their lengths don't match, so the caller skips the whole event.
     *                        Individual products with an invalid price/quantity are skipped per-index;
     *                        if that leaves no valid products, this throws too so the whole event is
     *                        skipped client-side (parity with iOS, which throws emptyProductsArray)
     *                        rather than dispatching an empty products list.
     */
    static JSONArray getProductsAsWireJson(@Nullable JSONObject products) throws JSONException {
        if (products == null) {
            throw new JSONException("Missing required ecommerce products object");
        }

        JSONArray productIds = products.optJSONArray(BrazeConstants.Ecommerce.PRODUCT_ID);
        JSONArray productNames = products.optJSONArray(BrazeConstants.Ecommerce.PRODUCT_NAME);
        JSONArray variantIds = products.optJSONArray(BrazeConstants.Ecommerce.VARIANT_ID);
        JSONArray prices = products.optJSONArray(BrazeConstants.Ecommerce.PRICE);
        JSONArray quantities = products.optJSONArray(BrazeConstants.Ecommerce.QUANTITY);
        if (productIds == null || productNames == null || variantIds == null || prices == null || quantities == null) {
            throw new JSONException("Missing required ecommerce product arrays");
        }

        int count = productIds.length();
        if (productNames.length() != count || variantIds.length() != count
                || prices.length() != count || quantities.length() != count) {
            throw new JSONException("Mismatched ecommerce product array lengths");
        }

        JSONArray result = new JSONArray();

        JSONArray imageUrls = optionalMatchedArray(products, BrazeConstants.Ecommerce.IMAGE_URL, count);
        JSONArray productUrls = optionalMatchedArray(products, BrazeConstants.Ecommerce.PRODUCT_URL, count);
        JSONArray metadatas = optionalMatchedArray(products, BrazeConstants.Ecommerce.METADATA, count);

        for (int i = 0; i < count; i++) {
            JSONObject product = new JSONObject();
            try {
                // price and quantity are required per product; getDouble/getInt throw JSONException on
                // a missing/non-numeric value and skip only this line item. Quantity is not silently
                // defaulted (matches the typed getProductsFromNestedArrays path and iOS).
                product.put(BrazeConstants.Ecommerce.PRODUCT_ID, productIds.opt(i));
                product.put(BrazeConstants.Ecommerce.PRODUCT_NAME, productNames.opt(i));
                product.put(BrazeConstants.Ecommerce.VARIANT_ID, variantIds.opt(i));
                product.put(BrazeConstants.Ecommerce.PRICE, prices.getDouble(i));
                product.put(BrazeConstants.Ecommerce.QUANTITY, quantities.getInt(i));
                if (imageUrls != null && keyHasValue(imageUrls, i)) {
                    product.put(BrazeConstants.Ecommerce.IMAGE_URL, imageUrls.optString(i));
                }
                if (productUrls != null && keyHasValue(productUrls, i)) {
                    product.put(BrazeConstants.Ecommerce.PRODUCT_URL, productUrls.optString(i));
                }
                if (metadatas != null && metadatas.optJSONObject(i) != null) {
                    product.put(BrazeConstants.Ecommerce.METADATA, metadatas.optJSONObject(i));
                }
            } catch (JSONException jex) {
                // Missing/non-numeric required price or quantity, or a non-finite price (NaN/Infinity)
                // that makes JSONObject.put throw mid-build; skip this product rather than appending a
                // partially-built or invalid line item.
                Log.w(BrazeConstants.TAG, "Skipping invalid wire-schema ecommerce product at index " + i, jex);
                continue;
            }

            result.put(product);
        }

        if (result.length() == 0) {
            // Every product was invalid; throw so the caller skips the whole event client-side
            // rather than dispatching an event with an empty products list (parity with iOS, which
            // throws emptyProductsArray).
            throw new JSONException("No valid ecommerce products");
        }

        return result;
    }

    /**
     * Builds a JSONArray of plain discount objects from the nested Ecommerce.DISCOUNTS object, for
     * use as the "discounts" value in the ecommerce.order_cancelled / ecommerce.order_refunded
     * logCustomEvent wire payload. Same nested-parallel-arrays convention as
     * {@link #getDiscountsFromNestedArrays}.
     *
     * @param discounts the nested Ecommerce.DISCOUNTS object (may be null)
     * @return a JSONArray of plain JSONObjects, empty if the nested object is absent or empty
     */
    static JSONArray getDiscountsAsWireJson(@Nullable JSONObject discounts) {
        JSONArray result = new JSONArray();
        if (discounts == null) {
            return result;
        }

        JSONArray codes = discounts.optJSONArray(BrazeConstants.Ecommerce.DISCOUNT_CODE);
        JSONArray amounts = discounts.optJSONArray(BrazeConstants.Ecommerce.DISCOUNT_AMOUNT);
        JSONArray types = discounts.optJSONArray(BrazeConstants.Ecommerce.DISCOUNT_TYPE);

        int count = Math.max(codes != null ? codes.length() : 0,
                Math.max(amounts != null ? amounts.length() : 0, types != null ? types.length() : 0));

        for (int i = 0; i < count; i++) {
            JSONObject discount = new JSONObject();
            try {
                if (codes != null && keyHasValue(codes, i)) {
                    discount.put(BrazeConstants.Ecommerce.DISCOUNT_CODE, codes.optString(i));
                }
                if (amounts != null && keyHasValue(amounts, i)) {
                    // Only carry a real numeric amount; a non-numeric value coerces to NaN, which
                    // JSONObject.put would reject -- skip it explicitly to parallel the typed path.
                    double amt = amounts.optDouble(i, Double.NaN);
                    if (!Double.isNaN(amt)) {
                        discount.put(BrazeConstants.Ecommerce.DISCOUNT_AMOUNT, amt);
                    }
                }
                if (types != null && keyHasValue(types, i)) {
                    discount.put(BrazeConstants.Ecommerce.DISCOUNT_TYPE, types.optString(i));
                }
            } catch (JSONException jex) {
                Log.w(BrazeConstants.TAG, "Failed to build wire-schema ecommerce discount JSON", jex);
                continue;
            }
            result.put(discount);
        }

        return result;
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
     * Reads a required scalar currency and normalizes it to uppercase. Currency is required for all
     * six recommended ecommerce events (the Braze SDK base EcommerceEvent constructor rejects a null
     * currency, and Braze validates the value against ISO-4217 canonical uppercase), so this reuses
     * {@link #requireScalarString} -- throwing when the key is absent, a JSON null, an array, or any
     * non-String value -- then uppercases so a common lowercase input like "usd" is accepted rather
     * than dropped at event construction. Matches the iOS remote command's requireCurrency strictness.
     *
     * @param json the payload
     * @param key  the key to read
     * @return the uppercased currency
     * @throws JSONException if the value is absent or is not a scalar String
     */
    static String requireCurrency(JSONObject json, String key) throws JSONException {
        return requireScalarString(json, key).toUpperCase(Locale.ROOT);
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
