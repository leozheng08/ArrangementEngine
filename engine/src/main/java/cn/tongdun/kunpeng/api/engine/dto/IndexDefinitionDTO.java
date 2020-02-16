package cn.tongdun.kunpeng.api.engine.dto;

import cn.tongdun.ddd.common.domain.ConcurrencySafeEntity;
import lombok.Data;

import java.util.Date;

/**
 * 指标定义
 */
@Data
public class IndexDefinitionDTO extends CommonDTO {

    private static final long serialVersionUID = -7495719717393984207L;

    /**
     * 名称
     */
    private String name;

    /**
     * 指标模板
     */
    private String template;

    /**
     * 参数
     * http://wiki.tongdun.me/pages/viewpage.action?pageId=34439963
     */
    private String params;

    /**
     * 策略uuid
     */
    private String policyUuid;

    /**
     * 子策略
     */
    private String subPolicyUuid;

    /**
     * 描述
     */
    private String description;

    /**
     * 状态
     * 1：已启用
     * 0：未启用
     */
    private Integer status;
    private boolean deleted;
    private Date gmtDelete;


}
