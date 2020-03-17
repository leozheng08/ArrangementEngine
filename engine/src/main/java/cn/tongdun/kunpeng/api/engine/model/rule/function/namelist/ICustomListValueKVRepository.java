package cn.tongdun.kunpeng.api.engine.model.rule.function.namelist;

import java.util.List;

/**
 * 自定义列表数据从数据库查询
 * @Author: liang.chen
 * @Date: 2020/2/28 下午3:08
 */
public interface ICustomListValueKVRepository {


    void putCustomListValueData(CustomListValue customListValue);

    void removeCustomListValueData(CustomListValue customListValue);

}
