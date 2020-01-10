/*
 * Copyright 2017-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.tongdun.kunpeng.api.redis;

import cn.fraudmetrix.common.client.redis.BinaryRedisClient;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * {@link RedisCacheWriter} implementation capable of reading/writing binary data from/to Redis in {@literal standalone}
 * and {@literal cluster} environments. Works upon a given {@link RedisConnectionFactory} to obtain the actual
 * {@link RedisConnection}. <br />
 * {@link TdRedisCacheWriter} can be used in
 * {@link RedisCacheWriter#lockingRedisCacheWriter(RedisConnectionFactory) locking} or
 * {@link RedisCacheWriter#nonLockingRedisCacheWriter(RedisConnectionFactory) non-locking} mode. While
 * {@literal non-locking} aims for maximum performance it may result in overlapping, non atomic, command execution for
 * operations spanning multiple Redis interactions like {@code putIfAbsent}. The {@literal locking} counterpart prevents
 * command overlap by setting an explicit lock key and checking against presence of this key which leads to additional
 * requests and potential command wait times.
 *
 * @author Christoph Strobl
 * @author Mark Paluch
 * @since 2.0
 */
public class TdRedisCacheWriter implements RedisCacheWriter {

	private final BinaryRedisClient redisClient;
	private final Duration sleepTime;

	/**
	 * @param redisClient must not be {@literal null}.
	 */
	TdRedisCacheWriter(BinaryRedisClient redisClient) {
		this(redisClient, Duration.ZERO);
	}

	/**
	 * @param redisClient must not be {@literal null}.
	 * @param sleepTime sleep time between lock request attempts. Must not be {@literal null}. Use {@link Duration#ZERO}
	 *          to disable locking.
	 */
	TdRedisCacheWriter(BinaryRedisClient redisClient, Duration sleepTime) {

		Assert.notNull(redisClient, "ConnectionFactory must not be null!");
		Assert.notNull(sleepTime, "SleepTime must not be null!");
		this.redisClient = redisClient;
		this.sleepTime = sleepTime;
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.redis.cache.RedisCacheWriter#put(java.lang.String, byte[], byte[], java.time.Duration)
	 */
	@Override
	public void put(String name, byte[] key, byte[] value, @Nullable Duration ttl) {

		Assert.notNull(name, "Name must not be null!");
		Assert.notNull(key, "Key must not be null!");
		Assert.notNull(value, "Value must not be null!");


		execute(name, connection -> {

			if (shouldExpireWithin(ttl)) {
				redisClient.set(key,value);
				redisClient.pexpire(key, ttl.toMillis());
			} else {
				redisClient.set(key, value);
			}

			return "OK";
		});
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.redis.cache.RedisCacheWriter#get(java.lang.String, byte[])
	 */
	@Override
	public byte[] get(String name, byte[] key) {

		Assert.notNull(name, "Name must not be null!");
		Assert.notNull(key, "Key must not be null!");

		return execute(name, connection -> connection.get(key));
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.redis.cache.RedisCacheWriter#putIfAbsent(java.lang.String, byte[], byte[], java.time.Duration)
	 */
	@Override
	public byte[] putIfAbsent(String name, byte[] key, byte[] value, @Nullable Duration ttl) {

		Assert.notNull(name, "Name must not be null!");
		Assert.notNull(key, "Key must not be null!");
		Assert.notNull(value, "Value must not be null!");

		return execute(name, redisClient -> {

			if (isLockingCacheWriter()) {
				doLock(name, redisClient);
			}

			try {
				Long result = redisClient.setnx(key, value);
				if (result != null && result == 1L) {

					if (shouldExpireWithin(ttl)) {
						redisClient.pexpire(key, ttl.toMillis());
					}
					return null;
				}

				return redisClient.get(key);
			} finally {

				if (isLockingCacheWriter()) {
					doUnlock(name, redisClient);
				}
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.redis.cache.RedisCacheWriter#remove(java.lang.String, byte[])
	 */
	@Override
	public void remove(String name, byte[] key) {

		Assert.notNull(name, "Name must not be null!");
		Assert.notNull(key, "Key must not be null!");

		execute(name, connection -> connection.del(key));
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.redis.cache.RedisCacheWriter#clean(java.lang.String, byte[])
	 */
	@Override
	public void clean(String name, byte[] pattern) {

//		Assert.notNull(name, "Name must not be null!");
//		Assert.notNull(pattern, "Pattern must not be null!");
//
//		execute(name, redisClient -> {
//
//			boolean wasLocked = false;
//
//			try {
//
//				if (isLockingCacheWriter()) {
//					doLock(name, redisClient);
//					wasLocked = true;
//				}
//
//				//redisClient  未提供scan 迭代当前数据库中的数据库键
//				byte[][] keys = Optional.ofNullable(redisClient.keys(pattern)).orElse(Collections.emptySet())
//						.toArray(new byte[0][]);
//
//				if (keys.length > 0) {
//					redisClient.del(keys);
//				}
//			} finally {
//
//				if (wasLocked && isLockingCacheWriter()) {
//					doUnlock(name, redisClient);
//				}
//			}
//
//			return "OK";
//		});
	}

	/**
	 * Explicitly set a write lock on a cache.
	 *
	 * @param name the name of the cache to lock.
	 */
	void lock(String name) {
		execute(name, connection -> doLock(name, connection));
	}

	/**
	 * Explicitly remove a write lock from a cache.
	 *
	 * @param name the name of the cache to unlock.
	 */
	void unlock(String name) {
		executeLockFree(redisClient -> doUnlock(name, redisClient));
	}

	private Boolean doLock(String name, BinaryRedisClient redisClient) {
		Long result = redisClient.setnx(createCacheLockKey(name), new byte[0]);
		if(result != null && result == 1L ){
			return true;
		} else {
			return false;
		}
	}

	private Long doUnlock(String name, BinaryRedisClient redisClient) {
		return redisClient.del(createCacheLockKey(name));
	}

	boolean doCheckLock(String name, BinaryRedisClient redisClient) {
		return redisClient.exists(createCacheLockKey(name));
	}

	/**
	 * @return {@literal true} if {@link RedisCacheWriter} uses locks.
	 */
	private boolean isLockingCacheWriter() {
		return !sleepTime.isZero() && !sleepTime.isNegative();
	}

	private <T> T execute(String name, Function<BinaryRedisClient, T> callback) {
		checkAndPotentiallyWaitUntilUnlocked(name, redisClient);
		return callback.apply(redisClient);
	}

	private void executeLockFree(Consumer<BinaryRedisClient> callback) {
			callback.accept(redisClient);
	}

	private void checkAndPotentiallyWaitUntilUnlocked(String name, BinaryRedisClient redisClient) {

		if (!isLockingCacheWriter()) {
			return;
		}

		try {

			while (doCheckLock(name, redisClient)) {
				Thread.sleep(sleepTime.toMillis());
			}
		} catch (InterruptedException ex) {

			// Re-interrupt current thread, to allow other participants to react.
			Thread.currentThread().interrupt();

			throw new PessimisticLockingFailureException(String.format("Interrupted while waiting to unlock cache %s", name),
					ex);
		}
	}

	private static boolean shouldExpireWithin(@Nullable Duration ttl) {
		return ttl != null && !ttl.isZero() && !ttl.isNegative();
	}

	private static byte[] createCacheLockKey(String name) {
		return (name + "~lock").getBytes(StandardCharsets.UTF_8);
	}
}
