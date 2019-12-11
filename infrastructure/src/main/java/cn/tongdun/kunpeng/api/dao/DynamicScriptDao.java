package cn.tongdun.kunpeng.api.dao;

import cn.tongdun.kunpeng.api.dataobj.ScriptDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * Created by coco on 17/10/13.
 */
public interface DynamicScriptDao {
    int insert(ScriptDO scriptDO);

    int update(ScriptDO scriptDO);

    int exist(ScriptDO scriptDO);

    int delete(List<Integer> ids);

    List<ScriptDO> queryWithFielName(Map<String, Object> params);

    List<ScriptDO> query(Map<String, Object> params);

    List<ScriptDO> queryByProductCode(@Param("productCode") String productCode);
}
