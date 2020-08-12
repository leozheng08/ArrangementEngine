package cn.tongdun.kunpeng.api.basedata.step.geoip;

import cn.fraudmetrix.module.riskbase.geoip.GeoipEntity;
import cn.tongdun.kunpeng.api.application.step.IRiskStep;
import cn.tongdun.kunpeng.api.application.step.Risk;
import cn.tongdun.kunpeng.api.basedata.BasedataConstant;
import cn.tongdun.kunpeng.api.basedata.service.GeoIpServiceExtPt;
import cn.tongdun.kunpeng.api.common.data.AbstractFraudContext;
import cn.tongdun.kunpeng.api.common.data.ReasonCode;
import cn.tongdun.kunpeng.api.common.util.ReasonCodeUtil;
import cn.tongdun.kunpeng.client.data.IRiskResponse;
import cn.tongdun.kunpeng.client.data.RiskRequest;
import cn.tongdun.kunpeng.share.json.JSON;
import cn.tongdun.kunpeng.share.utils.TraceUtils;
import cn.tongdun.tdframework.core.extension.ExtensionExecutor;
import cn.tongdun.tdframework.core.pipeline.Step;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * geoIp信息获取
 *
 * @Author: liang.chen
 * @Date: 2020/2/10 下午2:19
 */
@Component
@Step(pipeline = Risk.NAME, phase = Risk.BASIC_DATA)
public class GeoIpStep implements IRiskStep {

    @Autowired
    private ExtensionExecutor extensionExecutor;

    @Override
    public boolean invoke(AbstractFraudContext context, IRiskResponse response, RiskRequest request) {

        String ip = context.getIpAddress();
        if (StringUtils.isNotBlank(ip)) {
            // 兼容用户使用代理的情形
            int index = ip.indexOf(",");
            if (index > 0) {
                ip = ip.substring(0, index).trim();
            }
            GeoipEntity geoip = null;
            try {
                final String finalIp = ip;
                geoip = extensionExecutor.execute(GeoIpServiceExtPt.class, context.getBizScenario(), extension -> extension.getIpInfo(finalIp));
            } catch (Exception e) {
                ReasonCodeUtil.add(context, ReasonCode.GEOIP_SERVICE_CALL_ERROR, "geoip");
                logger.error(TraceUtils.getFormatTrace() + "GeoIp query error!ip:" + ip, e);
            }
            if (null != geoip) {
                context.addExternalObj(BasedataConstant.EXTERNAL_OBJ_GEOIP_ENTITY, geoip);
            }

            String[] ipSegs = ip.split("\\.");
            if (ipSegs.length >= 3) {
                String ip3 = ipSegs[0] + "." + ipSegs[1] + "." + ipSegs[2];
                context.set("ip3", ip3);
            }
            request.getFieldValues().put("geoIp_response", JSON.toJSONString(geoip));
        }

        return true;
    }

}
