package cn.tongdun.kunpeng.api.engine.model.script;

import java.util.List;
import java.util.Set;

/**
 * @Author: liang.chen
 * @Date: 2020/2/19 下午8:24
 */
public interface IDynamicScriptRepository {

    List<DynamicScript> queryGroovyByPartners(Set<String> partners);

    DynamicScript queryByUuid(String uuid);


}
