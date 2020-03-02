package cn.tongdun.kunpeng.api.basedata.step.ipreputation;

import cn.tongdun.kunpeng.api.application.step.IRiskStep;
import cn.tongdun.kunpeng.api.application.step.Risk;
import cn.tongdun.kunpeng.client.data.IRiskResponse;
import cn.tongdun.kunpeng.common.data.AbstractFraudContext;
import cn.tongdun.tdframework.core.extension.ExtensionExecutor;
import cn.tongdun.tdframework.core.pipeline.Step;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * ip画像信息获取
 * @Author: liang.chen
 * @Date: 2020/2/10 下午2:19
 */
@Component
@Step(pipeline = Risk.NAME,phase = Risk.BASIC_DATA)
public class IpReputationStep implements IRiskStep{

    @Autowired
    private ExtensionExecutor extensionExecutor;

    @Override
    public boolean invoke(AbstractFraudContext context, IRiskResponse response, Map<String, String> request){

        //调用Ip画像之前，调用参数的组织

        //调用Ip画像

        //Ip画像信息处理

        return true;
    }

}
