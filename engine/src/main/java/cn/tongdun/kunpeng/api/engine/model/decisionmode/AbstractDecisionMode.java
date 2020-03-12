package cn.tongdun.kunpeng.api.engine.model.decisionmode;

import cn.tongdun.kunpeng.api.engine.model.VersionedEntity;
import lombok.Data;

/**
 * @Author: liang.chen
 * @Date: 2019/12/17 上午11:47
 */
@Data
public class AbstractDecisionMode extends VersionedEntity {
    //策略id
    private String policyUuid;
}
