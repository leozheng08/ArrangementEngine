package cn.tongdun.kunpeng.api.repository;

import cn.tongdun.kunpeng.api.dao.AdminDictionaryDao;
import cn.tongdun.kunpeng.api.dataobj.AdminDictionaryDO;
import cn.tongdun.kunpeng.api.dataobj.RuleFieldDO;
import cn.tongdun.kunpeng.api.dataobj.SelectDO;
import cn.tongdun.kunpeng.api.eventtype.EventType;
import cn.tongdun.kunpeng.api.eventtype.IEventTypeRepository;
import cn.tongdun.kunpeng.api.field.RuleField;
import cn.tongdun.tdframework.core.logger.Logger;
import cn.tongdun.tdframework.core.logger.LoggerFactory;
import cn.tongdun.tdframework.core.pipeline.PipelineExecutor;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author: liang.chen
 * @Date: 2019/12/10 下午1:46
 */
@Repository
public class AdminDictionaryRepository  {

    private Logger logger = LoggerFactory.getLogger(PipelineExecutor.class);

    @Autowired
    private AdminDictionaryDao adminDictionaryDao;



    public List<AdminDictionaryDO> queryByParams(Map<String, Object> map) {
        return adminDictionaryDao.queryByParams(map);
    }

    public List<SelectDO> getSelectList(String key) {
        List<SelectDO> result = Collections.emptyList();
        if (StringUtils.isBlank(key) || StringUtils.equals("All", key)) {
            return result;
        }
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("key", key);
        List<AdminDictionaryDO> list = queryByParams(map);
        if (list.isEmpty()) {
            return result;
        }

        String value = list.get(0).getValue();
        try {
            result = JSON.parseArray(value, SelectDO.class);
        } catch (Exception e) {
            logger.warn("字典值json解析出错,value:"+value,e);
        }
        return result;
    }
}