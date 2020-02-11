package cn.tongdun.kunpeng.api.basedata.fp;

import cn.tongdun.kunpeng.api.application.step.IRiskStep;
import cn.tongdun.kunpeng.api.application.step.Risk;
import cn.tongdun.kunpeng.api.basedata.fp.ext.IFpGetAppTypeExtPt;
import cn.tongdun.kunpeng.common.data.AbstractFraudContext;
import cn.tongdun.kunpeng.common.data.RiskResponse;
import cn.tongdun.tdframework.core.extension.ExtensionExecutor;
import cn.tongdun.tdframework.core.pipeline.Step;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

/**
 * @Author: liang.chen
 * @Date: 2020/2/10 下午2:19
 */
@Step(pipeline = Risk.NAME,phase = Risk.BASIC_DATA)
public class DeviceInfoStep implements IRiskStep{

    @Autowired
    private ExtensionExecutor extensionExecutor;

    @Override
    public boolean invoke(AbstractFraudContext context, RiskResponse response, Map<String, String> request){

        //取得应用类型，并调用到上下文中
        String appType = getAppType(context);
        //todo context.setAppType()



        return true;
    }

    /**
     * 取得应用类型
     * @param context
     * @return
     */
    private String getAppType(AbstractFraudContext context){
        String appType = extensionExecutor.execute(IFpGetAppTypeExtPt.class, context.getBizScenario(), extension -> extension.getAppType(context));
        return appType;
    }


}
