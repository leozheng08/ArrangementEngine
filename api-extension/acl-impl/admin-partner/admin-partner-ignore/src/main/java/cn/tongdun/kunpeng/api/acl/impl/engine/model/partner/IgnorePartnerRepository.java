package cn.tongdun.kunpeng.api.acl.impl.engine.model.partner;

import cn.tongdun.kunpeng.api.acl.engine.model.partner.IPartnerRepository;
import cn.tongdun.kunpeng.api.acl.engine.model.partner.PartnerDTO;
import cn.tongdun.kunpeng.api.common.Constant;
import cn.tongdun.kunpeng.api.engine.model.partner.Partner;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 只有一个default默认合作方
 * @Author: liang.chen
 * @Date: 2020/4/13 上午11:27
 */
@Repository
public class IgnorePartnerRepository implements IPartnerRepository{

    private static final PartnerDTO DEFAULT_PARTNER = createDefaultPartner();

    //查询所有合作方编码
    @Override
    public Set<String> queryAllPartnerCode(){
        return new HashSet<String>(){{add(Constant.DEFAULT_PARTNER);}};
    }
    //查询合作信息列表
    @Override
    public List<PartnerDTO> queryEnabledByPartners(Set<String> partners){
        return new ArrayList<PartnerDTO>(){{add(DEFAULT_PARTNER);}};
    }

    //查询单个合作方信息
    @Override
    public PartnerDTO queryByPartnerCode(String partnerCode){
        return DEFAULT_PARTNER;
    }


    private static PartnerDTO createDefaultPartner(){
        PartnerDTO partner = new PartnerDTO();
        partner.setPartnerCode(Constant.DEFAULT_PARTNER);
        partner.setUuid(Constant.DEFAULT_PARTNER);
        partner.setDisplayName(Constant.DEFAULT_PARTNER);
        partner.setStatus(1);
        return partner;
    }
}
