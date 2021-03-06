package com.tealium.remotecommands.braze;

import androidx.test.runner.AndroidJUnit4;

import com.appboy.enums.Gender;
import com.appboy.models.outgoing.AppboyProperties;

import junit.framework.Assert;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.math.BigDecimal;

@RunWith(AndroidJUnit4.class)
public class BrazeUtilityMethodTests {

    public BrazeUtilityMethodTests() { }

    @Test
    public void genderStringToEnumTest(){

        // Male
        Assert.assertEquals(BrazeUtils.getGenderEnumFromString("Male"), Gender.MALE);
        Assert.assertEquals(BrazeUtils.getGenderEnumFromString("m"), Gender.MALE);

        // Female
        Assert.assertEquals(BrazeUtils.getGenderEnumFromString("female"), Gender.FEMALE);
        Assert.assertEquals(BrazeUtils.getGenderEnumFromString("f"), Gender.FEMALE);

        // Other
        Assert.assertEquals(BrazeUtils.getGenderEnumFromString("otHer"), Gender.OTHER);
        Assert.assertEquals(BrazeUtils.getGenderEnumFromString("o"), Gender.OTHER);

        // N/A
        Assert.assertEquals(BrazeUtils.getGenderEnumFromString("not_applicable"), Gender.NOT_APPLICABLE);
        Assert.assertEquals(BrazeUtils.getGenderEnumFromString("na"), Gender.NOT_APPLICABLE);

        // Prefer not to say
        Assert.assertEquals(BrazeUtils.getGenderEnumFromString("prefer_NOT_to_say"), Gender.PREFER_NOT_TO_SAY);
        Assert.assertEquals(BrazeUtils.getGenderEnumFromString("no"), Gender.PREFER_NOT_TO_SAY);

        // Unknown
        Assert.assertEquals(BrazeUtils.getGenderEnumFromString("unknown"), Gender.UNKNOWN);

        // default
        Assert.assertEquals(BrazeUtils.getGenderEnumFromString("UNEXPECTED VALUE"), null);
    }

    @Test
    public void addCustomPropertyTests(){
        Object stringValue = "test";
        Object integerValue = 10;
        Object doubleValue = 10.10;
        Object booleanValue = true;
        Object integerStringValue = "10";
        Object doubleStringValue = "10.10";
        Object booleanStringValue = "true";
        // Object dateValue = ""; //TODO: get an example of the data in simple date format.

        AppboyProperties props = new AppboyProperties();
        props = BrazeUtils.addCustomProperty("stringValue", stringValue, props);
        props = BrazeUtils.addCustomProperty("integerValue", integerValue, props);
        props = BrazeUtils.addCustomProperty("doubleValue", doubleValue, props);
        props = BrazeUtils.addCustomProperty("booleanValue", booleanValue, props);
        props = BrazeUtils.addCustomProperty("integerStringValue", integerStringValue, props);
        props = BrazeUtils.addCustomProperty("doubleStringValue", doubleStringValue, props);
        props = BrazeUtils.addCustomProperty("booleanStringValue", booleanStringValue, props);

        JSONObject appboyPropsJson = props.forJsonPut();
        try {
            Assert.assertEquals(stringValue, appboyPropsJson.getString("stringValue"));
            Assert.assertTrue(appboyPropsJson.get("stringValue") instanceof  String);
            Assert.assertEquals(integerValue, appboyPropsJson.getInt("integerValue"));
            Assert.assertTrue(appboyPropsJson.get("integerValue") instanceof  Integer);
            Assert.assertEquals(doubleValue, appboyPropsJson.getDouble("doubleValue"));
            Assert.assertTrue(appboyPropsJson.get("doubleValue") instanceof  Double);
            Assert.assertEquals(booleanValue, appboyPropsJson.getBoolean("booleanValue"));
            Assert.assertTrue(appboyPropsJson.get("booleanValue") instanceof  Boolean);

            /*
            * At the time of writing, the Android SDK will stringify values in a HashMap such that
            * the native type is lost. The method being tested here will attempt to recover that.
            * As a result the expected types should be integer/double/booolean despite the value
            * that was put in, was actually a string.
            * */
            Assert.assertEquals(integerValue, appboyPropsJson.getInt("integerStringValue"));
            Assert.assertTrue(appboyPropsJson.get("integerStringValue") instanceof  Integer);
            Assert.assertEquals(doubleValue, appboyPropsJson.getDouble("doubleStringValue"));
            Assert.assertTrue(appboyPropsJson.get("doubleStringValue") instanceof  Double);
            Assert.assertEquals(booleanValue, appboyPropsJson.getBoolean("booleanStringValue"));
            Assert.assertTrue(appboyPropsJson.get("booleanStringValue") instanceof  Boolean);

        }catch(JSONException jex){
            Assert.fail();
        }

        AppboyProperties props2 = BrazeUtils.addCustomProperty("stringValue", stringValue);
        JSONObject appboyPropsJson2 = props2.forJsonPut();
        try {
            // Shorthand method should generate a new AppboyProperties if one isn't suppplied
            Assert.assertNotNull(props2);

            Assert.assertEquals(stringValue, appboyPropsJson2.getString("stringValue"));
            Assert.assertTrue(appboyPropsJson2.get("stringValue") instanceof  String);

        }catch(JSONException jex){
            Assert.fail();
        }
    }

