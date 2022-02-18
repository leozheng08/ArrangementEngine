package cn.tongdun.kunpeng.api.engine.reload.impl;

import cn.tongdun.kunpeng.api.engine.convertor.impl.PolicyIndexConvertor;
import cn.tongdun.kunpeng.api.engine.dto.IndexDefinitionDTO;
import cn.tongdun.kunpeng.api.engine.model.policyindex.IPolicyIndexRepository;
import cn.tongdun.kunpeng.api.engine.model.policyindex.PolicyIndex;
import cn.tongdun.kunpeng.api.engine.model.policyindex.PolicyIndexCache;
import cn.tongdun.kunpeng.api.engine.reload.IReload;
import cn.tongdun.kunpeng.api.engine.reload.ReloadFactory;
import cn.tongdun.kunpeng.api.engine.reload.dataobject.IndexDefinitionEventDO;
import cn.tongdun.kunpeng.share.utils.TraceUtils;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author: liang.chen
 * @Date: 2019/12/10 下午1:44
 * 策略指标的缓存加载
 */
@Component
public class PolicyIndexReLoadManager implements IReload<IndexDefinitionEventDO> {

    private Logger logger = LoggerFactory.getLogger(PolicyIndexReLoadManager.class);

    @Autowired
    private IPolicyIndexRepository policyIndexRepository;

    @Autowired
    private PolicyIndexCache policyIndexCache;

    @Autowired
    private PolicyFieldReloadManager policyFieldReloadManager;

    @Autowired
    private PolicyIndexConvertor policyIndexConvertor;
    @Autowired
    private ReloadFactory reloadFactory;
    @Autowired
    private PolicyIndicatrixItemReloadManager policyIndicatrixItemReloadManager;

    @PostConstruct
    public void init() {
        reloadFactory.register(IndexDefinitionEventDO.class, this);
    }

    @Override
    public boolean create(IndexDefinitionEventDO eventDO) {
        return addOrUpdate(eventDO);
    }

    @Override
    public boolean update(IndexDefinitionEventDO eventDO) {
        return addOrUpdate(eventDO);
    }

    @Override
    public boolean activate(IndexDefinitionEventDO eventDO) {
        return addOrUpdate(eventDO);
    }

    /**
     * 更新事件类型
     *
     * @return
     */
    public boolean addOrUpdate(IndexDefinitionEventDO eventDO) {
        IndexDefinitionDTO indexDefinitionDTO = policyIndexRepository.queryByUuid(eventDO.getUuid());
        if (indexDefinitionDTO == null) {
            return true;
        }
        return reload(indexDefinitionDTO.getPolicyUuid());
    }

    private boolean reload(String policyUuid) {
        logger.debug(TraceUtils.getFormatTrace() + "PolicyIndex reload start, policyUuid:{}", policyUuid);
        try {
            List<IndexDefinitionDTO> indexDefinitionDTOList = policyIndexRepository.queryByPolicyUuid(policyUuid);
            //缓存策略指标
            if (null != indexDefinitionDTOList && !indexDefinitionDTOList.isEmpty()) {
                List<PolicyIndex> policyIndexList = policyIndexConvertor.convert(indexDefinitionDTOList);
                Map<String,PolicyIndex> policyIndexMap = policyIndexList.stream().collect(Collectors.toMap(PolicyIndex::getUuid,policyIndex -> policyIndex));
                if(MapUtils.isNotEmpty(policyIndexMap)){
                    policyIndexCache.put(policyUuid, policyIndexMap);
                }
            } else {
                policyIndexCache.remove(policyUuid);
            }

            //刷新引用到的平台指标
            policyIndicatrixItemReloadManager.reload(policyUuid);
            //刷新引用到的策略字段
            policyFieldReloadManager.reload(policyUuid);


        } catch (Exception e) {
            logger.error(TraceUtils.getFormatTrace() + "PolicyIndex reload failed, policyUuid:{}", policyUuid, e);
            return false;
        }
        logger.debug(TraceUtils.getFormatTrace() + "PolicyIndex reload success, policyUuid:{}", policyUuid);
        return true;
    }


    /**
     * 删除事件类型
     *
     * @param eventDO
     * @return
     */
    @Override
    public boolean remove(IndexDefinitionEventDO eventDO) {
        IndexDefinitionDTO indexDefinitionDTO = policyIndexRepository.queryByUuid(eventDO.getUuid());
        if (indexDefinitionDTO == null) {
            return true;
        }
        return reload(indexDefinitionDTO.getPolicyUuid());
    }


    /**
     * 关闭状态
     *
     * @param eventDO
     * @return
     */
    @Override
    public boolean deactivate(IndexDefinitionEventDO eventDO) {
        return remove(eventDO);
    }

}
