package cn.tongdun.kunpeng.api.engine.model.rule.function.namelist;


import cn.tongdun.kunpeng.share.kv.IScoreKVRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 自定义列表数据从数据库加载并设置到缓存
 * @Author: liang.chen
 * @Date: 2020/2/28 下午3:08
 */
@Component
public class CustomListValueManager {
    private static final Integer PAGE_SIZE = 10;

    @Autowired
    ICustomListValueRepository customListValueRepository;

    @Autowired
    private IScoreKVRepository redisScoreKVRepository;

    public void loadDataToRedis(){
        List<String> customLists = customListValueRepository.selectAllAvailable();
        if (null != customLists) {
            customLists.stream().forEach(customListUuid -> {
                int count = customListValueRepository.selectCountByListUuid(customListUuid);
                if (count >0) {
                    int totalPage = totalPage(count);
                    for (int page = 0; page <totalPage; page++) {
                        List<CustomListValue> list = customListValueRepository.selectByListUuid(customListUuid, page * PAGE_SIZE, PAGE_SIZE);
                        if (null != list) {
                            for (CustomListValue listValue : list) {
                                redisScoreKVRepository.zadd(customListUuid, (double) listValue.getExpireTime(), listValue.getValue());
                            }
                        }
                    }
                }
            });
        }
    }

    private static int totalPage(int totalCount) {
        return totalCount % PAGE_SIZE == 0 ? (totalCount / PAGE_SIZE) : (totalCount / PAGE_SIZE) + 1;
    }
}
