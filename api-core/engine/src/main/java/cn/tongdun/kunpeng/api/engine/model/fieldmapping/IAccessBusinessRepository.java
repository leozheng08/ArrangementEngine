package cn.tongdun.kunpeng.api.engine.model.fieldmapping;


import java.util.List;
import java.util.Set;

/**
 * @author: yuanhang
 * @date: 2020-06-12 11:20
 **/
public interface IAccessBusinessRepository {

    /**
     * 获取所有可用的业务接入
     * @return
     */
    List<AccessBusiness> queryAllUsableAccess(Set<String> partnerCode);

    /**
     * 根据uuid查询业务对接
     * @param uuid
     * @return
     */
    AccessBusiness selectByUuid(String uuid);
}