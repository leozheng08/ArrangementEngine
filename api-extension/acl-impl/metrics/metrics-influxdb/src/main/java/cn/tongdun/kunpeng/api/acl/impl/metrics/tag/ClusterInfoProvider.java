package cn.tongdun.kunpeng.api.acl.impl.metrics.tag;

import cn.fraudmetrix.metrics.TagProvider;
import cn.tongdun.kunpeng.api.common.config.ILocalEnvironment;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

/**
 * @author kui.yuan@fraudmetrix.cn
 */
public class ClusterInfoProvider implements TagProvider {

    @Autowired
    private ILocalEnvironment localEnvironment;

    @Override
    public Map<String, String> provide() throws Exception {
        String localCluster = localEnvironment.getCluster();
        Map<String, String> map = new HashMap<>();
        map.put("cluster", localCluster);
        return map;
    }
}
