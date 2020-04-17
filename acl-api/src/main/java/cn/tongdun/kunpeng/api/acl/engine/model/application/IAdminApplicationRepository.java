package cn.tongdun.kunpeng.api.acl.engine.model.application;

import java.util.List;
import java.util.Set;

/**
 * @Author: liang.chen
 * @Date: 2019/12/10 下午1:46
 */
public interface IAdminApplicationRepository {

    //查询应用信息列表
    List<AdminApplicationDTO> queryApplicationsByPartners(Set<String> partners);
}
