package cn.tongdun.kunpeng.api.engine.model.partner;

import cn.tongdun.kunpeng.api.engine.cache.AbstractLocalCache;
import com.google.common.collect.Lists;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 合作方信息缓存partnerCode -> Partner
 *
 * @Author: liang.chen
 * @Date: 2019/12/16 下午8:01
 */
@Component
public class PartnerCache extends AbstractLocalCache<String, Partner> {

    //partnerCode -> Partner
    private Map<String, Partner> partnerMap = new ConcurrentHashMap<>();


    @PostConstruct
    public void init() {
        register(Partner.class);
    }

    @Override
    public Partner get(String partnerCode) {
        return partnerMap.get(partnerCode);
    }

    @Override
    public void put(String partnerCode, Partner partner) {
        partnerMap.put(partnerCode, partner);
    }

    @Override
    public Partner remove(String partnerCode) {
        return partnerMap.remove(partnerCode);
    }

    public List<String> getAllPartnerCodes() {
        return Lists.newArrayList(partnerMap.keySet());
    }
}
