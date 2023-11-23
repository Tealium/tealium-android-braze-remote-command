package com.tealium.remotecommands.braze;

import android.util.Log;

import com.braze.enums.BrazeDateFormat;
import com.braze.enums.Month;
import com.braze.enums.Gender;
import com.braze.models.outgoing.BrazeProperties;
import com.braze.support.DateTimeUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;

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
     * As a result the expected types should be integer/double/booolean etc despite the value
     * that was sent in the event might actually have been a string.
     * <p>
     * This is a helper method that will take
     *
     * @param key        - name of the custom property to add.
     * @param data       - value to add to the custom property
     * @param properties - an existing AppboyProperties object to add this key-value pair to. If
     *                   null, then a new AppboyProperties object will be created to be returned
     * @return The amended BrazeProperties including the additional one supplied
     */
    public static BrazeProperties addCustomProperty(String key, Object data, BrazeProperties properties) {
        if (properties == null) {
            Log.d(BrazeConstants.TAG, "Creating new AppboyProperties");
            properties = new BrazeProperties();
        }

        if (data instanceof String) {
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

        } else if (data instanceof Integer) {
            properties.addProperty(key, (Integer) data);
        } else if (data instanceof Double) {
            properties.addProperty(key, (Double) data);
        } else if (data instanceof Boolean) {
            properties.addProperty(key, (Boolean) data);
        } else if (data instanceof Date) {
            properties.addProperty(key, (Date) data);
        }

        return properties;
    }

    /**
     * Short-hand method for calling addCustomProperty, generating a new AppboyProperties object at
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
     * Helper method to translate a JSONObject of key-value pairs into an AppboyProperties object.
     * The values in the JSONObject should only be supported types for the AppboyProperties class,
     * which at the time of writing is only String, Integer, Double, Date and Boolean
     *
     * @param customProperties - JSONObject of Key-Value pairs.
     * @return AppboyProperties containing the Key-Value pairs supplied
     */
    public static BrazeProperties extractCustomProperties(JSONObject customProperties) {
        BrazeProperties props = new BrazeProperties();
        if (customProperties != null) {
            try {
                // add the provided custom properties into the new payload object
                Iterator<String> iterator = customProperties.keys();
                while (iterator.hasNext()) {
                    String key = iterator.next();
                    props = BrazeUtils.addCustomProperty(key, customProperties.get(key), props);
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
        } catch (ParseException ignore) { }

        try {
            // try with ISO8601 date format
            date = BrazeUtils.ISO_8601_DATE_FORMAT.parse(dateString);
        } catch (ParseException ignore) { }

        if (date == null) {
            // try Braze date formats
            for (BrazeDateFormat dateFormat: BrazeDateFormat.values()) {
                try {
                    // try with ISO8601 date format
                    date = DateTimeUtils.parseDate(dateString, dateFormat);
                } catch (Exception ignore) {
                    /* Method does not specify ParseException, but does throw during tests. */
                }

                if (date != null) break;
            }
        }
        return date;
    }
}
