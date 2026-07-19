package com.msa.auth.common.utill;

public class validation {
    public static Boolean isStringNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
}
