package cn.tongdun.kunpeng.api.engine.model.access;

import cn.tongdun.kunpeng.api.engine.model.StatusEntity;
import lombok.Data;

import java.util.List;

/**
 * @author: yuanhang
 * @date: 2020-06-10 14:29
 **/
@Data
public class AccessBusiness extends StatusEntity {

    /**
     * 合作方标识
     */
    private String partnerCode;

    /**
     * 应用名称
     */
    private String appName;

    /**
     * 业务接入名称
     */
    private String accessName;

    /**
     * 业务接入标识
     */
    private String accessTag;

    /**
     * 接入参数
     */
    private List<AccessParam> accessParams;

    /**
     * 状态,默认 0:关闭，1:开启
     */
    private Integer status;

    /**
     * 是否删除,默认0:未删除，1:已删除
     */
    private Integer isDeleted;

}
