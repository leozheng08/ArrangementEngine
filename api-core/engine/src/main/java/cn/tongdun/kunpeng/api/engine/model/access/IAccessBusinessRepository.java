package cn.tongdun.kunpeng.api.engine.model.access;


import java.util.List;

/**
 * @author: yuanhang
 * @date: 2020-06-12 11:20
 **/
public interface IAccessBusinessRepository {

    /**
     * 获取所有可用的业务接入
     * @return
     */
    List<AccessBusiness> queryAllUsableAccess();
}