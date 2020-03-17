package cn.tongdun.kunpeng.api.infrastructure.persistence.repository;

import cn.tongdun.kunpeng.api.engine.model.rule.function.namelist.CustomListValue;
import cn.tongdun.kunpeng.api.engine.model.rule.function.namelist.ICustomListValueKVRepository;
import cn.tongdun.kunpeng.share.kv.IScoreKVRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * 自定义列表数据缓存到redis缓存中
 * @Author: liang.chen
 * @Date: 2020/3/13 下午6:19
 */
@Repository
public class CustomListValueKVRepository implements ICustomListValueKVRepository{

    @Autowired
    private IScoreKVRepository redisScoreKVRepository;

    @Override
    public void putCustomListValueData(CustomListValue customListValue){
        redisScoreKVRepository.zadd(customListValue.getCustomListUuid(), customListValue.getExpireTime(), customListValue.getValue());
    }


    @Override
    public void removeCustomListValueData(CustomListValue customListValue){
        //redisScoreKVRepository.zremrangeByLex(customListValue.getCustomListUuid(), customListValue.getExpireTime(), customListValue.getValue());
    }


}
