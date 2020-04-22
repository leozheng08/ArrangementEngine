package cn.tongdun.kunpeng.api.engine.model.intfdefinition;

import com.google.common.collect.Maps;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Map;

public enum APIResult {

    DUBBO_API_RESULT_MISSING_PARAMETER(false,"100","缺少参数"),
    DUBBO_API_RESULT_SUCCESS(true,"200","调用外部服务成功"),
    DUBBO_API_RESULT_INTERNAL_ERROR(false,"500","内部服务器错误"),
    DUBBO_API_RESULT_EXTERNAL_CALL_ERROR(false,"501","调用外部服务失败"),
    DUBBO_API_RESULT_TIMEOUT(false,"502","调用外部服务超时"),
    DUBBO_API_RESULT_SKIP(false,"601","调用外部接口降级");


    private boolean success;
    private String  reasonCode;
    private String  reasonDesc;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getReasonCode() {
        return reasonCode;
    }

    public void setReasonCode(String reasonCode) {
        this.reasonCode = reasonCode;
    }

    public String getReasonDesc() {
        return reasonDesc;
    }

    public void setReasonDesc(String reasonDesc) {
        this.reasonDesc = reasonDesc;
    }

    APIResult(boolean success, String reasonCode, String reasonDesc){
        this.success = success;
        this.reasonCode = reasonCode;
        this.reasonDesc = reasonDesc;
    }

    public Map<String,Object> toMap(){
        Map<String, Object> map = Maps.newHashMap();
        map.put("success", this.success);
        map.put("reasonCode", this.reasonCode);
        map.put("reasonDesc", this.reasonDesc);
        return map;
    }


    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SIMPLE_STYLE);
    }
}

