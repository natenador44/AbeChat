package com.abechat.server.exception;

public class EnvironmentInitException extends RuntimeException {
    public static EnvironmentInitException noDbUrlSpecified() {
        return new EnvironmentInitException("DB_URL not specified");
    }

    private EnvironmentInitException(String message) {
        super(message);
    }
}
