package cn.tongdun.kunpeng.api.ruledetail;

import cn.fraudmetrix.module.tdrule.rule.ConditionDetail;

/**
 * @author: yuanhang
 * @date: 2020-10-23 10:15
 * 额外补充部分平台指标输出字段
 **/
public class PlatformIndexCustomDetail extends ConditionDetail {

    private String indexName;

    private String indexDim;

    private String indexResult;

    private String indexDesc;

    public String getIndexName() {
        return indexName;
    }

    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }

    public String getIndexDim() {
        return indexDim;
    }

    public void setIndexDim(String indexDim) {
        this.indexDim = indexDim;
    }

    public String getIndexResult() {
        return indexResult;
    }

    public void setIndexResult(String indexResult) {
        this.indexResult = indexResult;
    }

    public String getIndexDesc() {
        return indexDesc;
    }

    public void setIndexDesc(String indexDesc) {
        this.indexDesc = indexDesc;
    }

}
