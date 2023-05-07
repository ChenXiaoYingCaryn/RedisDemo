package com.example.redisdemo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.concurrent.*;

/**
 * @author xiaoying
 * @create 2023/4/30 10:16
 */
@RestController
@RequiredArgsConstructor
public class LettuceTest {
    @Resource
    private RedisTemplate redisTemplate;
    private static final Executor executor = new ThreadPoolExecutor(3000, 3000, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingDeque<>());

    @PostMapping("/redisTest")
    public void redisTest(@RequestParam int count, @RequestParam String key) throws InterruptedException {
        long startTime = System.currentTimeMillis();
        CountDownLatch latch = new CountDownLatch(count);
        for (int i = 0; i < count; i++) {
            String k = "key".concat(String.valueOf(i));
            String v = "test".concat(String.valueOf(i));
            executor.execute(() -> {
                try {
                    redisTemplate.opsForValue().set(k, v);
                    Object o = redisTemplate.opsForValue().get(key);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();
        System.out.println("系统耗费时间：" + (System.currentTimeMillis() - startTime));
    }
}
