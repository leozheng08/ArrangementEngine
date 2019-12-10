新时代基于spring boot的应用,使用JAR包方式启动,结构简单高效,不需要依赖外部TOMCAT

### 顶层设计原则

一次打包,到处运行.应用包与具体的环境和机房彻底分离,尽量统一公司常规的配置,

在此基础上尽量屏蔽框架对业务的复杂性,同时保留了灵活性和可介入性.

### 手动创建工程

执行下列命令

wget http://10.57.17.156:7999/generate

bash generate APP

会在当前目录下生成一个名为APP的标准应用,APP不得有除英文小写字母数字中划线之外的任何其它符号

### 自动创建工程

申请新应用时ZEUS系统会自动从此模版中创建功能,无需人工干预

### 工程结构介绍

均为标准MAVEN项目

* core/ 核心工程代码所在模块
* client/ 二方库子模块,存放接口定义,常量等代码

是否可以增加更多的子模块?

这是一个问题,原则上这两个模块可以满足几乎一切业务场景,建议通过包名而不是module来划分业务模块;

原有的子模块划分被证明在大部分情况下是毫无用处的,并增加了开发和维护上的复杂性.

### 配置管理

业务首次使用请登陆shutter创建一个新应用配置(名称必须与APP精确对应)

业务所有的properties应统一在shutter中配置和管理,不要编辑application-*.properties文件,

也不要在里面添加任何应用自定义的属性项, 这些文件仅仅是给shutter和基础框架初始化使用

### 应用打包方式

mvn clean package

会在/core/target目录下生成相应JAR包(名为APP.jar),这个JAR包可以直接启动

同时,会在/target目录下生成最终分发包(名为APP-dist.tgz)

生产环境下打包命令参考 ./core/src/main/resources/special/build.sh

#### 二方库打包方式

cd client

mvn clean deploy

会将二方库打包并上传到maven仓库,请严格注意修改client的版本,需要修改client/pom.xml中的version以及core/pom.xml中的client-version

### 启动方式

#### IDEA启动

选择AppMain类并作为标准Java应用启动即可

#### 线下命令行启动

java -jar APP.jar --spring.profiles.active=default

其中

* --spring.profiles.active指定了启动时加载哪个配置,如test代表application-test.properties,线下可以不需要传递该参数,使用default
* --app.dc参数则标识当前运行机房(目前有HZ/SH/IDNU/SHU/BJU/GZU/SGU),线下则不需要传入该参数

#### 生产环境启动

发布系统将tgz拷贝到机器上并解压,首先执行preboot脚本,该脚本用于自定义一些特殊启动逻辑;
脚本export下列两个环境变量

* JVM_EXT_PARAM 自定义JVM启动参数,如Xmx等
* APP_EXT_PARAM 自定义应用启动参数,将被添加到启动命令行末尾(如spring专用的'--'开头变量)

发布系统解压tgz:/lib中的APP-client-VERSION.jar

发布系统环境变量规范

* export APP=appdemo #应用名称
* export CLUSTER=appdemo #集群名称
* export DC=hz #当前机房,可取值sh,hz,idnu
* export ENV=default #当前环境,可取值如下:
    * default 开发环境(默认值)
    * test 测试环境
    * smoke 冒烟测试
    * dev-common 开发公共环境
    * test-common 测试公共环境
    * staging 预发环境
    * production 正式生产环境
    * sandbox 沙盒环境
    * tar 压测环境
* export GRAYLOG='192.168.6.28' #graylog地址
* export SHUTTER='10.57.17.39:8188,10.57.17.40:8188' #shutter地址
* export APP_RUN_MODE=fg #fg表示前台启动,否则默认为后台启动

