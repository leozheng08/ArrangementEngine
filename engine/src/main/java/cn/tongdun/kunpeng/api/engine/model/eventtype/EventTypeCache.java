package cn.tongdun.kunpeng.api.engine.model.eventtype;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Author: liang.chen
 * @Date: 2019/12/10 下午1:45
 */
@Component
@Data
public class EventTypeCache {
    private List<EventType> eventTypeList;
}