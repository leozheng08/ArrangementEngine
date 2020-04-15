package cn.tongdun.kunpeng.api.acl.impl.engine.model.partner;

import cn.tongdun.kunpeng.api.acl.engine.model.partner.IPartnerRepository;
import cn.tongdun.kunpeng.api.acl.engine.model.partner.PartnerDTO;
import cn.tongdun.kunpeng.api.engine.model.partner.Partner;
import cn.tongdun.kunpeng.api.infrastructure.persistence.dataobj.AdminPartnerDO;
import cn.tongdun.kunpeng.api.infrastructure.persistence.mybatis.mappers.kunpeng.AdminPartnerDAO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class DbPartnerRepository implements IPartnerRepository{

    @Autowired
    private AdminPartnerDAO adminPartnerDAO;

    /**
     * 查询所有合作方编码
     * @return
     */
    @Override
    public Set<String> queryAllPartnerCode(){
        List<String> partnerCodes = adminPartnerDAO.selectAllEnabledPartnerCodes();
        Set partners = new HashSet(partnerCodes);
        return partners;
    }

    //取得所有策略定义清单
    @Override
    public List<PartnerDTO> queryEnabledByPartners(Set<String> partners){
        List<AdminPartnerDO> list = adminPartnerDAO.selectEnabledByPartners(partners);

        List<PartnerDTO> result = list.stream().map(adminPartnerDO->{
                PartnerDTO partner = new PartnerDTO();
                BeanUtils.copyProperties(adminPartnerDO,partner);
                return partner;
            }).collect(Collectors.toList());

        return result;
    }

    @Override
    public PartnerDTO queryByPartnerCode(String partnerCode){
        AdminPartnerDO adminPartnerDO = adminPartnerDAO.selectByPartnerCode(partnerCode);
        if(adminPartnerDO == null){
            return null;
        }

        PartnerDTO result = new PartnerDTO();
        BeanUtils.copyProperties(adminPartnerDO,result);
        return result;
    }
}
