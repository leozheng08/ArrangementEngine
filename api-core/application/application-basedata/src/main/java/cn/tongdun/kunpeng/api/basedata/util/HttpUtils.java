package cn.tongdun.kunpeng.api.basedata.util;

import lombok.SneakyThrows;
import okhttp3.*;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author yuanhang
 * @date 05/27/2020
 */
public class HttpUtils {

    private static final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(Duration.ofMillis(100))
            .writeTimeout(Duration.ofMillis(100))
            .readTimeout(Duration.ofMillis(100))
            .connectionPool(new ConnectionPool(100, 10, TimeUnit.SECONDS))
            .build();

    /**
     * 多个请求异步访问http服务
     *
     * @param results
     * @param requests
     * @return
     */
    public static Map postAsyncJson(List<Request> requests, Map<Request, Object> results) throws Exception {
        requests.forEach(r -> {
            Call call = client.newCall(r);
            call.enqueue(new Callback() {
                @SneakyThrows
                @Override
                public void onFailure(Call call, IOException e) {
                    call.timeout().throwIfReached();
                    results.put(r, e);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    results.put(r, response);
                }
            });
        });
        return results;
    }

    /**
     * 串行访问接口
     * // TODO 单个请求异常处理逻辑
     * @param requests
     * @param results
     * @return
     * @throws Exception
     */
    public static Map postJson(List<Request> requests, Map<Request, Object> results) throws Exception {
        for (int var0 = 0; var0 < requests.size(); var0++) {
            Response response = client.newCall(requests.get(var0)).execute();
            results.put(requests.get(var0), response);
        }
        return results;
    }

}
