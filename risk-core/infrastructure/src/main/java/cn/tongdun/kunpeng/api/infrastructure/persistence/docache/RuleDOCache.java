package cn.tongdun.kunpeng.api.infrastructure.persistence.docache;

import cn.tongdun.kunpeng.api.engine.reload.docache.AbstractDataObjectCache;
import cn.tongdun.kunpeng.api.infrastructure.persistence.mybatis.mappers.kunpeng.PolicyDAO;
import cn.tongdun.kunpeng.api.infrastructure.persistence.mybatis.mappers.kunpeng.RuleDAO;
import cn.tongdun.kunpeng.share.dataobject.PolicyDO;
import cn.tongdun.kunpeng.share.dataobject.PolicyIndicatrixItemDO;
import cn.tongdun.kunpeng.share.dataobject.RuleDO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Author: liang.chen
 * @Date: 2020/4/26 下午2:34
 */
@Component
public class RuleDOCache extends AbstractDataObjectCache<RuleDO> {

    @Autowired
    private RuleDAO ruleDAO;


    @Override
    public void refresh(String uuid) {
        RuleDO ruleDO = ruleDAO.selectByUuid(uuid);
        set(ruleDO);
    }

    @Override
    public void refreshAll() {
        List<RuleDO> list = ruleDAO.selectAll();
        if(list == null || list.isEmpty()){
            return;
        }
        list.forEach(dataObject ->{
            set(dataObject);
        });
    }

    /**
     * 添加索引
     * @param dataObject
     */
    @Override
    public void setByIdx(RuleDO dataObject){
        //添加合作方索引. 索引按zadd(idex_name,分数固定为0,索引值:uuid)方式添加，通过zrangebylex来按索引查询
        scoreKVRepository.zadd(cacheKey+"_bizType_bizUuid",0,
                StringUtils.join(dataObject.getBizType(),".",dataObject.getBizUuid(),":",dataObject.getUuid()));
    }

    /**
     * 删除索引
     * @param dataObject
     */
    @Override
    public void removeIdx(RuleDO dataObject){
        scoreKVRepository.zrem(cacheKey+"_bizType_bizUuid",
                StringUtils.join(dataObject.getBizType(),".",dataObject.getBizUuid(),":",dataObject.getUuid()));
    }
}
