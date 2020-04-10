package cn.tongdun.kunpeng.api.acl.impl.event.notify.db;

import cn.tongdun.kunpeng.api.acl.api.event.notice.AbstractDomainEventRepository;
import cn.tongdun.kunpeng.api.common.util.DateUtil;
import cn.tongdun.kunpeng.api.infrastructure.persistence.mybatis.mappers.kunpeng.DomainEventDAO;
import cn.tongdun.kunpeng.share.dataobject.DomainEventDO;
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
public class DbDomainEventRepository extends AbstractDomainEventRepository {

    private static Logger logger = LoggerFactory.getLogger(DbDomainEventRepository.class);

    @Autowired
    private DomainEventDAO domainEventDAO;

    @Override
    public List<String> pullLastEventMsgs(){

        try {
            Date lastDate = DateUtil.getLastMinute(LAST_MINUTES);
            //从数据中查询近几分钟的领域事件
            List<DomainEventDO> domainEventDOList= domainEventDAO.queryByGmtModify(lastDate);
            if(domainEventDOList.isEmpty()){
                return new ArrayList<>();
            }

            List<String> eventList = domainEventDOList.stream().map(domainEventDO->{
                return domainEventDO.getEventData();
            }).collect(Collectors.toList());

            //业务去重
            return deduplicationd(eventList);

        } catch (Exception e) {
            logger.error("update rule form redis error!",e);
        }
        return new ArrayList<>();
    }


    @Override
    public void putEventMsgToRemoteCache(String eventMsg,Long occurredTime){
        throw new RuntimeException("putEventMsgToRemoteCache not support");
    }
}
