package cn.tongdun.kunpeng.api.application.output;

import cn.tongdun.kunpeng.api.application.step.IRiskStep;
import cn.tongdun.kunpeng.api.application.step.Risk;
import cn.tongdun.kunpeng.client.data.IRiskResponse;
import cn.tongdun.kunpeng.client.data.RiskRequest;
import cn.tongdun.kunpeng.common.data.AbstractFraudContext;
import cn.tongdun.kunpeng.common.data.ReasonCode;
import cn.tongdun.kunpeng.common.data.SubReasonCode;
import cn.tongdun.tdframework.core.extension.ExtensionExecutor;
import cn.tongdun.tdframework.core.pipeline.Step;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 通用的输出, 填充返回结果到RiskResponse
 * @Author: liang.chen
 * @Date: 2020/2/20 下午5:54
 */
@Component
@Step(pipeline = Risk.NAME,phase = Risk.OUTPUT,order = 1100)
public class ReasonCodeStep implements IRiskStep {

    @Autowired
    private ExtensionExecutor extensionExecutor;

    @Override
    public boolean invoke(AbstractFraudContext context, IRiskResponse response, RiskRequest request){

        dealWithSubReasonCodes(context, response);
        if (!response.isSuccess() && StringUtils.isBlank(response.getReasonCode())) {
            // false的情况下，如果没有状态码，手动设置500。这种情况通常是有未处理的异常
            response.setReasonCode(ReasonCode.INTERNAL_ERROR.toString());
        }

        String respStr = JSON.toJSONString(response, SerializerFeature.DisableCircularReferenceDetect);
        logger.warn("partner:{}, RESP_F:{}", context.getPartnerCode(), respStr);
        return true;
    }

    public void dealWithSubReasonCodes(AbstractFraudContext context, IRiskResponse response) {
        if (CollectionUtils.isNotEmpty(context.getSubReasonCodes())) {
            Set<String> sub = new HashSet<>();
            for (SubReasonCode subReasonCode : context.getSubReasonCodes()) {
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

            if(!sub.isEmpty()){
                response.setSubReasonCodes(String.join(",", sub));
            }

            logger.info("partner:{}, sub_reason_code:{}", context.getPartnerCode(), JSON.toJSONString(context.getSubReasonCodes()));
        }
    }

}
