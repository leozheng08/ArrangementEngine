package cn.tongdun.kunpeng.api.acl.engine.model.partner;

import java.util.List;
import java.util.Set;

/**
 * @Author: liang.chen
 * @Date: 2019/12/10 下午1:46
 */
public interface IPartnerRepository {

    //查询合作信息列表
    List<PartnerDTO> queryEnabledByPartners(Set<String> partners);

    //查询单个合作方信息
    PartnerDTO queryByPartnerCode(String partnerCode);
}
