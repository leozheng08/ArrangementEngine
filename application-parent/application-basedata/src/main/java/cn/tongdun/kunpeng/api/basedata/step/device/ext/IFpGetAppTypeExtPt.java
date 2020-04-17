package cn.tongdun.kunpeng.api.basedata.step.device.ext;

import cn.tongdun.kunpeng.api.common.data.AbstractFraudContext;
import cn.tongdun.tdframework.core.extension.IExtensionPoint;

/**
 * 确认appType	从black_box的base64 解码后json串，取得appType.(注：forseti-api是根据传的app_name取得appType)
 * @Author: liang.chen
 * @Date: 2020/2/10 下午3:11
 */
public interface IFpGetAppTypeExtPt extends IExtensionPoint {
    String getAppType(AbstractFraudContext context);
}
