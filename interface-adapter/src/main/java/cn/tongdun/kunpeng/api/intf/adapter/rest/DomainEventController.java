package cn.tongdun.kunpeng.api.intf.adapter.rest;

import cn.tongdun.kunpeng.api.engine.reload.DomainEventHandle;
import cn.tongdun.kunpeng.api.engine.reload.RawDomainEventHandle;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

/**
 * @Author: liang.chen
 * @Date: 2019/8/7 下午7:50
 */

@RestController
@RequestMapping("/manager")
public class DomainEventController {
    private Logger logger = LoggerFactory.getLogger(DomainEventController.class);

    @Autowired
    private RawDomainEventHandle rawDomainEventHandle;




    @RequestMapping(value = "addDomainEvent", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public String addDomainEvent(@RequestBody String domainEvent) {

        rawDomainEventHandle.handleRawMessage( JSONObject.parseObject(domainEvent));
        return "success";
    }
}
