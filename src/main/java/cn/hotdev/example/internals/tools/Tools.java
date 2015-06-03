package cn.hotdev.example.internals.tools;


import org.springframework.validation.ObjectError;

import java.util.List;

public class Tools {

    public static String formatString(String format, Object... arguments) {
        return String.format(format.replaceAll("\\{\\}", "%s"), arguments);
    }

    public static String formatStringList(String message, List<?> arguments, String delimiter) {
        int size = arguments.size();
        if (size <= 1) {
            return message + arguments.get(0).toString();
        }

        int i;
        StringBuilder builder = new StringBuilder();
        builder.append(message);
        for (i = 0; i < size; i++) {
            if (i > 0)
                builder.append(delimiter);
            builder.append(arguments.get(i).toString());
        }
        return builder.toString();
    }

    public static String formatErrorList(List<ObjectError> arguments) {
        String message = "errors: ";
        String delimiter = ", ";

        int size = arguments.size();
        if (size <= 1) {
            ObjectError objectError = arguments.get(0);
            return message + objectError.getDefaultMessage();
        }

        int i;
        StringBuilder builder = new StringBuilder();
        builder.append(message);
        for (i = 0; i < size; i++) {
            ObjectError objectError = arguments.get(i);

            if (i > 0)
                builder.append(delimiter);
            builder.append(objectError.getDefaultMessage());
        }
        return builder.toString();
    }

    public static String stringMultiply(String origin, int size) {
        if (size <= 1) {
            return origin;
        }

        int i;
        StringBuilder builder = new StringBuilder();
        for (i = 0; i < size; i++) {
            builder.append(origin);
        }
        return builder.toString();
    }
}
