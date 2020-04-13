package cn.tongdun.kunpeng.api.acl.impl.engine.model.partner;

import cn.tongdun.kirin.web.client.intf.KirinUserService;
import cn.tongdun.kirin.web.client.object.KirinPartner;
import cn.tongdun.kunpeng.api.acl.engine.model.partner.IPartnerRepository;
import cn.tongdun.kunpeng.api.acl.engine.model.partner.PartnerDTO;
import cn.tongdun.kunpeng.api.common.Constant;
import cn.tongdun.kunpeng.api.engine.model.partner.Partner;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 中博信的实现
 * @Author: liang.chen
 * @Date: 2020/4/13 上午11:27
 */
@Repository
public class ZbxPartnerRepository implements IPartnerRepository{

    @Resource(name = "kirinUserServiceForPartner")
    private KirinUserService kirinUserService;

    //查询合作信息列表
    @Override
    public List<PartnerDTO> queryEnabledByPartners(Set<String> partnerCodes){

        if(partnerCodes == null || partnerCodes.isEmpty()) {
            return null;
        }

        List<PartnerDTO> result = new ArrayList<>();
        for(String partnerCode:partnerCodes){
            PartnerDTO partner = queryByPartnerCode(partnerCode);
            if(partner != null){
                result.add(partner);
            }
        }

        return result;
    }

    //查询单个合作方信息
    @Override
    public PartnerDTO queryByPartnerCode(String partnerCode){
        List<KirinPartner> kirinPartnerList = kirinUserService.getPartnerAppList(partnerCode);
        if (kirinPartnerList == null || kirinPartnerList.isEmpty()) {
            return null;
        }
        PartnerDTO partner = convert(kirinPartnerList.get(0));
        return partner;
    }

    private PartnerDTO convert(KirinPartner kirinPartner){
        PartnerDTO partner = new PartnerDTO();
        BeanUtils.copyProperties(kirinPartner,partner);

        return partner;
    }

}
