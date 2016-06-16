package com.enroquesw.mcs.comm.mobilityRPC.services.exception;

/**
 * <code>ServiceBaseException</code> es la clase de las excepciones que pueden ser lanzadas durante las invocaciones de las llamadas remotas en los Callers y de las respuestas de los CallProcessors en la Maquina Remota.
 *
 * @author Julio Morales
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
