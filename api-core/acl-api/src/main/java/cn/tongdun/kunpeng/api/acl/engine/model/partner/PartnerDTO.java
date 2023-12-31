package cn.tongdun.kunpeng.api.acl.engine.model.partner;

import cn.tongdun.kunpeng.client.dto.CommonDTO;
import lombok.Data;

/**
 * @Author: liang.chen
 * @Date: 2019/12/16 下午3:21
 */
@Data
public class PartnerDTO extends CommonDTO {

    private String            partnerCode;                             // 合作标识
    private String            partnerKey;                              // 合作方密钥
    private String            displayName;                             // 合作方公司名称
    private Boolean           testAccount;                             // 是否是测试账号
    private String            industryType;                            // 行业类型
    private String            partnerType;                             // 合作类型
    private String            secondIndustryType;                      // 二级行业类型

}
