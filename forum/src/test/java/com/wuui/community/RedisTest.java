package com.wuui.community;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.*;

import java.io.FileOutputStream;
import java.util.Set;

/**
 * @author Dee
 * @create 2022-05-21-17:23
 * @describe
 */
@SpringBootTest
public class RedisTest {

    @Autowired
    RedisTemplate redisTemplate;

    @Test
    public void boundStringTest(){
        String key = "test:count";
        BoundValueOperations operations = redisTemplate.boundValueOps(key);
        operations.increment();
        operations.increment();
        operations.increment();
        System.out.println(operations.get());
    }

    @Test
    public void redisHashTest(){
        String key = "test:user";
        BoundHashOperations operations = redisTemplate.boundHashOps(key);
        operations.put("name","liming");
        operations.put("age",20);
        operations.put("gender","男");
        System.out.println(operations.get("name"));
    }

    //编程式事务
    @Test
    public void RedisTransactionTest(){
        Object obj = redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                //启用事务
                operations.multi();

                String key = "test:person";
                operations.opsForZSet().add(key, "刘备", 180);
                operations.opsForZSet().add(key, "诸葛亮", 200);
                operations.opsForZSet().add(key, "曹操", 195);

                Set set = operations.opsForZSet().reverseRange(key, 0, 5);
                for (Object obj : set) {
                    //在事务过程中是不执行的
                    System.out.println(obj);
                }

                //提交事务
                return operations.exec();
            }
        });

        System.out.println(obj);

    }

    //HyperLogLog主要获取重复数据中的独立总数
    //将3组数局合并，再求数据中的独立数据总数
    @Test
    public void testHyperLogLog(){
        String redisKey1 = "test:hll:01";
        for (int i = 1; i <= 10000; i++) {
            redisTemplate.opsForHyperLogLog().add(redisKey1, i);
        }

        String redisKey2 = "test:hll:02";
        for (int i = 5000; i <= 10000; i++) {
            redisTemplate.opsForHyperLogLog().add(redisKey2, i);
        }

        String redisKey3 = "test:hll:03";
        for (int i = 10001; i <= 20000; i++) {
            redisTemplate.opsForHyperLogLog().add(redisKey3, i);
        }

        String redisKey4 = "test:hll:04";
        redisTemplate.opsForHyperLogLog().union(redisKey4, redisKey1, redisKey2, redisKey3);

        System.out.println(redisTemplate.opsForHyperLogLog().size(redisKey4)); //19833
    }

    @Test
    public void testBitMap(){
        ValueOperations operations = redisTemplate.opsForValue();

        String redisKey1 = "test:bm:01";
        operations.setBit(redisKey1, 0, true);
        operations.setBit(redisKey1, 1, true);
        operations.setBit(redisKey1, 2, true);

        String redisKey2 = "test:bm:02";
        operations.setBit(redisKey2, 2, true);
        operations.setBit(redisKey2, 3, true);
        operations.setBit(redisKey2, 4, true);

        String redisKey3 = "test:bm:03";
        operations.setBit(redisKey3, 4, true);
        operations.setBit(redisKey3, 5, true);
        operations.setBit(redisKey3, 6, true);

        String redisKey = "test:bm:union";
        Object obj = redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                //对3组数据进行OR运算
                connection.bitOp(RedisStringCommands.BitOperation.OR,
                        redisKey.getBytes(), redisKey1.getBytes(), redisKey2.getBytes(), redisKey3.getBytes());
                return null;
            }
        });
//        System.out.println(operations.getBit(redisKey, 6));

        //统计数组中为true的个数
        Object size = redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                return connection.bitCount(redisKey.getBytes());
            }
        });
        System.out.println(size);
    }

}
