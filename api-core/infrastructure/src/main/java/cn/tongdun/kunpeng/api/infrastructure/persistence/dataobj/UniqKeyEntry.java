package cn.tongdun.kunpeng.api.infrastructure.persistence.dataobj;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

/**
 * @author: yuanhang
 * @date: 2020-08-21 10:35
 **/
@Data
@AllArgsConstructor
public class UniqKeyEntry {

    private String policyName;

    private Date eventOccurTime;

}