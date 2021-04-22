

import cn.tongdun.kunpeng.api.application.content.constant.ModelResultEnum;
import cn.tongdun.kunpeng.api.application.content.function.image.FilterConditionDO;
import cn.tongdun.kunpeng.api.application.content.function.image.ImageFunction;
import cn.tongdun.kunpeng.api.application.content.function.image.functionV1.ImageFunctionV1;
import cn.tongdun.kunpeng.api.common.data.AbstractFraudContext;
import org.junit.Test;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @description:
 * @author: zhongxiang.wang
 * @date: 2021-01-29 15:13
 */

@SpringJUnitConfig()
public class ImageFunctionTest {

    private String logoModelResult = "[{\"score\": 0.9977513551712036,\"logoName\": \"chanel\"},{\"score\": 0.9977513551712126,\"logoName\": \"lv\"},{\"score\": 0.3477513551712036,\"logoName\": \"lv\"}]";
    private String conditions = "[[{\"field\":\"logoName\",\"operator\":\"include\",\"value\":\"lv\"},{\"field\":\"score\",\"operator\":\">\",\"value\":500}],[{\"field\":\"logoName\",\"operator\":\"include\",\"value\":\"chanel\"},{\"field\":\"score\",\"operator\":\">\",\"value\":40}]]";
    private String logicOperator = "&&";
    private String model = ModelResultEnum.LOGO_RECOGNITION_MODEL.getName();
    private ImageFunctionV1 imageFunction = new ImageFunctionV1();

    @Test
    public void test(){

    }
}
