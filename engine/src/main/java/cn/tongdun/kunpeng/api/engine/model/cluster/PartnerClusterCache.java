package cn.tongdun.kunpeng.api.engine.model.cluster;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * 以集群为维度加载的缓存
 * @Author: liang.chen
 * @Date: 2019/12/12 上午10:44
 */
@Data
@Component
public class PartnerClusterCache {

    Set<String> partners = new HashSet<>();

    /**
     * 添加本集群下的合作方
     * @param partner
     */
    public void addPartner(String partner){
        partners.add(partner);
    }

    /**
     * 本集群是否包含此合作方
     * @param partner
     * @return
     */
    public boolean contains(String partner){
        return partners.contains(partner);
    }
}
