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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.math.BigDecimal;
import java.util.Date;

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

    @Test
    public void parseDateTest_SimpleDateFormat() {
        Date date = BrazeUtils.parseDate("2000-01-01T01:01:01Z");

        assertEquals(1, date.getDate());
        assertEquals(0, date.getMonth());
        assertEquals(2000 - 1900, date.getYear());
    }

    @Test
    public void parseDateTest_BrazeShort() {
        Date date = BrazeUtils.parseDate("2000-01-01");

        assertEquals(1, date.getDate());
        assertEquals(0, date.getMonth());
        assertEquals(2000 - 1900, date.getYear());
    }

    @Test
    public void parseDateTest_BrazeLong() {
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
