package cn.tongdun.kunpeng.api.application.content.function.image.fucntionV2;

import cn.tongdun.kunpeng.api.application.content.constant.MathOperatorEnum;
import cn.tongdun.kunpeng.api.application.content.function.image.FilterConditionDO;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class FilterConditionDTO implements Serializable {

    private Double value;
    private MathOperatorEnum mathOperatorEnum;
    List<FilterConditionDO> filterConditionDOS;

    public FilterConditionDTO(List<FilterConditionDO> filterConditionDOS) {
        this.value = Double.parseDouble(filterConditionDOS.get(1).getRightValue());
        for(MathOperatorEnum mathOperatorEnum : MathOperatorEnum.values()){
            if(mathOperatorEnum.getName().equals(filterConditionDOS.get(1).getOperator())){
                this.mathOperatorEnum = mathOperatorEnum;
            }
        }
        this.filterConditionDOS = filterConditionDOS;
    }




}
