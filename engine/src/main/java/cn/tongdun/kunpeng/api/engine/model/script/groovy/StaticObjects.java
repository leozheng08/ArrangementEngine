package cn.tongdun.kunpeng.api.engine.model.script.groovy;

import cn.tongdun.kunpeng.api.common.data.AbstractFraudContext;
import com.alibaba.dubbo.common.utils.ConcurrentHashSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.concurrent.ConcurrentHashMap;

public class StaticObjects {

    private static ConcurrentHashMap<String, Object> objects            = new ConcurrentHashMap<>();
    private static final String                      ROOT               = "groovy/";
    private static Logger logger             = LoggerFactory.getLogger(StaticObjects.class);
    private static Logger emailFeatureLogger = LoggerFactory.getLogger("emailFeature");
    private static ConcurrentHashSet<String>         englishList;
    private static ConcurrentHashSet<String>         nameList;
    private static ConcurrentHashSet<String>         pinyinList;

    public static void load() {
        englishList = loadSet("english.txt");
        objects.put("english", englishList);

        nameList = loadSet("name.txt");
        objects.put("name", nameList);

        pinyinList = loadSet("pinyin.txt");
        objects.put("pinyin", pinyinList);
    }

    public static void saveFeature(AbstractFraudContext context, String feature) {
        String id = context.getSeqId();
        emailFeatureLogger.info(id + ": " + feature);
    }

    private static ConcurrentHashSet<String> loadSet(String file) {
        file = ROOT + file;
        try (InputStream in = StaticObjects.class.getClassLoader().getResourceAsStream(file)) {
            ConcurrentHashSet<String> result = new ConcurrentHashSet<>();
            String content = StreamUtils.copyToString(in, Charset.forName("utf8"));
            String[] lines = content.split("\\n");
            for (String line : lines) {
                if (!line.isEmpty()) {
                    result.add(line);
                }
            }
            return result;
        } catch (IOException e) {
            logger.error("动态脚本加载静态文件", file, e.getMessage());
            return new ConcurrentHashSet<>();
        }
    }

    public static void unload() {
        objects = null;
    }

    public static Object getObject(String name) {
        return objects.get(name);
    }
}
