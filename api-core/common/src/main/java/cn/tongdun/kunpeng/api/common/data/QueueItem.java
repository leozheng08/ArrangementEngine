package cn.tongdun.kunpeng.api.common.data;

import cn.tongdun.kunpeng.client.data.IRiskResponse;
import cn.tongdun.kunpeng.client.data.RiskRequest;

import java.io.Serializable;

public class QueueItem implements Serializable {

    private AbstractFraudContext context;
    private RiskRequest request;
    private IRiskResponse response;

    public QueueItem(){
    }
    public QueueItem(AbstractFraudContext context,IRiskResponse response,RiskRequest request){
        this.context = context;
        this.response = response;
        this.request = request;
    }

    public AbstractFraudContext getContext() {
        return context;
    }

    public void setContext(AbstractFraudContext context) {
        this.context = context;
    }

    public IRiskResponse getResponse() {
        return response;
    }

    public void setResponse(IRiskResponse response) {
        this.response = response;
    }
}
