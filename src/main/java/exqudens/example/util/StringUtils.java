package exqudens.example.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;
import java.util.stream.Collectors;

public class StringUtils {

    public static List<String> tokenize(String value, String separator) {
        if (value == null) {
            return null;
        }
        return Collections
            .list(new StringTokenizer(value, separator))
            .stream()
            .map(Object::toString)
            .collect(Collectors.toCollection(ArrayList::new));
    }

}
