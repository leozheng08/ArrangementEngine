package cn.tongdun.kunpeng.api.engine.reload;

import cn.tongdun.kunpeng.share.dataobject.EventTypeDO;
import cn.tongdun.tdframework.core.pipeline.IStep;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @Author: liang.chen
 * @Date: 2019/12/10 下午4:30
 */
public interface IReload<T> {

//    //create创建,import导入,update修改,activate激活,recover恢复  都归此类操作
//    boolean addOrUpdate(T t);
//
//    //删除
//    boolean remove(T t);
//
//    //关闭
//    boolean deactivate(T t);
//
//    //批量变更
//    default boolean batchUpdate(List<T> list){
//        throw new RuntimeException("not support");
//    }

    boolean create(T t);
    boolean update(T t);
    boolean activate(T t);
    boolean deactivate(T t);
    boolean remove(T t);

    default boolean BATCH_REMOVE(List<T> list) {
        if(list == null){
            return true;
        }
        boolean result = true;
        for(T t : list) {
            if(!remove(t)){
                result = false;
            }
        }
        return result;
    };
    default boolean BATCH_CREATE(List<T> list){
        if(list == null){
            return true;
        }
        boolean result = true;
        for(T t : list) {
            if(!create(t)){
                result = false;
            }
        }
        return result;
    };
    default boolean BATCH_UPDATE(List<T> list) {
        if(list == null){
            return true;
        }
        boolean result = true;
        for(T t : list) {
            if(!update(t)){
                result = false;
            }
        }
        return result;
    };
    default boolean BATCH_ACTIVATE(List<T> list) {
        if(list == null){
            return true;
        }
        boolean result = true;
        for(T t : list) {
            if(!activate(t)){
                result = false;
            }
        }
        return result;
    };
    default boolean BATCH_DEACTIVATE(List<T> list) {
        if(list == null){
            return true;
        }
        boolean result = true;
        for(T t : list) {
            if(!deactivate(t)){
                result = false;
            }
        }
        return result;
    };



    default boolean importEvent(T t){
        return create(t);
    };

    default boolean recover(T t){
        throw new RuntimeException("not support");
    };
    default boolean suspend(T t){
        throw new RuntimeException("not support");
    };
    default boolean terminate(T t){
        throw new RuntimeException("not support");
    };
    default boolean sort(){
        throw new RuntimeException("not support");
    };


}
