package exqudens.example;

import java.util.Arrays;
import java.util.Objects;

import exqudens.example.controller.Controller;
import exqudens.example.model.CommandLineResult;

public class Application {

    public static void main(String... args) {
        new Application().run(args);
    }

    public final Controller controller;

    public Application() {
        this.controller = new Controller(this);
    }

    public void run(String... args) {
        try {
            CommandLineResult result = controller.executeCommandLine(Arrays.asList(args));

            Objects.requireNonNull(result);
            Objects.requireNonNull(result.code());
            Objects.requireNonNull(result.out());
            Objects.requireNonNull(result.err());

            if (result.code().intValue() == 0) {
                System.out.println(result.out());
                System.exit(result.code().intValue());
            } else {
                System.err.println(result.err());
                System.exit(result.code().intValue());
            }
        } catch (Exception e) {
            e.printStackTrace(System.err);
            System.exit(1);
        }
    }

}
