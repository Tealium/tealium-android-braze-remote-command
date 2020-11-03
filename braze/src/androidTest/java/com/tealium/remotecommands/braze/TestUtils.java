package com.tealium.remotecommands.braze;

import junit.framework.Assert;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Collection;
import java.util.Map;

public class TestUtils {

    public static final String INSTANCE_NAME = "main";

    public static boolean assertContainsOnly(Collection<String> actual, Collection<String> expected) {
        String unexpectedMethod = null;
        for (String entry : actual) {
            if (!expected.contains(entry)) {
                unexpectedMethod = entry;
                break;
            }
        }
        Assert.assertNull("assertContainsOnly: Unexpected method name found: " + unexpectedMethod, unexpectedMethod);
        return true;
    }

    public static boolean assertContainsAll(Collection<String> actual, Collection<String> expected) {
        boolean result = actual.containsAll(expected);
        Assert.assertTrue("assertContainsAll: Not all expected methods were found.", result);
        return result;
    }

    public static boolean assertContainsAllAndOnly(Collection<String> actual, Collection<String> expected) {
        return assertContainsAll(actual, expected) && assertContainsOnly(actual, expected);
    }

    public static boolean compareMapToJson(Map<String, Object> eventData, JSONObject jsonData) {
        boolean isError = false;
        for (String key : eventData.keySet()) {
            try {
                Object obj = eventData.get(key);

                if (!jsonData.has(key) || jsonData.get(key) == null) {
                    // if we dont have the key in the payload, then we're missing data that we're expecting
                    isError = true;
                    break;
                } else if (obj instanceof Integer && !((Integer) eventData.get(key)).equals(jsonData.getInt(key))) {
                    isError = true;
                    break;
                } else if (obj instanceof Double && !((Double) eventData.get(key)).equals(jsonData.getDouble(key))) {
                    isError = true;
                    break;
                } else if (obj instanceof Long && !((Long) eventData.get(key)).equals(jsonData.getLong(key))) {
                    isError = true;
                    break;
                } else if (obj instanceof JSONObject && !((JSONObject) eventData.get(key)).toString().equals(jsonData.getJSONObject(key).toString())) {
                    isError = true;
                    break;
                } else if (obj instanceof JSONArray && !((JSONArray) eventData.get(key)).toString().equals(jsonData.getJSONObject(key).toString())) {
                    isError = true;
                    break;
                } else if (obj instanceof String && !((String) eventData.get(key)).equals(jsonData.getString(key))) {
                    isError = true;
                    break;
                }
            } catch (Exception e) {
                isError = true;
            }
        }
        if (isError) {
            Assert.fail("Mismatched data between event and JSONObject.");
        }
        return !isError;
    }
}