package cn.tongdun.kunpeng.api.consumer.infrastructure.persistence.repository.impl;

import cn.tongdun.kunpeng.api.infrastructure.persistence.mybatis.mappers.kunpeng.CustomList4ConsumerDAO;
import cn.tongdun.kunpeng.api.infrastructure.persistence.mybatis.mappers.kunpeng.CustomListValue4ConsumerDAO;
import cn.tongdun.kunpeng.api.consumer.infrastructure.persistence.repository.intf.CustomListDataService;
import cn.tongdun.kunpeng.share.dataobject.CustomListDO;
import cn.tongdun.kunpeng.share.dataobject.CustomListValueDO;
import cn.tongdun.kunpeng.share.utils.GenerateUUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("customListDataService")
public class CustomListDataServiceImpl implements CustomListDataService {

    private static final Logger logger = LoggerFactory.getLogger(CustomListDataServiceImpl.class);

    @Autowired
    private CustomList4ConsumerDAO customListDOMapper;
    @Autowired
    private CustomListValue4ConsumerDAO customListValueDOMapper;

    @Override
    public CustomListDO selectByUuid(String uuid) {
        return customListDOMapper.selectByUuid(uuid);
    }

    @Override
    public boolean insert(String partnerCode, String appName, String customListUuid, String itemValue,
                          String description, String createBy, Date effactTime, Date expireTime) {
        String listNameUuid = customListUuid;
        String listDataVal = itemValue;
        Timestamp now = new Timestamp(System.currentTimeMillis());
        CustomListValueDO listDataDO = new CustomListValueDO();
        listDataDO.setPartnerCode(partnerCode);
        if (isComplexList(listNameUuid)) {
            listDataDO.setColumnValues(listDataVal);
            listDataDO.setColumnType(1);
        } else {
            listDataDO.setDataValue(listDataVal);
            listDataDO.setColumnType(0);
        }
        listDataDO.setCustomListUuid(listNameUuid);
        listDataDO.setUuid(GenerateUUID.newUUID());
        listDataDO.setDescription(description);
        listDataDO.setCreator(createBy);
        listDataDO.setOperator(createBy);
        listDataDO.setGmtCreate(now);
        listDataDO.setGmtModify(now);
        listDataDO.setGmtEffect(effactTime);
        listDataDO.setGmtExpire(expireTime);
        Integer result = null;
        try {
            result = customListValueDOMapper.insert(listDataDO);
        } catch (Exception e) {
            logger.error("ruletrigger occur insert error",e);
        }
        if(result == null) return false;
        return result > 0;
    }

    @Override
    public boolean exist(String partnerCode, String dataValue, String listNameUuid) {

        CustomListValueDO customListValueDO = queryCustomListValueDO(partnerCode,dataValue,listNameUuid);
        if (customListValueDO == null) {
            return false;
        }
        return true;
    }



    @Override
    public boolean isComplexList(String customListUuid) {
        CustomListDO customListDO = customListDOMapper.selectByUuid(customListUuid);
        if (null != customListDO){
            return customListDO.getColumnType()==1;
        }
        return false;
    }

    @Override
    public String expireTimeExist(String partnerCode, String dataValue, String listNameUuid, Date gmtExpire) {
        CustomListValueDO customListValueDO = queryCustomListValueDO(partnerCode,dataValue,listNameUuid);
        if (customListValueDO==null){
            return "insert";
        }
        if (customListValueDO.getGmtExpire()!=gmtExpire) {
            return "replace";
        }

        return "ignore";
    }

    @Override
    public CustomListValueDO queryCustomListValueDO(String partnerCode, String dataValue, String listNameUuid) {
        Map<String, Object> queryMap = new HashMap<String, Object>();
        queryMap.put("partnerCode", partnerCode);
        if (isComplexList(listNameUuid)){
            queryMap.put("columnValues",dataValue);
        } else {
            queryMap.put("dataValue", dataValue);
        }
        queryMap.put("customListUuid", listNameUuid);
        List<CustomListValueDO> listDatas = null;
        try {
            listDatas = customListValueDOMapper.queryByParams(queryMap);
        } catch (Exception e) {
            logger.error("ruletrigger queryByParams occur sql error", e);
        }
        if (listDatas == null) {
            return null;
        }
        if (listDatas.isEmpty()) {
            return null;
        }
        return listDatas.get(0);
    }

    @Override
    public boolean replace(String partnerCode, String listUuid, String fieldString, String createBy, Date expireTime) {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        CustomListValueDO customListValueDO = queryCustomListValueDO(partnerCode,fieldString,listUuid);
        if (customListValueDO == null) {
            return false;
        }
        customListValueDO.setGmtExpire(expireTime);
        customListValueDO.setGmtModify(now);
        customListValueDO.setOperator(createBy);
        Integer result = null;
        try {
            result = customListValueDOMapper.update(customListValueDO);
        } catch (Exception e) {
            logger.error("ruletrigger update occur sql error", e);
        }
        if(result == null) return false;
        return result > 0;
    }
}
