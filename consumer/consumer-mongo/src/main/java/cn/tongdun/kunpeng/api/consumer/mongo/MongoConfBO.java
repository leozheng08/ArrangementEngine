package cn.tongdun.kunpeng.api.consumer.mongo;

import lombok.Data;

@Data
public class MongoConfBO {

    private String mongoHost;

    private String mongoUserName;

    private String mongoPassword;

    private String mongoDBName;

    private String mongoCollectionName;

    private Integer connectionsPerHost;

    private Integer threadsAllowedToBlockForConnectionMultiplier;

    private Integer minConnectionsPerHost;

    private Integer connectTimeout;

    private Integer maxWaitTime;
}
