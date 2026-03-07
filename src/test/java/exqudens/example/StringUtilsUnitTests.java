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
    public void test_split_1() {
        List<String> expected = List.of("aaa");
        logger.info("expected: {}", expected);

        List<String> actual = StringUtils.split("aaa", " ");
        logger.info("actual: {}", actual);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void test_split_2() {
        List<String> expected = List.of("aaa", "");
        logger.info("expected: {}", expected);

        List<String> actual = StringUtils.split("aaa ", " ");
        logger.info("actual: {}", actual);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void test_split_3() {
        List<String> expected = List.of("", "aaa");
        logger.info("expected: {}", expected);

        List<String> actual = StringUtils.split(" aaa", " ");
        logger.info("actual: {}", actual);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void test_split_4() {
        List<String> expected = List.of("aaa", "bbb", "ccc");
        logger.info("expected: {}", expected);

        List<String> actual = StringUtils.split("aaa bbb ccc", " ");
        logger.info("actual: {}", actual);

        Assertions.assertEquals(expected, actual);
    }

}
