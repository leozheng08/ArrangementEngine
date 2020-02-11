package cn.tongdun.kunpeng.api.engine.model.policy;

import cn.tongdun.ddd.common.domain.Entity;
import cn.tongdun.kunpeng.api.engine.model.runmode.AbstractRunMode;
import lombok.Data;

import java.util.List;

/**
 * 策略。将会放到缓存中，属性尽量简化，不要保留跟策略运行无关的属性
 * @Author: liang.chen
 * @Date: 2019/12/16 下午3:21
 */
@Data
public class Policy extends Entity {
    private String            policyUuId;
    private String            name;
    private String            partnerCode;
    private String            appName;
    private String            appType;
    private String            appDisplayName;
    private String            eventId;
    private String            eventType;
    private String            version;

    private String           riskType;

    private String            antlrCode;
    private boolean           selfOutput;
    private String            dataPermission;
    //执行方式，并行执行子策略、决策流、决策表、决策树
    private AbstractRunMode runMode;

    private List<String> subPolicyList;
}
