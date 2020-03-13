package cn.tongdun.kunpeng.api.engine.reload;

import cn.tongdun.kunpeng.share.dataobject.EventTypeDO;
import cn.tongdun.tdframework.core.pipeline.IStep;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author: liang.chen
 * @Date: 2019/12/10 下午4:30
 */
public interface IReload<T> {

    //create创建,import导入,update修改,activate激活,recover恢复  都归此类操作
    boolean addOrUpdate(T t);

    //删除
    boolean remove(T t);

    //关闭
    boolean deactivate(T t);
}
