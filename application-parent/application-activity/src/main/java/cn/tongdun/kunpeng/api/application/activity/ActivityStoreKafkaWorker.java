package cn.tongdun.kunpeng.api.application.activity;

import cn.tongdun.kunpeng.common.data.QueueItem;
import org.springframework.stereotype.Component;

import java.util.function.Predicate;

/**
 * @Author: liang.chen
 * @Date: 2020/3/3 下午7:42
 */
@Component
public class ActivityStoreKafkaWorker implements EventWorker{

    @Override
    public void onEvent(QueueItem item){

    }

    @Override
    public String getName(){
        return getClass().getSimpleName();
    }


    //过滤条件
    @Override
    public Predicate<QueueItem> getFilter(){
        return (item)->{
            //过滤掉测试标记的
            if(item.getContext().isTestFlag()){
                return false;
            }
            return true;
        };
    }
}
