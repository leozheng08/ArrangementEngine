package cn.tongdun.kunpeng.api.intf.adapter.rest;

import cn.tongdun.kunpeng.api.engine.model.rule.function.namelist.CustomListValueManager;
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
@RequestMapping("/manager")
public class CustomListController {
    private Logger logger = LoggerFactory.getLogger(CustomListController.class);

    @Autowired
    CustomListValueManager customListValueManager;


    @RequestMapping(value = "loadDataToRedis", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public String loadDataToRedis(@RequestHeader Map<String,String> header, @RequestParam Map<String,String> request,
                                    HttpServletResponse response) {

        customListValueManager.loadDataToRedis();
        return "";
    }
}
