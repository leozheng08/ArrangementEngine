package cn.tongdun.kunpeng.api.acl.impl.engine.model.cluster;

import cn.tongdun.kirin.web.client.intf.KirinUserService;
import cn.tongdun.kirin.web.client.object.KirinPartner;
import cn.tongdun.kunpeng.api.acl.engine.model.cluster.IPartnerClusterRepository;
import cn.tongdun.kunpeng.api.common.Constant;
import cn.tongdun.kunpeng.api.engine.model.eventtype.EventType;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
 * @Date: 2020/4/13 上午11:15
 */
@Repository
public class ZbxPartnerClusterRepository implements IPartnerClusterRepository{


    @Resource(name = "kirinUserServiceForPartner")
    private KirinUserService kirinUserService;


    @Override
    public Set<String> queryPartnerByCluster(String cluster){
        return queryAllPartner();
    }


    @Override
    public Set<String> queryAllPartner(){
        List<KirinPartner> kirinPartnerList = kirinUserService.getPartnerAppList(null);
        if(kirinPartnerList == null || kirinPartnerList.isEmpty()){
            return null;
        }

        Set<String> result = new HashSet<>();
        result = kirinPartnerList.stream().map(kirinPartner->{
            return kirinPartner.getPartnerCode();
        }).collect(Collectors.toSet());
        return result;
    }
}
