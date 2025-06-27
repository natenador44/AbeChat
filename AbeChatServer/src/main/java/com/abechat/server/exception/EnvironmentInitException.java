package com.abechat.server.exception;

import com.abechat.server.config.ReleaseDataSourceConfig;

public class EnvironmentInitException extends RuntimeException {
    public static EnvironmentInitException noDbUrlSpecified() {
        return new EnvironmentInitException(ReleaseDataSourceConfig.DB_URL_ENV + " not specified");
    }

    public static EnvironmentInitException noDbUserSpecified() {
        return new EnvironmentInitException(ReleaseDataSourceConfig.DB_USER_ENV + " not specified");
    }

    public static EnvironmentInitException noDbPasswordSpecified() {
        return new EnvironmentInitException(ReleaseDataSourceConfig.DB_PASSWORD_ENV + " not specified");
    }

    private EnvironmentInitException(String message) {
        super(message);
    }
}
