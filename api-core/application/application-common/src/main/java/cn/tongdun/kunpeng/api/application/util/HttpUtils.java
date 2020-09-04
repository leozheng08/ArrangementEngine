package cn.tongdun.kunpeng.api.application.util;

import cn.tongdun.kunpeng.share.utils.TraceUtils;
import okhttp3.*;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author yuanhang
 * @date 05/27/2020
 */
public class HttpUtils {

    private static Logger logger = LoggerFactory.getLogger(HttpUtils.class);

    private static final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(Duration.ofMillis(150))
            .writeTimeout(Duration.ofMillis(150))
            .readTimeout(Duration.ofMillis(150))
            .connectionPool(new ConnectionPool(200, 30, TimeUnit.SECONDS))
            .build();

    /**
     * 多个请求异步访问http服务
     *
     * @param requests
     * @param results
     * @return
     */
    public static Map postAsyncJson(List<Request> requests, Map<Request, Object> results) {
        try {
            if (CollectionUtils.isNotEmpty(requests)) {
                CountDownLatch latch = new CountDownLatch(requests.size());
                requests.forEach(r -> {
                    Call call = client.newCall(r);
                    call.enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            results.put(r, e);
                            latch.countDown();
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            results.put(r, response.body().string());
                            latch.countDown();
                        }
                    });
                });
                latch.await();
            }
        } catch (Exception e) {
            logger.error(TraceUtils.getFormatTrace() + "mail model http request raise exception :{}", e);
            results.put(requests.get(0), e);
        }

        return results;
    }

    /**
     * 串行访问接口
     *
     * @param requests
     * @param results
     * @return
     * @throws Exception
     */
    public static Map postJson(List<Request> requests, Map<Request, Object> results) throws Exception {
        for (int var0 = 0; var0 < requests.size(); var0++) {
            try {
                Response response = client.newCall(requests.get(var0)).execute();
                results.put(requests.get(var0), response.body().string());
            }catch (Exception e) {
                results.put(requests.get(var0), e);
                throw e;
            }
        }
        return results;
    }



}
