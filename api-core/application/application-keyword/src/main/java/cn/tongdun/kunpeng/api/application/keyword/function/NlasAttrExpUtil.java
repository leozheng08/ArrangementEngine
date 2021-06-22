package cn.tongdun.kunpeng.api.application.keyword.function;

import cn.tongdun.kunpeng.api.application.context.FraudContext;
import cn.tongdun.kunpeng.api.engine.model.rule.util.VelocityHelper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * Created by wangrenjie on 16/10/27.
 * 关键词规则属性表达式解析工具类
 */
public class NlasAttrExpUtil {

    private static Logger logger = LoggerFactory.getLogger(NlasAttrExpUtil.class);

    public static boolean calc(FraudContext context, Map<String, Object> wordAttrValues, List<AttrExp> attrExps) {
        if (CollectionUtils.isEmpty(attrExps)) {
            return true;
        }
        Stack<Boolean> stack = new Stack<Boolean>();
        for (AttrExp attrExp : attrExps) {
            try {
                boolean flag = parseAttrExp(context, wordAttrValues, attrExp);
                if (StringUtils.isEmpty(attrExp.getAndOr())) {
                    stack.push(flag);
                } else if (AttrExp.AndOrEnum.and.name().equals(attrExp.getAndOr())) {
                    Boolean top = stack.pop();
                    stack.push(top && flag);
                } else if (AttrExp.AndOrEnum.or.name().equals(attrExp.getAndOr())) {
                    stack.push(flag);
                }
            } catch (Exception e) {
                logger.info("关键词表达式处理出错：{}", e.getMessage(), e);
            }
        }
        boolean result = false;
        while (!stack.isEmpty()) {
            result = stack.pop() || result;
        }
        return result;
    }

    private static boolean parseAttrExp(FraudContext context, Map<String, Object> wordAttrValues, AttrExp attrExp) {
        String left = attrExp.getLeft();
        Object leftObject = wordAttrValues.get(left);
        String leftValue = (null == leftObject) ? null : leftObject.toString();

        String operate = attrExp.getOperate();
        String right = attrExp.getRight();
        String rightValue = AttrExp.RightTypeEnum.context.name().equalsIgnoreCase(attrExp.getRightType())
                ? (String) VelocityHelper.getDimensionValue(context, right)
                : right;

        if (StringUtils.isEmpty(left)
                || StringUtils.isEmpty(operate)
                || (!AttrExp.OperateEnum.isNull.getValue().equals(operate)) && !AttrExp.OperateEnum.isNotNull.getValue().equals(operate) && StringUtils.isEmpty(leftValue)
                || (!AttrExp.OperateEnum.isNull.getValue().equals(operate)) && !AttrExp.OperateEnum.isNotNull.getValue().equals(operate) && StringUtils.isEmpty(rightValue)) {
            return true;
        }

        if (AttrExp.OperateEnum.isNull.getValue().equals(operate)) {
            return StringUtils.isEmpty(leftValue);
        } else if (AttrExp.OperateEnum.isNotNull.getValue().equals(operate)) {
            return StringUtils.isNotEmpty(leftValue);
        } else if (AttrExp.OperateEnum.dayu.getValue().equals(operate)) {
            return leftValue.compareToIgnoreCase(rightValue) > 0;
        } else if (AttrExp.OperateEnum.dayudengyu.getValue().equals(operate)) {
            return leftValue.compareToIgnoreCase(rightValue) >= 0;
        } else if (AttrExp.OperateEnum.dengyu.getValue().equals(operate)) {
            return leftValue.equalsIgnoreCase(rightValue);
        } else if (AttrExp.OperateEnum.budengyu.getValue().equals(operate)) {
            return !leftValue.equalsIgnoreCase(rightValue);
        } else if (AttrExp.OperateEnum.xiaoyudengyu.getValue().equals(operate)) {
            return leftValue.compareToIgnoreCase(rightValue) <= 0;
        } else if (AttrExp.OperateEnum.xiaoyu.getValue().equals(operate)) {
            return leftValue.compareToIgnoreCase(rightValue) < 0;
        }
        return true;
    }
}
