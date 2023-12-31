v1.3.10(2021-07-06) 黄良帅
该版本主要修复了binInfoQueryService和SaaSIdInfoService默认扩展点的问题
国内和印尼需要注意该改动是否有影响


v1.3.11(2021-07-07) 黄良帅
该版本主要修复了北美isContainApplicationId字段多余的问题
对其他版本无影响

v1.3.12(2021-07-08) 黄良帅
该版本主要修复了dealWithTrueIp方法的逻辑问题
对其他版本无影响

v1.3.13(2021-07-12) 黄良帅
该版本主要修复了UsDeviceInfoExt合并代码过程中丢失部分代码及增加CHANGELOG日志
对其他版本无影响

v1.3.17(2021-07-29) 陈庆然
- 该版本主要删掉了SaaSBinInfoService和SaaSIdInfoService，清除一些代码的强依赖
- 清除starters里的starter-sea模块
对其他版本无影响


v1.3.19(2021-08-25) 黄进
- 该版本新增了流量复制功能
对其他版本无影响
  
v1.3.21(2021-08-31)
- 该版本主要更新了 ReasonCode 的配置监控和WIKI对齐
- 补充了一些监控的业务信息如partner_code
- 修改InfluxDbMetricsImpl的实现，之前只能实现一个tag打点，现支持多个tag
对其他版本无影响

v1.3.22(2021-09-07)
- 合并代码

v1.3.33(2021-10-22)
- bugfix:解决dubbo调用的序列化问题
  client包下新增两个类USRiskResponse，USRiskResponseFactory
  client包下部分类实现序列化接口
备注：本次发布业务层仅涉及kunpeng-saas-api，仅升级类client包版本，未做其他变更


v1.3.34(2021-10-27)
- bugfix:修复规则不生效

v1.3.35(2021-11-04)
- bugfix:修复操作配置赋值问题
- 邮箱模型50601问题修改

v1.3.36(2021-11-09)
- bugfix:DefaultRuleDetailOutputExt.java，对于指标查询逻辑调整，判断非空 
- AssignFieldValueStep.java，对于eventOccurTime传入空串进行处理

v1.3.37(2021-12-07)
- 策略指标计算优化：去除step，优化缓存数据结构，真正的计算逻辑下沉到context.getPolicyIndex中
- 集群管理包变更，集群分配
- 发送Kafka消息时白名单合作方不分配，用于gateway压测

v1.3.38(2021-12-07)
- 集群管理，动态脚本修改

v1.3.39
- 自定义输出修改

v1.3.49
- 集群分配正式发布

v1.3.50(2021-12-20)
- fp-client版本升级到3.4.0

v1.3.51(2021-12-23)
- 输出详情新增spendTime
-集群分配正式发布

v1.3.52
- logback-core升级到1.2.9
- fp-client升级到3.4.0
- 批量调用调整，相关类放到module方便调用
- druid包升级到1.2.8
- RiskService.java,subReasonCode监控打点修改

v1.3.53
- 国内反欺诈拆分项目合并到master

v1.4.1(2022-03-02)
- 国内反欺诈拆分三期合并到master
- 详细变动点wiki：http://wiki.tongdun.me/pages/viewpage.action?pageId=44729183

v1.4.2(2022-03-11)
- 函数工具箱规则模板NPE问题修复
- 指标结果解析日志精简

v1.4.4(2022-04-02)
- domainEvent线程池队列长度调整
- 正则表达式超时报错状态修改
- chassis查询接口订正
- 三方接口调用状态码处理

v1.4.41(2022-04-07)
- DictionaryManager字典缓存修改，初始化加载

v1.4.92(2022-08-01)
- 规则变更通知改动（国内、海外）
- wiki:http://wiki.tongdun.me/pages/viewpage.action?pageId=46243669

v1.4.94(2022-08-12)
- ReasonCode新增邮箱模型相关状态码

v1.5.1(2022-08-24)
- 1.shenwei-gateway替换gaea-api依赖：jar依赖，接口，版本号

V1.5.3(2022-09-09)
- 调用指标明文转密文
- 此处的  1.5.3 等价于 1.5.3-tmp2

V1.5.4(2022-09-09)
- 冠军挑战者的优化

V1.5.53(2020-09-16)
- Risk新加处理阿拉丁黑白名单的phase,对其他站点无影响
- liangshuai的变更,自定义列表数据生效，未知具体影响

V1.5.54(2022-09-19)
- 由于北美cardbin接口的dubbo.xml写在了平台层，故引入时需加以下配置：
- cardbin.dubbo.version=1.0.0
  cardbin.dubbo.timeout=100
- 但是该情况是不合理的，后续将该配置放到业务层中

V1.5.55(2022-09-19)
- 北美cardbin接口改造为dubbo接口
- 删除V1.5.54中平台层的cardbin dubbo.xml中的配置；其他业务层引入时无需再加dubbo配置

v1.5.56
- 默认实现决策接口返回值增加规则标签字段；(刘佩)

v1.5.58(2022-10-18)
- 批量操作缓存一致性问题修复

v1.5.60(2022-10-27)
- context中新增appOs字段

v1.5.63(2022-11-02)
- 规则模板优化

v1.5.64(2022-11-02)
- 新增规则模板的判空操作


v1.5.66(2022-11-16)
- kunpeng-share版本升级

v1.5.67(v1.5.68 2022-11-30) 
- context新增os、browser字段
- response新增cs_detail
- 判断是否场景化请求逻辑由eventId更新为eventType判断