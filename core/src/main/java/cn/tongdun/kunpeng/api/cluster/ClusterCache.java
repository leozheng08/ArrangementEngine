package cn.tongdun.kunpeng.api.cluster;

import cn.tongdun.kunpeng.api.cache.ILocalCache;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * 以集群为维度加载的缓存
 * @Author: liang.chen
 * @Date: 2019/12/12 上午10:44
 */
@Data
@Component
public class ClusterCache {

    Set<String> partners = new HashSet<>();

}
