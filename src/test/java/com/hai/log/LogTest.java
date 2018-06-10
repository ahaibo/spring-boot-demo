package com.hai.log;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Administrator on 2017/11/14.
 */
public class LogTest {
    private static Logger logger = LoggerFactory.getLogger(LogTest.class);

    @Test
    public void test() {
        logger.trace("-------------trace");
        logger.debug("-------------trace");
        logger.info("-------------trace");
        logger.warn("-------------trace");
        logger.error("-------------trace");
    }
}
