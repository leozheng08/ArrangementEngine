package cn.tongdun.kunpeng.api.engine.model.rule.function.location;

import cn.fraudmetrix.horde.biz.common.Utils;
import cn.fraudmetrix.horde.biz.entity.IpReputationRulesObj;
import cn.fraudmetrix.module.riskbase.constant.ProxyType;
import cn.fraudmetrix.module.riskbase.service.intf.ProxyIpService;
import cn.fraudmetrix.module.tdrule.context.ExecuteContext;
import cn.fraudmetrix.module.tdrule.function.AbstractFunction;
import cn.fraudmetrix.module.tdrule.function.CalculateResult;
import cn.fraudmetrix.module.tdrule.model.FunctionParam;
import cn.fraudmetrix.module.tdrule.spring.SpringContextHolder;
import cn.tongdun.kunpeng.common.Constant;
import cn.tongdun.kunpeng.common.data.AbstractFraudContext;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class Proxy extends AbstractFunction {
    private static final Logger logger = LoggerFactory.getLogger(Proxy.class);
//[
//  {
//    "name": "proxyIpType",
//    "type": "string",
//    "value": "SOCKS"
//  }
//]

    private String proxyIpType;


    @Override
    public String getName() {
        return Constant.Function.LOCATION_PROXY;
    }

    @Override
    public void parse(List<FunctionParam> list) {
        if (CollectionUtils.isEmpty(list)) {
            return;
        }

        list.forEach(functionParam -> {
            if (StringUtils.equals("proxyIpType", functionParam.getName())) {
                proxyIpType = functionParam.getValue();
            }
        });
    }

    @Override
    public CalculateResult run(ExecuteContext executeContext) {
        AbstractFraudContext context = (AbstractFraudContext) executeContext;
        ProxyIpService proxyIpService = SpringContextHolder.getBean("proxyIpService", ProxyIpService.class);

        String ip = context.getIpAddress();

        if (StringUtils.isNotBlank(ip)) {
            boolean isProxyIp = false;
            boolean switchCfg = true;               // FIXME: 2/7/20 shutter switch
            if (switchCfg) {
                IpReputationRulesObj ipReputationRulesObj = null;       // FIXME: 2/12/20 fix reference
                // IP画像只处理VPN、HTTP、SOCKS三种，为了兼容历史，转一下再调IP画像的方法
                String proxyType = getProxyTypeByProtocol(proxyIpType);
                if (ipReputationRulesObj != null && StringUtils.isNotBlank(proxyType)) {
                    isProxyIp = Utils.isProxy(ipReputationRulesObj.getProxyHistoryObj(), proxyType);
                }
            }
            else {
                ProxyType proxyType = null;
                try {
                    proxyType = ProxyType.valueOf(proxyIpType);
                }
                catch (Exception e) {
                    logger.error("isProxyIp proxyType convert failed proxyIpType {}", proxyIpType, e);
                }
                isProxyIp = proxyIpService.isProxy(ip, proxyType);
            }
            if (isProxyIp) {

                return new CalculateResult(true, null);
            }
        }

        if (StringUtils.equals("HTTP", proxyIpType)) {
            Map<String, Object> deviceInfo = context.getDeviceInfo();
            if (deviceInfo == null) {
                return new CalculateResult(false, null);
            }
            Object isUseHttpProxy = deviceInfo.get("proxyHeaders");
            if (isUseHttpProxy != null && StringUtils.isNotBlank(isUseHttpProxy.toString())) {

                return new CalculateResult(true, null);
            }
        }

        return new CalculateResult(false, null);
    }


    private String getProxyTypeByProtocol(String protocol) {
        if (StringUtils.isBlank(protocol)) {
            return null;
        }
        switch (protocol) {
            case "L2TP":
            case "PPTP":
            case "VPN":
                return "VPN";
            case "SOCK4":
            case "SOCK5":
            case "SOCKS":
                return "SOCKS";
            case "HTTP":
                return "HTTP";
            default:
                return null;
        }
    }


}
