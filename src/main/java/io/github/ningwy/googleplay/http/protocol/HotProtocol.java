package io.github.ningwy.googleplay.http.protocol;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import io.github.ningwy.googleplay.utils.GsonUtils;

/**
 * 排行网络请求
 * Created by ningwy on 2016/9/7.
 */
public class HotProtocol extends BaseProtocol<List<String>> {

    @Override
    public String getParams() {
        return "";
    }

    @Override
    public String getKey() {
        return "hot";
    }

    @Override
    public List<String> parseData(String json) {
        Gson gson = GsonUtils.getGson();
        Type type = new TypeToken<List<String>>(){}.getType();
        return gson.fromJson(json, type);
    }

}
