package exqudens.example.util.command;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.Objects;

import org.apache.commons.cli.Options;

import exqudens.example.model.CommandLineResult;

import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;

public abstract class ACommand {

    protected final List<Option> options;
    protected final String command;
    protected final List<String> args;

    protected final Options cliOptions;

    protected ACommand(List<Option> options, String command, List<String> args) {
        Objects.requireNonNull(options);
        Objects.requireNonNull(command);
        Objects.requireNonNull(args);

        this.command = command;
        this.args = args;
        this.options = options;

        Options cliOptions = new Options();
        options.forEach(cliOptions::addOption);

        this.cliOptions = cliOptions;
    }

    public String usage() {
        HelpFormatter formatter = new HelpFormatter();
        StringWriter out = new StringWriter();
        PrintWriter pw = new PrintWriter(out);
        formatter.printHelp(pw, 80, command, "", cliOptions, formatter.getLeftPadding(), formatter.getDescPadding(), "");
        pw.flush();
        return out.toString();
    }

    public abstract CommandLineResult execute();

}
