package com.enroquesw.mcs.comm.mobilityRPC.exceptions;

/**
 * La Clase Generica <code>MovilityRPCException</code> de emision de Excepciones a usar a Nivel de MyMovilityRPC [Servidor y Cliente]
 */
public class MovilityRPCException extends java.lang.RuntimeException{
    private String keyCode;

    public MovilityRPCException(String keyCode, String message) {
        super(message);
        this.keyCode = keyCode;
    }

    public MovilityRPCException(String message, Throwable cause, String keyCode) {
        super(message, cause);
        this.keyCode = keyCode;
    }

    public MovilityRPCException (Throwable cause, String keyCode, String message) {
        super(cause);
        this.keyCode = keyCode;
    }

    public String getKeyCode() {
        return keyCode;
    }
}
