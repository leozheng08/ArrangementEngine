package cn.tongdun.kunpeng.api.engine.model.intfdefinition;

import java.util.Arrays;

/**
 * 决策流三方接口解析后的调用时准备参数
 * @author jie
 * @date 2021/1/13
 */
public class DecisionFlowInterfaceCallInfo {

    /**
     * 泛化调用参数类型
     */
    private String[] inputParamType;

    /**
     * 泛化调用参数值
     */
    private Object[] inputParamValue;

    public String[] getInputParamType() {
        return inputParamType;
    }

    public void setInputParamType(String[] inputParamType) {
        this.inputParamType = inputParamType;
    }

    public Object[] getInputParamValue() {
        return inputParamValue;
    }

    public void setInputParamValue(Object[] inputParamValue) {
        this.inputParamValue = inputParamValue;
    }

    @Override
    public String toString() {
        return "DecisionFlowInterfaceCallInfo{" +
                "inputParamType=" + Arrays.toString(inputParamType) +
                ", inputParamValue=" + Arrays.toString(inputParamValue) +
                '}';
    }
}
