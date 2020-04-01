package cn.tongdun.kunpeng.api.springboot.autoconfig.kafka;

/**
 * kafka
 *
 * @author zhengwei
 * @date 2020/3/13 5:34 下午
 **/
//@Configuration
//@Import({KafkaAutoConfiguration.KafkaConfigCenterConfiguration.class, KafkaAutoConfiguration.KafkaProducerConfiguration.class})
public class KafkaAutoConfiguration {

//    /**
//     * ZKConfigCenter
//     *
//     * @author zhengwei
//     * @date 2020/3/13 3:25 下午
//     **/
//    @Configuration
//    @Slf4j
//    @ConditionalOnMissingBean(ZKConfigCenter.class)
//    @ConditionalOnClass(ZKConfigCenter.class)
//    public static class KafkaConfigCenterConfiguration implements ImportBeanDefinitionRegistrar {
//        @Override
//        public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
//            RootBeanDefinition beanDefinition = new RootBeanDefinition(ZKConfigCenter.class);
//            beanDefinition.setInitMethodName("init");
//            beanDefinition.setDestroyMethodName("close");
//            beanDefinition.getPropertyValues().add("zkserver", "${configcenter.endpoint:192.168.6.55:2181,192.168.6.56:2181,192.168.6.57:2181}");
//            beanDefinition.getPropertyValues().add("businessUnit", "${business.unit:main}");
//            registry.registerBeanDefinition("zkConfigCenter", beanDefinition);
//            log.info("Register ZKConfigCenter success");
//        }
//    }
//
//    /**
//     * SimpleProducer
//     *
//     * @author zhengwei
//     * @date 2020/3/13 3:25 下午
//     **/
//    @Configuration
//    @Slf4j
//    @ConditionalOnMissingBean(SimpleProducer.class)
//    @ConditionalOnClass(SimpleProducer.class)
//    public static class KafkaProducerConfiguration implements ImportBeanDefinitionRegistrar {
//        @Override
//        public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
//            RootBeanDefinition beanDefinition = new RootBeanDefinition(SimpleProducer.class);
//            beanDefinition.setInitMethodName("init");
//            beanDefinition.setDestroyMethodName("close");
//
//            List<String> topics = Lists.newArrayList("kunpeng_admin_event");
//
//            beanDefinition.getPropertyValues().add("topics", topics);
//
//            beanDefinition.getPropertyValues().add("configCenter", new RuntimeBeanReference("zkConfigCenter"));
//            registry.registerBeanDefinition("simpleProducer", beanDefinition);
//            log.info("Register SimpleProducer success");
//        }
//    }

}
