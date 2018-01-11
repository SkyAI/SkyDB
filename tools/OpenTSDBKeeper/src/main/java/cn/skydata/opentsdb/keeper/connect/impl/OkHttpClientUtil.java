package cn.skydata.opentsdb.keeper.connect.impl;

import lombok.extern.slf4j.Slf4j;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Slf4j
public class OkHttpClientUtil {
    private OkHttpClient client;

    private static OkHttpClientUtil okHttpClientUtil;

    public synchronized static OkHttpClientUtil getInstance() {
        if (okHttpClientUtil == null)
            okHttpClientUtil = new OkHttpClientUtil();
        return okHttpClientUtil;
    }

    private OkHttpClientUtil() {
        client = new OkHttpClient.Builder()
                .connectionPool(new ConnectionPool(10, 10, TimeUnit.MINUTES))
                .retryOnConnectionFailure(true)
                .connectTimeout(1, TimeUnit.SECONDS)
                .readTimeout(1, TimeUnit.SECONDS)
                .build();
    }

    /**
     * Make an sync call
     *
     * @throws IOException
     */
    public Response call(Request request) throws IOException {
        Response response = client.newCall(request).execute();
        return response;
    }

    /**
     * Build a Get Request with no param
     *
     * @param url request url
     */
    public static Request buildNonParamGetRequest(String url) {
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        return request;
    }
}
