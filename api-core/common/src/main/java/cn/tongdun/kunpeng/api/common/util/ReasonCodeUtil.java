package cn.tongdun.kunpeng.api.common.util;

import cn.tongdun.kunpeng.api.common.data.AbstractFraudContext;
import cn.tongdun.kunpeng.api.common.data.ReasonCode;
import cn.tongdun.kunpeng.api.common.data.SubReasonCode;
import cn.tongdun.kunpeng.share.utils.TraceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeoutException;

public class ReasonCodeUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReasonCodeUtil.class);

    public static boolean isTimeout(Exception e){
        if(e == null){
            return false;
        }
        if(e instanceof InterruptedException || e instanceof TimeoutException){
            return true;
        }
        Throwable cause = e.getCause();
        if(cause == null){
            return false;
        }
        if(cause instanceof InterruptedException || cause instanceof TimeoutException || cause instanceof com.alibaba.dubbo.remoting.TimeoutException) {
            return true;
        }
        return false;
    }

    public static void add(AbstractFraudContext context, ReasonCode subReasonCode, String service) {
        try{
            context.addSubReasonCode(new SubReasonCode(subReasonCode.getCode(),subReasonCode.getDescription(),service));
        }
        catch (Exception e){
            LOGGER.warn(TraceUtils.getFormatTrace()+"sub_reason_code sub_reason_code_write_error");
        }
    }



}
