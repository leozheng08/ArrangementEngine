package cn.tongdun.kunpeng.api.engine.constant;

/**
 * @Author: liang.chen
 * @Date: 2020/4/28 上午12:18
 */
public class ReloadConstant {

    //放在线程上下文中的属性：强制从数据库中查询
    public static final String THREAD_CONTEXT_ATTR_FORCE_FROM_DB = "force_from_db";

    public static final String THREAD_CONTEXT_ATTR_MODIFIED_VERSION = "query_modified_version";


    public static final String IDX_NAME_ALL = "all";
    public static final String IDX_NAME_ALL_AVAILABLE = "allAvailable";
    public static final String SUFFIX_AVAILABLE = "Available";
    public static final String IDX_NAME_UUID = "uuid";
}
