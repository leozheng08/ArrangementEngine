package cn.tongdun.kunpeng.api.consumer.infrastructure.persistence.repository.intf;

import cn.tongdun.kunpeng.share.dataobject.CustomListDO;
import cn.tongdun.kunpeng.share.dataobject.CustomListValueDO;

import java.util.Date;

public interface CustomListDataService {

    CustomListDO selectByUuid(String uuid);

    boolean insert(String partnerCode, String appName, String listUuid, String fieldString, String description, String createBy, Date effactTime, Date expireTime);

    boolean exist(String partnerCode, String dataValue, String listNameUuid);

    boolean isComplexList(String customListUuid);

    String expireTimeExist(String partnerCode, String dataValue, String listNameUuid, Date gmtExpire);

    CustomListValueDO queryCustomListValueDO(String partnerCode, String dataValue, String listNameUuid);

    boolean replace(String partnerCode, String listUuid, String fieldString, String createBy, Date expireTime);


}
