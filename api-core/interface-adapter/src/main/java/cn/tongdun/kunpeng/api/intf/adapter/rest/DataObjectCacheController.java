package cn.tongdun.kunpeng.api.intf.adapter.rest;

import cn.tongdun.kunpeng.api.engine.constant.ReloadConstant;
import cn.tongdun.kunpeng.api.engine.reload.docache.DataObjectCacheFactory;
import cn.tongdun.kunpeng.api.engine.reload.docache.IDataObjectCache;
import cn.tongdun.kunpeng.share.dataobject.EventTypeDO;
import cn.tongdun.tdframework.core.concurrent.ThreadContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.Map;

/**
 * @Author: liang.chen
 * @Date: 2019/8/7 下午7:50
 */

@RestController
@RequestMapping("/manager")
public class DataObjectCacheController {
    private Logger logger = LoggerFactory.getLogger(DataObjectCacheController.class);

    @Autowired
    private DataObjectCacheFactory dataObjectCacheFactory;




    @RequestMapping(value = "loadAllDataObjectToRedis", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public String loadDataToRedis(@RequestHeader Map<String,String> header, @RequestParam Map<String,String> request,
                                  HttpServletResponse response) {

        //强制从数据库中查询
        ThreadContext.getContext().setAttr(ReloadConstant.THREAD_CONTEXT_ATTR_FORCE_FROM_DB,true);
        StringBuilder builder = new StringBuilder();
        try {

            Collection<IDataObjectCache> list = dataObjectCacheFactory.getDOCaches();
            if(list == null || list.isEmpty()){
                return "dataObjectCaches is empty!";
            }
            //刷新全部
            list.forEach(dataObjectCache ->{
                refreshAll(dataObjectCache,builder);
            });
//            IDataObjectCache dataObjectCache = dataObjectCacheFactory.getDOCache(EventTypeDO.class);
//            refreshAll(dataObjectCache, builder);
        }finally {
            ThreadContext.clear();
        }
        return builder.toString();
    }

    private void refreshAll(IDataObjectCache dataObjectCache,StringBuilder builder){
        dataObjectCache.refreshAll();
        Class tClass = (Class)((ParameterizedType)dataObjectCache.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        builder.append(tClass.getTypeName() + " refreshAll success ");

    }
}
