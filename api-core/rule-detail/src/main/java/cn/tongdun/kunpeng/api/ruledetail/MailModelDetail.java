package cn.tongdun.kunpeng.api.ruledetail;

import cn.fraudmetrix.module.tdrule.model.IDetail;
import cn.fraudmetrix.module.tdrule.util.DetailCallable;

/**
 *
 * @author yuanhang
 *
 * @date 05/27/2020
 *
 */
public class MailModelDetail extends ConditionDetail implements DetailCallable {

    private Double ranResult;

    private Integer simResult;

    private Long time;

    private Long interfaceTime;

    @Override
    public IDetail call() {
        return this;
    }

    public Double getRanResult() {
        return ranResult;
    }

    public void setRanResult(Double ranResult) {
        this.ranResult = ranResult;
    }

    public Integer getSimResult() {
        return simResult;
    }

    public void setSimResult(Integer simResult) {
        this.simResult = simResult;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public Long getInterfaceTime() {
        return interfaceTime;
    }

    public void setInterfaceTime(Long interfaceTime) {
        this.interfaceTime = interfaceTime;
    }
}
