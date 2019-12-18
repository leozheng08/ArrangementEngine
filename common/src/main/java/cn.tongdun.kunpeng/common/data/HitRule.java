package cn.tongdun.kunpeng.common.data;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: liang.chen
 * @Date: 2019/12/13 下午4:44
 */
@Data
public class HitRule implements Serializable {

    private static final long serialVersionUID = 6297666052880082771L;
    private String               id;                                     // 规则编号
    private String               uuid;
    private String               name;                                   // 规则名称
    private String               decision;                               // 该条规则决策结果
    private Integer              score= 0;                               // 规则分数
    private String               parentUuid;

}
