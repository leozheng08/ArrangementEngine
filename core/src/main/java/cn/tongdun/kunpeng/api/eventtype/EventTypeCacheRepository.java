package cn.tongdun.kunpeng.api.eventtype;

import cn.tongdun.kunpeng.api.eventtype.EventType;
import cn.tongdun.kunpeng.api.field.RuleField;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Author: liang.chen
 * @Date: 2019/12/10 下午1:45
 */
@Component
@Data
public class EventTypeCacheRepository {
    private List<EventType> eventTypeList;
}
