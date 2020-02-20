package cn.tongdun.kunpeng.api.infrastructure.redis.impl;

import cn.tongdun.kunpeng.share.kv.IKVResponse;

/**
 * Created by lvyadong on 2020/02/20.
 */
public class RedisKVResponse implements IKVResponse {

    private boolean success;
    private String message;

    public RedisKVResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    @Override
    public boolean isSuccess() {
        return this.success;
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}

