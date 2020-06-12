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

    private Double rand;

    private String ranResult;

    private String simResult;

    private Long time;

    @Override
    public IDetail call() {
        return this;
    }

    public Double getRand() {
        return rand;
    }

    public void setRand(Double rand) {
        this.rand = rand;
    }

    public String getRanResult() {
        return ranResult;
    }

    public void setRanResult(String ranResult) {
        this.ranResult = ranResult;
    }

    public String getSimResult() {
        return simResult;
    }

    public void setSimResult(String simResult) {
        this.simResult = simResult;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

}
