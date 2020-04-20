package cn.tongdun.kunpeng.client.api;

import cn.tongdun.ddd.common.result.SingleResult;
import cn.tongdun.kunpeng.client.dto.DynamicScriptDTO;

/**
 * @Author changkai.yang
 * @Date 2020/4/16 下午5:10
 */
public interface IDynamicScriptCheck {

    /**
     * 动态脚本检查
     * @param dynamicScriptDTO
     * @return
     */
    SingleResult<Boolean> checkDynamicScript(DynamicScriptDTO dynamicScriptDTO);
}
