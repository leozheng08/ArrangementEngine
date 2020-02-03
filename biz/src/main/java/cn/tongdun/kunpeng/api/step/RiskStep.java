package cn.tongdun.kunpeng.api.step;


import cn.tongdun.kunpeng.api.context.FraudContext;
import cn.tongdun.kunpeng.common.data.RiskResponse;
import cn.tongdun.tdframework.core.logger.Logger;
import cn.tongdun.tdframework.core.logger.LoggerFactory;
import cn.tongdun.tdframework.core.pipeline.IStep;

import java.util.Map;


/**
 * API执行步骤
 * @Author: liang.chen
 * @Date: 2019/12/17 下午3:53
 */
public interface RiskStep extends IStep {

    String        PARTNER_CODE = "partner_code";
    String        SECRET_KEY   = "secret_key";
    String        EVENT_ID     = "event_id";

    /**
     * 执行方法
     * 
     * @param context
     * @param response
     * @return 执行通过 true,否则false,false会停止继续执行下面的过滤器
     */
    public boolean invoke(FraudContext context, RiskResponse response, Map<String, String> request);

}
