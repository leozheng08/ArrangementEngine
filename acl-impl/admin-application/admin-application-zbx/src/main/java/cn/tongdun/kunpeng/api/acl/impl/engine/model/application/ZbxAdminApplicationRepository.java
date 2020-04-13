package cn.tongdun.kunpeng.api.acl.impl.engine.model.application;

import cn.tongdun.kirin.web.client.intf.KirinUserService;
import cn.tongdun.kirin.web.client.object.KirinApplication;
import cn.tongdun.kirin.web.client.object.KirinPartner;
import cn.tongdun.kunpeng.api.acl.engine.model.application.AdminApplicationDTO;
import cn.tongdun.kunpeng.api.acl.engine.model.application.IAdminApplicationRepository;
import cn.tongdun.kunpeng.api.common.Constant;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 只有一个default默认appName
 * @Author: liang.chen
 * @Date: 2020/4/13 上午11:27
 */
@Repository
public class ZbxAdminApplicationRepository implements IAdminApplicationRepository{

    @Resource(name = "kirinUserServiceForApplication")
    private KirinUserService kirinUserService;

    //查询合作信息列表
    @Override
    public List<AdminApplicationDTO> queryEnabledByPartners(Set<String> partnerCodes){
        if(partnerCodes == null || partnerCodes.isEmpty()) {
            return null;
        }

        List<AdminApplicationDTO> result = new ArrayList<>();
        for(String partnerCode:partnerCodes){
            List<KirinApplication> kirinApplicationList= kirinUserService.getApplications(partnerCode);
            if(kirinApplicationList == null || kirinApplicationList.isEmpty()){
                continue;
            }
            kirinApplicationList.forEach(kirinApplication->{
                result.add(convert(kirinApplication));
            });
        }

        return result;
    }

    //查询单个合作方信息
    @Override
    public AdminApplicationDTO queryByAppName(String partnerCode,String appName){
        KirinApplication kirinApplication = kirinUserService.getApplication(partnerCode,appName);
        if(kirinApplication == null){
            return null;
        }
        return convert(kirinApplication);
    }

    private AdminApplicationDTO convert(KirinApplication kirinApplication){
        AdminApplicationDTO adminApplicationDTO = new AdminApplicationDTO();
        BeanUtils.copyProperties(kirinApplication,adminApplicationDTO);
        return adminApplicationDTO;
    }


}
