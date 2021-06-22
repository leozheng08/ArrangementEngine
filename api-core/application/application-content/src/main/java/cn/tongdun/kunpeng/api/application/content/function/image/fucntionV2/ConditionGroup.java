package cn.tongdun.kunpeng.api.application.content.function.image.fucntionV2;

import cn.tongdun.kunpeng.api.application.content.function.image.FilterConditionDO;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ConditionGroup implements Serializable {

    //条件组的左边界和右边界
    private List<FilterConditionDO> leftCondition;
    private List<FilterConditionDO> rightCondition;
}
