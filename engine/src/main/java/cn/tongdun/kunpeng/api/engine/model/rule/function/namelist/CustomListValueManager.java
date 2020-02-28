package cn.tongdun.kunpeng.api.engine.model.rule.function.namelist;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 自定义列表数据从数据库加载并设置到缓存
 * @Author: liang.chen
 * @Date: 2020/2/28 下午3:08
 */
@Component
public class CustomListValueManager {

    @Autowired
    ICustomListValueRepository customListValueRepository;

    public void loadDataToRedis(){

    }
}
