package cn.tongdun.kunpeng.api.acl.impl.event.notify.redis;

import cn.tongdun.kunpeng.api.acl.event.notice.AbstractDomainEventRepository;
import cn.tongdun.kunpeng.api.common.util.DateUtil;
import cn.tongdun.kunpeng.share.kv.IScoreKVRepository;
import cn.tongdun.kunpeng.share.kv.IScoreValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 从缓存或储存中拉取近几分钟的领域事件
 * 采用redis实现
 * @Author: liang.chen
 * @Date: 2020/3/11 下午6:42
 */
@Repository
public class RedisDomainEventRepository extends AbstractDomainEventRepository {

    private static Logger logger = LoggerFactory.getLogger(RedisDomainEventRepository.class);

    private static final String SPLIT_CHAR = "^^";


    //放到redis缓存上的超期时间
    private static final long EXPIRE_TIME = (LAST_MINUTES+1)*60*1000L;


    @Autowired
    private IScoreKVRepository scoreKVRepository;


    @Override
    public List<String> pullLastEventMsgs(){

        Set<IScoreValue> scoreValueSet = new LinkedHashSet<>();
        try {
            for(int i=LAST_MINUTES-1;i>=0;i--){
                String lastKey = DateUtil.getLastMinuteStr(i);
                Set<IScoreValue> scoreValueSetTmp = scoreKVRepository.zrangeByScoreWithScores(lastKey,0,Long.MAX_VALUE);
                if(scoreValueSetTmp.isEmpty()){
                    continue;
                }

                scoreValueSet.addAll(scoreValueSetTmp);
            }


        } catch (Exception e) {
            logger.error("update rule form redis error!",e);
        }

        List<String> eventList = scoreValueSet.stream().map(scoreValue->{
            return scoreValue.getValue().toString();
        }).collect(Collectors.toList());

        //业务去重
        return deduplicationd(eventList);
    }


    @Override
    public void putEventMsgToRemoteCache(String eventMsg,Long occurredTime){
        String currentKey = DateUtil.getYYYYMMDDHHMMStr();
        scoreKVRepository.zadd(currentKey,occurredTime,eventMsg);
        scoreKVRepository.setTtl(currentKey,EXPIRE_TIME);
    }
}
