package cn.tongdun.kunpeng.api.engine.model.decisionflow;

import lombok.Data;

@Data
public class ModelParam {

    /**
     * 模型变量code
     */
    private String field;

    /**
     * 引擎变量类型
     */
    private String rightFieldType;
    /**
     *引擎变量code
     */
    private String rightField;

    /**
     * 引擎变量名称
     */
    private String rightFieldName;
}
