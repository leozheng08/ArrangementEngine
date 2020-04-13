package cn.tongdun.kunpeng.api.acl.engine.model.application;

import java.util.List;
import java.util.Set;

/**
 * @Author: liang.chen
 * @Date: 2019/12/10 下午1:46
 */
public interface IAdminApplicationRepository {

    //查询应用信息列表
    List<AdminApplicationDTO> queryEnabledByPartners(Set<String> partners);

    //查询单个应用信息
    AdminApplicationDTO queryByAppName(String partnerCode,String appName);
}
