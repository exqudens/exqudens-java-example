# exqudens-java-example

## how-to-generate-maven-wrapper

1. `mvn wrapper:wrapper -Dtype=only-script`

## how-to-test

1. `mvn test`

## how-to-test-in-vscode

1. `mvn clean compile test-compile exec:java@vscode` or `mvn -Dvscode.test.default=example.Tests.* clean compile test-compile exec:java@vscode`
2. Select and run `test` launch configuration.
