package cn.tongdun.kunpeng.api.acl.impl.engine.model.application;

import cn.tongdun.kunpeng.api.acl.engine.model.application.AdminApplicationDTO;
import cn.tongdun.kunpeng.api.acl.engine.model.application.IAdminApplicationRepository;
import cn.tongdun.kunpeng.api.acl.engine.model.partner.IPartnerRepository;
import cn.tongdun.kunpeng.api.acl.engine.model.partner.PartnerDTO;
import cn.tongdun.kunpeng.api.common.Constant;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 只有一个default默认appName
 * @Author: liang.chen
 * @Date: 2020/4/13 上午11:27
 */
@Repository
public class IgnoreAdminApplicationRepository implements IAdminApplicationRepository{

    private static final AdminApplicationDTO DEFAULT_APPLICATION = createDefaultAppliction();

    //查询合作信息列表
    @Override
    public List<AdminApplicationDTO> queryEnabledByPartners(Set<String> partners){
        return new ArrayList<AdminApplicationDTO>(){{add(DEFAULT_APPLICATION);}};
    }

    //查询单个合作方信息
    @Override
    public AdminApplicationDTO queryByAppName(String partnerCode,String appName){
        return DEFAULT_APPLICATION;
    }


    private static AdminApplicationDTO createDefaultAppliction(){
        AdminApplicationDTO applicationDTO = new AdminApplicationDTO();
        applicationDTO.setUuid(Constant.DEFAULT_APPLICATION);
        applicationDTO.setName(Constant.DEFAULT_APPLICATION);
        applicationDTO.setStatus(1);
        return applicationDTO;
    }
}
