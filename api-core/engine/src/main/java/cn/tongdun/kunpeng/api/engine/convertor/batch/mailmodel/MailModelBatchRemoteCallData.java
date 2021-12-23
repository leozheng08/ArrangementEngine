package cn.tongdun.kunpeng.api.engine.convertor.batch.mailmodel;

import cn.tongdun.kunpeng.api.engine.convertor.batch.AbstractBatchRemoteCallData;
import com.google.common.collect.Sets;
import lombok.Data;

import java.util.List;
import java.util.Set;

/**
 * @author: mengtao
 * @create: 2021-12-21 19:49
 */

@Data
public class MailModelBatchRemoteCallData extends AbstractBatchRemoteCallData{

    private String typeStr;

    private Set<String> keys = Sets.newHashSet();

    private String operate;

    private Integer threshold;

    private List<String> mailTypes;

    private String timeInterval = "2";

    private String simResult = "10";
}
