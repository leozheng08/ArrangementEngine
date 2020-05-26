package cn.tongdun.kunpeng.api.engine.dto;

import cn.tongdun.kunpeng.client.dto.CommonDTO;
import lombok.Data;

/**
 * @Author: liuq
 * @Date: 2020/5/25 8:27 下午
 */
@Data
public class DictionaryDTO extends CommonDTO {
    // 键
    private String key;
    // 值
    private String value;
}
