package cn.tongdun.kunpeng.api.common.data;

import com.alibaba.dubbo.common.utils.ConcurrentHashSet;

import java.io.Serializable;
import java.util.Set;

/**
 * Created by coco on 17/8/29.
 */
public class SubReasonCode implements Serializable {

    private static final long serialVersionUID = 136121107227026662L;

    private String sub_code; //子状态码
    private String sub_msg;  //子状态码对应描述信息
    private String ext_service;  //内部服务枚举值（服务应用名称）
    private Set<ExtCode> ext_code;


    public SubReasonCode() {
    }

    public SubReasonCode(String sub_code, String sub_msg, String ext_service) {
        this.sub_code = sub_code;
        this.ext_service = ext_service;
        this.sub_msg = sub_msg;
    }

    public SubReasonCode(ReasonCode reasonCode, String ext_service) {
        this.sub_code = reasonCode.getCode();
        this.sub_msg = reasonCode.getDescription();
        this.ext_service = ext_service;
    }

    public SubReasonCode.ExtCode extCodeConstructor(String ext_service_name, String ext_reasonCode, String ext_message) {
        return new ExtCode(ext_service_name, ext_reasonCode, ext_message);
    }

    public SubReasonCode addExtCode(ExtCode extCode) {

        if (this.ext_code == null) {
            this.ext_code = new ConcurrentHashSet<>();
        }
        this.ext_code.add(extCode);
        return this;
    }

    public String getSub_code() {
        return sub_code;
    }

    public void setSub_code(String sub_code) {
        this.sub_code = sub_code;
    }

    public String getExt_service() {
        return ext_service;
    }

    public void setExt_service(String ext_service) {
        this.ext_service = ext_service;
    }

    public String getSub_msg() {
        return sub_msg;
    }

    public void setSub_msg(String sub_msg) {
        this.sub_msg = sub_msg;
    }

    public Set<ExtCode> getExt_code() {
        return ext_code;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        SubReasonCode that = (SubReasonCode) o;

        if (sub_code == null || that.sub_code == null) {
            return false;
        }
        if (sub_msg == null || that.sub_msg == null) {
            return false;
        }
        if (ext_service == null || that.ext_service == null) {
            return false;
        }

        if (!sub_code.equals(that.sub_code)) {
            return false;
        }
        if (!sub_msg.equals(that.sub_msg)) {
            return false;
        }
        return ext_service.equals(that.ext_service);

    }

    @Override
    public int hashCode() {
        if (sub_code == null || sub_msg == null || ext_service == null) {
            return 100;
        }
        int result = sub_code.hashCode();
        result = 31 * result + sub_msg.hashCode();
        result = 31 * result + ext_service.hashCode();
        return result;
    }

    public class ExtCode implements Serializable {

        private static final long serialVersionUID = -5090240562187609130L;

        private String ext_service_name;
        private String ext_reasonCode;  //内部服务返回的错误码
        private String ext_message;  //内部服务返回的错误码对应描述

        public ExtCode() {
        }

        public ExtCode(String ext_service_name, String ext_reasonCode, String ext_message) {
            this.ext_service_name = ext_service_name;
            this.ext_reasonCode = ext_reasonCode;
            this.ext_message = ext_message;
        }

        public String getExt_service_name() {
            return ext_service_name;
        }

        public void setExt_service_name(String ext_service_name) {
            this.ext_service_name = ext_service_name;
        }

        public String getExt_reasonCode() {
            return ext_reasonCode;
        }

        public void setExt_reasonCode(String ext_reasonCode) {
            this.ext_reasonCode = ext_reasonCode;
        }

        public String getExt_message() {
            return ext_message;
        }

        public void setExt_message(String ext_message) {
            this.ext_message = ext_message;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            ExtCode extCode = (ExtCode) o;

            if (ext_service_name != null ? !ext_service_name.equals(extCode.ext_service_name) : extCode.ext_service_name != null) {
                return false;
            }
            if (ext_reasonCode != null ? !ext_reasonCode.equals(extCode.ext_reasonCode) : extCode.ext_reasonCode != null) {
                return false;
            }
            return ext_message != null ? ext_message.equals(extCode.ext_message) : extCode.ext_message == null;

        }

        @Override
        public int hashCode() {
            int result = ext_service_name != null ? ext_service_name.hashCode() : 0;
            result = 31 * result + (ext_reasonCode != null ? ext_reasonCode.hashCode() : 0);
            result = 31 * result + (ext_message != null ? ext_message.hashCode() : 0);
            return result;
        }
    }
}
