package ninja.egg82.analytics.utils;

import java.util.concurrent.ConcurrentLinkedDeque;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class JSONUtil {
    private JSONUtil() {}

    private static ConcurrentLinkedDeque<JSONParser> pool = new ConcurrentLinkedDeque<>(); // JSONParser is not stateless and thus requires a pool in multi-threaded environments

    static {
        pool.add(new JSONParser());
    }

    public static JSONObject parseObject(String input) throws ParseException, ClassCastException {
        if (input == null) {
            throw new IllegalArgumentException("input cannot be null.");
        }

        JSONParser parser = getParser();
        JSONObject retVal = (JSONObject) parser.parse(input);
        pool.add(parser);
        return retVal;
    }

    public static JSONArray parseArray(String input) throws ParseException, ClassCastException {
        if (input == null) {
            throw new IllegalArgumentException("input cannot be null.");
        }

        JSONParser parser = getParser();
        JSONArray retVal = (JSONArray) parser.parse(input);
        pool.add(parser);
        return retVal;
    }

    public static Object parseGeneric(String input) throws ParseException {
        if (input == null) {
            throw new IllegalArgumentException("input cannot be null.");
        }

        JSONParser parser = getParser();
        Object retVal = parser.parse(input);
        pool.add(parser);
        return retVal;
    }

    private static JSONParser getParser() {
        JSONParser parser = pool.pollFirst();
        if (parser == null) {
            parser = new JSONParser();
        }
        return parser;
    }
}
