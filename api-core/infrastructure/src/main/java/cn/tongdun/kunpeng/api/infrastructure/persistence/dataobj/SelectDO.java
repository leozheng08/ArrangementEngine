/*
 * Copyright 2014 FraudMetrix.cn All right reserved. This software is the
 * confidential and proprietary information of FraudMetrix.cn ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with FraudMetrix.cn.
 */
package cn.tongdun.kunpeng.api.infrastructure.persistence.dataobj;

import lombok.Data;

import java.io.Serializable;

@Data
public class SelectDO implements Serializable {

    private static final long serialVersionUID = 8292030413369004844L;
    private String            name;                                   // 名称
    private String            dName;                                  // 展示名称
    private Object            type;                                   // 类型
    private boolean           necessary        = false;               // 是否必需

    public SelectDO(){
        super();
    }

    public SelectDO(String name, String dName){
        this.name = name;
        this.dName = dName;
    }


    @Override
    public String toString() {
        return "SelectDO [name=" + name + ", dName=" + dName + ", type=" + type + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        SelectDO other = (SelectDO) obj;
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        return true;
    }



}
