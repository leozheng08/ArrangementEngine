package cn.tongdun.kunpeng.api.engine.model.access;

import cn.tongdun.kunpeng.api.engine.model.VersionedEntity;
import lombok.Data;

/**
 * @author: yuanhang
 * @date: 2020-06-12 10:52
 **/
@Data
public class AccessParam extends VersionedEntity {

    /**
     * uuid
     *
     */
    private String accessUuid;

    /**
     * 保留字段
     */
    private String parentParamsId;

    /**
     * 用户参数
     */
    private String accessParam;

    /**
     * 字段名称
     */
    private String fieldName;

    /**
     * 字段类型
     */
    private String paramType;

    /**
     * 输入/输出
     */
    private String inputOutput;

    /**
     * 是否必传/输出
     */
    private Byte isMust;

}
