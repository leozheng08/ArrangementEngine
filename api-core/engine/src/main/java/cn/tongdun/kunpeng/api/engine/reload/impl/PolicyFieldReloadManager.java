package cn.tongdun.kunpeng.api.engine.reload.impl;

import cn.tongdun.kunpeng.api.engine.convertor.IConvertor;
import cn.tongdun.kunpeng.api.engine.convertor.IConvertorFactory;
import cn.tongdun.kunpeng.api.engine.dto.PolicyFieldDTO;
import cn.tongdun.kunpeng.api.engine.model.policy.IPolicyRepository;
import cn.tongdun.kunpeng.api.engine.model.policyfield.PolicyField;
import cn.tongdun.kunpeng.api.engine.model.policyfield.PolicyFieldCache;
import cn.tongdun.kunpeng.api.engine.reload.IReload;
import cn.tongdun.kunpeng.api.engine.reload.ReloadFactory;
import cn.tongdun.kunpeng.api.engine.reload.dataobject.PolicyFieldEventDO;
import cn.tongdun.kunpeng.api.engine.reload.dataobject.PolicyIndicatrixItemEventDO;
import cn.tongdun.kunpeng.share.utils.TraceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zeyuan.zheng@tongdun.cn
 * @date 2/17/22 3:09 PM
 */
@Component
public class PolicyFieldReloadManager implements IReload<PolicyFieldEventDO> {

    private Logger logger = LoggerFactory.getLogger(PolicyFieldReloadManager.class);

    @Autowired
    PolicyFieldCache policyFieldCache;

    @Autowired
    IPolicyRepository iPolicyRepository;

    @Autowired
    private ReloadFactory reloadFactory;

    @Autowired
    private IConvertorFactory iConvertorFactory;

    @PostConstruct
    public void init() {
        reloadFactory.register(PolicyFieldEventDO.class, this);
    }

    @Override
    public boolean create(PolicyFieldEventDO policyFieldEventDO) {
        return addOrUpdate(policyFieldEventDO);
    }

    @Override
    public boolean update(PolicyFieldEventDO policyFieldEventDO) {
        return addOrUpdate(policyFieldEventDO);
    }

    @Override
    public boolean activate(PolicyFieldEventDO policyFieldEventDO) {
        return addOrUpdate(policyFieldEventDO);
    }

    /**
     * 更新策略字段
     *
     * @return
     */
    public boolean addOrUpdate(PolicyFieldEventDO policyFieldEventDO) {
        return reload(policyFieldEventDO.getPolicyUuid());
    }

    @Override
    public boolean deactivate(PolicyFieldEventDO policyFieldEventDO) {
        return remove(policyFieldEventDO);
    }

    @Override
    public boolean remove(PolicyFieldEventDO policyFieldEventDO) {
        return reload(policyFieldEventDO.getPolicyUuid());
    }

    public boolean reload(String policyUuid) {
        logger.debug(TraceUtils.getFormatTrace() + "PolicyField reload start, policyUuid:{}, currentTime={}", policyUuid, System.currentTimeMillis());
        try {
            List<PolicyField> policyFieldList = new ArrayList<>();
            List<PolicyFieldDTO> policyFieldDTOList = iPolicyRepository.queryPolicyFieldDTOByPolicyUuid(policyUuid);
            IConvertor<PolicyFieldDTO, PolicyField> fieldIConvertor = iConvertorFactory.getConvertor(PolicyFieldDTO.class);
            if(!CollectionUtils.isEmpty(policyFieldDTOList)){
                for (PolicyFieldDTO policyFieldDTO : policyFieldDTOList) {
                    PolicyField policyField = fieldIConvertor.convert(policyFieldDTO);
                    policyFieldList.add(policyField);
                }
                policyFieldCache.put(policyUuid, policyFieldList);
            }
            logger.debug(TraceUtils.getFormatTrace() + "PolicyField reload end, policyUuid:{}, currentTime={}", policyUuid, System.currentTimeMillis());
        } catch (Exception e) {
            logger.error(TraceUtils.getFormatTrace() + "PolicyField reload failed, policyUuid:{}", policyUuid, e);
            return false;
        }
        logger.debug(TraceUtils.getFormatTrace() + "PolicyField reload success, policyUuid:{}", policyUuid);
        return true;
    }
}
