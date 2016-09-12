package io.github.ningwy.googleplay.http.protocol;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.github.ningwy.googleplay.domain.AppPageInfo;

/**
 * 主页访问网络的封装
 * Created by ningwy on 2016/9/5.
 */
public class HomeProtocol extends BaseProtocol<ArrayList<AppPageInfo>> {

    private ArrayList<String> pictures;


    @Override
    public String getParams() {
        return "";//注意没有参数不能返回null只能返回""
    }

    @Override
    public String getKey() {
        return "home";
    }

    @Override
    public ArrayList<AppPageInfo> parseData(String json) {
//        Gson gson = GsonUtils.getGson();
//        Type type = new TypeToken<ArrayList<AppPageInfo>>(){}.getType();
//        return gson.fromJson(json, type);
        try {
            JSONObject jo = new JSONObject(json);

            // 解析应用列表数据
            JSONArray ja = jo.getJSONArray("list");
            ArrayList<AppPageInfo> appInfoList = new ArrayList<>();
            for (int i = 0; i < ja.length(); i++) {
                JSONObject jo1 = ja.getJSONObject(i);

                AppPageInfo info = new AppPageInfo();
                info.des = jo1.getString("des");
                info.downloadUrl = jo1.getString("downloadUrl");
                info.iconUrl = jo1.getString("iconUrl");
                info.id = jo1.getString("id");
                info.name = jo1.getString("name");
                info.packageName = jo1.getString("packageName");
                info.size = jo1.getLong("size");
                info.stars = (float) jo1.getDouble("stars");

                appInfoList.add(info);
            }

            // 初始化轮播条的数据
            JSONArray ja1 = jo.getJSONArray("picture");
            pictures = new ArrayList<String>();
            for (int i = 0; i < ja1.length(); i++) {
                String pic = ja1.getString(i);
                pictures.add(pic);
            }

            return appInfoList;

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public ArrayList<String> getPictureList() {
        return pictures;
    }
}
