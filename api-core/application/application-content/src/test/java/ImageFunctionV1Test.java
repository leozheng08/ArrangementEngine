import cn.fraudmetrix.module.tdrule.context.ExecuteContext;
import cn.fraudmetrix.module.tdrule.exception.ParseException;
import cn.fraudmetrix.module.tdrule.function.FunctionDesc;
import cn.fraudmetrix.module.tdrule.function.FunctionResult;
import cn.fraudmetrix.module.tdrule.model.FunctionParam;
import cn.hutool.json.JSONUtil;
import cn.tongdun.kunpeng.api.application.content.constant.ModelResultEnum;
import cn.tongdun.kunpeng.api.application.content.function.image.FilterConditionDO;
import cn.tongdun.kunpeng.api.application.content.function.image.functionV1.ImageFunctionV1;
import cn.tongdun.kunpeng.api.common.data.AbstractFraudContext;
import cn.tongdun.kunpeng.share.json.JSON;
import org.elasticsearch.common.mvel2.util.FastList;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.context.junit4.SpringRunner;
import org.testng.mustache.Model;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(MockitoJUnitRunner.class)
public class ImageFunctionV1Test {

    private String keyOfResult1 = "imageBrandLogoModelResult";
    private String keyOfResult2 = "imageLogoModelResult";
    private String keyOfResult3 = "imagePoliticsModelResult";
    private String keyOfResult4 = "imageTerrorModelResult";

    private String model = "image_logo_model_result";

    private String params = "[\n" +
            "      {\n" +
            "        \"name\": \"conditions\",\n" +
            "        \"type\": \"string\",\n" +
            "        \"value\": \"[[{\\\"field\\\":\\\"image_brand_logo_model_result\\\",\\\"operator\\\":\\\"include\\\",\\\"value\\\":\\\"lv\\\"},{\\\"field\\\":\\\"score\\\",\\\"operator\\\":\\\">=\\\",\\\"value\\\":\\\"45\\\"}],[{\\\"field\\\":\\\"image_brand_logo_model_result\\\",\\\"operator\\\":\\\"include\\\",\\\"value\\\":\\\"lv\\\"},{\\\"field\\\":\\\"score\\\",\\\"operator\\\":\\\"<=\\\",\\\"value\\\":\\\"50\\\"}]]\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"name\": \"logicOperator\",\n" +
            "        \"type\": \"string\",\n" +
            "        \"value\": \"&&\"\n" +
            "      }\n" +
            "    ]";

    private String modelResult1 = "[{\"score\": 0.9977513551712036,\"label\": \"channel\"},{\"score\": 0.9977513551712126,\"label\": \"lv\"},{\"score\": 0.3477513551712036,\"label\": \"lv\"}]";
    private String modelResult2 = "[{\"score\": 0.9977513551712036,\"label\": \"coach\"},{\"score\": 0.9977513551712126,\"label\": \"nike\"},{\"score\": 0.3477513551712036,\"label\": \"nike\"}]";
    private String modelResult3 = "[{\"score\": 0.9977513551712036,\"label\": \"dior\"},{\"score\": 0.9977513551712126,\"label\": \"lv\"},{\"score\": 0.3477513551712036,\"label\": \"Jaeger\"}]";
    private String modelResult4 = "[{\"score\": 0.9977513551712036,\"label\": \"supreme\"},{\"score\": 0.9977513551712126,\"label\": \"burberry\"},{\"score\": 0.3477513551712036,\"label\": \"lv\"}]";

    private String condition = "[[{\"field\":\"image_brand_logo_model_result\",\"operator\":\"include\",\"value\":\"dior\"},{\"field\":\"score\",\"operator\":\">\",\"value\":0.5}],[{\"field\":\"image_brand_logo_model_result\",\"operator\":\"include\",\"value\":\"Jaeger\"},{\"field\":\"score\",\"operator\":\"<\",\"value\":1}]]";
    private ImageFunctionV1 imageFunctionV1;
    private AbstractFraudContext abstractFraudContext;

    Class v1 =  Class.forName("cn.tongdun.kunpeng.api.application.content.function.image.functionV1.ImageFunctionV1");
    ImageFunctionV1 v1Instance = (ImageFunctionV1) v1.newInstance();

