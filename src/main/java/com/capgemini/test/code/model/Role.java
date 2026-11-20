package com.capgemini.test.code.model;

import com.capgemini.test.code.exception.ValidationException;
import java.util.Arrays;

public enum Role {
    ADMIN("admin"),
    SUPERADMIN("superadmin");

    private final String value;

    Role(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static Role fromString(String text) {
        return Arrays.stream(values())
                .filter(r -> r.value.equalsIgnoreCase(text))
                .findFirst()
                .orElseThrow(() -> new ValidationException("rol"));
    }
}
