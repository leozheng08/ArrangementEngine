package cn.tongdun.kunpeng.api.engine.model.policy.challenger;

import cn.tongdun.kunpeng.api.common.util.JsonUtil;
import cn.tongdun.kunpeng.api.engine.model.StatusEntity;
import cn.tongdun.kunpeng.share.json.JSON;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * @Author: liang.chen
 * @Date: 2020/4/24 下午1:59
 */
@Data
public class PolicyChallenger extends StatusEntity {

    /**
     * 策略uuid policy_uuid
     */
    private String policyDefinitionUuid;

    /**
     * 开始 start_time
     */
    private Date startTime;

    /**
     * 结束 end_time
     */
    private Date endTime;


    /**
     * 流量配置 [{"ratio":40.0,"versionUuid":"b225f25f34044a9a9a6929ce12703068"},{"ratio":60.0,"versionUuid":"efa8c9ea504f413caf0634dbab760583"}]
     * 流量配置 [{"challengerTag":"CHAMP","ratio":40.0,"versionUuid":"b225f25f34044a9a9a6929ce12703068"},{"challengerTag":"CLG","ratio":60.0,"versionUuid":"efa8c9ea504f413caf0634dbab760583"}]
     */
    private String config;

    /**
     * config 的内容解析后的Config对象
     */
    private List<Config> challengerConfig;

    public List<Config> getChallengerConfig() {
        if(challengerConfig != null){
            return challengerConfig;
        }

        if (StringUtils.isBlank(config)) {
            return null;
        }

        List<HashMap> jsonArray = JSON.parseArray(config, HashMap.class);
        int size = jsonArray.size();
        List<Config> configs = new ArrayList<>(size);
        for (HashMap map :jsonArray) {
            Config temp = new Config();
            temp.setChallengerTag(JsonUtil.getString(map,"challengerTag"));
            temp.setVersionUuid(JsonUtil.getString(map,"versionUuid"));
            temp.setRatio(JsonUtil.getInteger(map,"ratio"));
            configs.add(temp);
        }
        challengerConfig = configs;
        return challengerConfig;
    }

    @Data
    public class Config {
        String challengerTag;
        String versionUuid;
        Integer ratio;
    }
}
