package cn.tongdun.kunpeng.api.engine.convertor.batch.keyword;

import cn.hutool.core.map.MapUtil;
import cn.tongdun.kunpeng.api.common.Constant;
import cn.tongdun.kunpeng.api.engine.convertor.batch.AbstractBatchRemoteCallData;
import cn.tongdun.kunpeng.api.engine.convertor.batch.BatchRemoteCallDataBuilder;
import cn.tongdun.kunpeng.api.engine.convertor.batch.BatchRemoteCallDataBuilderFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @description:
 * @author: zhongxiang.wang
 * @date: 2021-02-02 16:00
 */
public class BatchRemoteCallDataBuilderFactoryTest {

    private Map<String, Map<String, List<Object>>> map = new HashMap();

    @Before
    public void before(){
        KeywordBatchRemoteCallData data1 = new KeywordBatchRemoteCallData();
        data1.setRuleUuid("uuid123");
        data1.setTemplate("keyword");

        KeywordBatchRemoteCallData data3 = new KeywordBatchRemoteCallData();
        data3.setRuleUuid("uuid123");
        data3.setTemplate("noa");

        KeywordBatchRemoteCallData data2 = new KeywordBatchRemoteCallData();
        data2.setRuleUuid("uuid1234");
        data2.setTemplate("keyword");

        Map<String, List<Object>> m = new HashMap<>();
        m.put("keyword",Arrays.asList(data1));
        m.put("noa",Arrays.asList(data3));
        map.put("puuid123",m);

        map.put("puuid1234",MapUtil.of("keyword", Arrays.asList(data1)));

        System.out.println(map.toString());
    }


    @Test
    public void test(){
        Map<String, List<Object>> batchDatas = map.get("puuid123");
        if(!CollectionUtils.isEmpty(batchDatas)){
            List<Object> datas = batchDatas.get("keyword");
            if(!CollectionUtils.isEmpty(datas)){
                List<Object> result = datas.stream().filter(obj -> !"uuid1234".equals(((AbstractBatchRemoteCallData) obj).getRuleUuid())).collect(Collectors.toList());
                map.put("puuid123",MapUtil.of("keyword",result));
            }
        }
        System.out.println(map.toString());
    }

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
