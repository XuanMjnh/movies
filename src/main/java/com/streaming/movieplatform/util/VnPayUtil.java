package com.streaming.movieplatform.util;

import jakarta.servlet.http.HttpServletRequest;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public final class VnPayUtil {

    private static final DateTimeFormatter VNPAY_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private VnPayUtil() {
    }

    public static String formatDate(LocalDateTime value) {
        return value.format(VNPAY_DATE_FORMAT);
    }

    public static String generateTxnRef() {
        return "VNP" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                + ThreadLocalRandom.current().nextInt(100000, 999999);
    }

    public static String buildPaymentUrl(String baseUrl, Map<String, String> params, String secretKey) {
        String query = buildSignedQuery(params, secretKey);
        return baseUrl + "?" + query;
    }

    public static boolean verifySignature(Map<String, String> params, String secretKey) {
        String providedHash = params.get("vnp_SecureHash");
        if (providedHash == null || providedHash.isBlank()) {
            return false;
        }
        Map<String, String> filtered = new LinkedHashMap<>();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (key == null || !key.startsWith("vnp_")) {
                continue;
            }
            if ("vnp_SecureHash".equals(key) || "vnp_SecureHashType".equals(key)) {
                continue;
            }
            if (value == null || value.isBlank()) {
                continue;
            }
            filtered.put(key, value);
        }
        String calculated = hmacSHA512(secretKey, buildHashData(filtered));
        return calculated.equalsIgnoreCase(providedHash);
    }

    public static String getClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        String realIp = request.getHeader("X-Real-IP");
        if (realIp != null && !realIp.isBlank()) {
            return realIp.trim();
        }
        return request.getRemoteAddr();
    }

    public static String sanitizeOrderInfo(String raw) {
        if (raw == null) {
            return "";
        }
        String noAccent = Normalizer.normalize(raw, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        String normalized = noAccent.replaceAll("[^A-Za-z0-9 ]", " ")
                .replaceAll("\\s+", " ")
                .trim();
        return normalized.length() > 255 ? normalized.substring(0, 255) : normalized;
    }

    public static Map<String, String> extractVnpParams(Map<String, String[]> requestParams) {
        Map<String, String> result = new LinkedHashMap<>();
        for (Map.Entry<String, String[]> entry : requestParams.entrySet()) {
            String key = entry.getKey();
            if (key == null || !key.startsWith("vnp_")) {
                continue;
            }
            String[] values = entry.getValue();
            if (values != null && values.length > 0) {
                result.put(key, values[0]);
            }
        }
        return result;
    }

    private static String buildSignedQuery(Map<String, String> params, String secretKey) {
        String hashData = buildHashData(params);
        String query = buildQueryString(params);
        String secureHash = hmacSHA512(secretKey, hashData);
        return query + "&vnp_SecureHash=" + secureHash;
    }

    private static String buildHashData(Map<String, String> params) {
        List<Map.Entry<String, String>> entries = new ArrayList<>(params.entrySet());
        entries.removeIf(entry -> entry.getValue() == null || entry.getValue().isBlank());
        entries.sort(Comparator.comparing(Map.Entry::getKey));

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < entries.size(); i++) {
            Map.Entry<String, String> entry = entries.get(i);
            if (i > 0) {
                builder.append('&');
            }
            builder.append(urlEncode(entry.getKey()))
                    .append('=')
                    .append(urlEncode(entry.getValue()));
        }
        return builder.toString();
    }

    private static String buildQueryString(Map<String, String> params) {
        List<Map.Entry<String, String>> entries = new ArrayList<>(params.entrySet());
        entries.removeIf(entry -> entry.getValue() == null || entry.getValue().isBlank());
        entries.sort(Comparator.comparing(Map.Entry::getKey));

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < entries.size(); i++) {
            Map.Entry<String, String> entry = entries.get(i);
            if (i > 0) {
                builder.append('&');
            }
            builder.append(urlEncode(entry.getKey()))
                    .append('=')
                    .append(urlEncode(entry.getValue()));
        }
        return builder.toString();
    }

    private static String urlEncode(String value) {
        return URLEncoder.encode(Objects.toString(value, ""), StandardCharsets.US_ASCII);
    }

    private static String hmacSHA512(String key, String data) {
        try {
            Mac hmac512 = Mac.getInstance("HmacSHA512");
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
            hmac512.init(secretKey);
            byte[] bytes = hmac512.doFinal(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder hash = new StringBuilder(bytes.length * 2);
            for (byte b : bytes) {
                hash.append(String.format(Locale.ROOT, "%02x", b));
            }
            return hash.toString();
        } catch (Exception ex) {
            throw new IllegalStateException("Không thể tạo chữ ký VNPAY", ex);
        }
    }
}
