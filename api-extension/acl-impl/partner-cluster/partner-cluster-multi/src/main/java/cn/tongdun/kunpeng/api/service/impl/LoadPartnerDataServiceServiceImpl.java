package cn.tongdun.kunpeng.api.service.impl;

import cn.tongdun.kunpeng.api.acl.engine.model.application.AdminApplicationDTO;
import cn.tongdun.kunpeng.api.acl.engine.model.application.IAdminApplicationRepository;
import cn.tongdun.kunpeng.api.acl.engine.model.partner.IPartnerRepository;
import cn.tongdun.kunpeng.api.acl.engine.model.partner.PartnerDTO;
import cn.tongdun.kunpeng.api.engine.load.bypartner.step.PolicyLoadByPartnerService;
import cn.tongdun.kunpeng.api.engine.model.application.AdminApplication;
import cn.tongdun.kunpeng.api.engine.model.application.AdminApplicationCache;
import cn.tongdun.kunpeng.api.engine.model.cluster.PartnerClusterCache;
import cn.tongdun.kunpeng.api.engine.model.fieldmapping.AccessBusinessCache;
import cn.tongdun.kunpeng.api.engine.model.fieldmapping.IAccessBusinessRepository;
import cn.tongdun.kunpeng.api.engine.model.partner.Partner;
import cn.tongdun.kunpeng.api.engine.model.partner.PartnerCache;
import cn.tongdun.kunpeng.api.engine.model.policy.challenger.IPolicyChallengerRepository;
import cn.tongdun.kunpeng.api.engine.model.policy.challenger.PolicyChallenger;
import cn.tongdun.kunpeng.api.engine.model.policy.challenger.PolicyChallengerCache;
import cn.tongdun.kunpeng.api.engine.model.policy.definition.IPolicyDefinitionRepository;
import cn.tongdun.kunpeng.api.engine.model.policy.definition.PolicyDefinition;
import cn.tongdun.kunpeng.api.engine.model.policy.definition.PolicyDefinitionCache;
import cn.tongdun.kunpeng.api.engine.model.script.DynamicScript;
import cn.tongdun.kunpeng.api.engine.model.script.IDynamicScriptRepository;
import cn.tongdun.kunpeng.api.engine.model.script.groovy.GroovyCompileManager;
import cn.tongdun.kunpeng.api.service.ILoadPartnerDataService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 集群内新增一个合作方加载该合作方下数据
 * @author: mengtao
 * @create: 2021-11-04 19:04
 */

@Component
public class LoadPartnerDataServiceServiceImpl implements ILoadPartnerDataService {

    private final static Logger logger = LoggerFactory.getLogger(LoadPartnerDataServiceServiceImpl.class);

    @Autowired
    AccessBusinessCache accessBusinessCache;

    @Autowired
    PartnerClusterCache partnerClusterCache;

    @Autowired
    IAccessBusinessRepository accessBusinessRepository;

    @Autowired
    AdminApplicationCache adminApplicationCache;

    @Autowired
    IAdminApplicationRepository adminApplicationRepository;

    @Autowired
    PartnerCache partnerCache;

    @Autowired
    IPartnerRepository partnerRepository;

    @Autowired
    private PolicyLoadByPartnerService loadPolicyByPartnerService;

    @Autowired
    private PolicyDefinitionCache policyDefinitionCache;

    @Autowired
    private IPolicyDefinitionRepository policyDefinitionRepository;

    @Autowired
    private IPolicyChallengerRepository policyChallengerRepository;

    @Autowired
    private PolicyChallengerCache policyChallengerCache;

    @Autowired
    IDynamicScriptRepository dynamicScriptRepository;

    @Autowired
    GroovyCompileManager groovyCompileManager;

    @Override
    public void load(String partnerCode){
        Set<String> partners = new HashSet<>();
        partners.add(partnerCode);
        //刷新合作方集群缓存
        partnerClusterCache.addPartner(partnerCode);
        //应用对象数据
        loadAdminApplication(partners);
        //合作方缓存
        loadPartner(partners);
        //动态脚本
        loadDynamicScript(partners);
        //策略定义缓存
        loadPolicyDefinition(partners);
        //加载挑战者策略
        loadPolicyChallenger(partners);
        //加载策略
        loadPolicyByPartnerService.loadByPartner(partners);
    }

    private void loadAdminApplication(Set<String> partners){
        List<AdminApplicationDTO> adminApplicationDTOList = adminApplicationRepository.queryApplicationsByPartners(partners);
        if(adminApplicationDTOList == null || adminApplicationDTOList.isEmpty()){
            return;
        }
        List<AdminApplication> adminApplicationList = adminApplicationDTOList.stream().map(adminApplicationDTO->{
            AdminApplication adminApplication = new AdminApplication();
            BeanUtils.copyProperties(adminApplicationDTO,adminApplication);
            return adminApplication;
        }).collect(Collectors.toList());
        for(AdminApplication adminApplication:adminApplicationList){
            adminApplicationCache.addAdminApplication(adminApplication);
        }
    }

    private void loadPolicyDefinition(Set<String> partners){
        //取得策略定义列表
        List<PolicyDefinition> PolicyModifiedDOList = policyDefinitionRepository.queryByPartners(partners);
        for(PolicyDefinition policyDefinition:PolicyModifiedDOList) {
            policyDefinitionCache.put(policyDefinition.getUuid(),policyDefinition);
        }
        if(CollectionUtils.isNotEmpty(PolicyModifiedDOList)) {
            logger.info("partner cluster update,policyDefinitionSize={}", PolicyModifiedDOList.size());
        }
    }

    private void loadPolicyChallenger(Set<String> partners){
        //取得挑战者任务列表
        List<PolicyChallenger> policyChallengerList = policyChallengerRepository.queryAvailableByPartners(partners);
        for(PolicyChallenger policyChallenger:policyChallengerList) {
            policyChallengerCache.put(policyChallenger.getPolicyDefinitionUuid(),policyChallenger);
        }
    }

    private void loadPartner(Set<String> partners){
        List<PartnerDTO> partnerDTOList = partnerRepository.queryEnabledByPartners(partners);
        if(partnerDTOList == null || partnerDTOList.isEmpty()){
            return;
        }
        List<Partner> partnerList = partnerDTOList.stream().map(partnerDTO->{
            Partner partner = new Partner();
            BeanUtils.copyProperties(partnerDTO,partner);
            return partner;
        }).collect(Collectors.toList());
        for(Partner partner:partnerList){
            partnerCache.put(partner.getPartnerCode(),partner);
       }
    }

    private void loadDynamicScript(Set<String> partners){
        List<DynamicScript> scripts = dynamicScriptRepository.queryGroovyByPartners(partners);
        for (DynamicScript script : scripts) {
            try {
                groovyCompileManager.addOrUpdate(script);
            } catch (Exception e) {
                logger.info("load DynamicScript fail!e={}", ExceptionUtils.getStackTrace(e));
            }
        }
    }
}
