package com.tealium.remotecommands.braze;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.braze.enums.Gender;
import com.braze.enums.Month;
import com.braze.models.outgoing.BrazeProperties;
import com.braze.models.recommended.ecommerce.EcommerceProduct;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

@RunWith(RobolectricTestRunner.class)
public class BrazeUtilityMethodTests {

    @Test
    public void genderStringToEnumTest() {

        // Male
        assertEquals(BrazeUtils.getGenderEnumFromString("Male"), Gender.MALE);
        assertEquals(BrazeUtils.getGenderEnumFromString("m"), Gender.MALE);

        // Female
        assertEquals(BrazeUtils.getGenderEnumFromString("female"), Gender.FEMALE);
        assertEquals(BrazeUtils.getGenderEnumFromString("f"), Gender.FEMALE);

        // Other
        assertEquals(BrazeUtils.getGenderEnumFromString("otHer"), Gender.OTHER);
        assertEquals(BrazeUtils.getGenderEnumFromString("o"), Gender.OTHER);

        // N/A
        assertEquals(BrazeUtils.getGenderEnumFromString("not_applicable"), Gender.NOT_APPLICABLE);
        assertEquals(BrazeUtils.getGenderEnumFromString("na"), Gender.NOT_APPLICABLE);

        // Prefer not to say
        assertEquals(BrazeUtils.getGenderEnumFromString("prefer_NOT_to_say"), Gender.PREFER_NOT_TO_SAY);
        assertEquals(BrazeUtils.getGenderEnumFromString("no"), Gender.PREFER_NOT_TO_SAY);

        // Unknown
        assertEquals(BrazeUtils.getGenderEnumFromString("unknown"), Gender.UNKNOWN);

        // default
        assertNull(BrazeUtils.getGenderEnumFromString("UNEXPECTED VALUE"));
    }

    @Test
    public void monthEnumFromIntTests() {
        assertEquals(Month.JANUARY, BrazeUtils.getMonthEnumFromInt(0));
        assertEquals(Month.FEBRUARY, BrazeUtils.getMonthEnumFromInt(1));
        assertEquals(Month.MARCH, BrazeUtils.getMonthEnumFromInt(2));
        assertEquals(Month.APRIL, BrazeUtils.getMonthEnumFromInt(3));
        assertEquals(Month.MAY, BrazeUtils.getMonthEnumFromInt(4));
        assertEquals(Month.JUNE, BrazeUtils.getMonthEnumFromInt(5));
        assertEquals(Month.JULY, BrazeUtils.getMonthEnumFromInt(6));
        assertEquals(Month.AUGUST, BrazeUtils.getMonthEnumFromInt(7));
        assertEquals(Month.SEPTEMBER, BrazeUtils.getMonthEnumFromInt(8));
        assertEquals(Month.OCTOBER, BrazeUtils.getMonthEnumFromInt(9));
        assertEquals(Month.NOVEMBER, BrazeUtils.getMonthEnumFromInt(10));
        assertEquals(Month.DECEMBER, BrazeUtils.getMonthEnumFromInt(11));

        assertNull(BrazeUtils.getMonthEnumFromInt(-1));
        assertNull(BrazeUtils.getMonthEnumFromInt(12));
    }

