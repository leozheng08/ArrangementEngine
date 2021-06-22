package cn.tongdun.kunpeng.api.application.output.ext;

import cn.tongdun.kunpeng.api.common.data.AbstractFraudContext;
import cn.tongdun.kunpeng.api.common.data.BizScenario;
import cn.tongdun.kunpeng.api.common.data.ReasonCode;
import cn.tongdun.kunpeng.api.common.data.SubReasonCode;
import cn.tongdun.kunpeng.client.data.IRiskResponse;
import cn.tongdun.kunpeng.client.data.RiskRequest;
import cn.tongdun.kunpeng.share.json.JSON;
import cn.tongdun.kunpeng.share.utils.TraceUtils;
import cn.tongdun.tdframework.core.extension.Extension;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

/**
 * @author: yuanhang
 * @date: 2021-03-18 09:57
 **/
@Extension(tenant = BizScenario.DEFAULT, business = BizScenario.DEFAULT, partner = BizScenario.DEFAULT)
public class DefaultReasonCodeOutputExt implements IReasonCodeOutputExtPt{

    Logger logger = LoggerFactory.getLogger(DefaultReasonCodeOutputExt.class);

    @Override
    public boolean dealWithSubReasonCodes(AbstractFraudContext context, IRiskResponse response, RiskRequest request) {
        dealWithSubReasonCodes(context, response);
        if (!response.isSuccess() && StringUtils.isBlank(response.getReasonCode())) {
            // false的情况下，如果没有状态码，手动设置500。这种情况通常是有未处理的异常
            response.setReasonCode(ReasonCode.INTERNAL_ERROR.toString());
        }

        String respStr = JSON.toJSONString(response);
        logger.info(TraceUtils.getFormatTrace()+"partner:{}, RESP_F:{}", context.getPartnerCode(), respStr);
        return true;
    }

    public void dealWithSubReasonCodes(AbstractFraudContext context, IRiskResponse response) {
        if (CollectionUtils.isNotEmpty(context.getSubReasonCodes())) {
            Set<String> sub = new HashSet<>();
            Set<String> subReasonCodes = new HashSet<>();
            for (SubReasonCode subReasonCode : context.getSubReasonCodes()) {
                subReasonCodes.add(subReasonCode.getSub_code());
                //404没有对应的策略配置
                if (subReasonCode.getSub_code().startsWith(ReasonCode.POLICY_NOT_EXIST.getCode())) {
                    sub.add(ReasonCode.POLICY_NOT_EXIST.getCode());
                }
                //处理子服务流量不足
                if (subReasonCode.getSub_code().startsWith(ReasonCode.SERVICE_FLOW_ERROR.getCode())) {
                    sub.add(ReasonCode.SERVICE_FLOW_ERROR.getCode());
                }
                //处理内部查无结果
                if (subReasonCode.getSub_code().startsWith(ReasonCode.DATA_NOT_READY.getCode())) {
                    sub.add(ReasonCode.DATA_NOT_READY.getCode());
                }
                //处理内部加密部分数据获取不全
                if (subReasonCode.getSub_code().startsWith(ReasonCode.ENCRYPTION_FIELD_NOT_READY.getCode())) {
                    sub.add(ReasonCode.ENCRYPTION_FIELD_NOT_READY.getCode());
                }
            }

            //reasonCode对外输出有优先级
            if (sub.contains(ReasonCode.POLICY_NOT_EXIST.getCode())) {
                response.setReasonCode(ReasonCode.POLICY_NOT_EXIST.getCode());
            } else if (sub.contains(ReasonCode.SERVICE_FLOW_ERROR.getCode())) {
                response.setReasonCode(ReasonCode.SERVICE_FLOW_ERROR.getCode());
            } else if (sub.contains(ReasonCode.DATA_NOT_READY.getCode())) {
                response.setReasonCode(ReasonCode.DATA_NOT_READY.getCode());
            }else if(sub.contains(ReasonCode.ENCRYPTION_FIELD_NOT_READY.getCode())){
                response.setReasonCode(ReasonCode.ENCRYPTION_FIELD_NOT_READY.getCode());
            }

            if(!subReasonCodes.isEmpty()){
                response.setSubReasonCodes(String.join(",", subReasonCodes));
            }

            logger.info(TraceUtils.getFormatTrace()+"partner:{}, sub_reason_code:{}", context.getPartnerCode(), JSON.toJSONString(context.getSubReasonCodes()));
        }
    }
}
