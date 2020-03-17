package cn.tongdun.kunpeng.api.engine.reload.impl;

import cn.tongdun.kunpeng.api.engine.convertor.impl.PolicyIndexConvertor;
import cn.tongdun.kunpeng.api.engine.dto.IndexDefinitionDTO;
import cn.tongdun.kunpeng.api.engine.model.policyindex.IPolicyIndexRepository;
import cn.tongdun.kunpeng.api.engine.model.policyindex.PolicyIndex;
import cn.tongdun.kunpeng.api.engine.model.policyindex.PolicyIndexCache;
import cn.tongdun.kunpeng.api.engine.reload.IReload;
import cn.tongdun.kunpeng.api.engine.reload.ReloadFactory;
import cn.tongdun.kunpeng.share.dataobject.IndexDefinitionDO;
import cn.tongdun.kunpeng.share.dataobject.PolicyDecisionModeDO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * @Author: liang.chen
 * @Date: 2019/12/10 下午1:44
 */
@Component
public class PolicyIndexReLoadManager  implements IReload<IndexDefinitionDO> {

    private Logger logger = LoggerFactory.getLogger(PolicyIndexReLoadManager.class);

    @Autowired
    private IPolicyIndexRepository policyIndexRepository;

    @Autowired
    private PolicyIndexCache policyIndexCache;

    @Autowired
    private PolicyIndexConvertor policyIndexConvertor;
    @Autowired
    private ReloadFactory reloadFactory;

    @PostConstruct
    public void init(){
        reloadFactory.register(IndexDefinitionDO.class,this);
    }

    /**
     * 更新事件类型
     * @return
     */
    @Override
    public boolean addOrUpdate(IndexDefinitionDO indexDefinitionDO){
        return reload(indexDefinitionDO.getPolicyUuid());
    }

    private boolean reload(String policyUuid){
        logger.debug("PolicyIndexReLoadManager start, policyUuid:{}",policyUuid);
        try {
            List<IndexDefinitionDTO> indexDefinitionDTOList = policyIndexRepository.queryByPolicyUuid(policyUuid);
            //缓存策略指标
            if (null!= indexDefinitionDTOList && !indexDefinitionDTOList.isEmpty()){
                List<PolicyIndex> policyIndexList=policyIndexConvertor.convert(indexDefinitionDTOList);
                if (null!=policyIndexList && !policyIndexList.isEmpty()){
                    policyIndexCache.putList(policyUuid,policyIndexList);
                }
            } else {
                policyIndexCache.removeList(policyUuid);
            }

        } catch (Exception e){
            logger.error("PolicyIndexReLoadManager failed, policyUuid:{}",policyUuid,e);
            return false;
        }
        logger.debug("PolicyIndexReLoadManager success, policyUuid:{}",policyUuid);
        return true;
    }


    /**
     * 删除事件类型
     * @param indexDefinitionDO
     * @return
     */
    @Override
    public boolean remove(IndexDefinitionDO indexDefinitionDO){
        return reload(indexDefinitionDO.getPolicyUuid());
    }


    /**
     * 关闭状态
     * @param indexDefinitionDO
     * @return
     */
    @Override
    public boolean deactivate(IndexDefinitionDO indexDefinitionDO){
        return remove(indexDefinitionDO);
    }

}
