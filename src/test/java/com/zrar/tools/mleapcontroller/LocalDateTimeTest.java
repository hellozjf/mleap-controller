package com.zrar.tools.mleapcontroller;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;

/**
 * @author Jingfeng Zhou
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class LocalDateTimeTest {

    @Test
    public void test() {
        LocalDateTime localDateTime = LocalDateTime.now();
        log.debug("localDateTime = {}", localDateTime);
    }
}
