package cn.tongdun.kunpeng.api.intf.adapter.rest;

import cn.tongdun.kunpeng.api.application.RiskService;
import cn.tongdun.kunpeng.common.data.RiskResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @Author: liang.chen
 * @Date: 2019/8/7 下午7:50
 */

@RestController
@RequestMapping("/")
public class RiskServiceController {

    @Autowired
    RiskService riskService;

    @RequestMapping(value = "riskService", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public RiskResponse riskService(@RequestHeader Map<String,String> header, @RequestParam Map<String,String> request) {

        request.putAll(header);

        return riskService.riskService(request);
    }

}
