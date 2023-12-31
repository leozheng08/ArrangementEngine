package cn.tongdun.kunpeng.api.engine.model;

import lombok.Data;

import java.util.Date;

/**
 * 带有变更版本的实体
 * @Author: liang.chen
 * @Date: 2020/2/16 上午11:51
 */
@Data
public abstract class VersionedEntity extends UUIDEntity {

    private Date gmtModify;

    public long getModifiedVersion(){
        return gmtModify.getTime();
    }
}
