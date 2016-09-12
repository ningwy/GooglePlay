package io.github.ningwy.googleplay.utils;

import com.google.gson.Gson;

/**
 * gson工具类封装——单例模式
 * Created by ningwy on 2016/9/6.
 */
public class GsonUtils {

    private static Gson gson;

    public static Gson getGson() {
        if (gson == null) {
            synchronized (GsonUtils.class) {
                if (gson == null) {
                    gson = new Gson();
                    return gson;
                }
            }
        }
        return gson;
    }
}
