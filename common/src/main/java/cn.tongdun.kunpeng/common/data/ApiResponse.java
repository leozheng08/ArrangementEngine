/*
 * Copyright 2014 FraudMetrix.cn All right reserved. This software is the
 * confidential and proprietary information of FraudMetrix.cn ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with FraudMetrix.cn.
 */
package cn.tongdun.kunpeng.common.data;

import java.io.Serializable;

/**
 * @author zxb 2014年2月27日 下午3:30:42
 */
public class ApiResponse implements Serializable {

    private static final long serialVersionUID = 4152462611121573434L;
    protected Boolean         success          = false;               // 执行是否成功，不成功时对应reason_code
    protected String          reason_code;                            // 错误码及原因描述，正常执行完扫描时为空
    protected Object          attribute;                              // 可以放额外的数据

    public Object getAttribute() {
        return attribute;
    }

    public void setAttribute(Object attribute) {
        this.attribute = attribute;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getReason_code() {
        return reason_code;
    }

    public void setReason_code(String reason_code) {
        this.reason_code = reason_code;
    }

}
