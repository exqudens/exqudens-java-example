package exqudens;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
            Set<String> testIds = tests
                .stream()
                .map(TestIdentifier::getSource)
                .flatMap(Optional::stream)
                .filter(MethodSource.class::isInstance)
                .map(MethodSource.class::cast)
                .map(v -> List.of(v.getClassName() + ".*", v.getClassName() + "." + v.getMethodName()))
                .flatMap(List::stream)
                .collect(Collectors.toCollection(LinkedHashSet::new));
            List<String> ids = new ArrayList<>(testIds);
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
                        .orElse("@template-options-key@");
                    String templateDefaultKey = Arrays
                        .stream(args)
                        .filter(v -> v.startsWith("--template-default-key="))
                        .map(v -> split(v, "--template-default-key="))
                        .flatMap(List::stream)
                        .map(String::trim)
                        .filter(v -> !v.isEmpty())
                        .findFirst()
                        .orElse("@template-default-key@");
                    String templateOptionsVal = Arrays
                        .stream(args)
                        .filter(v -> v.startsWith("--template-options-val="))
                        .map(v -> split(v, "--template-options-val="))
                        .flatMap(List::stream)
                        .map(String::trim)
                        .filter(v -> !v.isEmpty())
                        .findFirst()
                        .orElse("");
                    String templateDefaultVal = Arrays
                        .stream(args)
                        .filter(v -> v.startsWith("--template-default-val="))
                        .map(v -> split(v, "--template-default-val="))
                        .flatMap(List::stream)
                        .map(String::trim)
                        .filter(v -> !v.isEmpty())
                        .findFirst()
                        .orElse("");

                    String separatorForOptions = "\"," + System.lineSeparator() + "                \"";

                    List<String> options = Stream
                        .concat(
                            split(templateOptionsVal, ",").stream().map(String::trim).filter(v -> !v.isEmpty()),
                            ids.stream()
                        ).collect(Collectors.toCollection(ArrayList::new));
                    String optionsValue = String.join(separatorForOptions, options);
                    String defaultValue = templateDefaultVal.isEmpty() ? options.get(0) : templateDefaultVal;

                    String outpuString = Files.readString(template, StandardCharsets.UTF_8);
                    outpuString = outpuString.replace(templateOptionsKey, String.join(separatorForOptions, optionsValue));
                    outpuString = outpuString.replace(templateDefaultKey, defaultValue);

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
        List<String> result = null;
        if (value == null) {
            return result;
        }
        if (separator == null) {
            result = value.chars().mapToObj(c -> String.valueOf((char) c)).collect(Collectors.toCollection(ArrayList::new));
            return result;
        }
        if (!value.contains(separator)) {
            result = Arrays.asList(value);
            return result;
        }
        result = new ArrayList<>();
        int start = 0;
        int end;
        while ((end = value.indexOf(separator, start)) != -1) {
            result.add(value.substring(start, end));
            start = end + separator.length();
        }
        result.add(value.substring(start));
        return result;
    }

}
