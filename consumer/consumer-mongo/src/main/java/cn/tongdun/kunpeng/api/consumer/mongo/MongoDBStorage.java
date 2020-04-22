package cn.tongdun.kunpeng.api.consumer.mongo;

import cn.tongdun.kunpeng.api.consumer.util.TimeUtils;
import cn.tongdun.kunpeng.share.json.JSON;
import com.mongodb.*;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.bson.types.Binary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class MongoDBStorage implements IMongoDBStorage {

    private Logger logger = LoggerFactory.getLogger(MongoDBStorage.class);

    @Value("${mongo.conf}")
    private String mongoConf;

    @Value("${mongo.second.ttl}")
    private int secondTTL;

    public MongoDBStorage(){
        super();
    }

    public MongoDBStorage(String mongoConf) {
        this.mongoConf = mongoConf;
    }

    private MongoCollection<Document> collection;

    @PostConstruct
    public void init() {
        if (StringUtils.isEmpty(mongoConf)) {
            throw new RuntimeException("please check your config, could not be empty.");
        }
        try {
            MongoConfBO mongoConfBO = JSON.parseObject(mongoConf, MongoConfBO.class);
            //连接mongoDB的服务
            List<ServerAddress> listAdd = new ArrayList<>();
            String[] split = mongoConfBO.getMongoHost().split(",");
            for (int i = 0; i < split.length; i++) {
                String[] ip = split[i].split(":");
                ServerAddress serverAdderss = new ServerAddress(ip[0], Integer.parseInt(ip[1]));
                listAdd.add(serverAdderss);
            }

            MongoCredential credential = MongoCredential.createScramSha1Credential(
                    mongoConfBO.getMongoUserName(), mongoConfBO.getMongoDBName(), mongoConfBO.getMongoPassword().toCharArray());

            MongoClientOptions.Builder builder = new MongoClientOptions.Builder();
            //设置每个主机的最大连接数
            builder.connectionsPerHost(mongoConfBO.getConnectionsPerHost());
            builder.threadsAllowedToBlockForConnectionMultiplier(mongoConfBO.getThreadsAllowedToBlockForConnectionMultiplier());
            //设置最小的连接数
            builder.minConnectionsPerHost(mongoConfBO.getMinConnectionsPerHost());
            //设置超时时间 2秒
            builder.connectTimeout(mongoConfBO.getConnectTimeout());
            //设置线程等待的最长时间
            builder.maxWaitTime(mongoConfBO.getMaxWaitTime());
            builder.readPreference(ReadPreference.secondaryPreferred());
            MongoClientOptions myOptions = builder.build();
            MongoClient mongoClient = new MongoClient(listAdd, credential, myOptions);
            MongoDatabase database = mongoClient.getDatabase(mongoConfBO.getMongoDBName());
            collection = database.getCollection(mongoConfBO.getMongoCollectionName());
        }catch (Exception e){
            logger.error("init mongodb failed: ", e);
            throw new RuntimeException(e);
        }
        logger.debug("------ Service ready: [{}] ------", this.getClass().getName());
    }


    /**
     * 查询
     *
     * @param encodedKey
     * @return
     * @throws Exception
     */
    @Override
    public byte[] get(String encodedKey) throws Exception {
        try {
            FindIterable<Document> documents = collection.find(Filters.eq("key", encodedKey));
            MongoCursor<Document> iterator = documents.iterator();
            List<Binary> list = new ArrayList<>();
            while (iterator.hasNext()) {
                Document document = iterator.next();
                Binary binary = document.get("events", Binary.class);
                list.add(binary);
            }
            if (CollectionUtils.isNotEmpty(list)) {
                return list.get(0).getData();
            }
        } catch (Exception e) {
            logger.error("get from mongo error", e);
            throw new Exception(e);
        }
        return null;
    }

    /**
     * 存储
     *
     * @param encodedKey
     * @param data
     */
    @Override
    public void set(String encodedKey, byte[] data) throws Exception {
        Document document = new Document("key", encodedKey)
                .append("expireAt", TimeUtils.addTime(new Date(), secondTTL, Calendar.SECOND))
                .append("events", data);
        byte[] bytes = get(encodedKey);
        if (bytes == null) {
            collection.insertOne(document);
        } else {
            collection.findOneAndReplace(Filters.eq("key", encodedKey), document);
        }
    }

    /**
     * 删除
     *
     * @param encodeKey
     * @throws Exception
     */
    @Override
    public void delete(String encodeKey) throws Exception {
        try {
            collection.deleteOne(Filters.eq("key", encodeKey));
        } catch (Exception e) {
            logger.error("delete from mongo error", e);
            throw new Exception(e);
        }
    }
}
