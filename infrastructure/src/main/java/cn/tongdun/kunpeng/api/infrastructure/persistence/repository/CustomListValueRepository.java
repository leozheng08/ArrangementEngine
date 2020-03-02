package cn.tongdun.kunpeng.api.infrastructure.persistence.repository;

import cn.tongdun.kunpeng.api.engine.model.rule.function.namelist.CustomListValue;
import cn.tongdun.kunpeng.api.engine.model.rule.function.namelist.ICustomListValueRepository;
import cn.tongdun.kunpeng.api.infrastructure.persistence.mybatis.mappers.kunpeng.CustomListDOMapper;
import cn.tongdun.kunpeng.api.infrastructure.persistence.mybatis.mappers.kunpeng.CustomListValueDOMapper;
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
    CustomListDOMapper customListDOMapper;

    @Autowired
    CustomListValueDOMapper customListValueDOMapper;


    @Override
    public List<String> selectAllAvailable() {
        List<CustomListDO> customListDOS= customListDOMapper.selectAllAvailable();

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
        return customListValueDOMapper.selectCountByListUuid(customListUuid);
    }

    @Override
    public List<CustomListValue> selectByListUuid(String customListUuid, Integer offset, Integer size) {
        List<CustomListValueDO> valueDOList = customListValueDOMapper.selectByListUuid(customListUuid, offset, size);

        if (null != valueDOList) {
            List<CustomListValue> result = new ArrayList<>();
            for (CustomListValueDO listValueDO : valueDOList) {
                //列表类型，0为普通，1为复合
                int type = listValueDO.getColumnType();
                String value = null;
                if (type == 0) {
                    value = listValueDO.getDataValue();
                } else if (type ==1) {
                    value = listValueDO.getColumnValues();
                }
                long expireTime = listValueDO.getGmtExpire().getTime();
                if (StringUtils.isNoneBlank(value)) {
                    CustomListValue customListValue = new CustomListValue(value, expireTime);
                    result.add(customListValue);
                }
            }
            return result;
        }

        return null;
    }

}
