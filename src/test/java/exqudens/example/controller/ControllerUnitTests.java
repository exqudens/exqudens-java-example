package exqudens.example.controller;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exqudens.example.Application;
import exqudens.example.model.CommandLineResult;

public class ControllerUnitTests {

    private static final Logger logger = LoggerFactory.getLogger(ControllerUnitTests.class);

    @Test
    void test_executeCommandLine_1() {
        logger.info("bgn");

        Application application = new Application();
        List<String> args = List.of();
        CommandLineResult result = application.controller.executeCommandLine(args);
        logger.info("result: {}", result);

        logger.info("end");
    }

    @Test
    void test_executeCommandLine_encode_1() {
        logger.info("bgn");

        Application application = new Application();
        List<String> args = List.of("encode", "--value", "aaa");
        CommandLineResult result = application.controller.executeCommandLine(args);
        logger.info("result: {}", result);

        logger.info("end");
    }

}
