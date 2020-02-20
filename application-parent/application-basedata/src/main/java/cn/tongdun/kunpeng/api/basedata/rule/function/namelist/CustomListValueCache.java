package cn.tongdun.kunpeng.api.basedata.rule.function.namelist;

import cn.tongdun.kunpeng.share.kv.Cursor;
import cn.tongdun.kunpeng.share.kv.IScoreKVRepository;
import cn.tongdun.kunpeng.share.kv.IScoreValue;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created by lvyadong on 2020/01/16.
 */
@Component
public class CustomListValueCache {

    private static final Logger logger = LoggerFactory.getLogger(CustomListValueCache.class);
    //单一列表元素最大数量
    private static final int MAX_LIST_SIZE = 100_000;

    @Autowired
    private IScoreKVRepository redisScoreKVRepository;

    //TODO: 先判断列表大小，过大直接返回一个新的507的子码，意为过大的列表数据不允许前后缀及模糊匹配
    private List<String> loadData(String listUuid) {
        if (StringUtils.isNotBlank(listUuid)) {
            Long count = redisScoreKVRepository.zcard(listUuid);
            if (count != null && count < MAX_LIST_SIZE) {
                Set<IScoreValue<String>> results = loadDataByRedis(listUuid);
                if (CollectionUtils.isNotEmpty(results)) {
                    List<String> list = new ArrayList<>(results.size());
                    results.forEach(scoreValue -> list.add(scoreValue.getValue()));
                    return list;
                }
            } else {
                if (count != null && count >= MAX_LIST_SIZE) {
                    logger.error("list data is too long! listUuid: {}", listUuid);
                }
            }
        }
        return null;
    }

    private Set<IScoreValue<String>> loadDataByRedis(String listUuid) {
        Cursor cursor = Cursor.first();
        int count = 10_000;
        Set<IScoreValue<String>> results = new HashSet<>(1024);
        boolean first = true;
        Date now = new Date();
        while (first || cursor.getCursor() != 0) {
            first = false;
            Set<IScoreValue<String>> tmp = redisScoreKVRepository.zscan(listUuid, cursor, count);
            if (CollectionUtils.isNotEmpty(tmp)) {
                tmp.forEach(scoreValue -> {
                    if (scoreValue != null && scoreValue.getScore() != null && isEffectiveValue(scoreValue.getScore(), now)) {
                        results.add(scoreValue);
                    }
                });
            }
        }
        return results;
    }

    public boolean isEffectiveValue(double expire, Date now) {
        return expire == 0 || expire >= now.getTime();
    }

    public List<String> get(String listUuid) {
        return loadData(listUuid);
    }

    public double getZsetScore(String listUuid, String data) {
        if (StringUtils.isAnyBlank(listUuid, data)) {
            return -1D;
        }
        Double score = redisScoreKVRepository.zscore(listUuid, data);
        if (score == null) {
            score = -1D;
        }
        return score;
    }
}
