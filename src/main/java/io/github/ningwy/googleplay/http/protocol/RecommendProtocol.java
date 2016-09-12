package io.github.ningwy.googleplay.http.protocol;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import io.github.ningwy.googleplay.utils.GsonUtils;

/**
 * 推荐页面网络请求
 * Created by ningwy on 2016/9/6.
 */
public class RecommendProtocol extends BaseProtocol<List<String>> {

    @Override
    public String getParams() {
        return "";
    }

    @Override
    public String getKey() {
        return "recommend";
    }

    @Override
    public List<String> parseData(String json) {
        Gson gson = GsonUtils.getGson();
        Type type = new TypeToken<List<String>>(){}.getType();
        return gson.fromJson(json, type);
    }
}
