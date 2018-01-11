package cn.skydata.opentsdb.keeper.connect.impl;

import cn.skydata.opentsdb.keeper.connect.SystemClient;
import cn.skydata.opentsdb.keeper.vo.JvmInfo;
import cn.skydata.opentsdb.keeper.vo.RegionClientInfo;
import cn.skydata.opentsdb.keeper.vo.ThreadInfo;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Slf4j
public class SystemClientImpl implements SystemClient {

    private static final String END_POINT_REGION_CLIENTS = "/api/stats/region_clients";
    private static final String END_POINT_THREAD = "/api/stats/threads";
    private static final String END_POINT_JVM = "/api/stats/jvm";

    /**
     * Url for OpenTSDB. e.g. http://192.192.141.40:4242
     */
    private String url;

    public SystemClientImpl(String url) {
        this.url = url;
    }

    @Override
    public Optional<List<RegionClientInfo>> checkRegionClientInfo() {
        String fullPath = url + END_POINT_REGION_CLIENTS;

        Request request = OkHttpClientUtil.buildNonParamGetRequest(fullPath);
        try {
            Response response = OkHttpClientUtil.getInstance().call(request);
            if (response.isSuccessful()) {
                String data = response.body().string();
                response.body().close();
                return Optional.of(JSON.parseArray(data, RegionClientInfo.class));
            }
        } catch (IOException e) {
            log.error("[checkRegionClientInfo] Check Region Client Info for {} fail!", url, e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<List<ThreadInfo>> checkThreadInfo() {
        String fullPath = url + END_POINT_THREAD;

        Request request = OkHttpClientUtil.buildNonParamGetRequest(fullPath);
        try {
            Response response = OkHttpClientUtil.getInstance().call(request);
            if (response.isSuccessful()) {
                String data = response.body().string();
                response.body().close();
                return Optional.of(JSON.parseArray(data, ThreadInfo.class));
            }
        } catch (IOException e) {
            log.error("[checkRegionClientInfo] Check Thread Info for {} fail!", url, e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<JvmInfo> checkJvmInfo() {
        String fullPath = url + END_POINT_JVM;

        Request request = OkHttpClientUtil.buildNonParamGetRequest(fullPath);
        try {
            Response response = OkHttpClientUtil.getInstance().call(request);
            if (response.isSuccessful()) {
                String data = response.body().string();
                response.body().close();
                return Optional.of(JSON.parseObject(data, JvmInfo.class));
            }
        } catch (IOException e) {
            log.error("[checkRegionClientInfo] Check JVM Info for {} fail!", url, e);
        }
        return Optional.empty();
    }
}
