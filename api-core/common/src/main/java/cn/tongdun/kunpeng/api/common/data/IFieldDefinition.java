package cn.tongdun.kunpeng.api.common.data;


public interface IFieldDefinition {

    String getFieldType() ;

    String getFieldCode();

    String getDisplayName() ;

    String getProperty() ;

    String getDataType() ;

    Integer getMaxLength();

    String getEventType();

    String getPartnerCode();

    boolean isExtField();

    String getFieldName();

}
