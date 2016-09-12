package io.github.ningwy.googleplay.http.protocol;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import io.github.ningwy.googleplay.domain.SubjectInfo;
import io.github.ningwy.googleplay.utils.GsonUtils;

/**
 * 专题页面网络请求
 * Created by ningwy on 2016/9/6.
 */
public class SubjectProtocol extends BaseProtocol<List<SubjectInfo>> {

    @Override
    public String getParams() {
        return "";
    }

    @Override
    public String getKey() {
        return "subject";
    }

    @Override
    public List<SubjectInfo> parseData(String json) {
        Type type = new TypeToken<List<SubjectInfo>>(){}.getType();
        Gson gson = GsonUtils.getGson();
        List<SubjectInfo> data = gson.fromJson(json, type);
        return data;
    }
}
