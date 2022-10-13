package cn.tongdun.kunpeng.api.engine.convertor.batch.keywordnew;

import cn.tongdun.kunpeng.api.engine.convertor.batch.AbstractBatchRemoteCallData;
import lombok.Data;

/**
 * 新关键词批量远程调用数据
 */
@Data
public class KeywordNewBatchRemoteCallData extends AbstractBatchRemoteCallData {

    /**
     * 匹配字段
     * eg：partnerCode
     */
    private String calcField;
    /**
     * 批量累积的数据
     * eg：关键词逻辑中，新关键词策略标签组：test
     */
    private String definitionList;

}
