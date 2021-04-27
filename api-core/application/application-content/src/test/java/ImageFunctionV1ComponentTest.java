import cn.fraudmetrix.module.tdrule.exception.ParseException;
import cn.fraudmetrix.module.tdrule.function.FunctionDesc;
import cn.fraudmetrix.module.tdrule.model.FunctionParam;
import cn.hutool.json.JSONUtil;
import cn.tongdun.kunpeng.api.application.content.constant.ModelResultEnum;
import cn.tongdun.kunpeng.api.application.content.function.image.FilterConditionDO;
import cn.tongdun.kunpeng.api.application.content.function.image.functionV1.ImageFunctionV1;
import cn.tongdun.kunpeng.share.json.JSON;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;
import org.testng.mustache.Model;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ImageFunctionV1ComponentTest {

    private String keyOfResult1 = "image_brand_logo_model_result";
    private String keyOfResult2 = "image_logo_model_result";
    private String keyOfResult3 = "image_politics_model_result";
    private String keyOfResult4 = "image_terror_model_result";

    private String model = "image_logo_model_result";

    private String params = "[\n" +
            "      {\n" +
            "        \"name\": \"conditions\",\n" +
            "        \"type\": \"string\",\n" +
            "        \"value\": \"[[{\\\"field\\\":\\\"logoName\\\",\\\"operator\\\":\\\"include\\\",\\\"value\\\":\\\"lv\\\"},{\\\"field\\\":\\\"score\\\",\\\"operator\\\":\\\">=\\\",\\\"value\\\":\\\"45\\\"}],[{\\\"field\\\":\\\"logoName\\\",\\\"operator\\\":\\\"include\\\",\\\"value\\\":\\\"lv\\\"},{\\\"field\\\":\\\"score\\\",\\\"operator\\\":\\\"<=\\\",\\\"value\\\":\\\"50\\\"}]]\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"name\": \"logicOperator\",\n" +
            "        \"type\": \"string\",\n" +
            "        \"value\": \"&&\"\n" +
            "      }\n" +
            "    ]";

    private String modelResult1 = "[{\"score\": 0.9977513551712036,\"label\": \"chanel\"},{\"score\": 0.9977513551712126,\"label\": \"lv\"},{\"score\": 0.3477513551712036,\"label\": \"lv\"}]";
    private String modelResult2 = "[{\"score\": 0.9977513551712036,\"label\": \"coach\"},{\"score\": 0.9977513551712126,\"label\": \"nike\"},{\"score\": 0.3477513551712036,\"label\": \"nike\"}]";
    private String modelResult3 = "[{\"score\": 0.9977513551712036,\"label\": \"dior\"},{\"score\": 0.9977513551712126,\"label\": \"lv\"},{\"score\": 0.3477513551712036,\"label\": \"Jaeger\"}]";
    private String modelResult4 = "[{\"score\": 0.9977513551712036,\"label\": \"supreme\"},{\"score\": 0.9977513551712126,\"label\": \"burberry\"},{\"score\": 0.3477513551712036,\"label\": \"lv\"}]";

    String condition = "[[{\"field\":\"logoName\",\"operator\":\"include\",\"value\":\"dior\"},{\"field\":\"score\",\"operator\":\">\",\"value\":0.5}],[{\"field\":\"logoName\",\"operator\":\"include\",\"value\":\"Jaeger\"},{\"field\":\"score\",\"operator\":\"<\",\"value\":1}]]";

    Class v1 =  Class.forName("cn.tongdun.kunpeng.api.application.content.function.image.functionV1.ImageFunctionV1");
    ImageFunctionV1 v1Instance = (ImageFunctionV1) v1.newInstance();
    public ImageFunctionV1ComponentTest() throws ClassNotFoundException, IllegalAccessException, InstantiationException {
    }

    @Test
    public void parseLogoModelResultTest() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method parseLogoModelResult = v1.getDeclaredMethod("parseLogoModelResult",String.class);
        parseLogoModelResult.setAccessible(true);
        List<Map> lablelAndScoreModelResults = (List<Map>)parseLogoModelResult.invoke(v1Instance,modelResult1);
        for(Map map : lablelAndScoreModelResults){
            System.out.println(map.get("label"));
            System.out.println(map.get("score"));
        }
    }




    @Test
    public void matchModelTest() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Map<ModelResultEnum, String> modelResult = construct();
        Method matchModel = v1.getDeclaredMethod("matchModel",Map.class,String.class);
        matchModel.setAccessible(true);
        String matchModelResult = (String) matchModel.invoke(v1Instance,modelResult,model);
        System.out.println(matchModelResult);
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
        for(List<FilterConditionDO> filter : filters) {
            FilterConditionDO labelCondition = filter.get(0);
            FilterConditionDO ScoreCondition = filter.get(1);
            System.out.println(labelCondition.toString());
            System.out.println(ScoreCondition.toString());
        }

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
        String model = (String) modelField.get(v1Instance);
        System.out.println(condition);
        System.out.println(logicOperator);
        System.out.println(model);
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
        System.out.println(res==true);

    }

}
