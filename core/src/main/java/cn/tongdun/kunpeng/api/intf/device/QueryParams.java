package cn.tongdun.kunpeng.api.intf.device;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

/**
 * @Author: liang.chen
 * @Date: 2020/1/7 上午9:57
 */
@Data
public class QueryParams implements Serializable {
        private String platform;
        private String blackBox;
        private String tokenId;
        private String type;
        private String partnerCode;
        private String appName;
        private String sequenceId;
        private String responseType;

        public QueryParams() {
        }

        public QueryParams(String platform, String partnerCode, String appName, String sequenceId) {
            this.platform = platform;
            this.partnerCode = partnerCode;
            this.appName = appName;
            this.sequenceId = sequenceId;
        }

        /** @deprecated */
        @Deprecated
        public QueryParams(String platform, String partnerCode, String blackBoxOrTokenId, String appName, String sequenceId) {
            this.platform = platform;
            if (StringUtils.equalsIgnoreCase(platform, "web")) {
                this.type = "";
                this.tokenId = blackBoxOrTokenId;
            } else {
                this.blackBox = blackBoxOrTokenId;
            }

            this.partnerCode = partnerCode;
            this.appName = appName;
            this.sequenceId = sequenceId;
        }
}
