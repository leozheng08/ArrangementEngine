package cn.tongdun.kunpeng.api.infrastructure.config;

import cn.tongdun.kunpeng.common.Constant;
import cn.tongdun.kunpeng.common.config.IBaseConfig;
import cn.tongdun.kunpeng.share.config.IConfigRepository;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: liang.chen
 * @Date: 2020/2/25 下午8:11
 */
@Component
public class BaseConfig implements IBaseConfig{
    private Logger logger = LoggerFactory.getLogger(BaseConfig.class);

    // 配置内容为json,例：
    // {"credit":["PreFilter","PreCredit","Loan","LoaningQuery"],"anti_fraud":[]}
    @Value("${business.event.type}")
    private String businessEventTypeJson;
    //eventType->businussType
    private Map<String,String> eventType2BusinussMap = new HashMap<>();

    @Autowired
    private IConfigRepository dynamicConfig;

    //根据event_type区分业务类型，如credit信贷，anti_fraud反欺诈
    @Override
    public String getBusinessByEventType(String eventType){
        return getBusinessByEventType(eventType, Constant.BUSINESS_ANTI_FRAUD);
    }

    public String getBusinessByEventType(String eventType,String defaultValue){
        if(eventType2BusinussMap.isEmpty()){
            if(StringUtils.isBlank(businessEventTypeJson)){
                return defaultValue;
            }

            try{
                JSONObject json = JSONObject.parseObject(businessEventTypeJson);
                for(Map.Entry<String, Object> entry:json.entrySet()){
                    JSONArray array = (JSONArray)entry.getValue();
                    if(array == null){
                        continue;
                    }
                    for(Object obj:array){
                        eventType2BusinussMap.put(obj.toString(),entry.getKey());
                    }
                }
            } catch (Exception e){
                logger.error("getBusinessByEventType error",e);
            }
        }

        String businuss = eventType2BusinussMap.get(eventType);
        if(StringUtils.isBlank(businuss)){
            return defaultValue;
        }
        return businuss;

    }
}
