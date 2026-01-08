package com.tagit;

import java.util.logging.Logger;

import org.slf4j.LoggerFactory;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class TagItAppTests {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(TagItAppTests.class);
    @Test
    void contextLoads() {
        
        logger.info("Context loads successfully for tests.");
    }

}