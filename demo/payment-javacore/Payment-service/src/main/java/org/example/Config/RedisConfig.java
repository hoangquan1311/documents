package org.example.Config;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisConfig {

    private JedisPool jedisPool;

    public RedisConfig() {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        jedisPool = new JedisPool(poolConfig, "localhost", 6379);
    }

    public Jedis getJedis() {
        return jedisPool.getResource();
    }
}
