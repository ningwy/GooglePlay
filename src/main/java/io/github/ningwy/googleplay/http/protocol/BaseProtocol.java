package io.github.ningwy.googleplay.http.protocol;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import io.github.ningwy.googleplay.http.HttpHelper;
import io.github.ningwy.googleplay.utils.IOUtils;
import io.github.ningwy.googleplay.utils.StringUtils;
import io.github.ningwy.googleplay.utils.UIUtils;

/**
 * 协议封装的基类
 * Created by ningwy on 2016/9/5.
 */
public abstract class BaseProtocol<T> {

    /**
     *
     * @param index index用于分页
     */
    public T getData(int index) {
        //读取缓存
        String result = getCache(index);
        if (StringUtils.isEmpty(result)) {//如果没有缓存，则从网络获取
            //从网络获取数据
            result = getDataFromNet(index);
        }

        if (result != null) {
            T data = parseData(result);
            return data;
        }
        return null;
    }

    /**
     * 从网络获取数据
     * @param index index用于分页
     */
    public String getDataFromNet(int index) {
        //url:http://127.0.0.1:8090/home?index=20&name=zhangsan&age=19
        String url = HttpHelper.URL + getKey() + "?index=" + index + getParams();

        HttpHelper.HttpResult httpResult = HttpHelper.get(url);
        if (httpResult != null) {
            String result = httpResult.getString();
            //写缓存
            if (result != null) {
                setCache(index, result);
            }
            return result;
        }
        return null;
    }

    /**
     * 写缓存
     * @param index 合成文件名
     * @param json 缓存数据
     */
    private void setCache(int index, String json) {
        //缓存目录
        File cacheDir = UIUtils.getContext().getCacheDir();
        //缓存文件，文件名为url
        File cacheFile = new File(cacheDir, getKey() + "?index=" + index + getParams());
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(cacheFile);
            //缓存有效期：30分钟
            long dealLine = System.currentTimeMillis() + 30 * 60 * 1000;
            //将缓存有效期写在第一行
            fileWriter.write(dealLine + "\n");
            //写缓存数据
            fileWriter.write(json);
            fileWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.close(fileWriter);
        }
    }

    /**
     * 读取缓存
     * @param index 缓存名
     * @return 缓存数据
     */
    private String getCache(int index) {
        //缓存目录
        File cacheDir = UIUtils.getContext().getCacheDir();
        //缓存文件，文件名为url
        File cacheFile = new File(cacheDir, getKey() + "?index=" + index + getParams());
        if (cacheFile.exists()) {
            BufferedReader br = null;
            try {
                br = new BufferedReader(new FileReader(cacheFile));
                String dealLine = br.readLine();
                if (!StringUtils.isEmpty(dealLine)) {
                    if (System.currentTimeMillis() > Long.parseLong(dealLine)) {
                        //缓存失效
                        return null;
                    } else {
                        //读取缓存
                        StringBuffer sb = new StringBuffer();
                        String len;
                        while ((len = br.readLine()) != null) {
                            sb.append(len);
                        }
                        return sb.toString();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                IOUtils.close(br);
            }
        }
        return null;
    }

    /**
     * url请求参数，交由子类去填充
     * @return 请求参数
     */
    public abstract String getParams();

    /**
     * 服务器根目录下的分类目录，包括首页，应用，游戏等，也是交由子类去填充
     * @return 服务器根目录下的分类目录
     */
    public abstract String getKey();

    /**
     * 解析数据
     */
    public abstract T parseData(String json);
}
