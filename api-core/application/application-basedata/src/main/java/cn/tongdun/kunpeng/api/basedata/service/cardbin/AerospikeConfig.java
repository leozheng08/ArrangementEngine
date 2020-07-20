package cn.tongdun.kunpeng.api.basedata.service.cardbin;

import com.aerospike.client.AerospikeClient;
import com.aerospike.client.Host;
import com.aerospike.client.policy.ClientPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class AerospikeConfig {
    private static final Logger logger = LoggerFactory.getLogger(AerospikeConfig.class);

    @Value("${cardbin.aerospike.host:192.168.100.100}")
    private String aerospikeHost;

    @Bean
    public AerospikeClient aerospikeClient() {
        ClientPolicy policy = new ClientPolicy();
        policy.maxConnsPerNode = 300;
        policy.connPoolsPerNode = 1;

        // 创建AerospikeClient
        logger.info("as配置：{}", aerospikeHost);
        List<Host> listHost = new ArrayList<>();
        String[] strHosts = aerospikeHost.split(",");
        for (String strHost : strHosts) {
            // 如果没有配置端口，则使用默认端口为3000
            Integer port = 3000;
            String[] hostPorts = strHost.split(":");

            if (hostPorts.length > 1) {
                port = Integer.parseInt(hostPorts[1]);
            }
            Host host = new Host(hostPorts[0], port);
            listHost.add(host);
        }

        return new AerospikeClient(policy, listHost.toArray(new Host[0]));
    }
}