    public ImageFunctionV1Test() throws IllegalAccessException, InstantiationException, ClassNotFoundException {
    }

    @Before
    public void init(){
        imageFunctionV1 = new ImageFunctionV1();
    }


    /**
     * ImageFucntion 私有方法自测
     */
    @Test
    public void parseLogoModelResultTest() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method parseLogoModelResult = v1.getDeclaredMethod("parseLogoModelResult",String.class);
        parseLogoModelResult.setAccessible(true);
        List<Map> modelResults = (List<Map>)parseLogoModelResult.invoke(v1Instance,modelResult1);
        Assert.assertEquals(modelResults.get(0).get("label"),"channel");
        Assert.assertEquals(modelResults.get(0).get("score"),0.9977513551712036 );
    }



    @Test
    public void getModelTest() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method parseCondition = v1.getDeclaredMethod("parseCondition", String.class);
        parseCondition.setAccessible(true);
        List<List<FilterConditionDO>> filters = (List<List<FilterConditionDO>> )parseCondition.invoke(v1Instance,condition);
        Method getModel = v1.getDeclaredMethod("getModel", List.class);
        getModel.setAccessible(true);
        String model = (String)getModel.invoke(v1Instance,filters);
        Assert.assertEquals(model, "image_brand_logo_model_result");
    }


    @Test
    public void matchModelTest() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Map<ModelResultEnum, String> modelResult = construct();
        Method matchModel = v1.getDeclaredMethod("matchModel",Map.class,String.class);
        matchModel.setAccessible(true);
        String matchModelResult = (String) matchModel.invoke(v1Instance,modelResult,model);
        Assert.assertEquals(matchModelResult, modelResult2);
    }

    public Map<ModelResultEnum,String> construct(){
        Map<ModelResultEnum, String> modelResultMap = new HashMap<>();
        modelResultMap.put(ModelResultEnum.BRAND_LOGO_RECOGNITION_MODEL,modelResult1);
        modelResultMap.put(ModelResultEnum.LOGO_RECOGNITION_MODEL,modelResult2);
        modelResultMap.put(ModelResultEnum.SPR_POLITICAL_MODEL,modelResult3);
        modelResultMap.put(ModelResultEnum.VIOLENT_TERROR_MODEL,modelResult4);
        return modelResultMap;

    }

    @Test
    public void parseConditionTest() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException {
        Method parseCondition = v1.getDeclaredMethod("parseCondition", String.class);
        parseCondition.setAccessible(true);
        List<List<FilterConditionDO>> filters = (List<List<FilterConditionDO>> )parseCondition.invoke(v1Instance,condition);
        FilterConditionDO labelCondition = filters.get(0).get(0);
        FilterConditionDO scoreCondition = filters.get(0).get(1);
        Assert.assertEquals(labelCondition.getLeftPropertyName(),"image_brand_logo_model_result");
        Assert.assertEquals(labelCondition.getOperator(),"include");
        Assert.assertEquals(labelCondition.getRightValue(),"dior");
        Assert.assertEquals(labelCondition.getRightValueType(),"STRING");

        Assert.assertEquals(scoreCondition.getLeftPropertyName(),"score");
        Assert.assertEquals(scoreCondition.getOperator(),">");
        Assert.assertEquals(scoreCondition.getRightValue(),"0.5");
        Assert.assertEquals(scoreCondition.getRightValueType(),"DOUBLE");

    }

    @Test
    public void parseFunctionTest() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, NoSuchFieldException {
        FunctionDesc functionDesc = new FunctionDesc();
        List<FunctionParam> functionParamList = JSON.parseArray(params, FunctionParam.class);
        functionDesc.setParamList(functionParamList);
        Method parseFucntion = v1.getDeclaredMethod("parseFunction",FunctionDesc.class);
        parseFucntion.setAccessible(true);
        parseFucntion.invoke(v1Instance,functionDesc);
        Field conditionField = v1.getDeclaredField("conditions");
        conditionField.setAccessible(true);
        Field logicOperatorField = v1.getDeclaredField("logicOperator");
        logicOperatorField.setAccessible(true);
        Field modelField = v1.getDeclaredField("model");
        modelField.setAccessible(true);
        String condition = (String)conditionField.get(v1Instance);
        String logicOperator = (String)logicOperatorField.get(v1Instance);
        Assert.assertEquals(condition, "[[{\"field\":\"image_brand_logo_model_result\",\"operator\":\"include\",\"value\":\"lv\"},{\"field\":\"score\",\"operator\":\">=\",\"value\":\"45\"}],[{\"field\":\"image_brand_logo_model_result\",\"operator\":\"include\",\"value\":\"lv\"},{\"field\":\"score\",\"operator\":\"<=\",\"value\":\"50\"}]]");
        Assert.assertEquals(logicOperator, "&&");
    }



    @Test
    public void isConditionSatisfiedTest() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String logicOperator = "&&";
        Method parseLogoModelResult = v1.getDeclaredMethod("parseLogoModelResult",String.class);
        parseLogoModelResult.setAccessible(true);
        List<Map> modelResults = (List<Map>)parseLogoModelResult.invoke(v1Instance,modelResult3);
        Method parseCondition = v1.getDeclaredMethod("parseCondition", String.class);
        parseCondition.setAccessible(true);
        List<List<FilterConditionDO>> conditionList = (List<List<FilterConditionDO>> )parseCondition.invoke(v1Instance,condition);

        List<List<FilterConditionDO>> hitFilters = new ArrayList<>();

        Method isConditionSatisfied = v1.getDeclaredMethod("isConditionSatisfied",String.class,List.class,List.class,List.class);
        isConditionSatisfied.setAccessible(true);
        Boolean res = (Boolean)isConditionSatisfied.invoke(v1Instance,logicOperator,modelResults,conditionList,hitFilters);
        Assert.assertEquals(res, true);

    }

    @Test
    public void achieveModelResultTest() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        abstractFraudContext = Mockito.mock(AbstractFraudContext.class);
        Mockito.when(abstractFraudContext.get(Mockito.eq(keyOfResult1))).thenReturn(modelResult1);
        Mockito.when(abstractFraudContext.get(Mockito.eq(keyOfResult2))).thenReturn(modelResult2);
        Mockito.when(abstractFraudContext.get(Mockito.eq(keyOfResult3))).thenReturn(modelResult3);
        Mockito.when(abstractFraudContext.get(Mockito.eq(keyOfResult4))).thenReturn(modelResult4);

        Method achieveModelResult = v1.getDeclaredMethod("achieveModelResult",AbstractFraudContext.class);
        achieveModelResult.setAccessible(true);
        Map<ModelResultEnum,String> modelResultMap = (Map<ModelResultEnum,String>)achieveModelResult.invoke(v1Instance,abstractFraudContext);
        Assert.assertEquals(modelResultMap.get(ModelResultEnum.BRAND_LOGO_RECOGNITION_MODEL),modelResult1);
        Assert.assertEquals(modelResultMap.get(ModelResultEnum.LOGO_RECOGNITION_MODEL),modelResult2);
        Assert.assertEquals(modelResultMap.get(ModelResultEnum.SPR_POLITICAL_MODEL),modelResult3);
        Assert.assertEquals(modelResultMap.get(ModelResultEnum.VIOLENT_TERROR_MODEL),modelResult4);
    }


    @Test
    public void isMatchTest() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        FilterConditionDO filterConditionDO = new FilterConditionDO();
        filterConditionDO.setRightValueType("String");
        filterConditionDO.setOperator("isnull");
        filterConditionDO.setRightValue("lv");
        Map<String,String> imageLogoScore = new HashMap<>();
        imageLogoScore.put("score","0.5");
        Method isMatch = v1.getDeclaredMethod("isMatch",FilterConditionDO.class, Map.class, String.class);
        isMatch.setAccessible(true);
        Boolean result = (Boolean) isMatch.invoke(v1Instance,filterConditionDO,imageLogoScore,"label");
        Assert.assertEquals(result,true);
    }

    /**
     * 测试满足任意条件命中
     */

    @Test
    public void testHitAnyCondition() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        FunctionDesc functionDesc;
        functionDesc = Mockito.mock(FunctionDesc.class);
        List<FunctionParam> functionParamList = new ArrayList<>();
        FunctionParam functionParam1 = new FunctionParam();
        functionParam1.setName("conditions");
        functionParam1.setValue("[[{\"field\":\"image_brand_logo_model_result\",\"operator\":\"include\",\"value\":\"lv\"},{\"field\":\"score\",\"operator\":\">=\",\"value\":\"45\"}],[{\"field\":\"image_brand_logo_model_result\",\"operator\":\"include\",\"value\":\"lv\"},{\"field\":\"score\",\"operator\":\"<=\",\"value\":\"50\"}]]");
        FunctionParam functionParam2 = new FunctionParam();
        functionParam2.setName("logicOperator");
        functionParam2.setValue("||");
        functionParamList.add(functionParam1);
        functionParamList.add(functionParam2);
        Mockito.when(functionDesc.getParamList()).thenReturn(functionParamList);
        abstractFraudContext = Mockito.mock(AbstractFraudContext.class);
        Mockito.when(abstractFraudContext.get(Mockito.eq(keyOfResult1))).thenReturn(modelResult1);
        Mockito.when(abstractFraudContext.get(Mockito.eq(keyOfResult2))).thenReturn(modelResult2);
        Mockito.when(abstractFraudContext.get(Mockito.eq(keyOfResult3))).thenReturn(modelResult3);
        Mockito.when(abstractFraudContext.get(Mockito.eq(keyOfResult4))).thenReturn(modelResult4);
        Method run = v1.getDeclaredMethod("run", ExecuteContext.class);
        run.setAccessible(true);
        Method parseFunction = v1.getDeclaredMethod("parseFunction",FunctionDesc.class);
        parseFunction.setAccessible(true);
        parseFunction.invoke(v1Instance,functionDesc);
        FunctionResult functionResult = (FunctionResult)run.invoke(v1Instance,abstractFraudContext);
        Assert.assertEquals(functionResult.getResult(), true);
    }

    /**
     * 测试满足任意条件 不命中
     */

    @Test
    public void testHitAnyCondition1() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        FunctionDesc functionDesc;
        functionDesc = Mockito.mock(FunctionDesc.class);
        List<FunctionParam> functionParamList = new ArrayList<>();
        FunctionParam functionParam1 = new FunctionParam();
        functionParam1.setName("conditions");
        functionParam1.setValue("[[{\"field\":\"image_brand_logo_model_result\",\"operator\":\"include\",\"value\":\"lv\"},{\"field\":\"score\",\"operator\":\">=\",\"value\":\"1\"}],[{\"field\":\"image_brand_logo_model_result\",\"operator\":\"include\",\"value\":\"supreme\"},{\"field\":\"score\",\"operator\":\"<=\",\"value\":\"0.5\"}]]");
        FunctionParam functionParam2 = new FunctionParam();
        functionParam2.setName("logicOperator");
        functionParam2.setValue("||");
        functionParamList.add(functionParam1);
        functionParamList.add(functionParam2);
        Mockito.when(functionDesc.getParamList()).thenReturn(functionParamList);
        abstractFraudContext = Mockito.mock(AbstractFraudContext.class);
        Mockito.when(abstractFraudContext.get(Mockito.eq(keyOfResult1))).thenReturn(modelResult1);
        Mockito.when(abstractFraudContext.get(Mockito.eq(keyOfResult2))).thenReturn(modelResult2);
        Mockito.when(abstractFraudContext.get(Mockito.eq(keyOfResult3))).thenReturn(modelResult3);
        Mockito.when(abstractFraudContext.get(Mockito.eq(keyOfResult4))).thenReturn(modelResult4);
        Method run = v1.getDeclaredMethod("run", ExecuteContext.class);
        run.setAccessible(true);
        Method parseFunction = v1.getDeclaredMethod("parseFunction",FunctionDesc.class);
        parseFunction.setAccessible(true);
        parseFunction.invoke(v1Instance,functionDesc);
        FunctionResult functionResult = (FunctionResult)run.invoke(v1Instance,abstractFraudContext);
        Assert.assertEquals(functionResult.getResult(), false);
    }




    /**
     * 测试满足全部条件命中
     */
    @Test
    public void testHitAllCondition() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        FunctionDesc functionDesc;
        functionDesc = Mockito.mock(FunctionDesc.class);
        List<FunctionParam> functionParamList = new ArrayList<>();
        FunctionParam functionParam1 = new FunctionParam();
        functionParam1.setName("conditions");
        functionParam1.setValue("[[{\"field\":\"image_brand_logo_model_result\",\"operator\":\"include\",\"value\":\"lv\"},{\"field\":\"score\",\"operator\":\">=\",\"value\":\"0.5\"}],[{\"field\":\"image_brand_logo_model_result\",\"operator\":\"include\",\"value\":\"lv\"},{\"field\":\"score\",\"operator\":\"<=\",\"value\":\"1\"}]]");
        FunctionParam functionParam2 = new FunctionParam();
        functionParam2.setName("logicOperator");
        functionParam2.setValue("&&");
        functionParamList.add(functionParam1);
        functionParamList.add(functionParam2);
        Mockito.when(functionDesc.getParamList()).thenReturn(functionParamList);
        abstractFraudContext = Mockito.mock(AbstractFraudContext.class);
        Mockito.when(abstractFraudContext.get(Mockito.eq(keyOfResult1))).thenReturn(modelResult1);
        Mockito.when(abstractFraudContext.get(Mockito.eq(keyOfResult2))).thenReturn(modelResult2);
        Mockito.when(abstractFraudContext.get(Mockito.eq(keyOfResult3))).thenReturn(modelResult3);
        Mockito.when(abstractFraudContext.get(Mockito.eq(keyOfResult4))).thenReturn(modelResult4);
        Method run = v1.getDeclaredMethod("run", ExecuteContext.class);
        run.setAccessible(true);
        Method parseFunction = v1.getDeclaredMethod("parseFunction",FunctionDesc.class);
        parseFunction.setAccessible(true);
        parseFunction.invoke(v1Instance,functionDesc);
        FunctionResult functionResult = (FunctionResult)run.invoke(v1Instance,abstractFraudContext);
        Assert.assertEquals(functionResult.getResult(), true);
    }


    /**
     * 测试满足未全部条件命中
     */
    @Test
    public void testHitAllCondition1() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        FunctionDesc functionDesc;
        functionDesc = Mockito.mock(FunctionDesc.class);
        List<FunctionParam> functionParamList = new ArrayList<>();
        FunctionParam functionParam1 = new FunctionParam();
        functionParam1.setName("conditions");
        functionParam1.setValue("[[{\"field\":\"image_brand_logo_model_result\",\"operator\":\"include\",\"value\":\"lv\"},{\"field\":\"score\",\"operator\":\">=\",\"value\":\"1\"}],[{\"field\":\"image_brand_logo_model_result\",\"operator\":\"include\",\"value\":\"lv\"},{\"field\":\"score\",\"operator\":\"<=\",\"value\":\"1\"}]]");
        FunctionParam functionParam2 = new FunctionParam();
        functionParam2.setName("logicOperator");
        functionParam2.setValue("&&");
        functionParamList.add(functionParam1);
        functionParamList.add(functionParam2);
        Mockito.when(functionDesc.getParamList()).thenReturn(functionParamList);
        abstractFraudContext = Mockito.mock(AbstractFraudContext.class);
        Mockito.when(abstractFraudContext.get(Mockito.eq(keyOfResult1))).thenReturn(modelResult1);
        Mockito.when(abstractFraudContext.get(Mockito.eq(keyOfResult2))).thenReturn(modelResult2);
        Mockito.when(abstractFraudContext.get(Mockito.eq(keyOfResult3))).thenReturn(modelResult3);
        Mockito.when(abstractFraudContext.get(Mockito.eq(keyOfResult4))).thenReturn(modelResult4);
        Method run = v1.getDeclaredMethod("run", ExecuteContext.class);
        run.setAccessible(true);
        Method parseFunction = v1.getDeclaredMethod("parseFunction",FunctionDesc.class);
        parseFunction.setAccessible(true);
        parseFunction.invoke(v1Instance,functionDesc);
        FunctionResult functionResult = (FunctionResult)run.invoke(v1Instance,abstractFraudContext);
        Assert.assertEquals(functionResult.getResult(), false);
    }


}
