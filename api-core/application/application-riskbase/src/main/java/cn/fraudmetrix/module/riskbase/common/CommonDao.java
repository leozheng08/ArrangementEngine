package cn.fraudmetrix.module.riskbase.common;

import org.apache.ibatis.annotations.Param;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

/**
 * 通用Dao，提供通用方法，如需特殊方法，可在自己接口中定义
 *
 * @author chenchanglong 2014年6月18日 下午4:59:14
 */
public interface CommonDao<T> {

    /**
     * “分页查询”参数
     */
    public static final String CURRENT_INDEX = "currentIndex";
    public static final String PAGE_SIZE = "pageSize";
    /**
     * “分页查询”参数默认值
     */
    public static final Integer DEFAULT_CURRENT_INDEX = 0;             // 首页
    public static final Integer DEFAULT_PAGE_SIZE = 10;            // 每页展示10条数据

    /**
     * 查询记录数
     *
     * @return
     */
    int count(Map<String, Object> params);

    /**
     * 查询所有数据
     *
     * @return List<T>
     */
    List<T> queryAll();

    /**
     * 查找最新数据
     *
     * @param timestamp
     * @return List<T>
     */
    List<T> queryLatest(Timestamp timestamp);

    /**
     * 条件查询
     *
     * @param params
     * @return List<T>
     */
    List<T> queryByParams(Map<String, Object> params);

    /**
     * 根据UUID查找
     *
     * @param uuid
     * @return T
     */
    T queryByUuid(@Param("uuid") String uuid);

    /**
     * 条件查询，结果分页
     *
     * @param params
     * @return List<T>
     */
    List<T> queryByPage(Map<String, Object> params);

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
     * 删除对象
     *
     * @param t
     * @return
     */
    Integer delete(T t);
}
