package cn.tongdun.kunpeng.api.acl.impl.engine.model.application;

import cn.fraudmetrix.chassis.api.common.ApiResult;
import cn.fraudmetrix.chassis.api.oper.dto.AppProductDTO;
import cn.fraudmetrix.chassis.api.oper.intf.ProductService;
import cn.tongdun.kunpeng.api.acl.engine.model.application.AdminApplicationDTO;
import cn.tongdun.kunpeng.api.acl.engine.model.application.IAdminApplicationRepository;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Author: liang.chen
 * @Date: 2020/4/13 上午11:27
 */
@Repository
public class ChassisAdminApplicationRepository implements IAdminApplicationRepository{

    private Logger logger = LoggerFactory.getLogger(ChassisAdminApplicationRepository.class);


    @Autowired
    private ProductService productService;

    @Override
    public List<AdminApplicationDTO> queryApplicationsByPartners(Set<String> partners){
        if (null==partners||partners.isEmpty()){
            return Collections.emptyList();
        }

        // 通过运用平台提供的dubbo接口获取app信息；如果获取失败，则采用原来的方式获取
        List<AppProductDTO> appList = new ArrayList<>();
        // 正常的话查询dubbo接口获取应用
        ApiResult<List<AppProductDTO>> appProductList = productService.getAppProductList(null);
        if(appProductList != null && appProductList.isSuccess() && appProductList.getData() != null) {
            appList = appProductList.getData();
        } else {
            return Collections.emptyList();
        }

        return appList.stream().filter(x -> partners.contains(x.getPartnerCode())).map(appProductDTO->{
            AdminApplicationDTO adminApplication = new AdminApplicationDTO();
            BeanUtils.copyProperties(appProductDTO,adminApplication);
            return adminApplication;
        }).collect(Collectors.toList());
    }

    @Override
    public AdminApplicationDTO selectApplicationByPartnerAppName(String partnerCode, String appName) {
        if (StringUtils.isAnyBlank(partnerCode, appName)) {
            return null;
        }

        // 通过运用平台提供的dubbo接口获取app信息；如果获取失败，则采用原来的方式获取
        List<AppProductDTO> appList = new ArrayList<>();
        // 正常的话查询dubbo接口获取应用
        ApiResult<List<AppProductDTO>> appProductList = productService.getAppList(partnerCode, appName);
        if(appProductList != null && appProductList.isSuccess() && appProductList.getData() != null) {
            appList = appProductList.getData();
        } else {
            logger.warn("chassis selectApplicationByPartnerAppName result is error:{}", appProductList);
            return null;
        }

        return appList.stream().findFirst().map(appProductDTO->{
            AdminApplicationDTO adminApplication = new AdminApplicationDTO();
            BeanUtils.copyProperties(appProductDTO,adminApplication);
            return adminApplication;
        }).get();
    }

    @Override
    public AdminApplicationDTO selectApplicationByUuid(String uuid) {
        return null;
    }
}
