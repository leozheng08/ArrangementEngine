package cn.tongdun.kunpeng.api.engine.model.decisionflow;

import lombok.Data;

import java.util.List;

@Data
public class ModelConfigInfo {

    /**
     * 模型uuid
     */
    private String uuid;

    /**
     * 模型版本
     */
    private String modelVersion;

    /**
     * 私有云版本的模型平台需要的参数，SAAS这边可以为空
     */
    private String modelType;

    private List<ModelParam> inputList;

    private List<ModelParam> outputList;

    /**
     * 公有云版本的模型平台需要的参数，北美这边可以为空
     */
    private boolean isNewModel;
    /**
     * 公有云版本的模型平台需要的参数，北美这边可以为空
     */
    private boolean isRiskServiceOutput;

}
