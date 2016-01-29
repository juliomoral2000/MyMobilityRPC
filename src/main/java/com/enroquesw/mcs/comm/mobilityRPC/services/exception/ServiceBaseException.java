package com.enroquesw.mcs.comm.mobilityRPC.services.exception;

/**
 * Created by Julio on 25/01/2016.
 */
public class ServiceBaseException extends java.lang.RuntimeException {
    private String keyCode;

    public ServiceBaseException(String keyCode, String message) {
        super(message);
        this.keyCode = keyCode;
    }

    public ServiceBaseException(String message, Throwable cause, String keyCode) {
        super(message, cause);
        this.keyCode = keyCode;
    }

    public ServiceBaseException(Throwable cause, String keyCode, String message) {
        super(cause);
        this.keyCode = keyCode;
    }

    public String getKeyCode() {
        return keyCode;
    }
}
