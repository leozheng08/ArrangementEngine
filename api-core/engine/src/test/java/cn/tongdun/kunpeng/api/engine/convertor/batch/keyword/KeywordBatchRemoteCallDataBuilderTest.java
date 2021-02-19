package cn.tongdun.kunpeng.api.engine.convertor.batch.keyword;

import cn.hutool.core.map.MapUtil;
import cn.tongdun.kunpeng.api.common.Constant;
import cn.tongdun.kunpeng.api.engine.cache.BatchRemoteCallDataCache;
import cn.tongdun.kunpeng.api.engine.convertor.batch.BatchRemoteCallDataBuilder;
import cn.tongdun.kunpeng.api.engine.convertor.batch.BatchRemoteCallDataBuilderFactory;
import cn.tongdun.kunpeng.api.engine.model.rule.Rule;
import cn.tongdun.kunpeng.client.dto.RuleConditionElementDTO;
import cn.tongdun.kunpeng.client.dto.RuleDTO;
import mockit.Injectable;
import mockit.Tested;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

/**
 * @description:
 * @author: zhongxiang.wang
 * @date: 2021-02-02 15:01
 */
@RunWith(MockitoJUnitRunner.class)
public class KeywordBatchRemoteCallDataBuilderTest {

    private String params = "[{\"name\":\"calcField\",\"type\":\"string\",\"value\":\"partnerCode\"},{\"name\":\"definitionList\",\"type\":\"string\",\"value\":\"sdgjcb\"}]";
    private String calcField = "partnerCode";
    private String definitionList = "sdgjcb";
    private String policyUuid = "uuid123";
    private String ruleUuid = "ruleuuid123";
    private String subPolicyUuid = "subPolicyuuid123";

    @Injectable
    private BatchRemoteCallDataCache cache;

    @Tested
    private KeywordBatchRemoteCallDataBuilder builder;

    @Before
    public void before(){
        builder = (KeywordBatchRemoteCallDataBuilder)BatchRemoteCallDataBuilderFactory.getBuilder(Constant.Function.KEYWORD_WORDLIST);
        cache = new BatchRemoteCallDataCache();
    }

    @Test
    public void test_createRemoteCallData(){
        RuleConditionElementDTO elementDTO = new RuleConditionElementDTO();
        elementDTO.setParams(params);
        KeywordBatchRemoteCallData remoteCallData = builder.createRemoteCallData(policyUuid,subPolicyUuid,ruleUuid,elementDTO);

        Assert.assertEquals(Constant.Function.KEYWORD_WORDLIST,remoteCallData.getTemplate());
        Assert.assertEquals(calcField,remoteCallData.getCalcField());
        Assert.assertEquals(definitionList,remoteCallData.getDefinitionList());
    }

    @Test
    public void test_appendBatchRemoteCallData(){
        RuleConditionElementDTO elementDTO = new RuleConditionElementDTO();
        elementDTO.setParams(params);
        RuleDTO dto = new RuleDTO();
        dto.setPolicyUuid(policyUuid);
        dto.setRuleConditionElements(Arrays.asList(elementDTO));
        Rule rule = new Rule();

        List<Object> batchDataDTOS = builder.build(policyUuid,subPolicyUuid,dto);
        //待优化，cache.put应该在具体的xxxReloadManager中设置，cache从builder中转移到了xxxReloadManager中了
        cache.put(policyUuid,MapUtil.of(Constant.Function.KEYWORD_WORDLIST,batchDataDTOS));
        Rule ruleNew = rule;
        //assignPrivate(builder,"cache",cache);

        Assert.assertNotNull(cache.get(dto.getPolicyUuid()));

        //缓存是否写入
        Assert.assertEquals(Constant.Function.KEYWORD_WORDLIST,((KeywordBatchRemoteCallData)cache.get(dto.getPolicyUuid()).get(Constant.Function.KEYWORD_WORDLIST).get(0)).getTemplate());
        Assert.assertEquals(calcField,((KeywordBatchRemoteCallData)cache.get(dto.getPolicyUuid()).get(Constant.Function.KEYWORD_WORDLIST).get(0)).getCalcField());
        Assert.assertEquals(definitionList,((KeywordBatchRemoteCallData)cache.get(dto.getPolicyUuid()).get(Constant.Function.KEYWORD_WORDLIST).get(0)).getDefinitionList());

        Assert.assertTrue(builder instanceof BatchRemoteCallDataBuilder);
    }

    public static void assignPrivate(Object object,String fieldName,Object value){
        try {
            Field field = object.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(object,value);
        } catch (Exception ex) {
            throw new RuntimeException();
        }
    }
}