    /**
     * Builds the nested Ecommerce.PRODUCTS object -- parallel arrays zipped by index -- from a
     * list of flat product field maps. Mirrors the shape BrazeRemoteCommand reads from a real
     * payload (product_id/product_name/variant_id/price/quantity required; image_url/product_url/
     * metadata optional per-index).
     */
    private JSONObject productsObject(JSONObject... products) throws JSONException {
        JSONArray ids = new JSONArray();
        JSONArray names = new JSONArray();
        JSONArray variants = new JSONArray();
        JSONArray prices = new JSONArray();
        JSONArray quantities = new JSONArray();
        JSONArray imageUrls = new JSONArray();
        JSONArray productUrls = new JSONArray();
        JSONArray metadatas = new JSONArray();
        for (JSONObject product : products) {
            ids.put(product.opt(BrazeConstants.Ecommerce.PRODUCT_ID));
            names.put(product.opt(BrazeConstants.Ecommerce.PRODUCT_NAME));
            variants.put(product.opt(BrazeConstants.Ecommerce.VARIANT_ID));
            prices.put(product.opt(BrazeConstants.Ecommerce.PRICE));
            quantities.put(product.opt(BrazeConstants.Ecommerce.QUANTITY));
            imageUrls.put(product.has(BrazeConstants.Ecommerce.IMAGE_URL) ? product.opt(BrazeConstants.Ecommerce.IMAGE_URL) : JSONObject.NULL);
            productUrls.put(product.has(BrazeConstants.Ecommerce.PRODUCT_URL) ? product.opt(BrazeConstants.Ecommerce.PRODUCT_URL) : JSONObject.NULL);
            metadatas.put(product.has(BrazeConstants.Ecommerce.METADATA) ? product.opt(BrazeConstants.Ecommerce.METADATA) : JSONObject.NULL);
        }
        JSONObject result = new JSONObject();
        result.put(BrazeConstants.Ecommerce.PRODUCT_ID, ids);
        result.put(BrazeConstants.Ecommerce.PRODUCT_NAME, names);
        result.put(BrazeConstants.Ecommerce.VARIANT_ID, variants);
        result.put(BrazeConstants.Ecommerce.PRICE, prices);
        result.put(BrazeConstants.Ecommerce.QUANTITY, quantities);
        result.put(BrazeConstants.Ecommerce.IMAGE_URL, imageUrls);
        result.put(BrazeConstants.Ecommerce.PRODUCT_URL, productUrls);
        result.put(BrazeConstants.Ecommerce.METADATA, metadatas);
        return result;
    }

    @Test
    public void productsFromNestedArraysTest() throws JSONException {
        JSONObject product = new JSONObject();
        product.put(BrazeConstants.Ecommerce.PRODUCT_ID, "sku123");
        product.put(BrazeConstants.Ecommerce.PRODUCT_NAME, "Widget");
        product.put(BrazeConstants.Ecommerce.VARIANT_ID, "widget_blue");
        product.put(BrazeConstants.Ecommerce.PRICE, 49.99);
        product.put(BrazeConstants.Ecommerce.QUANTITY, 2);
        product.put(BrazeConstants.Ecommerce.IMAGE_URL, "https://example.com/img.jpg");
        product.put(BrazeConstants.Ecommerce.PRODUCT_URL, "https://example.com/p");
        JSONObject props = new JSONObject();
        props.put("string-prop", "value");
        product.put(BrazeConstants.Ecommerce.METADATA, props);

        List<EcommerceProduct> result = BrazeUtils.getProductsFromNestedArrays(productsObject(product), false);

        assertEquals(1, result.size());
        EcommerceProduct parsed = result.get(0);
        assertEquals("sku123", parsed.getProductId());
        assertEquals("Widget", parsed.getProductName());
        assertEquals("widget_blue", parsed.getVariantId());
        assertEquals(49.99, parsed.getPrice(), 0.0001);
        assertEquals(2L, parsed.getQuantity());
        assertEquals("https://example.com/img.jpg", parsed.getImageUrl());
        assertEquals("https://example.com/p", parsed.getProductUrl());
        assertEquals("value", parsed.getMetadata().get("string-prop"));
    }

    @Test
    public void productsFromNestedArraysTest_EmptyForNullOrMissingArrays() {
        assertTrue(BrazeUtils.getProductsFromNestedArrays(null, false).isEmpty());
        assertTrue(BrazeUtils.getProductsFromNestedArrays(new JSONObject(), false).isEmpty());
    }

    @Test
    public void productsFromNestedArraysTest_SkipsProductMissingRequiredPrice() throws JSONException {
        JSONObject missingPrice = new JSONObject();
        missingPrice.put(BrazeConstants.Ecommerce.PRODUCT_ID, "sku-missing-price");
        missingPrice.put(BrazeConstants.Ecommerce.PRODUCT_NAME, "No Price");
        missingPrice.put(BrazeConstants.Ecommerce.VARIANT_ID, "variant");
        // price intentionally omitted; required per the wire schema, and must not silently
        // default to $0.
        missingPrice.put(BrazeConstants.Ecommerce.PRICE, "not-a-number");
        missingPrice.put(BrazeConstants.Ecommerce.QUANTITY, 1);

        JSONObject validProduct = new JSONObject();
        validProduct.put(BrazeConstants.Ecommerce.PRODUCT_ID, "sku123");
        validProduct.put(BrazeConstants.Ecommerce.PRODUCT_NAME, "Widget");
        validProduct.put(BrazeConstants.Ecommerce.VARIANT_ID, "widget_blue");
        validProduct.put(BrazeConstants.Ecommerce.PRICE, 49.99);
        validProduct.put(BrazeConstants.Ecommerce.QUANTITY, 1);

        List<EcommerceProduct> result = BrazeUtils.getProductsFromNestedArrays(productsObject(missingPrice, validProduct), false);

        assertEquals(1, result.size());
        assertEquals("sku123", result.get(0).getProductId());
    }

