# exqudens-java-example

## how-to-generate-maven-wrapper

1. `mvn wrapper:wrapper -Dtype=only-script`

## how-to-test

1. `mvn test`

## how-to-test-in-vscode

extensions:

- [Command Variable](https://marketplace.visualstudio.com/items?itemName=rioj7.command-variable#pickstringremember) `version >= v1.69.0`

steps:

1. `mvn clean compile test-compile exec:java@vscode`
2. Select and run `test` launch configuration.
