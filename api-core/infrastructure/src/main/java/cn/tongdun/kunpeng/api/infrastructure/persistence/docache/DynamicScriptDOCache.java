package cn.tongdun.kunpeng.api.infrastructure.persistence.docache;

import cn.tongdun.kunpeng.api.engine.model.constant.CommonStatusEnum;
import cn.tongdun.kunpeng.api.engine.reload.docache.AbstractDataObjectCache;
import cn.tongdun.kunpeng.api.infrastructure.persistence.mybatis.mappers.kunpeng.DynamicScriptDAO;
import cn.tongdun.kunpeng.share.dataobject.DynamicScriptDO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Author: liang.chen
 * @Date: 2020/4/26 下午2:34
 */
@Component
public class DynamicScriptDOCache extends AbstractDataObjectCache<DynamicScriptDO> {

    @Autowired
    private DynamicScriptDAO dynamicScriptDAO;


    @Override
    public void refresh(String uuid) {
        DynamicScriptDO dynamicScriptDO = dynamicScriptDAO.selectByUuid(uuid);
        set(dynamicScriptDO);
    }

    @Override
    public void refreshAll() {
        List<DynamicScriptDO> list = dynamicScriptDAO.selectAll();
        if (list == null || list.isEmpty()) {
            return;
        }
        list.forEach(dataObject -> {
            set(dataObject);
        });
    }

    /**
     * 添加索引
     *
     * @param dataObject
     */
    @Override
    public void setByIdx(DynamicScriptDO dataObject) {
        //添加合作方索引. 索引按zadd(idex_name,分数固定为0,索引值:uuid)方式添加，通过zrangebylex来按索引查询
        //TODO 去掉合作方 --刘佩
//        scoreKVRepository.zadd(cacheKey+"_partner",0,dataObject.getPartnerCode()+":"+dataObject.getUuid());

    }

    /**
     * 删除索引
     *
     * @param dataObject
     */
    @Override
    public void removeIdx(DynamicScriptDO dataObject) {
        //TODO 去掉合作方 --刘佩
//        scoreKVRepository.zrem(cacheKey + "_partner", dataObject.getPartnerCode() + ":" + dataObject.getUuid());
    }


    @Override
    public boolean isValid(DynamicScriptDO dataObject) {
        if (dataObject.getStatus() != null && dataObject.getStatus().equals(CommonStatusEnum.CLOSE.getCode())) {
            return false;
        }
        if (dataObject.isDeleted()) {
            return false;
        }

        return true;
    }
}