    /**
     * Builds the nested Ecommerce.DISCOUNTS object -- parallel arrays zipped by index -- from
     * separate code/amount/type arrays, so tests can exercise per-array-absent behavior.
     */
    private JSONObject discountsObject(JSONArray codes, JSONArray amounts, JSONArray types) throws JSONException {
        JSONObject result = new JSONObject();
        if (codes != null) result.put(BrazeConstants.Ecommerce.DISCOUNT_CODE, codes);
        if (amounts != null) result.put(BrazeConstants.Ecommerce.DISCOUNT_AMOUNT, amounts);
        if (types != null) result.put(BrazeConstants.Ecommerce.DISCOUNT_TYPE, types);
        return result;
    }

    @Test
    public void discountsFromNestedArraysTest() throws JSONException {
        JSONObject discounts = discountsObject(
                new JSONArray(new String[]{"SUMMER10"}),
                new JSONArray(new double[]{5.0}),
                new JSONArray(new String[]{"percentage"}));

        List<Map<String, Object>> result = BrazeUtils.getDiscountsFromNestedArrays(discounts);

        assertEquals(1, result.size());
        assertEquals("SUMMER10", result.get(0).get("code"));
        assertEquals(5.0, (Double) result.get(0).get("amount"), 0.0001);
        assertEquals("percentage", result.get(0).get("type"));
    }

    @Test
    public void discountsFromNestedArraysTest_SkipsAbsentArraysPerEntry() throws JSONException {
        JSONObject discounts = discountsObject(new JSONArray(new String[]{"SUMMER10"}), null, null);

        List<Map<String, Object>> result = BrazeUtils.getDiscountsFromNestedArrays(discounts);

        assertEquals(1, result.size());
        assertEquals("SUMMER10", result.get(0).get("code"));
        assertFalse(result.get(0).containsKey("amount"));
        assertFalse(result.get(0).containsKey("type"));
    }

    @Test
    public void discountsFromNestedArraysTest_EmptyForNullOrEmpty() {
        assertTrue(BrazeUtils.getDiscountsFromNestedArrays(null).isEmpty());
        assertTrue(BrazeUtils.getDiscountsFromNestedArrays(new JSONObject()).isEmpty());
    }

    @Test
    public void productsAsWireJsonTest_RenamesMetadataOnEachProduct() throws JSONException {
        JSONObject product = new JSONObject();
        product.put(BrazeConstants.Ecommerce.PRODUCT_ID, "sku123");
        product.put(BrazeConstants.Ecommerce.PRODUCT_NAME, "Widget");
        product.put(BrazeConstants.Ecommerce.VARIANT_ID, "widget_blue");
        product.put(BrazeConstants.Ecommerce.PRICE, 49.99);
        product.put(BrazeConstants.Ecommerce.QUANTITY, 2);
        product.put(BrazeConstants.Ecommerce.IMAGE_URL, "https://example.com/img.jpg");
        product.put(BrazeConstants.Ecommerce.PRODUCT_URL, "https://example.com/p");
        JSONObject props = new JSONObject();
        props.put("rewards_member", true);
        product.put(BrazeConstants.Ecommerce.METADATA, props);

        JSONArray result = BrazeUtils.getProductsAsWireJson(productsObject(product));

        assertEquals(1, result.length());
        JSONObject wireProduct = result.getJSONObject(0);
        assertEquals("sku123", wireProduct.getString(BrazeConstants.Ecommerce.PRODUCT_ID));
        assertEquals("Widget", wireProduct.getString(BrazeConstants.Ecommerce.PRODUCT_NAME));
        assertEquals("widget_blue", wireProduct.getString(BrazeConstants.Ecommerce.VARIANT_ID));
        assertEquals(49.99, wireProduct.getDouble(BrazeConstants.Ecommerce.PRICE), 0.0001);
        assertEquals(2, wireProduct.getInt(BrazeConstants.Ecommerce.QUANTITY));
        assertEquals("https://example.com/img.jpg", wireProduct.getString(BrazeConstants.Ecommerce.IMAGE_URL));
        assertEquals("https://example.com/p", wireProduct.getString(BrazeConstants.Ecommerce.PRODUCT_URL));
        assertTrue(wireProduct.has(BrazeConstants.Ecommerce.METADATA));
        assertEquals(true, wireProduct.getJSONObject(BrazeConstants.Ecommerce.METADATA).getBoolean("rewards_member"));
    }

