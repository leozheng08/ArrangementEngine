package cn.tongdun.kunpeng.api.engine.model.field;

import java.util.List;

/**
 * @Author: liang.chen
 * @Date: 2019/12/10 下午1:46
 */
public interface IFieldDefinitionRepository {

    List<FieldDefinition> queryAllSystemField();

    List<FieldDefinition> queryAllExtendField();


    FieldDefinition queryByUuid(String uuid);

}
