package cn.tongdun.kunpeng.api.engine.model.subpolicy.mode;

import cn.tongdun.kunpeng.api.common.data.*;
import cn.tongdun.kunpeng.api.engine.model.subpolicy.SubPolicy;
import cn.tongdun.kunpeng.client.data.PolicyMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * 子策略执行，根据subPolicyUuid从缓存中取得子策略实体SubPolicy对象后运行。
 *
 * @Author: liang.chen
 * @Date: 2019/12/16 下午7:58
 */
@Component
public class FirstMatchPolicyModeExecuter extends AbstractPolicyModeExecuter {

    private static Logger logger = LoggerFactory.getLogger(AbstractPolicyModeExecuter.class);

    @PostConstruct
    public void init(){
        policyModeCache.put(PolicyMode.FirstMatch,this);
    }

    /**
     * 首次匹配
     *
     * @param subPolicy
     * @param context
     * @param subPolicyResponse
     */
    @Override
    public void executeMatch(SubPolicy subPolicy, AbstractFraudContext context, SubPolicyResponse subPolicyResponse) {
        executePorcess(subPolicy, context, subPolicyResponse, ruleResponse -> {
            subPolicyResponse.setScore(ruleResponse.getScore());
            subPolicyResponse.setDecision(ruleResponse.getDecision());
            //return true表示中断规则运行。
            return true;
        });
    }

}
