package cn.tongdun.kunpeng.api.application.activity;

import cn.tongdun.kunpeng.api.application.step.ext.response.haiwai.RiskResponse;
import cn.tongdun.kunpeng.client.data.IRiskResponse;
import cn.tongdun.kunpeng.common.data.SubReasonCode;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.NameFilter;
import com.alibaba.fastjson.serializer.SerializeFilter;
import com.alibaba.fastjson.serializer.SerializerFeature;
import lombok.Data;

import java.util.Date;
import java.util.List;
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
    private Map<String,Object> activity;

    //应答内容
    private IRiskResponse response;

    //详细的错误子码
    private Set<SubReasonCode> subReasonCodes;


    /**
     * 消息的key
     * @return
     */
    @Override
    @JSONField(serialize=false)
    public String getMessageKey(){
        return sequenceId;
    }

    /**
     * 生成activity消息
     * @return
     */
    @Override
    public String toJsonString(){
        return JSON.toJSONString(this,SerializerFeature.DisableCircularReferenceDetect);
    }
}
