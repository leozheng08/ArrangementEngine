package cn.tongdun.kunpeng.api.infrastructure.persistence.repository;


import cn.tongdun.kunpeng.api.engine.dto.*;
import cn.tongdun.kunpeng.api.engine.model.policy.IPolicyRepository;
import cn.tongdun.kunpeng.api.infrastructure.persistence.mybatis.mappers.kunpeng.*;
import cn.tongdun.kunpeng.api.common.util.JsonUtil;
import cn.tongdun.kunpeng.share.dataobject.*;
import cn.tongdun.kunpeng.share.json.JSON;
import cn.tongdun.kunpeng.share.utils.TraceUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author: liang.chen
 * @Date: 2019/12/18 上午11:42
 */
@Repository
public class PolicyRepository implements IPolicyRepository{

    private Logger logger = LoggerFactory.getLogger(PolicyRepository.class);

    @Autowired
    private PolicyDefinitionDAO policyDefinitionDAO;
    @Autowired
    private PolicyDAO policyDAO;
    @Autowired
    private PolicyChallengerDAO policyChallengerDAO;
    @Autowired
    private SubPolicyRepository subPolicyRepository;
    @Autowired
    private PolicyDecisionModeRepository policyDecisionModeRepository;
    @Autowired
    private PolicyIndicatrixItemDAO policyIndicatrixItemDAO;
    @Autowired
    private PolicyFieldDAO policyFieldDAO;
    @Autowired
    private  DecisionFlowRepository decisionFlowRepository;

    @Autowired
    private IPolicyCustomOutputRepository outputRepository;

    @Autowired
    private IPolicyFieldEncryptionRepository fieldEncryptionRepository;

    @Autowired
    private IPolicyFieldNecessaryRepository fieldNecessaryRepository;


    //根据合作列表，取得运行版本的策略清单
    @Override
    public List<PolicyModifiedDTO> queryDefaultPolicyByPartners(Set<String> partners){
        if (null==partners||partners.isEmpty()){
            return Collections.emptyList();
        }
        return policyDAO.selectDefaultPolicyByPartners(partners);
    }


    //根据合作列表，取得挑战者版本的策略清单
    @Override
    public List<PolicyModifiedDTO> queryChallengerPolicyByPartners(Set<String> partners){
        if (null==partners||partners.isEmpty()){
            return Collections.emptyList();
        }
        List<PolicyModifiedDTO> result = new ArrayList<>();
        List<String> policyUuidList = new ArrayList<>();
        List<PolicyChallengerDO> policyChallengerDOList = policyChallengerDAO.selectAvailableByPartners(partners);
        for(PolicyChallengerDO policyChallengerDO:policyChallengerDOList){
            String config = policyChallengerDO.getConfig();
            if(StringUtils.isBlank(config)){
                continue;
            }
            try{
                //[{"ratio":40.0,"versionUuid":"b225f25f34044a9a9a6929ce12703068"},{"ratio":60.0,"versionUuid":"efa8c9ea504f413caf0634dbab760583"}]
                List<HashMap> jsonarray = JSON.parseArray(config, HashMap.class);
                for(Map jsonObject:jsonarray){
                    String versionUuid = JsonUtil.getString(jsonObject,"versionUuid");
                    if(StringUtils.isNotBlank(versionUuid)){
                        policyUuidList.add(versionUuid);
                    }
                }
            }catch (Exception e){
                logger.error(TraceUtils.getFormatTrace()+"queryChallengerPolicyByPartners error",e);
            }
        }

        if(policyUuidList.isEmpty()){
            return result;
        }

        return policyDAO.selectNotDefaultPolicyByUuids(policyUuidList);
    }

    //查询单个策略的信息，不包含各个子对象
    @Override
    public PolicyDTO queryByUuid(String uuid){
        PolicyDO policyDO = policyDAO.selectByUuid(uuid);

        if(policyDO == null){
            return null;
        }

        PolicyDTO policyDTO = new PolicyDTO();
        BeanUtils.copyProperties(policyDO,policyDTO);

        //取得策略定义表中的策略名称、eventId
        PolicyDefinitionDO policyDefinitionDO = policyDefinitionDAO.selectByUuid(policyDO.getPolicyDefinitionUuid());
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
        PolicyDO policyDO = policyDAO.selectByUuid(uuid);

        if(policyDO == null){
            return null;
        }

        PolicyDTO policyDTO = new PolicyDTO();
        BeanUtils.copyProperties(policyDO,policyDTO);

        //取得策略定义表中的策略名称、eventId
        PolicyDefinitionDO policyDefinitionDO = policyDefinitionDAO.selectByUuid(policyDO.getPolicyDefinitionUuid());
        policyDTO.setName(policyDefinitionDO.getName());
        policyDTO.setAppName(policyDefinitionDO.getAppName());
        policyDTO.setAppType(policyDefinitionDO.getAppType());
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

        //查询自定义输出
        policyDTO.setPolicyCustomOutputDTOList(outputRepository.selectByPolicyUuid(policyDTO.getUuid()));

        // 查询必传字段
        policyDTO.setPolicyFieldNecessaryDTOList(fieldNecessaryRepository.queryByPolicyDefinitionUuid(policyDTO.getPolicyDefinitionUuid()));

        // 查询加密字段
        policyDTO.setPolicyFieldEncryptionDTOList(fieldEncryptionRepository.queryByPolicyDefinitionUuid(policyDTO.getPolicyDefinitionUuid()));

        return policyDTO;
    }


    //查询平台指标
    private List<PolicyIndicatrixItemDTO> queryPolicyIndicatrixItemDTOByPolicyUuid(String policyUuid){
        List<PolicyIndicatrixItemDO>  policyIndicatrixItemDOList = policyIndicatrixItemDAO.selectEnabledByPolicyUuid(policyUuid);
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
        List<PolicyFieldDO>  policyFieldDOList = policyFieldDAO.selectByPolicyUuid(policyUuid);
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
