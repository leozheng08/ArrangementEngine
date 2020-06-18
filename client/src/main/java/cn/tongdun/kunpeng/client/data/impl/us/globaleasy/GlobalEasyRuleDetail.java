package cn.tongdun.kunpeng.client.data.impl.us.globaleasy;

import cn.tongdun.kunpeng.client.data.IRuleDetailList;

/**
 * @author: yuanhang
 * @date: 2020-06-18 17:51
 **/
public class GlobalEasyRuleDetail implements IRuleDetailList {

    private String description;

    private String data;

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String getData() {
        return data;
    }

    @Override
    public void setData(String data) {
        this.data = data;
    }
}
