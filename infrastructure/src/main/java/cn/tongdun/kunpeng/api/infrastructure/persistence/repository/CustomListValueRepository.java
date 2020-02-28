package cn.tongdun.kunpeng.api.infrastructure.persistence.repository;

import cn.tongdun.kunpeng.api.engine.model.rule.function.namelist.CustomListValueManager;
import cn.tongdun.kunpeng.api.engine.model.rule.function.namelist.ICustomListValueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * @Author: liang.chen
 * @Date: 2020/2/28 下午3:16
 */
@Repository
public class CustomListValueRepository implements ICustomListValueRepository {

    @Autowired
    CustomListManager customListManager;

    @Autowired
    CustomListValueManager customListValueManager;
}
