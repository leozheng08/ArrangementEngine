package cn.tongdun.kunpeng.api.infrastructure.persistence.repository;


import cn.tongdun.kunpeng.api.engine.dto.*;
import cn.tongdun.kunpeng.api.engine.model.policy.IPolicyRepository;
import cn.tongdun.kunpeng.api.infrastructure.persistence.mybatis.mappers.kunpeng.*;
import cn.tongdun.kunpeng.share.dataobject.*;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Author: liang.chen
 * @Date: 2019/12/18 上午11:42
 */
@Repository
public class DecisionFlowRepository implements IPolicyRepository{

    private Logger logger = LoggerFactory.getLogger(DecisionFlowRepository.class);

    @Autowired
    private PolicyDecisionModeDOMapper policyDecisionModeDOMapper;

    /**
     * 查询策略当前在用的运行模式
     */
    @Override
    public PolicyDecisionModeDTO queryByPolicyUuid(String policyUuid){
        PolicyDecisionModeDO policyDecisionModeDO = policyDecisionModeDOMapper.selectByPolicyUuid(policyUuid);
        if(policyDecisionModeDO == null) {
            return null;
        }

        PolicyDecisionModeDTO policyDecisionModeDTO = new PolicyDecisionModeDTO();
        BeanUtils.copyProperties(policyDecisionModeDO,policyDecisionModeDTO);

        return policyDecisionModeDTO;
    }


}