    @Test
    public void productsAsWireJsonTest_OmitsMetadataWhenAbsent() throws JSONException {
        JSONObject product = new JSONObject();
        product.put(BrazeConstants.Ecommerce.PRODUCT_ID, "sku123");
        product.put(BrazeConstants.Ecommerce.PRODUCT_NAME, "Widget");
        product.put(BrazeConstants.Ecommerce.VARIANT_ID, "widget_blue");
        product.put(BrazeConstants.Ecommerce.PRICE, 49.99);
        product.put(BrazeConstants.Ecommerce.QUANTITY, 1);

        JSONArray result = BrazeUtils.getProductsAsWireJson(productsObject(product));

        assertEquals(1, result.length());
        assertFalse(result.getJSONObject(0).has("metadata"));
    }

    @Test
    public void productsAsWireJsonTest_EmptyForNullOrMissingArrays() {
        assertEquals(0, BrazeUtils.getProductsAsWireJson(null).length());
        assertEquals(0, BrazeUtils.getProductsAsWireJson(new JSONObject()).length());
    }

    @Test
    public void productsAsWireJsonTest_SkipsProductMissingRequiredPrice() throws JSONException {
        JSONObject missingPrice = new JSONObject();
        missingPrice.put(BrazeConstants.Ecommerce.PRODUCT_ID, "sku-missing-price");
        missingPrice.put(BrazeConstants.Ecommerce.PRODUCT_NAME, "No Price");
        missingPrice.put(BrazeConstants.Ecommerce.VARIANT_ID, "variant");
        missingPrice.put(BrazeConstants.Ecommerce.PRICE, "not-a-number");
        missingPrice.put(BrazeConstants.Ecommerce.QUANTITY, 1);

        JSONObject validProduct = new JSONObject();
        validProduct.put(BrazeConstants.Ecommerce.PRODUCT_ID, "sku123");
        validProduct.put(BrazeConstants.Ecommerce.PRODUCT_NAME, "Widget");
        validProduct.put(BrazeConstants.Ecommerce.VARIANT_ID, "widget_blue");
        validProduct.put(BrazeConstants.Ecommerce.PRICE, 49.99);
        validProduct.put(BrazeConstants.Ecommerce.QUANTITY, 1);

        JSONArray result = BrazeUtils.getProductsAsWireJson(productsObject(missingPrice, validProduct));

        assertEquals(1, result.length());
        assertEquals("sku123", result.getJSONObject(0).getString(BrazeConstants.Ecommerce.PRODUCT_ID));
    }

    @Test
    public void parseDateTest_SimpleDateFormat() {
        TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
        Date date = BrazeUtils.parseDate("2000-01-01T01:01:01Z");

        assertEquals(1, date.getDate());
        assertEquals(0, date.getMonth());
        assertEquals(2000 - 1900, date.getYear());
    }

    @Test
    public void parseDateTest_BrazeShort() {
        TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
        Date date = BrazeUtils.parseDate("2000-01-01");

        assertEquals(1, date.getDate());
        assertEquals(0, date.getMonth());
        assertEquals(2000 - 1900, date.getYear());
    }

    @Test
    public void parseDateTest_BrazeLong() {
        TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
        Date date = BrazeUtils.parseDate("2000-01-01 01:01:01");

        assertEquals(1, date.getDate());
        assertEquals(0, date.getMonth());
        assertEquals(2000 - 1900, date.getYear());
    }

