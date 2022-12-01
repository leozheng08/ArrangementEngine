package cn.tongdun.kunpeng.api.application.activity.common;

import cn.fraudmetrix.alliance.intf.entity.MobilPhoneRuleObj;
import cn.fraudmetrix.horde.biz.entity.IpReputationRulesObj;
import cn.tongdun.kunpeng.api.common.data.SubReasonCode;
import cn.tongdun.kunpeng.client.data.IRiskResponse;
import cn.tongdun.kunpeng.share.json.JSON;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.Map;
import java.util.Set;

/**
 * Activity消息内容
 * @Author: liang.chen
 * @Date: 2020/3/4 下午2:31
 */
@Data
public class ActitivyMsg implements IActitivyMsg {

    //交易流水号
    private String sequenceId;

    //消息发送时间
    private long produceTime;

    //经处理后的各字段值，包含请求入参映射的字段值，或groovy脚本生成的字段值
    private Map<String,Object> request;

    //应答内容
    private IRiskResponse response;

    //详细的错误子码
    private Set<SubReasonCode> subReasonCodes;

    /**
     * geoip信息
     */
    private GeoipInfo geoipEntity;
    /**
     * 设备指纹信息
     */
    private Map<String, Object> deviceInfo;
    /**
     * ip画像的结果
     */
    private IpReputationRulesObj ipReputationRules;
    /**
     * 手机画像的结果
     */
    private MobilPhoneRuleObj mobilePhoneRule;
    /**
     * 消息的key
     * @return
     */
    @Override
    @JsonIgnore
    public String getMessageKey(){
        return sequenceId;
    }

    /**
     * 生成activity消息
     * @return
     */
    @Override
    public String toJsonString(){
        return JSON.toJSONString(this);
    }
}
