package exqudens.example.service;

import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.exception.ExceptionUtils;

import exqudens.example.controller.Controller;
import exqudens.example.model.CommandLineResult;
import exqudens.example.util.command.ACommand;
import exqudens.example.util.command.EncodeCommand;

public class CommandLineService {

    private final Controller controller;

    public CommandLineService(Controller controller) {
        Objects.requireNonNull(controller);

        this.controller = controller;
    }

    public CommandLineResult execute(List<String> args) {
        try {
            Class<?> applicationClass = controller.application.getClass();
            String command = Paths.get(applicationClass.getProtectionDomain().getCodeSource().getLocation().toURI()).toString().replace('\\', '/');
            Map<String, ACommand> commands = Map.of(
                "encode", new EncodeCommand(String.join(" ", command, "encode"), args.isEmpty() ? args : args.subList(1, args.size()))
            );

            String commandsInfo = commands.keySet().stream().collect(Collectors.joining(" | ", command + " [", "]"));

            if (args.isEmpty()) {
                return new CommandLineResult(0, commandsInfo, "");
            }

            String commandKey = args.get(0);

            if (!commands.containsKey(commandKey)) {
                return new CommandLineResult(1, "", commandsInfo);
            }

            return commands.get(commandKey).execute();
        } catch (Exception e) {
            return new CommandLineResult(1, "", ExceptionUtils.getStackTrace(e));
        }
    }

}
