package cn.tongdun.kunpeng.api.repository;

import cn.tongdun.kunpeng.api.dao.RuleFieldDao;
import cn.tongdun.kunpeng.api.dataobj.RuleFieldDO;
import cn.tongdun.kunpeng.api.engine.IRuleFieldRepository;
import cn.tongdun.kunpeng.api.engine.RuleField;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: liang.chen
 * @Date: 2019/12/10 下午1:46
 */
@Repository
public class FieldRepository implements IRuleFieldRepository {


    @Autowired
    private RuleFieldDao ruleFieldDao;


    @Override
    public List<RuleField> queryByParams(RuleField field){

        List<RuleFieldDO> list = ruleFieldDao.queryByParams((JSONObject)JSON.toJSON(field));

        List<RuleField> result = null;
        if(list != null){
            result = list.stream().map(ruleFieldDO->{
                RuleField ruleField = new RuleField();
                BeanUtils.copyProperties(ruleFieldDO,ruleField);
                return ruleField;
            }).collect(Collectors.toList());
        }
        return result;

    }
}
