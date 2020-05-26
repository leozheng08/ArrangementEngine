package cn.tongdun.kunpeng.api.engine.model.dictionary;

import cn.tongdun.kunpeng.api.engine.model.VersionedEntity;
import lombok.Data;

/**
 * @Author: liuq
 * @Date: 2020/5/25 8:30 下午
 */
@Data
public class Dictionary extends VersionedEntity {
    // 键
    private String key;
    // 值
    private String value;

}
