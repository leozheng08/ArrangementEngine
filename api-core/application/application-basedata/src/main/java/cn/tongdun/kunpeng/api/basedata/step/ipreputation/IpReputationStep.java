package cn.tongdun.kunpeng.api.basedata.step.ipreputation;

import cn.fraudmetrix.forseti.global.util.LogUtil;
import cn.fraudmetrix.horde.biz.entity.IpReputationRulesObj;
import cn.fraudmetrix.horde.biz.intf.IpReputationService;
import cn.tongdun.kunpeng.api.application.step.IRiskStep;
import cn.tongdun.kunpeng.api.application.step.Risk;
import cn.tongdun.kunpeng.api.basedata.BasedataConstant;
import cn.tongdun.kunpeng.api.common.data.ReasonCode;
import cn.tongdun.kunpeng.api.common.util.ReasonCodeUtil;
import cn.tongdun.kunpeng.client.data.IRiskResponse;
import cn.tongdun.kunpeng.client.data.RiskRequest;
import cn.tongdun.kunpeng.api.common.data.AbstractFraudContext;
import cn.tongdun.tdframework.core.extension.ExtensionExecutor;
import cn.tongdun.tdframework.core.pipeline.Step;
import com.codahale.metrics.Timer;
import io.micrometer.core.instrument.Measurement;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

/**
 * ip画像信息获取
 * @Author: liang.chen
 * @Date: 2020/2/10 下午2:19
 */
@Component
@Step(pipeline = Risk.NAME,phase = Risk.BASIC_DATA)
public class IpReputationStep implements IRiskStep{

    @Autowired
    private ExtensionExecutor extensionExecutor;

    private static final Pattern regexIpAddressv6v4 = Pattern.compile("((25[0-5]|2[0-4]\\d|1\\d\\d|[0-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[0-9]?\\d)){3})|((([0-9A-Fa-f]{1,4}:){7}([0-9A-Fa-f]{1,4}|:))|(([0-9A-Fa-f]{1,4}:){6}(:[0-9A-Fa-f]{1,4}|((25[0-5]|2[0-4]\\d|1\\d\\d|[0-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[0-9]?\\d)){3})|:))|(([0-9A-Fa-f]{1,4}:){5}(((:[0-9A-Fa-f]{1,4}){1,2})|:((25[0-5]|2[0-4]\\d|1\\d\\d|[0-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[0-9]?\\d)){3})|:))|(([0-9A-Fa-f]{1,4}:){4}(((:[0-9A-Fa-f]{1,4}){1,3})|((:[0-9A-Fa-f]{1,4})?:((25[0-5]|2[0-4]\\d|1\\d\\d|[0-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[0-9]?\\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){3}(((:[0-9A-Fa-f]{1,4}){1,4})|((:[0-9A-Fa-f]{1,4}){0,2}:((25[0-5]|2[0-4]\\d|1\\d\\d|[0-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[0-9]?\\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){2}(((:[0-9A-Fa-f]{1,4}){1,5})|((:[0-9A-Fa-f]{1,4}){0,3}:((25[0-5]|2[0-4]\\d|1\\d\\d|[0-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[0-9]?\\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){1}(((:[0-9A-Fa-f]{1,4}){1,6})|((:[0-9A-Fa-f]{1,4}){0,4}:((25[0-5]|2[0-4]\\d|1\\d\\d|[0-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[0-9]?\\d)){3}))|:))|(:(((:[0-9A-Fa-f]{1,4}){1,7})|((:[0-9A-Fa-f]{1,4}){0,5}:((25[0-5]|2[0-4]\\d|1\\d\\d|[0-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[0-9]?\\d)){3}))|:)))(%.+)?");

    @Autowired
    IpReputationService ipReputationService;

    @Override
    public boolean invoke(AbstractFraudContext context, IRiskResponse response, RiskRequest request){

        //调用Ip画像之前，调用参数的组织

        //调用Ip画像

        //Ip画像信息处理

        String ip = (String) context.get("ipAddress");
        if (StringUtils.isNotBlank(ip)) {
            int index = ip.indexOf(",");// 兼容用户使用代理的情形
            if (index > 0) {
                ip = ip.substring(0, index).trim();
            }
            try {
                boolean isIP = isIPv6v4(ip);
                if (isIP) {

                    IpReputationRulesObj rulesObj = null;
                    try {
                        rulesObj = ipReputationService.getIpInfosRulesObj2(ip);
                    } catch (Exception e) {
                        if (ReasonCodeUtil.isTimeout(e)) {
                            ReasonCodeUtil.add(context, ReasonCode.IP_REPUTATION_CALL_TIMEOUT, "IPReputation");
                        } else {
                            ReasonCodeUtil.add(context, ReasonCode.IP_REPUTATION_CALL_ERROR, "IPReputation");
                        }

                        logger.warn("IP画像:{},IP画像查询失败:{},message:{}", ip, e, e.getMessage());
                    }
                    if (null != rulesObj) {
                        context.addExternalObj(BasedataConstant.EXTERNAL_OBJ_IP_REPUTATION, rulesObj);
                    }
                }
            } catch (Exception e) {

                logger.info("IP画像:{},获取IP得分时发生异常, 可能是不是IPV4格式,error:{}", ip, e.getMessage());
            }
        }
        return true;
    }

    public static boolean isIPv6v4(String ip) {
        if (ip != null) {
            ip = ip.trim();
            ip = transIpToStandard(ip);
            return regexIpAddressv6v4.matcher(ip).matches();
        } else {
            return false;
        }

    }

    /**
     * IP转换成标准的,如把001.000.04.01 转成 1.0.4.1
     *
     * @param ip
     * @return
     */
    private static String transIpToStandard(String ip) {
        String[] split = ip.split("\\.");
        StringBuilder sb = null;
        if (split != null && split.length > 0) {
            sb = new StringBuilder();
            for (String s : split) {
                try {
                    sb.append(Integer.parseInt(s)).append(".");
                } catch (Exception e) {
                    sb.append(s).append(".");
                }
            }
        }
        if (sb != null) {
            sb.deleteCharAt(sb.length() - 1);
            return sb.toString();
        } else {
            return ip;
        }
    }

}
