package exqudens.example.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class StringUtils {

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
