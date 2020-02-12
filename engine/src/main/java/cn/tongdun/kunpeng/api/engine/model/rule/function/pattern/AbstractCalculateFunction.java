package cn.tongdun.kunpeng.api.engine.model.rule.function.pattern;

import cn.fraudmetrix.module.tdrule.eval.*;
import cn.fraudmetrix.module.tdrule.function.AbstractFunction;
import cn.fraudmetrix.module.tdrule.function.FunctionDesc;
import cn.fraudmetrix.module.tdrule.model.FunctionParam;
import cn.fraudmetrix.module.tdrule.util.FunctionLoader;
import cn.tongdun.kunpeng.api.engine.constant.PolicyIndexConstant;
import org.apache.commons.lang3.StringUtils;

/**
 * @Author: liuq
 * @Date: 2020/2/11 2:12 PM
 */
public abstract class AbstractCalculateFunction extends AbstractFunction {


    protected Variable buildVariable(FunctionParam functionParam, Class<?> clazz) {
        if (null == functionParam) {
            return null;
        }
        switch (functionParam.getType()) {
            case "input":
                return new Literal(functionParam.getValue(), clazz);
            case "GAEA_INDICATRIX":
                return new PlatformIndex(functionParam.getValue(), false);
            case "index":
                if (functionParam.getExtProperty(PolicyIndexConstant.POLICY_INDEX_STAGE_TAG) != null
                        && StringUtils.equalsIgnoreCase(functionParam.getExtProperty(PolicyIndexConstant.POLICY_INDEX_STAGE_TAG).toString(), "true")) {
                    FunctionDesc functionDesc = (FunctionDesc) functionParam.getExtProperty(PolicyIndexConstant.POLICY_INDEX_FUNC_DESC);
                    if (null == functionDesc) {
                        throw new IllegalArgumentException("AbstractCalculateFunction type is index and is policyIndex stage,but no functionDesc!");
                    }
                    return FunctionLoader.getFunction(functionDesc);
                } else {
                    return new PolicyIndex(functionParam.getValue());
                }
            case "context":
                return new Field(functionParam.getValue(), "Double");
            default:
                throw new IllegalArgumentException("AbstractCalculateFunction type is:" + functionParam.getType() + ",cann't use this method,please parse yourself!");
        }
    }
}
