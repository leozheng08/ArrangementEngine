package cn.tongdun.kunpeng.api.common.data;

import lombok.Data;

/**
 * @Author: liang.chen
 * @Date: 2019/12/13 下午4:23
 */
@Data
public class Response {
    private boolean success;
    private Integer score = 0;
    //决策结果,如
    private String decision;

    private long costTime;
}
