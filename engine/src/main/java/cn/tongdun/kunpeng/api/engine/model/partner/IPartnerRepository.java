package cn.tongdun.kunpeng.api.engine.model.partner;

import cn.tongdun.kunpeng.api.engine.dto.PolicyModifiedDTO;
import cn.tongdun.kunpeng.api.engine.model.eventtype.EventType;

import java.util.List;
import java.util.Set;

/**
 * @Author: liang.chen
 * @Date: 2019/12/10 下午1:46
 */
public interface IPartnerRepository {

    //查询合作信息列表
    List<Partner> queryEnabledByPartners(Set<String> partners);

    //查询单个合作方信息
    Partner queryByPartnerCode(String partnerCode);
}
