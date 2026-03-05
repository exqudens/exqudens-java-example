package exqudens;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.junit.platform.engine.discovery.DiscoverySelectors;
import org.junit.platform.engine.support.descriptor.MethodSource;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.TestIdentifier;
import org.junit.platform.launcher.TestPlan;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;

public class TestUtil {

    public static void main(String... args) throws Exception {
        Arrays.stream(args).filter(Objects::nonNull).map(v -> format("-- arg: '{0}'", v)).forEach(System.out::println);
        String command = Arrays
            .stream(args)
            .filter(v -> v.startsWith("--command="))
            .map(v -> split(v, "--command="))
            .flatMap(List::stream)
            .map(String::trim)
            .filter(v -> !v.isEmpty())
            .findFirst()
            .orElse("");
        String templateOptionsVal = Arrays
            .stream(args)
            .filter(v -> v.startsWith("--template-options-val="))
            .map(v -> split(v, "--template-options-val="))
            .flatMap(List::stream)
            .map(String::trim)
            .filter(v -> !v.isEmpty())
            .findFirst()
            .orElse(null);
        if (command.equals("discover")) {
            List<String> result = new ArrayList<>();
            Set<Path> classPaths = Arrays
                .stream(args)
                .filter(v -> v.startsWith("--scan-class-path="))
                .map(v -> split(v, "--scan-class-path="))
                .flatMap(List::stream)
                .map(String::trim)
                .filter(v -> !v.isEmpty())
                .map(v -> v.replace('\\', '/'))
                .map(Paths::get)
                .collect(Collectors.toCollection(LinkedHashSet::new));
            Launcher launcher = LauncherFactory.create();
            LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request().selectors(DiscoverySelectors.selectClasspathRoots(classPaths)).build();
            TestPlan plan = launcher.discover(request);
            Set<TestIdentifier> tests = plan.getRoots().stream().map(plan::getDescendants).flatMap(Set::stream).collect(Collectors.toCollection(LinkedHashSet::new));
            List<String> ids = tests
                .stream()
                .map(TestIdentifier::getSource)
                .flatMap(Optional::stream)
                .filter(MethodSource.class::isInstance)
                .map(MethodSource.class::cast)
                .map(v -> format("{0}.{1}", v.getClassName(), v.getMethodName()))
                .collect(Collectors.toCollection(ArrayList::new));
            result.add(format("-- ids.size: {0}", ids.size()));
            if (ids.isEmpty()) {
                result.forEach(System.out::println);
                return;
            } else {
                String outputType = Arrays
                    .stream(args)
                    .filter(v -> v.startsWith("--output-type="))
                    .map(v -> split(v, "--output-type="))
                    .flatMap(List::stream)
                    .map(String::trim)
                    .filter(v -> !v.isEmpty())
                    .findFirst()
                    .orElse("stdout");
                if (outputType.equals("stdout")) {
                    ids.forEach(System.out::println);
                } else if (outputType.equals("vscode-launch-json")) {
                    List<String> options = new ArrayList<>();
                    if (templateOptionsVal != null) {
                        split(templateOptionsVal, ",").stream().map(String::trim).filter(v -> !v.isEmpty()).forEach(options::add);
                    }
                    ids.stream().forEach(options::add);
                    Path output = Arrays
                        .stream(args)
                        .filter(v -> v.startsWith("--output="))
                        .map(v -> split(v, "--output="))
                        .flatMap(List::stream)
                        .map(String::trim)
                        .filter(v -> !v.isEmpty())
                        .map(v -> v.replace('\\', '/'))
                        .map(Paths::get)
                        .findFirst()
                        .orElse(Paths.get(".vscode/launch.json"));
                    Path template = Arrays
                        .stream(args)
                        .filter(v -> v.startsWith("--template="))
                        .map(v -> split(v, "--template="))
                        .flatMap(List::stream)
                        .map(String::trim)
                        .filter(v -> !v.isEmpty())
                        .map(v -> v.replace('\\', '/'))
                        .map(Paths::get)
                        .findFirst()
                        .orElse(Paths.get("src/test/resources/launch.json"));
                    String templateOptionsKey = Arrays
                        .stream(args)
                        .filter(v -> v.startsWith("--template-options-key="))
                        .map(v -> split(v, "--template-options-key="))
                        .flatMap(List::stream)
                        .map(String::trim)
                        .filter(v -> !v.isEmpty())
                        .findFirst()
                        .orElse("${template-options-key}");
                    String templateDefaultVal = Arrays
                        .stream(args)
                        .filter(v -> v.startsWith("--template-default-val="))
                        .map(v -> split(v, "--template-default-val="))
                        .flatMap(List::stream)
                        .map(String::trim)
                        .filter(v -> !v.isEmpty())
                        .findFirst()
                        .orElse(options.get(0));
                    String templateDefaultKey = Arrays
                        .stream(args)
                        .filter(v -> v.startsWith("--template-default-key="))
                        .map(v -> split(v, "--template-default-key="))
                        .flatMap(List::stream)
                        .map(String::trim)
                        .filter(v -> !v.isEmpty())
                        .findFirst()
                        .orElse(null);

                    String separatorForOptions = "\"," + System.lineSeparator() + "                \"";

                    String outpuString = Files.readString(template, StandardCharsets.UTF_8);
                    outpuString = outpuString.replace(templateOptionsKey, String.join(separatorForOptions, options));

                    if (templateDefaultKey != null) {
                        outpuString = outpuString.replace(templateDefaultKey, templateDefaultVal);
                    }

                    Files.createDirectories(output.getParent());
                    Files.writeString(output, outpuString, StandardCharsets.UTF_8);

                    result.add(format("-- generated: '{0}'", output.toString()));
                } else {
                    throw new Exception(format("unsupported output type: '{0}'", outputType));
                }
            }
            result.forEach(System.out::println);
        }
    }

    public static String format(Object format, Object... args) {
        String result = String.valueOf(format);
        if (format == null || args == null || args.length == 0) {
            return result;
        }
        result = result.replace("'", "''");
        result = MessageFormat.format(result, args);
        return result;
    }

    public static List<String> split(String value, String separator) {
        if (value == null) {
            return null;
        }
        if (separator == null) {
            return value.chars().mapToObj(c -> String.valueOf((char) c)).collect(Collectors.toCollection(ArrayList::new));
        } else {
            return Arrays.stream(value.split(Pattern.quote(separator))).collect(Collectors.toCollection(ArrayList::new));
        }
    }

}
