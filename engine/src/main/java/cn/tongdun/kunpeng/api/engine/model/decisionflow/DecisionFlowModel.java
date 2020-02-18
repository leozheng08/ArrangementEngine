package cn.tongdun.kunpeng.api.engine.model.decisionflow;

import lombok.Data;

import java.util.List;

@Data
public class DecisionFlowModel {

    private String uuid;

    private List<ModelParamInfo> inputList;

    private List<ModelParamInfo> outputList;

    private boolean isNewModel;

    private String modelVersion;

    private boolean isRiskServiceOutput;

}
