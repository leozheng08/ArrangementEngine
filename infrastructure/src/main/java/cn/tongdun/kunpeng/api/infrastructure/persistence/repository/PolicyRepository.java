package cn.tongdun.kunpeng.api.infrastructure.persistence.repository;


import cn.tongdun.kunpeng.api.engine.dto.*;
import cn.tongdun.kunpeng.api.engine.model.policy.IPolicyRepository;
import cn.tongdun.kunpeng.api.infrastructure.persistence.mybatis.mappers.kunpeng.*;
import cn.tongdun.kunpeng.share.dataobject.*;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
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
public class PolicyRepository implements IPolicyRepository{

    private Logger logger = LoggerFactory.getLogger(PolicyRepository.class);

    @Autowired
    private PolicyDefinitionDOMapper policyDefinitionDOMapper;
    @Autowired
    private PolicyDOMapper policyDOMapper;
    @Autowired
    private PolicyChallengerDOMapper policyChallengerDOMapper;
    @Autowired
    private SubPolicyRepository subPolicyRepository;
    @Autowired
    private PolicyDecisionModeRepository policyDecisionModeRepository;
    @Autowired
    private PolicyIndicatrixItemDOMapper policyIndicatrixItemDOMapper;
    @Autowired
    private PolicyFieldDOMapper policyFieldDOMapper;
    @Autowired
    private  DecisionFlowRepository decisionFlowRepository;


    //根据合作列表，取得运行版本的策略清单
    @Override
    public List<PolicyModifiedDTO> queryDefaultPolicyByPartners(Set<String> partners){
        return policyDOMapper.selectDefaultPolicyByPartners(partners);
    }


    //根据合作列表，取得挑战者版本的策略清单
    @Override
    public List<PolicyModifiedDTO> queryChallengerPolicyByPartners(Set<String> partners){
        List<PolicyModifiedDTO> result = new ArrayList<>();
        List<String> policyUuidList = new ArrayList<>();
        List<PolicyChallengerDO> policyChallengerDOList = policyChallengerDOMapper.selectAvailableByPartners(partners);
        for(PolicyChallengerDO policyChallengerDO:policyChallengerDOList){
            String config = policyChallengerDO.getConfig();
            if(StringUtils.isBlank(config)){
                continue;
            }
            try{
                //[{"ratio":40.0,"versionUuid":"b225f25f34044a9a9a6929ce12703068"},{"ratio":60.0,"versionUuid":"efa8c9ea504f413caf0634dbab760583"}]
                JSONArray jsonarray = JSONArray.parseArray(config);
                for(Object obj:jsonarray){
                    JSONObject jsonObject = (JSONObject)obj;
                    String versionUuid = jsonObject.getString("versionUuid");
                    if(StringUtils.isNotBlank(versionUuid)){
                        policyUuidList.add(versionUuid);
                    }
                }
            }catch (Exception e){
                logger.error("queryChallengerPolicyByPartners error",e);
            }
        }

        if(policyUuidList.isEmpty()){
            return result;
        }

        return policyDOMapper.selectPolicyByUuids(policyUuidList);
    }

    //查询单个策略的信息，不包含各个子对象
    @Override
    public PolicyDTO queryByUuid(String uuid){
        PolicyDO policyDO = policyDOMapper.selectByUuid(uuid);

        if(policyDO == null){
            return null;
        }

        PolicyDTO policyDTO = new PolicyDTO();
        BeanUtils.copyProperties(policyDO,policyDTO);

        //取得策略定义表中的策略名称、eventId
        PolicyDefinitionDO policyDefinitionDO = policyDefinitionDOMapper.selectByUuid(policyDO.getPolicyDefinitionUuid());
        policyDTO.setName(policyDefinitionDO.getName());
        policyDTO.setEventId(policyDefinitionDO.getEventId());
        policyDTO.setEventType(policyDefinitionDO.getEventType());
        policyDTO.setPartnerCode(policyDefinitionDO.getPartnerCode());

        String policyUuid = policyDTO.getUuid();
        //查询平台指标
        policyDTO.setPolicyIndicatrixItemList(queryPolicyIndicatrixItemDTOByPolicyUuid(policyUuid));
        //查询策略使用到的字段
        policyDTO.setPolicyFieldList(queryPolicyFieldDTOByPolicyUuid(policyUuid));

        return policyDTO;
    }


    //查询单个策略的完整信息，包含各个子对象
    @Override
    public PolicyDTO queryFullByUuid(String uuid){
        PolicyDO policyDO = policyDOMapper.selectByUuid(uuid);

        if(policyDO == null){
            return null;
        }

        PolicyDTO policyDTO = new PolicyDTO();
        BeanUtils.copyProperties(policyDO,policyDTO);

        //取得策略定义表中的策略名称、eventId
        PolicyDefinitionDO policyDefinitionDO = policyDefinitionDOMapper.selectByUuid(policyDO.getPolicyDefinitionUuid());
        policyDTO.setName(policyDefinitionDO.getName());
        policyDTO.setEventId(policyDefinitionDO.getEventId());
        policyDTO.setEventType(policyDefinitionDO.getEventType());
        policyDTO.setPartnerCode(policyDefinitionDO.getPartnerCode());

        String policyUuid = policyDTO.getUuid();
        //查询子策略
        policyDTO.setSubPolicyList(subPolicyRepository.queryFullByPolicyUuid(policyUuid));
        //查询平台指标
        policyDTO.setPolicyIndicatrixItemList(queryPolicyIndicatrixItemDTOByPolicyUuid(policyUuid));
        //查询策略使用到的字段
        policyDTO.setPolicyFieldList(queryPolicyFieldDTOByPolicyUuid(policyUuid));
        //策略运行模式
        policyDTO.setPolicyDecisionModeDTO(policyDecisionModeRepository.queryByPolicyUuid(policyUuid));

        //查询决策流
        policyDTO.setDecisionFlowDTO(decisionFlowRepository.queryByUuid(policyUuid));


        return policyDTO;
    }


    //查询平台指标
    private List<PolicyIndicatrixItemDTO> queryPolicyIndicatrixItemDTOByPolicyUuid(String policyUuid){
        List<PolicyIndicatrixItemDO>  policyIndicatrixItemDOList = policyIndicatrixItemDOMapper.selectByPolicyUuid(policyUuid);
        if(policyIndicatrixItemDOList == null) {
            return null;
        }

        List<PolicyIndicatrixItemDTO> result = null;
        result = policyIndicatrixItemDOList.stream().map(rolicyIndicatrixItemDO->{
            PolicyIndicatrixItemDTO policyIndicatrixItemDTO = new PolicyIndicatrixItemDTO();
            BeanUtils.copyProperties(rolicyIndicatrixItemDO,policyIndicatrixItemDTO);
            return policyIndicatrixItemDTO;
        }).collect(Collectors.toList());

        return result;
    }


    //查询策略使用到的字段
    private List<PolicyFieldDTO> queryPolicyFieldDTOByPolicyUuid(String policyUuid){
        List<PolicyFieldDO>  policyFieldDOList = policyFieldDOMapper.selectByPolicyUuid(policyUuid);
        if(policyFieldDOList == null) {
            return null;
        }

        List<PolicyFieldDTO> result = null;
        result = policyFieldDOList.stream().map(policyFieldDO->{
            PolicyFieldDTO policyFieldDTO = new PolicyFieldDTO();
            BeanUtils.copyProperties(policyFieldDO,policyFieldDTO);
            return policyFieldDTO;
        }).collect(Collectors.toList());

        return result;
    }


}
