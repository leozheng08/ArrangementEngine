package cn.tongdun.kunpeng.api.engine.convertor.batch;

import cn.tongdun.kunpeng.api.common.Constant;
import cn.tongdun.kunpeng.api.engine.convertor.batch.keyword.KeywordBatchRemoteCallDataBuilder;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @description: 根据策略模版获取对应的批量数据处理对象
 * @author: zhongxiang.wang
 * @date: 2021-01-28 14:44
 */
public class BatchRemoteCallDataBuilderFactory {

    private static final Map<String,BatchRemoteCallDataBuilder> builders = new HashMap<>(4);

    static {
        builders.put(Constant.Function.KEYWORD_WORDLIST,new KeywordBatchRemoteCallDataBuilder());
    }

    public static BatchRemoteCallDataBuilder getBuilder(String template) {
        return StringUtils.isBlank(template) ? null : builders.get(template);
    }

}
