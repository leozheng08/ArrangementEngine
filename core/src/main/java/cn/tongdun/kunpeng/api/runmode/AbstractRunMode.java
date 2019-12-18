package cn.tongdun.kunpeng.api.runmode;

import cn.tongdun.kunpeng.api.policy.Policy;
import cn.tongdun.kunpeng.api.runtime.impl.DecisionTool;
import lombok.Data;

/**
 * @Author: liang.chen
 * @Date: 2019/12/17 上午11:47
 */
@Data
public class AbstractRunMode {
    //策略id
    private String policyUuid;
}
