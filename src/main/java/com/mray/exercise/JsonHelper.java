package com.mray.exercise;

import java.util.Map;

/**
 * Created by mray on 2/9/15.
 */

public class JsonHelper {

    public static String buildJSONString(Map<String, String> dataMap) {
        StringBuilder builder = new StringBuilder("{");
        int i = 0;
        for(String key : dataMap.keySet()) {
            if(i++ > 0) {
                builder.append(",");
            }
            builder.append(String.format( "\"%s\" : \"%s\"", key, dataMap.get(key)));
        }
        builder.append("}");
        return builder.toString();
    }

    public static String buildJSONString(String k, String v) {
        StringBuilder builder = new StringBuilder("{");
        builder.append(String.format( "\"%s\" : \"%s\"", k, v));
        builder.append("}");
        return builder.toString();
    }

}
