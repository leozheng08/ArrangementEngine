package cn.tongdun.kunpeng.api.service.impl;

import cn.tongdun.kunpeng.api.acl.engine.model.application.AdminApplicationDTO;
import cn.tongdun.kunpeng.api.acl.engine.model.application.IAdminApplicationRepository;
import cn.tongdun.kunpeng.api.engine.model.application.AdminApplication;
import cn.tongdun.kunpeng.api.engine.model.cluster.PartnerClusterCache;
import cn.tongdun.kunpeng.api.engine.model.fieldmapping.IAccessBusinessRepository;
import cn.tongdun.kunpeng.api.engine.model.partner.PartnerCache;
import cn.tongdun.kunpeng.api.engine.model.policy.challenger.IPolicyChallengerRepository;
import cn.tongdun.kunpeng.api.engine.model.policy.challenger.PolicyChallenger;
import cn.tongdun.kunpeng.api.engine.model.policy.definition.IPolicyDefinitionRepository;
import cn.tongdun.kunpeng.api.engine.model.policy.definition.PolicyDefinition;
import cn.tongdun.kunpeng.api.engine.reload.dataobject.PolicyChallengerEventDO;
import cn.tongdun.kunpeng.api.engine.reload.dataobject.PolicyDefinitionEventDO;
import cn.tongdun.kunpeng.api.engine.reload.impl.AccessBusinessReloadManager;
import cn.tongdun.kunpeng.api.engine.reload.impl.AdminApplicationReloadManager;
import cn.tongdun.kunpeng.api.engine.reload.impl.PolicyChallengerReLoadManager;
import cn.tongdun.kunpeng.api.engine.reload.impl.PolicyDefinitionReLoadManager;
import cn.tongdun.kunpeng.api.service.IRemovePartnerDataService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author: mengtao
 * @create: 2021-11-04 19:27
 */

@Component
public class RemovePartnerDataServiceImpl implements IRemovePartnerDataService {

    @Autowired
    PartnerClusterCache partnerClusterCache;

    @Autowired
    IAccessBusinessRepository accessBusinessRepository;

    @Autowired
    AdminApplicationReloadManager adminApplicationReloadManager;

    @Autowired
    IAdminApplicationRepository adminApplicationRepository;

    @Autowired
    PartnerCache partnerCache;

    @Autowired
    AccessBusinessReloadManager accessBusinessReloadManager;

    @Autowired
    private IPolicyDefinitionRepository policyDefinitionRepository;

    @Autowired
    private PolicyDefinitionReLoadManager policyDefinitionReLoadManager;

    @Autowired
    private PolicyChallengerReLoadManager policyChallengerReLoadManager;

    @Autowired
    private IPolicyChallengerRepository policyChallengerRepository;

    @Override
    public void remove(String partnerCode){
        Set<String> partners = new HashSet<>();
        partners.add(partnerCode);
        //判断该集群下有无此合作方，有删除缓存，无跳过
//        if(!partnerClusterCache.contains(partnerCode)){
//            return;
//        }
        //刷新合作方集群缓存
        partnerClusterCache.remove(partnerCode);
        //应用对象数据
        removeAdminApplication(partners);
        //合作方缓存
        removePartner(partners);
        //策略定义缓存,级联删除策略下所有缓存
        removePolicyDefinition(partners);
        //挑战者策略
        removePolicyChallenger(partners);
    }

    private void removeAdminApplication(Set<String> partners){
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
            adminApplicationReloadManager.remove(adminApplication);
        }
    }

    private void removePartner(Set<String> partners){
        if(partners == null || partners.isEmpty()){
            return;
        }
        for (String partner :
                partners) {
            partnerCache.remove(partner);
        }
    }

    private void removePolicyDefinition(Set<String> partners){
        //取得策略定义列表
        List<PolicyDefinition> policyModifiedDOList = policyDefinitionRepository.queryByPartners(partners);
        for(PolicyDefinition policyDefinition:policyModifiedDOList) {
            PolicyDefinitionEventDO eventDO = new PolicyDefinitionEventDO();
            BeanUtils.copyProperties(policyDefinition,eventDO);
            policyDefinitionReLoadManager.remove(eventDO);
        }
    }

    private void removePolicyChallenger(Set<String> partners) {
        //取得挑战者任务列表
        List<PolicyChallenger> policyChallengerList = policyChallengerRepository.queryAvailableByPartners(partners);

        for(PolicyChallenger policyChallenger:policyChallengerList) {
            PolicyChallengerEventDO eventDO = new PolicyChallengerEventDO();
            BeanUtils.copyProperties(policyChallenger,eventDO);
            policyChallengerReLoadManager.remove(eventDO);
        }
    }
}
