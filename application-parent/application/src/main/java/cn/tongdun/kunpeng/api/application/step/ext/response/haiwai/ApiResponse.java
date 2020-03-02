package cn.tongdun.kunpeng.api.application.step.ext.response.haiwai;

import cn.tongdun.kunpeng.client.data.IApiResponse;
import com.alibaba.fastjson.annotation.JSONField;


public class ApiResponse implements IApiResponse {

    private static final long serialVersionUID = 4152462611121573434L;
    protected Boolean         success          = false;               // 执行是否成功，不成功时对应reason_code
    protected String          reasonCode;                            // 错误码及原因描述，正常执行完扫描时为空
    protected Object          attribute;                              // 可以放额外的数据

    public Object getAttribute() {
        return attribute;
    }

    public void setAttribute(Object attribute) {
        this.attribute = attribute;
    }

    @Override
    public boolean isSuccess() {
        return success;
    }

    @Override
    public void setSuccess(boolean success) {
        this.success = success;
    }

    @Override
    @JSONField(name="reason_code")
    public String getReasonCode() {
        return reasonCode;
    }

    @Override
    @JSONField(name="reason_code")
    public void setReasonCode(String reasonCode) {
        this.reasonCode = reasonCode;
    }

}
