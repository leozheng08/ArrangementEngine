package cn.tongdun.kunpeng.api.application.keyword.function;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by wangrenjie on 16/10/27.
 * 关键词属性表达式
 */
@Data
public class AttrExp implements Serializable {

    private String left;

    private String operate;

    private String right;

    private String rightType;

    private String andOr;

    public AttrExp() {
    }

    public AttrExp(String left, String operate, String right, String rightType, String andOr) {
        this.left = left;
        this.operate = operate;
        this.right = right;
        this.rightType = rightType;
        this.andOr = andOr;
    }

    @Override
    public String toString() {
        return "AttrExp{" +
                "left='" + left + '\'' +
                ", operate='" + operate + '\'' +
                ", right='" + right + '\'' +
                ", rightType='" + rightType + '\'' +
                ", andOr='" + andOr + '\'' +
                '}';
    }

    public enum OperateEnum {
        /**
         * 大于
         */
        dayu(">"),
        /**
         * 大于等于
         */
        dayudengyu(">="),
        /**
         * 等于
         */
        dengyu("=="),
        /**
         * 不等于
         */
        budengyu("!="),
        /**
         * 小于等于
         */
        xiaoyudengyu("<="),
        /**
         * 小于
         */
        xiaoyu("<"),
        /**
         * 为空
         */
        isNull("isNull"),

        /**
         * 不为空
         */
        isNotNull("isNotNull");

        String value;

        OperateEnum(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    public enum RightTypeEnum {
        /**
         * 输入值
         */
        input,
        /**
         * 用户传递值
         */
        context
    }

    public enum AndOrEnum {
        /**
         * 且
         */
        and,
        /**
         * 或
         */
        or
    }
}