    @Test
    public void addCustomPropertyTests() {
        Object stringValue = "test";
        Object integerValue = 10;
        Object doubleValue = 10.10;
        Object booleanValue = true;
        Object integerStringValue = "10";
        Object doubleStringValue = "10.10";
        Object booleanStringValue = "true";
        // Object dateValue = ""; //TODO: get an example of the data in simple date format.

        BrazeProperties props = new BrazeProperties();
        props = BrazeUtils.addCustomProperty("stringValue", stringValue, props);
        props = BrazeUtils.addCustomProperty("integerValue", integerValue, props);
        props = BrazeUtils.addCustomProperty("doubleValue", doubleValue, props);
        props = BrazeUtils.addCustomProperty("booleanValue", booleanValue, props);
        props = BrazeUtils.addCustomProperty("integerStringValue", integerStringValue, props);
        props = BrazeUtils.addCustomProperty("doubleStringValue", doubleStringValue, props);
        props = BrazeUtils.addCustomProperty("booleanStringValue", booleanStringValue, props);

        JSONObject brazePropsJson = props.forJsonPut();
        try {
            assertEquals(stringValue, brazePropsJson.getString("stringValue"));
            assertTrue(brazePropsJson.get("stringValue") instanceof String);
            assertEquals(integerValue, brazePropsJson.getInt("integerValue"));
            assertTrue(brazePropsJson.get("integerValue") instanceof Integer);
            assertEquals(doubleValue, brazePropsJson.getDouble("doubleValue"));
            assertTrue(brazePropsJson.get("doubleValue") instanceof Double);
            assertEquals(booleanValue, brazePropsJson.getBoolean("booleanValue"));
            assertTrue(brazePropsJson.get("booleanValue") instanceof Boolean);

            /*
             * At the time of writing, the Android SDK will stringify values in a HashMap such that
             * the native type is lost. The method being tested here will attempt to recover that.
             * As a result the expected types should be integer/double/booolean despite the value
             * that was put in, was actually a string.
             * */
            assertEquals(integerValue, brazePropsJson.getInt("integerStringValue"));
            assertTrue(brazePropsJson.get("integerStringValue") instanceof Integer);
            assertEquals(doubleValue, brazePropsJson.getDouble("doubleStringValue"));
            assertTrue(brazePropsJson.get("doubleStringValue") instanceof Double);
            assertEquals(booleanValue, brazePropsJson.getBoolean("booleanStringValue"));
            assertTrue(brazePropsJson.get("booleanStringValue") instanceof Boolean);

        } catch (JSONException jex) {
            fail();
        }

        BrazeProperties props2 = BrazeUtils.addCustomProperty("stringValue", stringValue);
        JSONObject brazePropsJson2 = props2.forJsonPut();
        try {
            // Shorthand method should generate a new BrazeProperties if one isn't supplied
            assertNotNull(props2);

            assertEquals(stringValue, brazePropsJson2.getString("stringValue"));
            assertTrue(brazePropsJson2.get("stringValue") instanceof String);

        } catch (JSONException jex) {
            fail();
        }
    }

