package cn.tongdun.kunpeng.api.intf.adapter.rest;

import cn.tongdun.kunpeng.api.basedata.service.cardbin.AerospikeService;
import cn.tongdun.kunpeng.api.basedata.service.cardbin.CardBinService;
import cn.tongdun.kunpeng.api.engine.model.rule.function.namelist.CustomListValueCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: liang.chen
 * @Date: 2019/8/7 下午7:50
 */

@RestController
@RequestMapping("/manager_god")
public class GodController {
    private Logger logger = LoggerFactory.getLogger(GodController.class);

    @Autowired
    private AerospikeService aerospikeService;

    @Autowired
    private CardBinService cardBinService;

    @Autowired
    private CustomListValueCache customListValueCache;



    @GetMapping(value = "cardbin_query", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public Object cardBinQuery(String cardbin) {

        Map<String, Object> resultMap = cardBinService.getRawCardBinInfo(cardbin);
        return resultMap;
    }

    @GetMapping(value = "custom_list_cache", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public Object customListCache(String listUuid, String value) {
        Map<String, Object> resultMap = new HashMap<>(2);
        List<String> valueList = customListValueCache.get(listUuid);
        double score = customListValueCache.getZsetScore(listUuid, value);
        resultMap.put("valueList", valueList);
        resultMap.put("score", score);
        return resultMap;
    }
}
