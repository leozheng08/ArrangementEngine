package cn.tongdun.kunpeng.api.engine.reload.reload;

import cn.tongdun.kunpeng.share.dataobject.EventTypeDO;
import cn.tongdun.tdframework.core.pipeline.IStep;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author: liang.chen
 * @Date: 2019/12/10 下午4:30
 */
public interface IReload<T> {


    boolean addOrUpdate(T t);

    boolean remove(T t);
}
