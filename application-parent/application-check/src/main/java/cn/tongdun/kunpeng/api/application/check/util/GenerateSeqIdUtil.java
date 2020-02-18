package cn.tongdun.kunpeng.api.application.check.util;

import cn.tongdun.kunpeng.common.util.NetWorkUtil;

import java.lang.management.ManagementFactory;

/**
 * @Author: liang.chen
 * @Date: 2020/2/17 下午11:25
 */
public class GenerateSeqIdUtil {

    /**
     * 生成事件的全局唯一序列号，本地IP+时间戳+6位随机数
     *
     * @return
     */
    private static String address;
    private static String pid;

    public static String generateSeqId() {
        StringBuilder sb = new StringBuilder();

        if (address == null) {
            String ip = NetWorkUtil.getLANIP("127.0.0.1");
            String[] split = ip.split("\\.");
            String part1 = String.format("%02x", Integer.valueOf(split[2]));
            String part2 = String.format("%02x", Integer.valueOf(split[3]));
            address = part1.toUpperCase() + part2.toUpperCase();
        }

        if (pid == null) {
            try {
                String processId = ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
                pid = String.format("%04x", Integer.valueOf(processId));
            } catch (Exception e) {
                pid = "0000";
            }
            pid = pid.toUpperCase();
        }

        String ri = (int) Math.floor(1000000 + Math.random() * 9000000) + "";

        sb.append(System.currentTimeMillis()).append("000X").append(address).append(pid).append(ri);

        return sb.toString();
    }
}
