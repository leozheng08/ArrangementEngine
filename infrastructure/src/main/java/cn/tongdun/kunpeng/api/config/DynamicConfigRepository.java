package cn.tongdun.kunpeng.api.config;

import cn.fraudmetrix.shutter.client.ShutterClient;
import cn.fraudmetrix.shutter.client.common.annotations.ShutterFile;
import cn.fraudmetrix.shutter.client.common.annotations.ShutterObservable;
import cn.fraudmetrix.shutter.client.common.observer.ShutterObserver;
import cn.fraudmetrix.shutter.client.support.ShutterConfig;
import cn.tongdun.kunpeng.share.config.AbstractConfigRepository;
import cn.tongdun.tdframework.core.logger.Logger;
import cn.tongdun.tdframework.core.logger.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: liang.chen
 * @Date: 2019/12/12 下午8:12
 */

@Component
@ShutterObservable(filenames = "kunpeng-api-observe.properties")
@ShutterFile(filename = "kunpeng-api-observe.properties", observable = true,cluster = "DEFAULT_GROUP", group = "DEFAULT_GROUP") // 支持改变监听
public class DynamicConfigRepository extends AbstractConfigRepository implements InitializingBean, ShutterObserver,IDynamicConfigRepository{

    public final static Logger logger = LoggerFactory.getLogger(DynamicConfigRepository.class);


    @Override
    public void afterPropertiesSet() throws Exception {
        setConfig();
    }

    @Override
    public void update(String fileName, Object oldValue, Object newValue) throws Exception {
        setConfig();
    }


    public String getPropertiesFileName() {
        return "kunpeng-api-observe.properties";
    }

    public void setConfig() {
        logger.info("emit update shutter observe");
        try {
            ShutterConfig shutterConfig = ShutterClient.getConfig();
            String appName = System.getenv("APPNAME");
            logger.info("ShutterMonitor.setConfig.appName={}", appName);
            Map<String, Object> newConfig = shutterConfig.getFileConfig(appName, "DEFAULT_GROUP")
                    .getProperties(getPropertiesFileName());
            if (newConfig == null) {
                newConfig = new HashMap<>();
            }
            setConfig(newConfig);
        } catch (Exception e) {
            logger.error("拉取配置时出现异常", e);
        }
    }
}
