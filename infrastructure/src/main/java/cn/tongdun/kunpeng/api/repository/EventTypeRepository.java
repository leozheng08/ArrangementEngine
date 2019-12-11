package cn.tongdun.kunpeng.api.repository;

import cn.tongdun.kunpeng.api.dao.AdminDictionaryDao;
import cn.tongdun.kunpeng.api.dao.RuleFieldDao;
import cn.tongdun.kunpeng.api.dataobj.RuleFieldDO;
import cn.tongdun.kunpeng.api.dataobj.SelectDO;
import cn.tongdun.kunpeng.api.eventtype.EventType;
import cn.tongdun.kunpeng.api.eventtype.IEventTypeRepository;
import cn.tongdun.kunpeng.api.field.IRuleFieldRepository;
import cn.tongdun.kunpeng.api.field.RuleField;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: liang.chen
 * @Date: 2019/12/10 下午1:46
 */
@Repository
public class EventTypeRepository implements IEventTypeRepository {


    @Autowired
    private AdminDictionaryRepository adminDictionaryRepository;


    @Override
    public List<EventType> queryAll(){

        List<SelectDO> allEventTypes = adminDictionaryRepository.getSelectList("EventType");

        List<EventType> resultEventTypeList = new ArrayList<EventType>();
        for (SelectDO et : allEventTypes) {
            EventType eventType = new EventType();
            eventType.setName(et.getName());
            eventType.setDisplayName(et.getDName());
            resultEventTypeList.add(eventType);
        }

        return resultEventTypeList;

    }
}
