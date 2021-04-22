package cn.tongdun.kunpeng.api.application.content.function.image.fucntionV2;


import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class ConditionModel implements Serializable {

    private String model;
    private Map<String, List<ConditionGroup>> labelToConditionGroups;

    public String getModel() {
        return model;
    }

    public Map<String, List<ConditionGroup>> getLabelToConditionGroups() {
        return labelToConditionGroups;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public void setLabelToConditionGroups(Map<String, List<ConditionGroup>> labelToConditionGroups) {
        this.labelToConditionGroups = labelToConditionGroups;
    }
}
