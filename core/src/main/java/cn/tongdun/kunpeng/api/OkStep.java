package cn.tongdun.kunpeng.api;

import cn.tongdun.kunpeng.api.engine.load.ILoad;
import cn.tongdun.kunpeng.api.engine.load.LoadPipeline;
import cn.tongdun.tdframework.core.pipeline.Step;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 开启OK页面，为启动加载流程的最后一个步骤
 * @Author: liang.chen
 * @Date: 2020/3/19 下午5:02
 */
@Component
@Step(pipeline = LoadPipeline.NAME, phase = LoadPipeline.OK)
public class OkStep implements ILoad {

    @Autowired
    private AppMain aAppMain;

    @Override
    public boolean load(){
        aAppMain.setOk();
        return true;
    }
}
