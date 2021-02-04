package cn.tongdun.kunpeng.api.engine.convertor.batch.keyword;

import cn.tongdun.kunpeng.api.common.Constant;
import cn.tongdun.kunpeng.api.engine.convertor.batch.BatchRemoteCallDataBuilder;
import cn.tongdun.kunpeng.api.engine.convertor.batch.BatchRemoteCallDataBuilderFactory;
import org.junit.Assert;
import org.junit.Test;

/**
 * @description:
 * @author: zhongxiang.wang
 * @date: 2021-02-02 16:00
 */
public class BatchRemoteCallDataBuilderFactoryTest {

    @Test
    public void test_getBuilder_not_null(){
        BatchRemoteCallDataBuilder builder = BatchRemoteCallDataBuilderFactory.getBuilder(Constant.Function.KEYWORD_WORDLIST);
        Assert.assertTrue(builder instanceof KeywordBatchRemoteCallDataBuilder);

        BatchRemoteCallDataBuilder builder2 = BatchRemoteCallDataBuilderFactory.getBuilder("xxxxxxx");
        Assert.assertNull(builder2);

    }

    @Test
    public void test_getBuilder_null(){
        BatchRemoteCallDataBuilder builder2 = BatchRemoteCallDataBuilderFactory.getBuilder("xxxxxxx");
        Assert.assertNull(builder2);
    }
}
