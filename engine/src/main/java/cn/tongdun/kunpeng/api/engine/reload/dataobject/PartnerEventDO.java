package cn.tongdun.kunpeng.api.engine.reload.dataobject;

import cn.tongdun.kunpeng.api.engine.model.VersionedEntity;
import lombok.Data;

/**
 * @Author: liang.chen
 * @Date: 2020/3/27 上午10:07
 */
@Data
public class PartnerEventDO  extends DomainEventDO {

    private String            partnerCode;                             // 合作标识
}