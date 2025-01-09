package com.jonnie.elearning.email;

import lombok.Getter;

@Getter
public enum EmailTemplateEngine {
    ACTIVATE_ACCOUNT("activate_account");
    private final String name;
    EmailTemplateEngine(String name) {
        this.name = name;
    }
}
