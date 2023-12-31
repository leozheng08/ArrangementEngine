package cn.tongdun.kunpeng.api.engine.reload.docache;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 *
 * @Author: liang.chen
 * @Date: 2020/4/26 下午7:14
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Cacheable {

    //采用索引名称,如果没填则默认按uuid查询
    String idxName() default "";

    boolean onlyAvailable() default false;
}