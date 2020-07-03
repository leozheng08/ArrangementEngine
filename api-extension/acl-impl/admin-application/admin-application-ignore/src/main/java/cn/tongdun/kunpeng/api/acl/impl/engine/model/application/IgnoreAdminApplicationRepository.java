package cn.tongdun.kunpeng.api.acl.impl.engine.model.application;

import cn.tongdun.kunpeng.api.acl.engine.model.application.AdminApplicationDTO;
import cn.tongdun.kunpeng.api.acl.engine.model.application.IAdminApplicationRepository;
import cn.tongdun.kunpeng.api.acl.engine.model.partner.IPartnerRepository;
import cn.tongdun.kunpeng.api.acl.engine.model.partner.PartnerDTO;
import cn.tongdun.kunpeng.api.common.Constant;
import cn.tongdun.kunpeng.share.dataobject.AdminApplicationDO;
import org.springframework.beans.BeanUtils;
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

    private static final AdminApplicationDTO DEFAULT_APPLICATION = createDefaultApplication();

    //查询合作信息列表
    @Override
    public List<AdminApplicationDTO> queryApplicationsByPartners(Set<String> partners){
        return new ArrayList<AdminApplicationDTO>(){{add(DEFAULT_APPLICATION);}};
    }

    @Override
    public AdminApplicationDTO selectApplicationByUuid(String uuid) {
        AdminApplicationDTO applicationDTO = new AdminApplicationDTO();
        applicationDTO.setCode(Constant.DEFAULT_APP_NAME);
        applicationDTO.setSecretKey(Constant.DEFAULT_APP_NAME);
        applicationDTO.setUuid(Constant.DEFAULT_APP_NAME);
        applicationDTO.setDisplayName(Constant.DEFAULT_APP_NAME);
        applicationDTO.setPartnerCode(Constant.DEFAULT_PARTNER);
        applicationDTO.setStatus(1);
        return applicationDTO;
    }

    private static AdminApplicationDTO createDefaultApplication(){
        AdminApplicationDTO applicationDTO = new AdminApplicationDTO();
        applicationDTO.setCode(Constant.DEFAULT_APP_NAME);
        applicationDTO.setSecretKey(Constant.DEFAULT_APP_NAME);
        applicationDTO.setUuid(Constant.DEFAULT_APP_NAME);
        applicationDTO.setDisplayName(Constant.DEFAULT_APP_NAME);
        applicationDTO.setPartnerCode(Constant.DEFAULT_PARTNER);
        applicationDTO.setStatus(1);
        return applicationDTO;
    }

}
