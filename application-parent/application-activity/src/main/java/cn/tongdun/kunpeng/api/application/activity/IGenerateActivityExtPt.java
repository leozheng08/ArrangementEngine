package cn.tongdun.kunpeng.api.application.activity;

import cn.tongdun.kunpeng.common.data.BizScenario;
import cn.tongdun.kunpeng.common.data.QueueItem;
import cn.tongdun.tdframework.core.extension.Extension;
import cn.tongdun.tdframework.core.extension.IExtensionPoint;

/**
 * 生成Activity消息的扩展点
 * @Author: liang.chen
 * @Date: 2020/3/4 下午2:52
 */
@Extension(business = BizScenario.DEFAULT,tenant = BizScenario.DEFAULT,partner = BizScenario.DEFAULT)
public interface IGenerateActivityExtPt extends IExtensionPoint {

    /**
     * 根据出入参、上下文生成Activity消息
     * @param queueItem
     * @return
     */
    IActitivyMsg generateActivity(QueueItem queueItem);
}
