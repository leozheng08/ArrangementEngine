package cn.tongdun.kunpeng.api.infrastructure.persistence.repository;

import cn.tongdun.kunpeng.api.engine.model.partner.IPartnerRepository;
import cn.tongdun.kunpeng.api.engine.model.partner.Partner;
import cn.tongdun.kunpeng.api.infrastructure.persistence.dataobj.AdminPartnerDO;
import cn.tongdun.kunpeng.api.infrastructure.persistence.mybatis.mappers.forseti.AdminPartnerDOMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class PartnerRepository implements IPartnerRepository{

    @Autowired
    private AdminPartnerDOMapper adminPartnerDOMapper;

    //取得所有策略定义清单
    @Override
    public List<Partner> queryEnabledByPartners(Set<String> partners){
        List<AdminPartnerDO> list = adminPartnerDOMapper.selectEnabledByPartners(partners);

        List<Partner> result = list.stream().map(adminPartnerDO->{
                Partner partner = new Partner();
                BeanUtils.copyProperties(adminPartnerDO,partner);
                return partner;
            }).collect(Collectors.toList());

        return result;
    }

    @Override
    public Partner queryByPartnerCode(String partnerCode){
        AdminPartnerDO adminPartnerDO = adminPartnerDOMapper.selectByPartnerCode(partnerCode);
        if(adminPartnerDO == null){
            return null;
        }

        Partner result = new Partner();
        BeanUtils.copyProperties(adminPartnerDO,result);
        return result;
    }
}
