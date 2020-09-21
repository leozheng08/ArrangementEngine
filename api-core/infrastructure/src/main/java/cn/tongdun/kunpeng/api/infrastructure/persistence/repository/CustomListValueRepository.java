package cn.tongdun.kunpeng.api.infrastructure.persistence.repository;

import cn.tongdun.kunpeng.api.engine.model.rule.function.namelist.CustomListValue;
import cn.tongdun.kunpeng.api.engine.model.rule.function.namelist.ICustomListValueRepository;
import cn.tongdun.kunpeng.api.infrastructure.persistence.mybatis.mappers.kunpeng.CustomListDAO;
import cn.tongdun.kunpeng.api.infrastructure.persistence.mybatis.mappers.kunpeng.CustomListValueDAO;
import cn.tongdun.kunpeng.share.dataobject.CustomListDO;
import cn.tongdun.kunpeng.share.dataobject.CustomListValueDO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: liang.chen
 * @Date: 2020/2/28 下午3:16
 */
@Repository
public class CustomListValueRepository implements ICustomListValueRepository {

    @Autowired
    private CustomListDAO customListDAO;

    @Autowired
    private CustomListValueDAO customListValueDAO;


    @Override
    public List<String> selectAllAvailable() {
        List<CustomListDO> customListDOS= customListDAO.selectAllAvailable();

        if (null != customListDOS) {
            List<String> uuidList = new ArrayList<String>();
            for (CustomListDO customListDO : customListDOS) {
                uuidList.add(customListDO.getUuid());
            }
            return uuidList;
        }
        return null;
    }

    @Override
    public int selectCountByListUuid(String customListUuid) {
        return customListValueDAO.selectCountByListUuid(customListUuid);
    }

    @Override
    public List<CustomListValue> selectByListUuid(String customListUuid, Integer offset, Integer size) {
        List<CustomListValueDO> valueDOList = customListValueDAO.selectByListUuid(customListUuid, offset, size);

        if (null != valueDOList) {
            List<CustomListValue> result = new ArrayList<>();
            for (CustomListValueDO listValueDO : valueDOList) {
                CustomListValue customListValue = convert(listValueDO);
                if(customListValue != null){
                    result.add(customListValue);
                }
            }
            return result;
        }

        return null;
    }

    /**
     * 根据customListValueUuid 查询列表数据
     * @param customListValueUuid
     * @return
     */
    @Override
    public CustomListValue selectByUuid(String customListValueUuid){
        CustomListValueDO customListValueDO = customListValueDAO.selectByUuid(customListValueUuid);
        if(customListValueDO == null) {
            return null;
        }
        return convert(customListValueDO);
    }

    /**
     * 根据customListValueUuid 查询列表数据
     * @param uuids
     * @return
     */
    @Override
    public List<CustomListValue> selectByUuids(List<String> uuids){
        List<CustomListValueDO> valueDOList = customListValueDAO.selectByUuids(uuids);

        if (null != valueDOList) {
            List<CustomListValue> result = new ArrayList<>();
            for (CustomListValueDO listValueDO : valueDOList) {
                CustomListValue customListValue = convert(listValueDO);
                if(customListValue != null){
                    result.add(customListValue);
                }
            }
            return result;
        }

        return null;
    }


    private CustomListValue convert(CustomListValueDO listValueDO){
        if(null==listValueDO){
            return null;
        }
        //列表类型，0为普通，1为复合，2 个人模糊匹配列表，3 企业模糊匹配列表
        int type = listValueDO.getColumnType();
        String value = null;
        if (type == 0) {
            value = listValueDO.getDataValue();
        } else if (type ==1) {
            value = listValueDO.getColumnValues();
        } else if (type ==2) {
            value = listValueDO.getColumnValues();
        } else if (type ==3) {
            value = listValueDO.getColumnValues();
        }
        long expireTime=0;
        if (listValueDO.getGmtExpire()==null){
            expireTime=Long.MAX_VALUE;
        }else {
            expireTime = listValueDO.getGmtExpire().getTime();
        }
        if (StringUtils.isBlank(value)) {
            return null;
        }
        CustomListValue customListValue = new CustomListValue(listValueDO.getCustomListUuid(),value, expireTime);
        customListValue.setUuid(listValueDO.getUuid());
        customListValue.setGmtModify(listValueDO.getGmtModify());
        customListValue.setDeleted(listValueDO.isDeleted());
        return customListValue;
    }


}
