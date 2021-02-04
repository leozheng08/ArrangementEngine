package cn.tongdun.kunpeng.api.engine.convertor.batch;

import cn.tongdun.kunpeng.api.common.Constant;
import cn.tongdun.kunpeng.api.engine.convertor.batch.keyword.KeywordBatchRemoteCallDataBuilder;

/**
 * @description: 根据策略模版获取对应的批量数据处理对象
 * @author: zhongxiang.wang
 * @date: 2021-01-28 14:44
 */
public class BatchRemoteCallDataBuilderFactory {

    public static BatchRemoteCallDataBuilder getBuilder(String template) {
        BatchRemoteCallDataBuilder batchDataBuilder;
        switch (template){
            case Constant.Function.KEYWORD_WORDLIST:
                batchDataBuilder = new KeywordBatchRemoteCallDataBuilder();
                break;
            default:
                batchDataBuilder = null;
                break;
        }
        return batchDataBuilder;
    }

}
