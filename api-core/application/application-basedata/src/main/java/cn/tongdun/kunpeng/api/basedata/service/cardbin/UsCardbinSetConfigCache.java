package cn.tongdun.kunpeng.api.basedata.service.cardbin;

import cn.tongdun.kunpeng.share.utils.TraceUtils;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Author: changkai.yang
 * @Date: 2020/7/24 下午6:11
 */
@Component
public class UsCardbinSetConfigCache {

    private static final Logger logger = LoggerFactory.getLogger(UsCardbinSetConfigCache.class);
    @Autowired
    @Qualifier("usJdbcTemplate2")
    JdbcTemplate usJdbcTemplate2;

    private String asCardbinSet = "bankcard_binall_1";

    public String getAsCardbinSet() {
        return asCardbinSet;
    }

    public void setAsCardbinSet(String asCardbinSet) {
        this.asCardbinSet = asCardbinSet;
    }

    @PostConstruct
    public void init(){
        //初始化加载一次配置
        loadConfig();

        //定时刷新
        ScheduledExecutorService scheduledExecutorService = new ScheduledThreadPoolExecutor(1);
        //定时器10秒钟执行一次
        scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    loadConfig();
                } catch (Exception e) {
                    logger.error(TraceUtils.getFormatTrace()+"定时刷新asCardbinSet缓存异常",e);
                }
            }
        }, 60, 60, TimeUnit.SECONDS);
    }

    private void loadConfig(){
        try {
            String sql = " select sim_type, route from sync_client_control where sim_type='bankcard_binall' ";
            List<Map<String, Object>> dataList = usJdbcTemplate2.queryForList(sql);
            if(!CollectionUtils.isEmpty(dataList)){
                String route = dataList.get(0).get("route").toString();
                if(route != null){
                    asCardbinSet = "bankcard_binall" + "_" + route;
                }
            }else {
                logger.error("查询不到asCardbinSet配置信息");
            }
        } catch (Exception e) {
            logger.error("查询asCardbinSet配置信息出错", e);
        }
    }
}
