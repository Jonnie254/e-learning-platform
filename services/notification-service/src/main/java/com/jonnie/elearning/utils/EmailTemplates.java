package com.jonnie.elearning.utils;

import lombok.Getter;

@Getter
public enum EmailTemplates {
    PAYMENT_CONFIRMATION("payment-confirmation.html", "Payment Confirmation"),
    CART_CONFIRMATION("cart-confirmation.html", "Cart Confirmation");


    private final String templateName;
    private final String subject;
    EmailTemplates(String templateName, String subject) {
        this.templateName = templateName;
        this.subject = subject;
    }
}
