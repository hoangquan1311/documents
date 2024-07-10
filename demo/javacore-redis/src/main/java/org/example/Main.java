package org.example;

import redis.clients.jedis.Jedis;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        Jedis jedis = new Jedis("localhost", 6379);

        jedis.lpush("messages", "Hello");
        jedis.lpush("messages", "World");
        jedis.lpush("messages", "For");
        jedis.lpush("messages", "Java");

        List<String> messages = jedis.lrange("messages", 0, 2);
        messages.forEach(s -> System.out.println(s + ""));
        jedis.close();
    }
}