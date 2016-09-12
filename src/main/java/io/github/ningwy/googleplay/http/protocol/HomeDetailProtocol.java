package io.github.ningwy.googleplay.http.protocol;

import com.google.gson.Gson;

import io.github.ningwy.googleplay.domain.AppPageInfo;
import io.github.ningwy.googleplay.utils.GsonUtils;

/**
 * 应用详情的网络请求
 * Created by ningwy on 2016/9/7.
 */
public class HomeDetailProtocol extends BaseProtocol<AppPageInfo> {

    private String packageName;

    public HomeDetailProtocol(String packageName) {
        this.packageName = packageName;
    }

    @Override
    public String getParams() {
        return "&packageName=" + packageName;
    }

    @Override
    public String getKey() {
        return "detail";
    }

    @Override
    public AppPageInfo parseData(String json) {
        Gson gson = GsonUtils.getGson();
        return gson.fromJson(json, AppPageInfo.class);
    }
}
