package cn.tongdun.kunpeng.api.infrastructure.persistence.repository;

import cn.tongdun.kunpeng.api.engine.model.dictionary.Dictionary;
import cn.tongdun.kunpeng.api.engine.model.dictionary.IDictionaryRepository;
import cn.tongdun.kunpeng.api.infrastructure.persistence.dataobj.AdminDictionaryDO;
import cn.tongdun.kunpeng.api.infrastructure.persistence.dataobj.SelectDO;
import cn.tongdun.kunpeng.api.infrastructure.persistence.mybatis.mappers.kunpeng.AdminDictionaryDAO;
import cn.tongdun.kunpeng.share.json.JSON;
import cn.tongdun.kunpeng.share.utils.TraceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cn.tongdun.tdframework.core.pipeline.PipelineExecutor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author: liang.chen
 * @Date: 2019/12/10 下午1:46
 */
@Repository
public class AdminDictionaryRepository implements IDictionaryRepository {

    private Logger logger = LoggerFactory.getLogger(PipelineExecutor.class);

    @Autowired
    private AdminDictionaryDAO adminDictionaryDAO;


    private List<AdminDictionaryDO> queryByParams(Map<String, Object> map) {
        return adminDictionaryDAO.queryByParams(map);
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
            logger.warn(TraceUtils.getFormatTrace() + "字典值json解析出错,value:" + value, e);
        }
        return result;
    }

    @Override
    public List<Dictionary> getDictionary(String key) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("key", key);
        List<AdminDictionaryDO> dictionaryDOList = queryByParams(map);
        if (null == dictionaryDOList || dictionaryDOList.isEmpty()) {
            return Collections.emptyList();
        }
        return dictionaryDOList.stream().map(adminDictionaryDO -> {
            Dictionary dictionary = new Dictionary();
            BeanUtils.copyProperties(adminDictionaryDO, dictionary);
            return dictionary;
        }).collect(Collectors.toList());
    }
}
