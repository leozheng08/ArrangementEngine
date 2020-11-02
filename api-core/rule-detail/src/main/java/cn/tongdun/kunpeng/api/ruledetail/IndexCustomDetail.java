package cn.tongdun.kunpeng.api.ruledetail;

import cn.fraudmetrix.module.tdrule.rule.ConditionDetail;

/**
 * @author: yuanhang
 * @date: 2020-10-23 10:15
 * 额外补充部分平台指标输出字段
 **/
public class IndexCustomDetail extends ConditionDetail {

    private String indexName;

    private Object indexDim;

    private Object indexResult;

    private String indexDesc;

    public String getIndexName() {
        return indexName;
    }

    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }

    public Object getIndexDim() {
        return indexDim;
    }

    public void setIndexDim(Object indexDim) {
        this.indexDim = indexDim;
    }

    public Object getIndexResult() {
        return indexResult;
    }

    public void setIndexResult(Object indexResult) {
        this.indexResult = indexResult;
    }

    public String getIndexDesc() {
        return indexDesc;
    }

    public void setIndexDesc(String indexDesc) {
        this.indexDesc = indexDesc;
    }

}