发布系统[启动脚本模版](https://gitlab.fraudmetrix.cn/module/cicd-shell-demo/raw/master/start_tpl.sh)和[启动脚本函数](https://gitlab.fraudmetrix.cn/module/cicd-shell-demo/raw/master/java/stop.sh)参考

发布系统[停机脚本](https://gitlab.fraudmetrix.cn/module/cicd-shell-demo/raw/master/java/stop.sh)参考


### 详解

#### 启动入口

AppMain为整个应用的启动入口类,一般情况下不需要对该类做任何修改

#### 日志

* 线下环境一般是/tmp/output/APP/logs
* 其它环境一般是/home/admin/output/APP/logs

其中,tomcat的accesslog默认已启用

#### 监控

在 ```app.xml``` 添加，shutter配置文件（需要获取```influxdb```应用的```influxdb.properties```的权限）。

```xml
<config:property-placeholder location="classpath*:properties/influxdb.properties" ignore-unresolvable="true"
                             application="influxdb" cluster="DEFAULT_GROUP"/>
```

在 ```applicatioin-*.properties``` 文件中添加配置。
```
management.metrics.export.influx.db=${app.name}
management.metrics.export.influx.step=10s
management.metrics.export.influx.uri=${influxdb.url}
```

增加MetricConfig.java
```java
package cn.tongdun.appdemo;

import cn.fraudmetrix.metrics.meta.ServerInfoProvider;
import cn.fraudmetrix.metrics.meta.TagProvider;
import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.Map;

@Configuration
public class MetricConfig {

    @Autowired
    private MeterRegistry registry;

    @Bean
    public TimedAspect timedAspect(MeterRegistry registry) {
        return new TimedAspect(registry);
    }

    @Bean
    public TagProvider serverInfoProvider() {
        return new ServerInfoProvider();
    }

    @PostConstruct
    public void init() throws Exception {
        Map<String, String> tags = serverInfoProvider().provide();
        for (Map.Entry<String, String> entry : tags.entrySet()) {
            registry.config().commonTags(entry.getKey(), entry.getValue());
        }
    }
}
```

使用示例请参考 ```/core/src/main/java/cn/tongdun/appdemo/biz/MetricsDemo.java```


#### 优雅停机任务

发布系统在停机时会触发/ok.html?down=true,等待若干秒后(6s)才会真正执行kill,
业务上若有需要预先停止的资源请调用AppMain.addPreHaltTask()方法添加相关逻辑;

#### TOMCAT特殊配置

对于大部分业务而言默认tomcat参数足够应对,如果有特殊需求(如增大线程池最大线程数),
请在相应环境的properties中修改

#### 运行时状态

该功能为2019.1.2新加入

用户通过访问/metadata/env/&lt;property.name&gt;/可获取当前进程的各类环境变量

例如/metadata/env/commit/即可拿到当前代码的git commit id

参考文档
https://docs.spring.io/spring-boot/docs/current/reference/html/common-application-properties.html

#### 默认JVM参数

以下配置针对生产环境标准4C8G机器
```text
-server -XX:MaxMetaspaceSize=256m -Xss256k -XX:+UseG1GC -XX:MaxGCPauseMillis=100
-XX:G1ReservePercent=10 -XX:InitiatingHeapOccupancyPercent=30
-XX:ConcGCThreads=4 -Xmx4g -Xms4g -XX:+PrintGCDateStamps -XX:+PrintGCDetails
-Xloggc:/home/admin/output/$APP/logs/gc.log -XX:+PrintGCApplicationStoppedTime
-XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/home/admin/output/$APP/
-Djava.awt.headless=true -Djava.net.preferIPv4Stack=true -Dfile.encoding=UTF-8
-Djava.net.preferIPv4Addresses -DdisableIntlRMIStatTask=true
-Dcom.sun.management.jmxremote
-Dcom.sun.management.jmxremote.authenticate=false
-Dcom.sun.management.jmxremote.ssl=false
-Djava.rmi.server.hostname=\`hostname\`
```

#### mybatis注意事项

> 若要使用mybatis，请注意使用spring集成版本mybatis

* 设置mybatis和spring-boot的依赖库，不要设置默认的spring库，否则可能引起莫名错误

    ```xml
    <mybatis-spring-boot-starter-version>1.3.2</mybatis-spring-boot-starter-version>
    ```
    ```xml
    <dependency>
        <groupId>org.mybatis.spring.boot</groupId>
        <artifactId>mybatis-spring-boot-starter</artifactId>
        <version>${mybatis-spring-boot-starter-version}</version>
    </dependency>
    ```

