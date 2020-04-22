package cn.tongdun.kunpeng.api.consumer.customlist.completion;

import cn.tongdun.kunpeng.api.infrastructure.persistence.mybatis.mappers.kunpeng.RuleActionElement4ConsumerDAO;
import cn.tongdun.kunpeng.share.dataobject.RuleActionElementDO;
import cn.tongdun.kunpeng.share.json.JSON;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Service
public class RuleConditionCache {

    private static final Logger logger = LoggerFactory.getLogger(RuleConditionCache.class);

    private LoadingCache<String, List<Map>> actionElementCache;

    private final static String ACTION_TYPE = "addCustomList";

    @Autowired
    private RuleActionElement4ConsumerDAO ruleActionElementDOMapper;

    @PostConstruct
    public void init() {
        initRuleActionElementCache();
    }

    private void initRuleActionElementCache() {
        // 组装Loader
        CacheLoader<String, List<Map>> loader = new CacheLoader<String, List<Map>>() {

            @Override
            public List<Map> load(String s) throws Exception {
                return actionElementNoCache(s);
            }
        };
        loader = new NullableCacheLoader<>(loader);

        // 组装缓存
        CacheBuilder<String, List<Map>> cacheBuilder = (CacheBuilder) CacheBuilder.newBuilder();
        cacheBuilder.refreshAfterWrite(60, TimeUnit.SECONDS);
        cacheBuilder.maximumSize(10000);
        actionElementCache = cacheBuilder.build(loader);
        actionElementCache = new NullableLoadingCache<>(actionElementCache);
    }

    public List<Map> getActionElements(String ruleUuid) {
        try {
            return actionElementCache.get(ruleUuid);
        } catch (ExecutionException e) {
            logger.error("Failed to get rule trigger", e);
            return null;
        }
    }

    private List<Map> actionElementNoCache(String ruleUuid) {
        List<RuleActionElementDO> actionElementDOList = ruleActionElementDOMapper.selectByRuleUuid(ruleUuid);
        if (null == actionElementDOList) {
            logger.error("Failed to find rule with uuid=" + ruleUuid);
            return null;
        }
        List<Map> list = Lists.newArrayList();
        for (RuleActionElementDO elementDO : actionElementDOList) {
            if (elementDO.getActionType().equals(ACTION_TYPE)) {
                List<HashMap> array = JSON.parseArray(elementDO.getActions(), HashMap.class);
                if (array == null) {
                    continue;
                }
                for (Map json : array) {
                    list.add(json);
                }
            }
        }
        return list;
    }
}
