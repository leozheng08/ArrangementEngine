package cn.tongdun.kunpeng.api.engine.model.adminapplication;

import cn.tongdun.kunpeng.api.engine.model.eventtype.EventType;

import java.util.List;
import java.util.Set;

/**
 * @Author: liang.chen
 * @Date: 2019/12/10 下午1:46
 */
public interface IAdminApplicationRepository {


    List<AdminApplication> queryApplicationsByPartners(Set<String> partners);
}
