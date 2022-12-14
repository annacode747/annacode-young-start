package cn.js.fan.cache.redis;

import cn.js.fan.web.Global;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @Author: qcg
 * @Description:
 * @Date: 2019/1/9 14:21
 */
public class RedisUtil {
    private static JedisPool jedisPool;
    private static RedisUtil redisUtil;

    public RedisUtil() {
    }

    public RedisUtil(JedisPool jedisPool1) {
        jedisPool = jedisPool1;
    }

    public Jedis getJedis() {
        Jedis jedis = jedisPool.getResource();
        jedis.select(Global.getInstance().getRedisDb());
        return jedis;
    }

    public static RedisUtil getInstance() {
        if (jedisPool == null) {
            String redisHost = Global.getInstance().getRedisHost();
            int redisPort = Global.getInstance().getRedisPort();
            String redisPassword = Global.getInstance().getRedisPassword();

            JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
            jedisPoolConfig.setMaxTotal(Global.getInstance().getRedisMaxTotal()); // 默认为8
            jedisPoolConfig.setMaxIdle(Global.getInstance().getRedisMinIdle()); // 默认为8
            jedisPoolConfig.setMinIdle(Global.getInstance().getRedisMinIdle());
            jedisPoolConfig.setMaxWaitMillis(Global.getInstance().getRedisMaxWaitMillis()); // 当资源池连接用尽后，调用者的最大等待时间，单位为毫秒
            // 多长空闲时间之后回收空闲连接
            jedisPoolConfig.setMinEvictableIdleTimeMillis(60000);
            // 跟验证有关
            jedisPoolConfig.setTestOnBorrow(true);
            // 跟验证有关
            jedisPoolConfig.setTestOnReturn(false);
            // 启动空闲连接的测试
            jedisPoolConfig.setTestWhileIdle(false);

            if (!"".equals(redisPassword)) {
                jedisPool = new JedisPool(jedisPoolConfig, redisHost, redisPort, 10000, redisPassword);
            } else {
                jedisPool = new JedisPool(redisHost, redisPort);
            }
            redisUtil = new RedisUtil(jedisPool);
        }
        return redisUtil;
    }

    public JedisPool getJedisPool() {
        return jedisPool;
    }

    public static void close(Jedis jedis) {
        jedis.close();
    }
}
