package com.enroquesw.mcs.comm.mobilityRPC.exceptions;

/**
 * La Clase <code>MovilityRPCClientException</code> de Excepciones a Nivel de MyMovilityRPC Cliente.
 *
 * @author Julio Morales
 */
public class MovilityRPCClientException extends MovilityRPCException {
    public MovilityRPCClientException(String keyCode, String message) {
        super(keyCode, message);
    }

    public MovilityRPCClientException(String message, Throwable cause, String keyCode) {
        super(message, cause, keyCode);
    }

    public MovilityRPCClientException(Throwable cause, String keyCode, String message) {
        super(cause, keyCode, message);
    }
}
