package cn.tongdun.kunpeng.api.dao;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 通用Dao，提供通用方法，如需特殊方法，可在自己接口中定义
 */
public interface CommonDao<T> {

    /**
     * 分页查询
     * 
     * @return List<T>
     */
    List<T> queryByPage(Map<String, Object> map);

    /**
     * 查询所有数据
     * 
     * @return List<T>
     */
    List<T> queryAll();

    /**
     * 根据uuid查询对象
     * 
     * @param uuid 该对象的uuid
     * @return 目标对象
     */
    T queryByUuid(@Param("uuid") String uuid);

    /**
     * 插入对象
     * 
     * @param t 要插入的对象
     * @return
     */
    Integer insert(T t);

    /**
     * 更新对象
     * 
     * @param t 要更新的对象
     * @return
     */
    Integer update(T t);

    /**
     * 根据自定义参数查找
     * 
     * @param params
     * @return
     */
    List<T> queryByParams(Map<String, Object> params);

    /**
     * 更具uuid删除对象
     * 
     * @param uuid 该对象的uuid
     * @return
     */
    Integer delete(@Param("uuid") String uuid);

    List<T> pageQueryAll(Map<String, Object> params);

}