    @Test
    public void jsonKeyHasValueTests(){
        JSONObject json = new JSONObject();
        try {
            json.put("string_key", "string");
            json.put("null_key", null);

            Assert.assertTrue(BrazeUtils.keyHasValue(json, "string_key"));
            Assert.assertFalse(BrazeUtils.keyHasValue(json, "null_key"));
            Assert.assertFalse(BrazeUtils.keyHasValue(json, "non_existent_key"));

        }catch (Exception e){
            Assert.fail();
        }
    }

    @Test
    public void isNullOrEmptyTests(){
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
            Assert.assertFalse(BrazeUtils.isNullOrEmpty(populatedString));
            Assert.assertTrue(BrazeUtils.isNullOrEmpty(emptyString));
            Assert.assertTrue(BrazeUtils.isNullOrEmpty((String)null));
            // JSONObjects
            Assert.assertFalse(BrazeUtils.isNullOrEmpty(populatedJsonObject));
            Assert.assertTrue(BrazeUtils.isNullOrEmpty(emptyJsonObject));
            Assert.assertTrue(BrazeUtils.isNullOrEmpty((JSONObject) null));
            // JSONArrays
            Assert.assertFalse(BrazeUtils.isNullOrEmpty(populatedJsonArray));
            Assert.assertTrue(BrazeUtils.isNullOrEmpty(emptyJsonArray));
            Assert.assertTrue(BrazeUtils.isNullOrEmpty((JSONArray) null));

        }catch (Exception e){
            Assert.fail();
        }
    }

    @Test
    public void jsonArrayToStringArrayTests(){
        JSONArray expectedArray = new JSONArray();
        expectedArray.put("value1");
        expectedArray.put("value2");

        JSONArray unexpectedArray = new JSONArray();
        unexpectedArray.put("value1");
        unexpectedArray.put(10);
        unexpectedArray.put(false);

        String[] expectedResult = BrazeUtils.getStringArrayFromJson(expectedArray);
        String[] unexpectedResult = BrazeUtils.getStringArrayFromJson(unexpectedArray);

        Assert.assertNotNull(expectedArray);
        Assert.assertNotNull(unexpectedArray);

        Assert.assertTrue(expectedResult.length == expectedArray.length());
        Assert.assertTrue(unexpectedResult.length == unexpectedArray.length());

        Assert.assertTrue(expectedResult[0].equals("value1") && expectedResult[1].equals("value2"));
        Assert.assertTrue(unexpectedResult[0].equals("value1") && unexpectedResult[1].equals("10") && unexpectedResult[2].equals("false"));
    }

    @Test
    public void jsonArrayToIntegerArrayTests(){
        JSONArray expectedArray = new JSONArray();
        expectedArray.put(10);
        expectedArray.put(20);

        JSONArray unexpectedArray = new JSONArray();
        unexpectedArray.put("value1");
        unexpectedArray.put(10);
        unexpectedArray.put(false);

        Integer[] expectedResult = BrazeUtils.getIntegerArrayFromJson(expectedArray);
        Integer[] unexpectedResult = BrazeUtils.getIntegerArrayFromJson(unexpectedArray);

        Assert.assertNotNull(expectedArray);
        Assert.assertNotNull(unexpectedArray);

        Assert.assertTrue(expectedResult.length == expectedArray.length());
        Assert.assertTrue(unexpectedResult.length == unexpectedArray.length());

        Assert.assertTrue(expectedResult[0] == 10 && expectedResult[1] == 20);
        Assert.assertTrue(unexpectedResult[0] == 1 && unexpectedResult[1] == 10 && unexpectedResult[2] == 1);
    }

    @Test
    public void jsonArrayToBigDecimalArrayTests(){
        JSONArray expectedArray = new JSONArray();
        expectedArray.put(10);
        expectedArray.put(20);

        JSONArray unexpectedArray = new JSONArray();
        unexpectedArray.put("value1");
        unexpectedArray.put(10);
        unexpectedArray.put(false);

        BigDecimal[] expectedResult = BrazeUtils.getBigDecimalArrayFromJson(expectedArray);
        BigDecimal[] unexpectedResult = BrazeUtils.getBigDecimalArrayFromJson(unexpectedArray);

        Assert.assertNotNull(expectedArray);
        Assert.assertNotNull(unexpectedArray);

        Assert.assertTrue(expectedResult.length == expectedArray.length());
        Assert.assertTrue(unexpectedResult.length == unexpectedArray.length());

        Assert.assertTrue(expectedResult[0].equals(new BigDecimal(10)) && expectedResult[1].equals(new BigDecimal(20)));
        Assert.assertTrue(unexpectedResult[0].equals(new BigDecimal(0)) && unexpectedResult[1].equals(new BigDecimal(10)) && unexpectedResult[2].equals(new BigDecimal(0)));
    }

    @Test
    public void jsonArrayToJSONObjectArrayTests(){
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

            Assert.assertNotNull(expectedArray);
            Assert.assertNotNull(unexpectedArray);

            Assert.assertTrue(expectedResult.length == expectedArray.length());
            Assert.assertTrue(unexpectedResult.length == unexpectedArray.length());

            Assert.assertTrue(expectedResult[0].toString().equals(filledObject.toString()) && expectedResult[1].toString().equals(emptyObject.toString()));
            Assert.assertTrue(unexpectedResult[0].toString().equals(filledObject.toString()) && unexpectedResult[1].toString().equals(emptyObject.toString()) && unexpectedResult[2].toString().equals(emptyObject.toString()));

        }catch(Exception e){
            Assert.fail();
        }
    }
}
