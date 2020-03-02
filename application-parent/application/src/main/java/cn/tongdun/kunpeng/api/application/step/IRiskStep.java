package cn.tongdun.kunpeng.api.application.step;


import cn.tongdun.kunpeng.client.data.IRiskResponse;
import cn.tongdun.kunpeng.client.data.RiskResponse;
import cn.tongdun.kunpeng.common.data.AbstractFraudContext;
import cn.tongdun.tdframework.core.pipeline.IStep;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;


/**
 * API执行步骤
 *
 * @Author: liang.chen
 * @Date: 2019/12/17 下午3:53
 */
public interface IRiskStep extends IStep {
    Logger logger = LoggerFactory.getLogger(IRiskStep.class);

    String PARTNER_CODE = "partner_code";
    String SECRET_KEY = "secret_key";
    String EVENT_ID = "event_id";

    /**
     * 执行方法
     *
     * @param context
     * @param response
     * @return 执行通过 true,否则false,false会停止继续执行下面的过滤器
     */
    public boolean invoke(AbstractFraudContext context, IRiskResponse response, Map<String, String> request);


    @Override
    default void errorHandle(Throwable e) {
        logger.error("riskStep error", e);
    }
}
