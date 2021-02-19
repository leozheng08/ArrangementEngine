package cn.tongdun.kunpeng.api.engine.convertor.batch;

import cn.tongdun.kunpeng.api.common.Constant;
import cn.tongdun.kunpeng.api.engine.convertor.batch.keyword.KeywordBatchRemoteCallDataBuilder;
import cn.tongdun.kunpeng.share.utils.TraceUtils;
import javassist.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @description: 根据策略模版获取对应的批量数据处理对象
 * @author: zhongxiang.wang
 * @date: 2021-01-28 14:44
 */
@Slf4j
public class BatchRemoteCallDataBuilderFactory {

    private static final Map<String,BatchRemoteCallDataBuilder> builders = new HashMap<>(4);

    static {
        builders.put(Constant.Function.KEYWORD_WORDLIST,new KeywordBatchRemoteCallDataBuilder());
    }

    public static BatchRemoteCallDataBuilder getBuilder(String template) {
        if(StringUtils.isBlank(template)){
            log.error(TraceUtils.getTrace() + "规则模版template参数为空，请确认数据是否正确。");
        }
        BatchRemoteCallDataBuilder batchRemoteCallDataBuilder = builders.get(template);
        if(null == batchRemoteCallDataBuilder){
            throw new RuntimeException("未查找到template="+template+"的BatchRemoteCallDataBuilder，请确认是否添加初始化");
        }
        return batchRemoteCallDataBuilder;
    }

}
