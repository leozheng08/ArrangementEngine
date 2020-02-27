package cn.tongdun.kunpeng.api.engine.model.rule.function.pattern;

/**
 * Created by Rosy on 16/8/20.
 */
public class RegularMatchData {
    private String dimValue;
    private Boolean result;

    public RegularMatchData() {
    }

    public String getDimValue() {
        return dimValue;
    }

    public void setDimValue(String dimValue) {
        this.dimValue = dimValue;
    }

    public Boolean getResult() {
        return result;
    }

    public void setResult(Boolean result) {
        this.result = result;
    }
}
