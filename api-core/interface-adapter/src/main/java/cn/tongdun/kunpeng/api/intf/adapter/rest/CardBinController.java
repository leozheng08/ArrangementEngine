package cn.tongdun.kunpeng.api.intf.adapter.rest;

import cn.tongdun.kunpeng.api.basedata.service.cardbin.CardBinService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @Author: liang.chen
 * @Date: 2019/8/7 下午7:50
 */

@RestController
@RequestMapping("/manager")
public class CardBinController {
    private Logger logger = LoggerFactory.getLogger(CardBinController.class);

    @Autowired
    private CardBinService cardBinService;



    @GetMapping(value = "cardbin_query", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public Object cardBinQuery(String cardbin) {

        Map<String, Object> resultMap = cardBinService.getRawCardBinInfo(cardbin);
        return resultMap;
    }
}
