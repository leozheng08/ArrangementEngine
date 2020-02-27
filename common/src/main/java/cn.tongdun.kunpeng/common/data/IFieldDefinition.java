package cn.tongdun.kunpeng.common.data;


public interface IFieldDefinition {

    String getFieldType() ;

    String getFieldCode();

    String getDisplayName() ;

    String getProperty() ;

    String getDataType() ;

    Integer getMaxLength();

    boolean isExtField();

}
