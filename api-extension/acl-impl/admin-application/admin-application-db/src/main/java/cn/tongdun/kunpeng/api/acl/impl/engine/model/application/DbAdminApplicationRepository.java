package cn.tongdun.kunpeng.api.acl.impl.engine.model.application;

import cn.tongdun.kunpeng.api.acl.engine.model.application.AdminApplicationDTO;
import cn.tongdun.kunpeng.api.acl.engine.model.application.IAdminApplicationRepository;
import cn.tongdun.kunpeng.api.infrastructure.persistence.mybatis.mappers.kunpeng.AdminApplicationDAO;
import cn.tongdun.kunpeng.share.dataobject.AdminApplicationDO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Author: liang.chen
 * @Date: 2020/4/13 上午11:27
 */
@Repository
public class DbAdminApplicationRepository implements IAdminApplicationRepository{

    private Logger logger = LoggerFactory.getLogger(DbAdminApplicationRepository.class);


    @Autowired
    private AdminApplicationDAO adminApplicationDAO;

    @Override
    public List<AdminApplicationDTO> queryApplicationsByPartners(Set<String> partners){
        if (null==partners||partners.isEmpty()){
            return Collections.emptyList();
        }
        List<AdminApplicationDO> list = adminApplicationDAO.queryApplicationsByPartners(partners);

        return list.stream().map(adminApplicationDO->{
            AdminApplicationDTO adminApplication = new AdminApplicationDTO();
            BeanUtils.copyProperties(adminApplicationDO,adminApplication);
            return adminApplication;
        }).collect(Collectors.toList());
    }

    @Override
    public AdminApplicationDTO selectApplicationByPartnerAppName(String partnerCode, String appName) {
        return null;
    }
}
