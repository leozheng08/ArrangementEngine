package cn.tongdun.kunpeng.api.springboot.autoconfig.kafka;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @Author: liang.chen
 * @Date: 2020/4/21 下午7:04
 */
@Configuration
@Import(ZkConfigCenterConfiguration.class)
public class KafkaAutoConfiguration {
}
