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