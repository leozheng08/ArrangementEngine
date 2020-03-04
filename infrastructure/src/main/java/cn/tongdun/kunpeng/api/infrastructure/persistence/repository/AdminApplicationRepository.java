package cn.tongdun.kunpeng.api.infrastructure.persistence.repository;

import cn.tongdun.kunpeng.api.engine.model.adminapplication.AdminApplication;
import cn.tongdun.kunpeng.api.engine.model.adminapplication.IAdminApplicationRepository;
import cn.tongdun.kunpeng.api.infrastructure.persistence.dataobj.AdminApplicationDO;
import cn.tongdun.kunpeng.api.infrastructure.persistence.mybatis.mappers.kunpeng.AdminApplicationDOMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author: liang.chen
 * @Date: 2019/12/10 下午1:46
 */
@Repository
public class AdminApplicationRepository implements IAdminApplicationRepository{

    private Logger logger = LoggerFactory.getLogger(AdminApplicationRepository.class);

    @Autowired
    private AdminApplicationDOMapper adminApplicationDOMapper;

    @Override
    public List<AdminApplication> queryApplicationsByPartners(Set<String> partners){
        List<AdminApplicationDO> list = adminApplicationDOMapper.queryApplicationsByPartners(partners);

        List<AdminApplication> result = list.stream().map(adminApplicationDO->{
            AdminApplication adminApplication = new AdminApplication();
            BeanUtils.copyProperties(adminApplicationDO,adminApplication);
            return adminApplication;
        }).collect(Collectors.toList());

        return result;
    }
}
