package cn.tongdun.kunpeng.client.data;

import java.io.Serializable;

/**
 * @Author: liang.chen
 * @Date: 2020/3/2 上午12:56
 */
public interface IOutputField extends Serializable {
    String getFieldName() ;

    void setFieldName(String fieldName) ;

    Object getValue() ;

    void setValue(Object value) ;

    String getType() ;

    void setType(String type) ;

    String getDesc() ;

    void setDesc(String desc) ;
}
