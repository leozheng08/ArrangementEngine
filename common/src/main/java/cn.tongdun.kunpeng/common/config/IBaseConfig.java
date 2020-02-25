package cn.tongdun.kunpeng.common.config;

import cn.tongdun.kunpeng.common.Constant;

/**
 * 公共的配置信息
 * @Author: liang.chen
 * @Date: 2020/2/25 下午8:09
 */
public interface IBaseConfig {

    //根据event_type区分业务类型，如credit信贷，anti_fraud反欺诈
    String getBusinessByEventType(String eventType);
}
