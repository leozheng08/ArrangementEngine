package cn.tongdun.kunpeng.api.intf.adapter.rest;

import cn.tongdun.kunpeng.api.application.RiskService;
import cn.tongdun.kunpeng.api.application.step.ContentRisk;
import cn.tongdun.kunpeng.client.data.IRiskResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @Author: liang.chen
 * @Date: 2019/8/7 下午7:50
 */

@RestController
@RequestMapping("/")
public class RiskServiceController {
    private Logger logger = LoggerFactory.getLogger(RiskServiceController.class);



    @Autowired
    RiskService riskService;



    @RequestMapping(value = {"riskService","riskService/v1.1","antifraudService","antifraudService/v1.1"}, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public String riskService(@RequestHeader Map<String,Object> header, @RequestParam Map<String,Object> request,
                                    HttpServletResponse response) {

        request.putAll(header);
        IRiskResponse riskResponse = riskService.riskService(request);

        response.setHeader("Content-type", "application/json;charset=UTF-8");
        if (StringUtils.isNotBlank(riskResponse.getReasonCode())) {
            // 用于zabbix状态码统计
            response.setHeader("Reason-code", riskResponse.getReasonCode().split(":")[0]);
        } else {
            // 用于zabbix状态码统计
            response.setHeader("Reason-code", "200");
            if (riskResponse.getDecisionType() != null) {
                response.setHeader("Decision-Type", riskResponse.getDecisionType().getIdentity());
            }
        }

        String body = riskResponse.toJsonString();
        return body;
    }

    /**
     * 内容安全精简链路
     * @param header
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = {"contentRiskService","contentRiskService/v1.1"}, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public String contentRiskService(@RequestHeader Map<String,Object> header, @RequestParam Map<String,Object> request,
                              HttpServletResponse response) {

        request.putAll(header);
        IRiskResponse riskResponse = riskService.riskService(request, ContentRisk.NAME);

        response.setHeader("Content-type", "application/json;charset=UTF-8");
        if (StringUtils.isNotBlank(riskResponse.getReasonCode())) {
            // 用于zabbix状态码统计
            response.setHeader("Reason-code", riskResponse.getReasonCode().split(":")[0]);
        } else {
            // 用于zabbix状态码统计
            response.setHeader("Reason-code", "200");
        }

        String body = riskResponse.toJsonString();
        return body;
    }

}
