package cn.tongdun.kunpeng.api.engine.dto;

import cn.tongdun.ddd.common.dto.BaseDTO;

/**
 * 通用字典配置
 */
public class CommonDictDTO extends BaseDTO {
    private static final long serialVersionUID = -6819619762415295L;

    private String name;
    private String dName;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getdName() {
        return dName;
    }

    public void setdName(String dName) {
        this.dName = dName;
    }
}