    @Test
    public void jsonKeyHasValueTests() {
        JSONObject json = new JSONObject();
        try {
            json.put("string_key", "string");
            json.put("null_key", null);

            assertTrue(BrazeUtils.keyHasValue(json, "string_key"));
            assertFalse(BrazeUtils.keyHasValue(json, "null_key"));
            assertFalse(BrazeUtils.keyHasValue(json, "non_existent_key"));

        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void isNullOrEmptyTests() {
        String populatedString = "Some String";
        String emptyString = "";
        JSONObject populatedJsonObject = new JSONObject();
        JSONArray populatedJsonArray = new JSONArray();
        JSONObject emptyJsonObject = new JSONObject();
        JSONArray emptyJsonArray = new JSONArray();
        try {
            populatedJsonObject.put("anyKey", "anyvalue");
            populatedJsonArray.put("anyvalue");

            // Strings
            assertFalse(BrazeUtils.isNullOrEmpty(populatedString));
            assertTrue(BrazeUtils.isNullOrEmpty(emptyString));
            assertTrue(BrazeUtils.isNullOrEmpty((String) null));
            // JSONObjects
            assertFalse(BrazeUtils.isNullOrEmpty(populatedJsonObject));
            assertTrue(BrazeUtils.isNullOrEmpty(emptyJsonObject));
            assertTrue(BrazeUtils.isNullOrEmpty((JSONObject) null));
            // JSONArrays
            assertFalse(BrazeUtils.isNullOrEmpty(populatedJsonArray));
            assertTrue(BrazeUtils.isNullOrEmpty(emptyJsonArray));
            assertTrue(BrazeUtils.isNullOrEmpty((JSONArray) null));

        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void jsonArrayToStringArrayTests() {
        JSONArray expectedArray = new JSONArray();
        expectedArray.put("value1");
        expectedArray.put("value2");

        JSONArray unexpectedArray = new JSONArray();
        unexpectedArray.put("value1");
        unexpectedArray.put(10);
        unexpectedArray.put(false);

        String[] expectedResult = BrazeUtils.getStringArrayFromJson(expectedArray);
        String[] unexpectedResult = BrazeUtils.getStringArrayFromJson(unexpectedArray);

        assertNotNull(expectedArray);
        assertNotNull(unexpectedArray);

        assertEquals(expectedResult.length, expectedArray.length());
        assertEquals(unexpectedResult.length, unexpectedArray.length());

        assertTrue(expectedResult[0].equals("value1") && expectedResult[1].equals("value2"));
        assertTrue(unexpectedResult[0].equals("value1") && unexpectedResult[1].equals("10") && unexpectedResult[2].equals("false"));
    }

    @Test
    public void jsonArrayToIntegerArrayTests() {
        JSONArray expectedArray = new JSONArray();
        expectedArray.put(10);
        expectedArray.put(20);

        JSONArray unexpectedArray = new JSONArray();
        unexpectedArray.put("value1");
        unexpectedArray.put(10);
        unexpectedArray.put(false);

        Integer[] expectedResult = BrazeUtils.getIntegerArrayFromJson(expectedArray);
        Integer[] unexpectedResult = BrazeUtils.getIntegerArrayFromJson(unexpectedArray);

        assertNotNull(expectedArray);
        assertNotNull(unexpectedArray);

        assertEquals(expectedResult.length, expectedArray.length());
        assertEquals(unexpectedResult.length, unexpectedArray.length());

        assertTrue(expectedResult[0] == 10 && expectedResult[1] == 20);
        assertTrue(unexpectedResult[0] == 1 && unexpectedResult[1] == 10 && unexpectedResult[2] == 1);
    }

    @Test
    public void jsonArrayToBigDecimalArrayTests() {
        JSONArray expectedArray = new JSONArray();
        expectedArray.put(10);
        expectedArray.put(20);

        JSONArray unexpectedArray = new JSONArray();
        unexpectedArray.put("value1");
        unexpectedArray.put(10);
        unexpectedArray.put(false);

        BigDecimal[] expectedResult = BrazeUtils.getBigDecimalArrayFromJson(expectedArray);
        BigDecimal[] unexpectedResult = BrazeUtils.getBigDecimalArrayFromJson(unexpectedArray);

        assertNotNull(expectedArray);
        assertNotNull(unexpectedArray);

        assertEquals(expectedResult.length, expectedArray.length());
        assertEquals(unexpectedResult.length, unexpectedArray.length());

        assertTrue(expectedResult[0].equals(new BigDecimal(10)) && expectedResult[1].equals(new BigDecimal(20)));
        assertTrue(unexpectedResult[0].equals(new BigDecimal(0)) && unexpectedResult[1].equals(new BigDecimal(10)) && unexpectedResult[2].equals(new BigDecimal(0)));
    }

    @Test
    public void jsonArrayToJSONObjectArrayTests() {
        JSONArray expectedArray = new JSONArray();
        JSONObject filledObject = new JSONObject();
        JSONObject emptyObject = new JSONObject();
        try {
            filledObject.put("key", "value");
            filledObject.put("anotherkey", "anotherValue");

            expectedArray.put(filledObject);
            expectedArray.put(emptyObject);

            JSONArray unexpectedArray = new JSONArray();
            unexpectedArray.put(filledObject);
            unexpectedArray.put(10);
            unexpectedArray.put(false);

            JSONObject[] expectedResult = BrazeUtils.getJSONObjectArrayFromJson(expectedArray);
            JSONObject[] unexpectedResult = BrazeUtils.getJSONObjectArrayFromJson(unexpectedArray);

            assertNotNull(expectedArray);
            assertNotNull(unexpectedArray);

            assertEquals(expectedResult.length, expectedArray.length());
            assertEquals(unexpectedResult.length, unexpectedArray.length());

            assertTrue(expectedResult[0].toString().equals(filledObject.toString()) && expectedResult[1].toString().equals(emptyObject.toString()));
            assertTrue(unexpectedResult[0].toString().equals(filledObject.toString()) && unexpectedResult[1].toString().equals(emptyObject.toString()) && unexpectedResult[2].toString().equals(emptyObject.toString()));

        } catch (Exception e) {
            fail();
        }
    }
}
