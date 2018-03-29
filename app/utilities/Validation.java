package utilities;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;

import java.util.Map;

/**
 * A utility class that holds methods for validating JSON and query parameters from the requests.
 */
public final class Validation {

    private Validation() {
    }

    /**
     * @param fieldName The name of the field that should be extracted from the map.
     * @param map       The query data whose data should be extracted.
     * @return The number that maps to the field name or -1 if it cannot be parsed.
     */
    @SuppressWarnings("WeakerAccess")
    public static int page(String fieldName, Map<String, String[]> map) {
        if (map.get(fieldName) == null || map.get(fieldName).length == 0) {
            return 0;
        }

        try {
            int page = Integer.parseInt(map.get(fieldName)[0]);
            if (page < 0) {
                return 0;
            } else {
                return page;
            }
        } catch (Exception e) {
            return 0;
        }
    }


    /**
     * @param fieldName The name of the field that should be extracted from the map.
     * @param map       The query data whose data should be extracted.
     * @return The number that maps to the field name or -1 if it cannot be parsed.
     */
    @SuppressWarnings("WeakerAccess")
    public static int integer(String fieldName, Map<String, String[]> map) {
        if (map.get(fieldName) == null || map.get(fieldName).length == 0) {
            return -1;
        }

        try {
            return Integer.parseInt(map.get(fieldName)[0]);
        } catch (Exception e) {
            return -1;
        }
    }

    /**
     * @param fieldName The name of the field in JSON that should be extracted from the node.
     * @param map       The query data whose data should be extracted.
     * @return The number that maps to the field name or -1 if it cannot be parsed.
     */
    @SuppressWarnings("WeakerAccess")
    public static int integer(String fieldName, Map<String, String[]> map, int defaultValue) {
        if (map.get(fieldName) == null || map.get(fieldName).length == 0) {
            return defaultValue;
        }

        try {
            return Integer.parseInt(map.get(fieldName)[0]);
        } catch (Exception e) {
            return -1;
        }
    }


    /**
     * @param fieldName The name of the field in JSON that should be extracted from the node.
     * @param jsonNode  The JSON node whose data should be extracted.
     * @return The number that maps to the field name or -1 if it cannot be parsed.
     */
    @SuppressWarnings("WeakerAccess")
    public static int integer(String fieldName, JsonNode jsonNode) {
        if (jsonNode.get(fieldName) == null) {
            return -1;
        }

        return jsonNode.findValue(fieldName).asInt();
    }

    /**
     * @param fieldName  The name of the field in JSON that should be extracted from the node.
     * @param jsonNode   The JSON node whose data should be extracted.
     * @param shouldTrim True to force trimming on the value and setting it to null if it's just whitespace, or false to
     *                   set any string (even whitespace) as non-null
     * @return The String that maps to the field name or null if the field is empty/length is 0.
     */
    public static String string(String fieldName, JsonNode jsonNode, boolean shouldTrim) {
        if (jsonNode.get(fieldName) == null) {
            return null;
        }

        String value = jsonNode.get(fieldName).asText();
        if (value != null && !jsonNode.get(fieldName).isNull()) {
            if (shouldTrim) {
                if (value.trim().isEmpty()) {
                    return null;
                } else {
                    return value.trim();
                }
            } else {
                return value;
            }
        } else {
            return null;
        }
    }

    /**
     * @param fieldName The name of the field in JSON that should be extracted from the node.
     * @param jsonNode  The JSON node whose data should be extracted.
     * @return The String that maps to the field name or -1 if the field is empty or invalid.
     */
    public static long getLong(String fieldName, JsonNode jsonNode) {
        if (jsonNode.get(fieldName) == null) {
            return -1;
        }

        jsonNode = jsonNode.get(fieldName);
        if (jsonNode.getNodeType() == JsonNodeType.NUMBER) {
            return jsonNode.asLong();
        } else {
            try {
                return Long.parseLong(jsonNode.asText());
            } catch (Exception e) {
                return -1;
            }
        }
    }


    /**
     * @param fieldName The name of the field in JSON that should be extracted from the node.
     * @param jsonNode  The JSON node whose data should be extracted.
     * @return The boolean that maps to the field name or false if the field is empty malformed.
     */
    public static boolean bool(String fieldName, JsonNode jsonNode) {
        return jsonNode.get(fieldName) != null && jsonNode.get(fieldName).asBoolean();
    }

    /**
     * @param fieldName The name of the field in JSON that should be extracted from the node.
     * @param jsonNode  The JSON node whose data should be extracted.
     * @return The boolean that maps to the field name or false if the field is empty malformed.
     */
    @SuppressWarnings("SameParameterValue")
    public static boolean bool(String fieldName, JsonNode jsonNode, boolean defaultValue) {
        if (jsonNode.get(fieldName) != null) {
            return jsonNode.get(fieldName).asBoolean();
        } else {
            return defaultValue;
        }
    }

    /**
     * @param fieldName The name of the field in JSON that should be extracted from the node.
     * @param map       The query map whose data should be extracted.
     * @return The boolean that maps to the field name or false if the field is empty malformed.
     */
    public static boolean bool(String fieldName, Map<String, String[]> map) {
        return map.get(fieldName) != null &&
                map.get(fieldName).length >= 1 &&
                Boolean.parseBoolean(map.get(fieldName)[0]);
    }


    /**
     * @param fieldName The name of the field in JSON that should be extracted from the node.
     * @param map       The query map whose data should be extracted.
     * @return The boolean that maps to the field name or false if the field is empty malformed.
     */
    public static boolean bool(String fieldName, Map<String, String[]> map, boolean defaultValue) {
        if (!map.containsKey(fieldName)) {
            return defaultValue;
        } else {
            return map.get(fieldName) != null
                    && map.get(fieldName).length >= 1
                    && Boolean.parseBoolean(map.get(fieldName)[0]);
        }
    }

    /**
     * Gets a given String from a map, if there is one, or returns null.
     *
     * @param keyName The name of the key to extract from the map.
     * @param map     The map from which the object should be extracted.
     * @return The given string that maps to the key or null if there is no such mapping.
     */
    public static String string(String keyName, Map<String, String[]> map) {
        if (map.get(keyName) != null && map.get(keyName).length >= 1) {
            return map.get(keyName)[0];
        } else {
            return null;
        }
    }

    /**
     * @param value The value to be checked if empty
     * @return True if empty (null or trimmed length is 0), false otherwise
     */
    public static boolean isEmpty(String value) {
        return value == null || value.trim().length() == 0;
    }

    /**
     * @param email The email that needs to be verified for correctness
     * @return True if the email is valid, false otherwise
     */
    public static boolean isEmailValid(String email) {
        if (email == null) {
            return false;
        }

        final String emailRegex = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
        return email.matches(emailRegex);
    }
}
