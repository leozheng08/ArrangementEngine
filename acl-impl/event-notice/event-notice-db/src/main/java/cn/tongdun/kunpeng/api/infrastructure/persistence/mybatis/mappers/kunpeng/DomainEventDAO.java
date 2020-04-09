package cn.tongdun.kunpeng.api.infrastructure.persistence.mybatis.mappers.kunpeng;

import cn.tongdun.kunpeng.share.dataobject.DomainEventDO;

import java.util.Date;
import java.util.List;

/**
 * @Author: liang.chen
 * @Date: 2020/4/9 下午4:58
 */
public interface DomainEventDAO {

    List<DomainEventDO> queryByGmtModify(Date gmtModified);
}
