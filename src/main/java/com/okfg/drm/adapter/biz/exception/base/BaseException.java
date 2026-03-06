package com.okfg.drm.adapter.biz.exception.base;

public class BaseException extends Exception{
    String code;
    String value;
    String desc;

    public BaseException(String code) {
        super(code);
        this.code = code;
    }

    public BaseException(String code, String value, String desc) {
        this.code = code;
        this.value = value;
        this.desc = desc;
    }

    public BaseException(String message, String code, String value, String desc) {
        super(message);
        this.code = code;
        this.value = value;
        this.desc = desc;
    }

    public BaseException(String message, Throwable cause, String code, String value, String desc) {
        super(message, cause);
        this.code = code;
        this.value = value;
        this.desc = desc;
    }

    public BaseException(Throwable cause, String code, String value, String desc) {
        super(cause);
        this.code = code;
        this.value = value;
        this.desc = desc;
    }

    public BaseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, String code, String value, String desc) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.code = code;
        this.value = value;
        this.desc = desc;
    }



}
