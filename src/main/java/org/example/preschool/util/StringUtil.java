package org.example.preschool.util;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.net.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class StringUtil {

    public static Aggregation buildAggregation(List<String> list) {
        List<AggregationOperation> aggregationOperations = list.stream().filter(org.apache.commons.lang3.StringUtils::isNotBlank).map(Aggregation::stage).toList();
        return Aggregation.newAggregation(aggregationOperations);
    }

    public static String generateId() {
        return UUID.randomUUID().toString();
    }

    public static List<String> stringToList(String str) {
        if (StringUtils.isEmpty(str)) return Collections.emptyList();
        String replace = str.replace("[", "");
        String replace1 = replace.replace("]", "");
        return new ArrayList<>(Arrays.asList(replace1.split(",")));
    }

    public static String getCurrentMethodName(String prefix) {
        return prefix + "." + StackWalker.getInstance()
                .walk(s -> s.skip(1).findFirst())
                .get()
                .getMethodName();
    }


    public static String getHost() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface iface = interfaces.nextElement();
                if (iface.isLoopback() || !iface.isUp()) {
                    continue;
                }

                Enumeration<InetAddress> addresses = iface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();
                    if (addr instanceof Inet4Address) {
                        return addr.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
            return null;
        }
        return null;

    }

    public static Map<String, Object> modelToMap(Object object) {
        Map<String, Object> map = new HashMap<>();
        Field[] fields = object.getClass().getDeclaredFields();
        Field[] extendsFields = object.getClass().getSuperclass().getDeclaredFields();
        Field[] both = (Field[]) ArrayUtils.addAll(fields, extendsFields);

        for (Field field : both) {
            field.setAccessible(true);
            try {
                map.put(field.getName(), field.get(object));
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        return map;
    }

    public static String arrayToString(List<String> list) {
        if (list == null)
            return "null";
        int iMax = list.size() - 1;
        if (iMax == -1)
            return "[]";

        StringBuilder b = new StringBuilder();
        b.append('[');
        for (int i = 0; ; i++) {
            b.append("'").append(list.get(i)).append("'");
            if (i == iMax) {
                return b.append(']').toString();
            }
            b.append(", ");
        }
    }

    public static String listToString(List<String> list) {
        if (list == null || list.isEmpty()) return null;
        return "[" + String.join(",", list) + "]";

    }

    public static int calculateTotalVolume(List<String> base64Strings) {
        int totalSize = 0;
        for (String base64String : base64Strings) {
            byte[] binaryData = Base64.getDecoder().decode(base64String);
            totalSize += binaryData.length;
        }
        return totalSize;
    }

    public static long convertMbToBytes(int megabytes) {
        // 1 MB = 1024 * 1024 bytes
        return (long) (megabytes * 1024 * 1024);
    }

    public static String getFileExtension(String filePath) {
        int lastIndexOfDot = filePath.lastIndexOf('.');
        if (lastIndexOfDot == -1) {
            return "";
        }
        return filePath.substring(lastIndexOfDot);
    }

    public static Map<String, String> getQueryParams(String query) throws Exception {
        Map<String, String> queryParams = new HashMap<>();
        if (query != null && !query.isEmpty()) {
            String[] pairs = query.split("&");
            for (String pair : pairs) {
                String[] keyValue = pair.split("=");
                String key = URLDecoder.decode(keyValue[0], "UTF-8");
                String value = keyValue.length > 1 ? URLDecoder.decode(keyValue[1], "UTF-8") : "";
                queryParams.put(key, value);
            }
        }
        return queryParams;
    }

    public static String convertEcoDate(String input) {

        if (input == null || input.isBlank()) {
            return "";
        }

        // bỏ ký tự 〜
        input = input.replace("〜", "").trim();

        DateTimeFormatter inputFormat =
                DateTimeFormatter.ofPattern(
                        "MMM,dd,yyyy HH:mm",
                        Locale.ENGLISH
                );

        DateTimeFormatter outputFormat =
                DateTimeFormatter.ofPattern(
                        "yyyy-MM-dd HH:mm:ss"
                );

        LocalDateTime dateTime =
                LocalDateTime.parse(input, inputFormat);

        return dateTime.format(outputFormat);
    }
}
