package cn.tongdun.kunpeng.api.engine.dto;

import lombok.Data;

/**
 * @description:  param dto
 * param like this: {"name":"calcField","type":"string","value":"partnerCode"},{"name":"definitionList","type":"string","value":"sdgjcb"}
 * @author: zhongxiang.wang
 * @date: 2021-01-28 15:37
 */
@Data
public class RuleParamDTO {

    private String name;
    private String type;
    private String value;

}
