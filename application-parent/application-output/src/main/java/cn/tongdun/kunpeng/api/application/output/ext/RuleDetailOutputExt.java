package cn.tongdun.kunpeng.api.application.output.ext;

import cn.fraudmetrix.module.tdrule.model.IDetail;
import cn.fraudmetrix.module.tdrule.util.DetailCallable;
import cn.tongdun.kunpeng.api.engine.model.decisionresult.DecisionResultType;
import cn.tongdun.kunpeng.api.engine.model.decisionresult.DecisionResultTypeCache;
import cn.tongdun.kunpeng.client.data.HitRule;
import cn.tongdun.kunpeng.client.data.PolicyResult;
import cn.tongdun.kunpeng.client.data.RiskResponse;
import cn.tongdun.kunpeng.common.data.AbstractFraudContext;
import cn.tongdun.kunpeng.common.data.BizScenario;
import cn.tongdun.kunpeng.common.data.PolicyResponse;
import cn.tongdun.kunpeng.common.data.SubPolicyResponse;
import cn.tongdun.tdframework.core.extension.Extension;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 详情输出扩展点默认实现
 * @Author: liang.chen
 * @Date: 2020/2/10 下午9:44
 */
@Extension(business = BizScenario.DEFAULT,tenant = BizScenario.DEFAULT,partner = BizScenario.DEFAULT)
public class RuleDetailOutputExt implements IRuleDetailOutputExtPt {


    @Autowired
    private DecisionResultTypeCache decisionResultTypeCache;

    @Override
    public boolean ruleDetailOutput(AbstractFraudContext context,RiskResponse response){

        Map<String, Map<String, DetailCallable>> functionHitDetail = context.getFunctionHitDetail();
        if(functionHitDetail == null || functionHitDetail.isEmpty()){
            return true;
        }

        Map<String,Object> details = new HashMap<>();
        for(Map<String, DetailCallable> ruleConditionDetails : functionHitDetail.values()){
            for(Map.Entry<String, DetailCallable> entry: ruleConditionDetails.entrySet()){
                details.put(entry.getKey(), entry.getValue().call());
            }
        }

        response.setHit_rule_detail_info(details);

        return true;
    }
}
