

1、更新版本，Ctrl+shirt+R全局替换project.version，版本定义规则：1.0.2-beta-${mmDD}-${seq}，例如1.0.2-beta-0703-01
2、执行  mvn install -Dmaven.test.skip=true
3、执行  mvn deploy -Dmaven.test.skip=true

