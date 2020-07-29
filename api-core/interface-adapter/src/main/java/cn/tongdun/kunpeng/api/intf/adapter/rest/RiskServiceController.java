package cn.tongdun.kunpeng.api.intf.adapter.rest;

import cn.tongdun.kunpeng.api.application.RiskService;
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
    public String riskService(@RequestHeader Map<String,String> header, @RequestParam Map<String,String> request,
                                    HttpServletResponse response) {

        request.putAll(header);
        IRiskResponse riskResponse = riskService.riskService(request);

        response.setHeader("Content-type", "application/json;charset=UTF-8");
        if (StringUtils.isNotBlank(riskResponse.getReasonCode())) {
            response.setHeader("Reason-code", riskResponse.getReasonCode().split(":")[0]); // 用于zabbix状态码统计
        } else {
            response.setHeader("Reason-code", "200"); // 用于zabbix状态码统计
        }

        String body = riskResponse.toJsonString();
        return body;
    }

}
