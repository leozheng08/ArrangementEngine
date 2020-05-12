package cn.tongdun.kunpeng.api.engine.model.policyindex;

import cn.fraudmetrix.module.tdrule.function.Function;
import cn.tongdun.kunpeng.api.engine.model.VersionedEntity;
import lombok.Data;

/**
 * @Author: liuq
 * @Date: 2020/2/18 10:03 AM
 */
@Data
public class PolicyIndex extends VersionedEntity {


    /**
     * 指标的计算函数
     */
    private Function calculateFunction;

    private String policyUuid;
}
