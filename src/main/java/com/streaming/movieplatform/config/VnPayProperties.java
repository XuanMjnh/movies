package com.streaming.movieplatform.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class VnPayProperties {

    @Value("${app.payment.vnpay.enabled:false}")
    private boolean enabled;

    @Value("${app.payment.vnpay.tmn-code:}")
    private String tmnCode;

    @Value("${app.payment.vnpay.hash-secret:}")
    private String hashSecret;

    @Value("${app.payment.vnpay.pay-url:https://sandbox.vnpayment.vn/paymentv2/vpcpay.html}")
    private String payUrl;

    @Value("${app.payment.vnpay.return-url:http://localhost:8080/payment/vnpay/return}")
    private String returnUrl;

    @Value("${app.payment.vnpay.locale:vn}")
    private String locale;

    @Value("${app.payment.vnpay.order-type:other}")
    private String orderType;

    @Value("${app.payment.vnpay.expire-minutes:15}")
    private int expireMinutes;

    public boolean isEnabled() {
        return enabled;
    }

    public String getTmnCode() {
        return tmnCode;
    }

    public String getHashSecret() {
        return hashSecret;
    }

    public String getPayUrl() {
        return payUrl;
    }

    public String getReturnUrl() {
        return returnUrl;
    }

    public String getLocale() {
        return locale;
    }

    public String getOrderType() {
        return orderType;
    }

    public int getExpireMinutes() {
        return expireMinutes;
    }

    public boolean isConfigured() {
        return enabled
                && StringUtils.hasText(tmnCode)
                && StringUtils.hasText(hashSecret)
                && StringUtils.hasText(payUrl)
                && StringUtils.hasText(returnUrl);
    }
}
