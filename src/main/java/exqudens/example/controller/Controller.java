package exqudens.example.controller;

import java.util.List;
import java.util.Objects;

import exqudens.example.Application;
import exqudens.example.model.CommandLineResult;
import exqudens.example.service.CommandLineService;

public class Controller {

    public final Application application;
    public final CommandLineService commandLineService;

    public Controller(Application application) {
        Objects.requireNonNull(application);

        this.application = application;
        this.commandLineService = new CommandLineService(this);
    }

    public CommandLineResult executeCommandLine(List<String> args) {
        return commandLineService.execute(args);
    }

}
