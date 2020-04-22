package cn.tongdun.kunpeng.api.engine.model;

import lombok.Data;

import java.util.Date;

/**
 * @Author: liang.chen
 * @Date: 2020/2/16 上午11:51
 */
@Data
public abstract class UUIDEntity extends Entity {

    private String uuid;
}
