package cn.tongdun.kunpeng.api.application.content.function.image;

import lombok.Data;

/**
 * 图像识别模型
 */
@Data
public class ModelResult {
    private String name;
    private String camelName;
    private String desc;

    public ModelResult(String modelResultName, String modelResultCamelName, String modelResultDesc) {
        this.name = name;
        this.camelName = name;
        this.desc = desc;
    }
}
