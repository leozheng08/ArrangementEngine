package cn.tongdun.kunpeng.api.engine.util;

import cn.fraudmetrix.module.tdrule.constant.FieldConstants;
import cn.fraudmetrix.module.tdrule.eval.*;
import cn.fraudmetrix.module.tdrule.exception.ParseException;
import cn.fraudmetrix.module.tdrule.model.ConditionParam;

/**
 * @Author: liuq
 * @Date: 2020/2/13 6:44 PM
 */
public class ConditionParamUtils {

    /**
     * 从条件的左右描述转为变量
     *
     * @param conditionParam
     * @return
     */
    public static Variable parseConditionParam(ConditionParam conditionParam) {
        if (null == conditionParam) {
            return null;
        }
        switch (conditionParam.getFieldType()) {
            case INPUT:
                return new Literal(conditionParam.getValue(), conditionParam.getDataType());
            case CONTEXT:
                return new Field(conditionParam.getName(), conditionParam.getDataType());
            case FUNC:
                throw new ParseException("ConditionParamUtils parseConditionParam error,Function can't user this method!conditionId:" + conditionParam.getConditionId());
            case POLICY_INDEX:
                return new PolicyIndex(conditionParam.getName());
            case PLATFORM_INDEX:
                Object isUseOriginValue = conditionParam.getExtProperty(FieldConstants.INDEX_USE_ORIGIN_VALUE);
                if (null != isUseOriginValue && Boolean.valueOf(isUseOriginValue.toString())) {
                    return new PlatformIndex(conditionParam.getName(), true);
                } else {
                    return new PlatformIndex(conditionParam.getName(), false);
                }
            default:
                throw new ParseException("illegal filedType:" + conditionParam.getFieldType() + ",conditionId:" + conditionParam.getConditionId());
        }
    }
}
