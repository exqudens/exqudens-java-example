package exqudens.example;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exqudens.example.util.StringUtils;

class StringUtilsUnitTests {

    private static final Logger logger = LoggerFactory.getLogger(StringUtilsUnitTests.class);

    @Test
    public void test_tokenize_1() {
        List<String> expected = List.of("aaa", "bbb", "ccc");
        logger.info("expected: {}", expected);

        List<String> actual = StringUtils.tokenize("aaa bbb ccc", " ");
        logger.info("actual: {}", actual);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void test_tokenize_2() {
        List<String> expected = List.of("1", "2", "3");
        logger.info("expected: {}", expected);

        List<String> actual = StringUtils.tokenize("1 2 3", " ");
        logger.info("actual: {}", actual);

        Assertions.assertEquals(expected, actual);
    }

}
