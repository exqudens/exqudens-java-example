package exqudens.example.util.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.exception.ExceptionUtils;

import exqudens.example.model.CommandLineResult;

public class EncodeCommand extends ACommand {

    private static final Option HELP_OPTION = Option.builder().longOpt("help").desc("Show help info.").get();
    private static final Option VALUE_OPTION = Option.builder().longOpt("value").desc("Value to encode.").hasArg().get();

    private static final List<Option> OPTIONS = List.of(
        HELP_OPTION,
        VALUE_OPTION
    );

    public EncodeCommand(String command, List<String> args) {
        super(OPTIONS, command, args);
    }

    @Override
    public CommandLineResult execute() {
        try {
            CommandLineParser parser = new DefaultParser();
            CommandLine commandLine = parser.parse(cliOptions, args.toArray(new String[0]));
            List<String> otherArgs = commandLine
                    .getArgList()
                    .stream()
                    .filter(Objects::nonNull)
                    .map(String::trim)
                    .filter(v -> !v.isEmpty())
                    .collect(Collectors.toCollection(ArrayList::new));

            if (!otherArgs.isEmpty()) {
                return new CommandLineResult(1, "", usage());
            } else if (commandLine.hasOption(HELP_OPTION)) {
                return new CommandLineResult(0, usage(), "");
            } else if (commandLine.hasOption(VALUE_OPTION)) {
                String value = commandLine.getOptionValue(VALUE_OPTION);
                String out = "`" + value + "`";
                return new CommandLineResult(0, out, "");
            }

            return new CommandLineResult(0, usage(), "");
        } catch (ParseException e) {
            return new CommandLineResult(1, "", usage());
        } catch (Exception e) {
            return new CommandLineResult(1, "", ExceptionUtils.getStackTrace(e));
        }
    }

}
