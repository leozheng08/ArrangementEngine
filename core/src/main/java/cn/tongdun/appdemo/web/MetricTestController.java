package cn.tongdun.appdemo.web;

import cn.tongdun.appdemo.biz.MetricsDemo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class MetricTestController {

    @Autowired
    private MetricsDemo metricsDemo;

    @RequestMapping("/metricTest")
    @ResponseBody
    String metricTest(
            @RequestParam(value = "method", required = false, defaultValue = "") String method,
            @RequestParam(value = "partner_code", required = false, defaultValue = "-") String partnerCode
    ) {
        switch (method) {
            case "execute1":
                metricsDemo.execute1();
            case "execute2":
                metricsDemo.execute2();
            case "execute3":
                metricsDemo.execute3(partnerCode);
            case "execute4":
                metricsDemo.execute4(partnerCode);
            default:
                metricsDemo.execute1();
        }
        return "ok";
    }
}
