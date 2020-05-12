package cn.tongdun.kunpeng.api.engine.reload.docache;

import cn.tongdun.ddd.common.domain.CommonEntity;

import java.util.List;

/**
 * @Author: liang.chen
 * @Date: 2020/4/26 下午2:33
 */
public interface IDataObjectCache<T extends CommonEntity>{


    /**
     * 将DO对象放到集中缓存中
     * @param dataObject
     */
    void set(T dataObject);

    /**
     * 根据uuid从redis缓存中获取
     * @param uuid
     * @return
     */
    T get(String uuid);

    /**
     * 批量获取
     * @param uuids
     * @return
     */
    List<T> batchGet(List<String> uuids);

    /**
     * 从缓存中删除
     */
    void remove(String uuid);

    /**
     * 按uuid从DB查询出来刷新到redis缓存
     * @param uuid
     */
    void refresh(String uuid);

    /**
     * 刷新所有数据，用于初始化数据使用
     */
    void refreshAll();

    /**
     * 根据索引从uuid从redis缓存中获取
     * @param idxName
     * @param args
     * @return
     */
    default List<T> getByIdx(String idxName, Object[] args,boolean onlyAvailable){
        throw new UnsupportedOperationException();
    }
}
