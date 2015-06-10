package cn.hotdev.example.tools;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public class StringTool {

    private static final Logger log = LoggerFactory.getLogger(StringTool.class);

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

    public static String md5(String format, Object... arguments) {

        String value = formatString(format, arguments);

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(value.getBytes("UTF-8"));
            byte b[] = md.digest();

            StringBuilder buf = new StringBuilder();
            for (byte aB : b) {
                int i = aB;
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
            //32位加密
            String result = buf.toString();
            // 16位的加密
            //result = buf.toString().substring(8, 24);

            log.info("md5({})={}", value, result);

            return result;
        } catch (NoSuchAlgorithmException e) {
            return null;
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }
}
