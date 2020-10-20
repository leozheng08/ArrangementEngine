package cn.tongdun.kunpeng.api.application.output.ext;

import cn.fraudmetrix.forseti.fp.model.constant.Android;
import cn.fraudmetrix.forseti.fp.model.constant.Ios;
import cn.fraudmetrix.forseti.fp.model.constant.Mini;
import cn.fraudmetrix.forseti.fp.model.constant.Web;
import cn.tongdun.kunpeng.api.common.data.*;
import cn.tongdun.kunpeng.api.engine.model.decisionresult.DecisionResultType;
import cn.tongdun.kunpeng.api.engine.model.decisionresult.DecisionResultTypeCache;
import cn.tongdun.kunpeng.client.data.*;
import cn.tongdun.kunpeng.client.data.impl.underline.RiskResponse;
import cn.tongdun.tdframework.core.extension.Extension;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 海外租户实现
 * @Author: liang.chen
 * @Date: 2020/2/10 下午9:44
 */
@Extension(tenant = "haiwai",business = BizScenario.DEFAULT,partner = BizScenario.DEFAULT)
public class HaiwaiGeneralOutputExt implements IGeneralOutputExtPt {


    @Autowired
    private DecisionResultTypeCache decisionResultTypeCache;

    @Override
    public boolean generalOutput(AbstractFraudContext context, IRiskResponse response, RiskRequest request){

        PolicyResponse policyResponse = context.getPolicyResponse();
        if(policyResponse == null){
            return true;
        }
        response.setSuccess(policyResponse.isSuccess());

        response.setFinalDecision(policyResponse.getDecision());
        response.setFinalScore(policyResponse.getScore());
        response.setPolicyUuid(policyResponse.getPolicyUuid());
        response.setPolicyName(policyResponse.getPolicyName());


        if (policyResponse.getSubPolicyResponses() == null || (policyResponse.getSubPolicyResponses().isEmpty())) {
            return true;
        }


        List<ISubPolicyResult> policyResults = new ArrayList<ISubPolicyResult>(policyResponse.getSubPolicyResponses().size());
        List<IHitRule> rules = new ArrayList<IHitRule>();
        StringBuilder riskTypeBuilder = new StringBuilder();
        for (SubPolicyResponse subPolicyResponse : policyResponse.getSubPolicyResponses()) {
            if (StringUtils.isNotBlank(subPolicyResponse.getRiskType())) {
                DecisionResultType decisionResultTyp = decisionResultTypeCache.get(subPolicyResponse.getDecision());

                if(decisionResultTyp != null && decisionResultTyp.isRisky()){
                    if (riskTypeBuilder.length() > 0) {
                        riskTypeBuilder.append(",");
                    }
                    riskTypeBuilder.append(subPolicyResponse.getRiskType()).append("_").append(decisionResultTyp.getCode().toLowerCase());
                }
            }
            ISubPolicyResult subPolicyResult = response.getFactory().newSubPolicyResult();
            subPolicyResult.setSubPolicyUuid(subPolicyResponse.getSubPolicyUuid());
            subPolicyResult.setSubPolicyName(subPolicyResponse.getSubPolicyName());
            subPolicyResult.setPolicyMode(subPolicyResponse.getPolicyMode());
            subPolicyResult.setPolicyScore(subPolicyResponse.getScore());
            subPolicyResult.setRiskType(subPolicyResponse.getRiskType());
            subPolicyResult.setPolicyDecision(subPolicyResponse.getDecision());

            List<IHitRule> hitRuleList = getHitRules(response.getFactory(),subPolicyResponse);
            if(hitRuleList != null && !hitRuleList.isEmpty()) {
                subPolicyResult.setHitRules(hitRuleList);
                rules.addAll(hitRuleList);
            }
            policyResults.add(subPolicyResult);
        }
        response.setSubPolicys(policyResults);
        if (rules.size() > 0) {
            ((RiskResponse)response).setHitRules(rules);
        }

        Map external = Maps.newHashMap();
        Map deviceInfo = context.getDeviceInfo();
        if (deviceInfo != null){
            String appOs = null;
            if (null != context.getAppType()) {
                appOs = (String)deviceInfo.get("appOs");
            }
            if (StringUtils.isNotEmpty(appOs)) {
                Map outputDeviceInfo = postProcessDeviceInfo(appOs, deviceInfo);
                external.put("deviceInfo", outputDeviceInfo);
            } else {
                external.put("deviceInfo", deviceInfo);
            }
        }
        response.setCustomPolicyResult(external);


        //风险最大的子策略
//        SubPolicyResponse finalResponse = policyResponse.getFinalSubPolicyResponse();
//        if(finalResponse != null) {
//            response.setSubPolicyName(finalResponse.getSubPolicyName());
//            response.setRiskType(riskTypeBuilder.toString());
//        }
        return true;
    }


    private List<IHitRule> getHitRules(IRiskResponseFactory factory,SubPolicyResponse subPolicyResponse){
        List<RuleResponse> ruleResponseList = subPolicyResponse.getRuleResponses();
        if(ruleResponseList == null || ruleResponseList.isEmpty()){
            return null;
        }

        List<IHitRule> hitRuleList = new ArrayList<>();
        for(RuleResponse ruleResponse:ruleResponseList){
            if (ruleResponse.isHit()) {
                IHitRule hitRule = createHitRule(factory,ruleResponse);
                hitRuleList.add(hitRule);
            }
        }

        return hitRuleList;
    }

    private IHitRule createHitRule(IRiskResponseFactory factory,RuleResponse ruleResponse){
        IHitRule hitRule = factory.newHitRule();
        hitRule.setId(ruleResponse.getId());
        hitRule.setName(ruleResponse.getName());
        hitRule.setUuid(ruleResponse.getUuid());
        hitRule.setParentUuid(ruleResponse.getParentUuid());
        hitRule.setScore(ruleResponse.getScore());
        //决策结果,如Accept、Review、Reject
        hitRule.setDecision(ruleResponse.getDecision());
        return hitRule;
    }

    /**
     * 根据appType精简输出
     * @param appType
     * @param deviceInfo
     */
    private Map<String, Object> postProcessDeviceInfo(String appType, Map<String,Object> deviceInfo) {
        Map<String, Object> result = Maps.newHashMap();
        if (StringUtils.isEmpty(appType)) {
            return deviceInfo;
        }
        if ("ios".equalsIgnoreCase(appType)) {
            Ios[] ios = Ios.values();
            Arrays.asList(ios).stream().forEach(r -> result.put(r.getKey(), deviceInfo.get(r.getKey())));
        } else if ("web".equalsIgnoreCase(appType)) {
            Web[] webs = Web.values();
            Arrays.asList(webs).stream().forEach(r -> result.put(r.getKey(), deviceInfo.get(r.getKey())));
        } else if ("android".equalsIgnoreCase(appType)) {
            Android[] androids = Android.values();
            Arrays.asList(androids).stream().forEach(r -> result.put(r.getKey(), deviceInfo.get(r.getKey())));
        } else if ("mini".equalsIgnoreCase(appType)) {
            Mini[] minis = Mini.values();
            Arrays.asList(minis).stream().forEach(r -> result.put(r.getKey(), deviceInfo.get(r.getKey())));
        }

        if (null != deviceInfo.get("geoIp")) {
            result.put("geoIp", deviceInfo.get("geoIp"));
        }
        return result;
    }
}
