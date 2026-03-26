package com.streaming.movieplatform.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public final class ReferenceCodeUtil {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private ReferenceCodeUtil() {
    }

    public static String generate(String prefix) {
        return prefix + "-" + LocalDateTime.now().format(FORMATTER) + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
