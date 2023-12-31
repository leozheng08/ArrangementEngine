package cn.tongdun.kunpeng.api.engine.reload;

import cn.tongdun.kunpeng.api.common.util.TimestampUtil;
import cn.tongdun.kunpeng.share.dataobject.EventTypeDO;
import cn.tongdun.tdframework.core.pipeline.IStep;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 各个实体支持的动作参见 http://wiki.tongdun.me/pages/viewpage.action?pageId=34035142
 *
 * @Author: liang.chen
 * @Date: 2019/12/10 下午4:30
 */
public interface IReload<T> {

    boolean create(T t);

    boolean update(T t);

    boolean activate(T t);

    boolean deactivate(T t);

    boolean remove(T t);


    default boolean remove(List<T> list) {
        if (list == null) {
            return true;
        }
        boolean result = true;
        for (T t : list) {
            if (!remove(t)) {
                result = false;
            }
        }
        return result;
    }

    ;

    default boolean create(List<T> list) {
        if (list == null) {
            return true;
        }
        boolean result = true;
        for (T t : list) {
            if (!create(t)) {
                result = false;
            }
        }
        return result;
    }

    ;

    default boolean update(List<T> list) {
        if (list == null) {
            return true;
        }
        boolean result = true;
        for (T t : list) {
            if (!update(t)) {
                result = false;
            }
        }
        return result;
    }

    ;

    default boolean activate(List<T> list) {
        if (list == null) {
            return true;
        }
        boolean result = true;
        for (T t : list) {
            if (!activate(t)) {
                result = false;
            }
        }
        return result;
    }

    ;

    default boolean deactivate(List<T> list) {
        if (list == null) {
            return true;
        }
        boolean result = true;
        for (T t : list) {
            if (!deactivate(t)) {
                result = false;
            }
        }
        return result;
    }

    ;


    //挑战者任务暂停
    default boolean suspend(T t) {
        throw new UnsupportedOperationException();
    }

    ;

    //挑战者任务终止
    default boolean terminate(T t) {
        throw new UnsupportedOperationException();
    }

    ;

    //排序
    default boolean sort(List<T> list) {
        throw new UnsupportedOperationException();
    }

    ;

    // 子策略批量导入
    default boolean imported(T t) {
        throw new UnsupportedOperationException();
    }

    ;

    //切换决策模式
    default boolean switchDecisionMode(T t) {
        throw new UnsupportedOperationException();
    }

    ;


    default int timestampCompare(long x, long y) {
        return TimestampUtil.compare(x, y);
    }
}
