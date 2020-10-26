package cn.tongdun.kunpeng.api.ruledetail;

import java.util.Map;
import cn.fraudmetrix.module.tdrule.rule.ConditionDetail;
/**
 * 自定义规则
 * @Author: liang.chen
 * @Date: 2020/2/6 下午9:29
 */
public class CustomDetail extends ConditionDetail{

    /**
     * 所有变量
     */
    private Map<String,Object> variables;

    public CustomDetail(){
        super("custom");
    }



}
