package cn.tongdun.kunpeng.api.basedata.step.device;

import cn.tongdun.kunpeng.api.application.step.IRiskStep;
import cn.tongdun.kunpeng.api.application.step.Risk;
import cn.tongdun.kunpeng.api.basedata.step.device.ext.IFpGetAppTypeExtPt;
import cn.tongdun.kunpeng.client.data.IRiskResponse;
import cn.tongdun.kunpeng.client.data.RiskRequest;
import cn.tongdun.kunpeng.api.common.data.AbstractFraudContext;
import cn.tongdun.tdframework.core.extension.ExtensionExecutor;
import cn.tongdun.tdframework.core.pipeline.Step;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 设备指纹信息获取
 * @Author: liang.chen
 * @Date: 2020/2/10 下午2:19
 */
@Component
@Step(pipeline = Risk.NAME,phase = Risk.BASIC_DATA)
public class DeviceInfoStep implements IRiskStep{

    @Autowired
    private ExtensionExecutor extensionExecutor;

    @Override
    public boolean invoke(AbstractFraudContext context, IRiskResponse response, RiskRequest request){

        //取得应用类型，并调用到上下文中
        //从black_box的base64 解码后json串，取得appType.(注：forseti-api是根据传的app_name取得appType)
        String appType = getAppType(context);


        //调用设备指纹之前，调用参数的组织

        //调用设备指纹




        return true;
    }

    /**
     * 取得应用类型
     * @param context
     * @return
     */
    private String getAppType(AbstractFraudContext context){
        String appType = extensionExecutor.execute(IFpGetAppTypeExtPt.class, context.getBizScenario(),
                extension -> extension.getAppType(context));
        return appType;
    }


}
