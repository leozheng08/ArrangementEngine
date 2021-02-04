package cn.tongdun.kunpeng.api.engine.convertor.batch.keyword;

import cn.tongdun.kunpeng.api.engine.convertor.batch.AbstractBatchRemoteCallData;
import lombok.Data;

/**
 * @description: 关键词批量远程调用数据
 * @author: zhongxiang.wang
 * @date: 2021-01-28 19:09
 */
@Data
public class KeywordBatchRemoteCallData extends AbstractBatchRemoteCallData {

    /**
     * 匹配字段
     * eg：partnerCode
     */
    private String calcField;
    /**
     * 批量累积的数据
     * eg：关键词逻辑中，关键词列表-涉赌关键词列表的code：sdgjcb
     */
    private String definitionList;

}
