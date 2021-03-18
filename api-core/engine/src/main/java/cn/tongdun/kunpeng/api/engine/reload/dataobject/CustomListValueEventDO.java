package cn.tongdun.kunpeng.api.engine.reload.dataobject;

import lombok.Data;

/**
 * @Author: liang.chen
 * @Date: 2020/3/27 下午3:30
 */
@Data
public class CustomListValueEventDO extends EventDO {

    /**
     * 名单值
     */
    private String dataValue;

    /**
     * 列表uuid
     */
    private String customListUuid;
}
