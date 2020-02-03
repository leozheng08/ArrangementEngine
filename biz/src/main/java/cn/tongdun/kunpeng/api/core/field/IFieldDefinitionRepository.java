package cn.tongdun.kunpeng.api.core.field;

import java.util.List;

/**
 * @Author: liang.chen
 * @Date: 2019/12/10 下午1:46
 */
public interface IFieldDefinitionRepository {


    List<FieldDefinition> queryByParams(FieldDefinition field);
}
