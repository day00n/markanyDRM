package com.okfg.drm.adapter.biz.module.jwt.exception;

/**
 * @author myeongju.jung
 */
public class AuthQueryException extends RuntimeException {

    public AuthQueryException(String message) {
        super(message);
    }

    public AuthQueryException(String message, Throwable cause) {
        super(message, cause);
    }
}
