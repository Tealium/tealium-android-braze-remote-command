package com.tealium.remotecommands.braze;

import android.app.Application;

import com.tealium.library.Tealium;

import junit.framework.Assert;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Collection;
import java.util.Map;

public class TestUtils {

    public static final String INSTANCE_NAME = "main";

    public static final String ACCOUNT_NAME = "tealiummobile";
    public static final String PROFILE_NAME = "braze";
    public static final String ENVIRONMENT_NAME = "dev";

    private static Application application;
    private static Tealium.Config defaultConfig;

    /**
     * Sets up an instance using a default name (main) and default Tealium.Config
     *
     * @param app
     */
    public static Tealium setupInstance(Application app) {
        if (getDefaultInstance() != null) {
            return getDefaultInstance();
        }
        application = app;
        defaultConfig = setupDefaultConfig(app);
        return setupInstance(app, INSTANCE_NAME, defaultConfig);
    }

    /**
     * Sets up an instance using a custom name and default Tealium.Config
     *
     * @param app
     * @param name
     */
    public static Tealium setupInstance(Application app, String name) {
        application = app;
        return setupInstance(app, name, setupDefaultConfig(app));
    }

    /**
     * Sets up an instance using a custom name and custom Tealium.Config
     *
     * @param app
     * @param name
     * @param config
     */
    public static Tealium setupInstance(Application app, String name, Tealium.Config config) {
        application = app;
        return Tealium.createInstance(name, config);
    }

    private static Tealium.Config setupDefaultConfig(Application app) {
        if (defaultConfig == null) {
            defaultConfig = Tealium.Config.create(app, ACCOUNT_NAME, PROFILE_NAME, ENVIRONMENT_NAME);
        }
        return defaultConfig;
    }

    /**
     * Sets up a Tealium.Config with default parameters
     *
     * @return
     */
    public static Tealium.Config getDefaultConfig() {
        return defaultConfig;
    }

    /**
     * Sets up a Tealium.Config item with all custom values provided.
     *
     * @param app
     * @param account
     * @param profile
     * @param environment
     * @return
     */
    public static Tealium.Config getCustomConfig(Application app, String account, String profile, String environment) {
        return Tealium.Config.create(app,
                account,
                profile,
                environment);
    }

    /**
     * Get's the default instance named "main"
     *
     * @return com.tealium.library.Tealium instance - us Nullable where no instance exists for that name.
     */
    public static Tealium getDefaultInstance() {
        return getNamedInstance(INSTANCE_NAME);
    }

    /**
     * Get's the instance named in the @name parameter.
     *
     * @return com.tealium.library.Tealium instance - us Nullable where no instance exists for that name.
     */
    public static Tealium getNamedInstance(String name) {
        return Tealium.getInstance(name);
    }


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
