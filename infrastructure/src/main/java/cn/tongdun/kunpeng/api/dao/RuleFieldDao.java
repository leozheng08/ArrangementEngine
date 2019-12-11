package cn.tongdun.kunpeng.api.dao;

import cn.tongdun.kunpeng.api.dataobj.RuleFieldDO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * Created by Rosy on 17/7/5.
 */
@Repository
public interface RuleFieldDao extends CommonDao<RuleFieldDO> {

    public Integer insertBatch(List<RuleFieldDO> ruleFieldDOs);

    public Integer deleteByParams(Map<String, Object> map);

    public Integer deleteBatch(List<String> list);

    public List<String> queryExtendUuids(RuleFieldDO ruleFieldDO);

    public List<RuleFieldDO> querySys(RuleFieldDO ruleFieldDO);

    List<RuleFieldDO> querySysByPage(Map<String, Object> map);

    Integer countSysByPage(Map<String, Object> map);

    public List<RuleFieldDO> selectDateFiled(RuleFieldDO ruleFieldDO);


    public List<RuleFieldDO> queryCommonField(Map<String, Object> map);

    public Integer updateVelocity(RuleFieldDO ruleFieldDO);

    /**
     * 更新是否根对象标志
     * @param ruleFieldDO
     * @return
     */
    Integer updateRootObject(RuleFieldDO ruleFieldDO);

    List<RuleFieldDO> queryExtendList(Map<String, Object> map);

    Integer queryExtendListCount(Map<String, Object> map);

    public List<RuleFieldDO> queryDecimalFields(Map<String, Object> map);

//    public List<FieldSelectDO> queryFieldSelect(Map<String, Object> map);

    public List<RuleFieldDO> querySystemFieldsByEventType(@Param("eventType") String eventType);

    public List<RuleFieldDO> getAllRuleFields();

    List<RuleFieldDO> queryByUuids(List<String> uuids);

    List<RuleFieldDO> queryFuzzyFields(Map<String, Object> map);

    List<RuleFieldDO> queryIncludeFields();

    List<RuleFieldDO> querySystemAndObjectField(Map<String, Object> params);

    List<RuleFieldDO> queryUniqueName(Map<String, Object> params);

    Integer countUniqueName(Map<String, Object> params);

    Integer updateUniqueName(Map<String, Object> params);

    List<RuleFieldDO> querySameUniqueName(Map<String, Object> params);

    List<RuleFieldDO> queryUniqueFields(Map<String, Object> params);

    List<RuleFieldDO> queryExtendUniqueFields();

    String queryUniqueNameByName(@Param("name") String name);

    List<Map<String,String>> queryDisplayNameAndEventType(List<String> names);
}
