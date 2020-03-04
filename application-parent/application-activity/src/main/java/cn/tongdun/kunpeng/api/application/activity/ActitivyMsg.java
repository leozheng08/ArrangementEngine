package cn.tongdun.kunpeng.api.application.activity;

import cn.tongdun.kunpeng.api.application.step.ext.response.haiwai.RiskResponse;
import cn.tongdun.kunpeng.common.data.SubReasonCode;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Activity消息内容
 * @Author: liang.chen
 * @Date: 2020/3/4 下午2:31
 */
@Data
public class ActitivyMsg implements IActitivyMsg {

    //交易流水号
    private String seqId;

    //消息发送时间
    private Date produceTime;

    //经处理后的各字段值，包含请求入参映射的字段值，或groovy脚本生成的字段值
    private Map<String,Object> request;

    //应答内容
    private RiskResponse response;

    //详细的错误子码
    private List<SubReasonCode> subReasonCodes;



    /**
     * 生成activity消息
     * @return
     */
    @Override
    public String toJsonString(){
        return JSON.toJSONString(this, SerializerFeature.DisableCircularReferenceDetect);
    }
}