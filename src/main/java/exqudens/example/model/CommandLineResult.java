package exqudens.example.model;

import java.util.Objects;

public record CommandLineResult(
    Integer code,
    String out,
    String err
) {

    public CommandLineResult {
        Objects.requireNonNull(code, "'code' is null");
        Objects.requireNonNull(code, "'out' is null");
        Objects.requireNonNull(code, "'err' is null");
    }

    public CommandLineResult() {
        this(1, "", "uknown error");
    }

}
