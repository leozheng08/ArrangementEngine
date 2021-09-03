package cn.tongdun.kunpeng.api.intf.adapter.dubbo;

import cn.tongdun.ddd.common.result.SingleResult;
import cn.tongdun.kunpeng.api.engine.model.script.groovy.GroovyClassGenerator;
import cn.tongdun.kunpeng.client.api.IDynamicScriptCheck;
import cn.tongdun.kunpeng.client.data.ScriptType;
import cn.tongdun.kunpeng.client.dto.DynamicScriptDTO;
import org.apache.commons.lang3.StringUtils;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * @Author: changkai.yang
 * @Date: 2020/4/20 上午11:15
 */
public class DynamicScriptCheckImpl implements IDynamicScriptCheck {

    @Override
    public SingleResult<Boolean> checkDynamicScript(DynamicScriptDTO dynamicScriptDTO) {
        try {
            String code = dynamicScriptDTO.getScriptCode();
            Long id = dynamicScriptDTO.getId();
            String scriptType = dynamicScriptDTO.getScriptType();
            if (ScriptType.isValid(scriptType)) {
                return SingleResult.failure(400, String.format("script type [%s] is invalid", scriptType));
            }
            if (StringUtils.equals(scriptType, "groovy")) {
                GroovyClassGenerator.compileMethod(id, code);
            }
            return SingleResult.success(true);
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            SingleResult singleResult = SingleResult.failure(500, sw.toString());
            singleResult.setData(false);
            return singleResult;
        }
    }
}
