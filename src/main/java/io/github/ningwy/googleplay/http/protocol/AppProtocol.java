package io.github.ningwy.googleplay.http.protocol;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.github.ningwy.googleplay.domain.AppPageInfo;

/**
 * 应用网络请求
 * Created by ningwy on 2016/9/6.
 */
public class AppProtocol extends BaseProtocol<ArrayList<AppPageInfo>> {

    @Override
    public String getParams() {
        return "";
    }

    @Override
    public String getKey() {
        return "app";
    }

    @Override
    public ArrayList<AppPageInfo> parseData(String json) {
        try {
            JSONArray jsonArray = new JSONArray(json);

            ArrayList<AppPageInfo> list = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                AppPageInfo info = new AppPageInfo();
                JSONObject jo = jsonArray.getJSONObject(i);

                info.des = jo.getString("des");
                info.downloadUrl = jo.getString("downloadUrl");
                info.iconUrl = jo.getString("iconUrl");
                info.id = jo.getString("id");
                info.name = jo.getString("name");
                info.packageName = jo.getString("packageName");
                info.size = jo.getLong("size");
                info.stars = (float) jo.getDouble("stars");

                list.add(info);
            }
            return list;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
