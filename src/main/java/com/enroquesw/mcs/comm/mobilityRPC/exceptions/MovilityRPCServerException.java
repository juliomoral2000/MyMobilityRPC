package com.enroquesw.mcs.comm.mobilityRPC.exceptions;

/**
 * La Clase <code>MovilityRPCServerException</code> de Excepciones a Nivel de MyMovilityRPC Servidor
 *
 * @author Julio Morales
 */
public class MovilityRPCServerException extends MovilityRPCException {

    public MovilityRPCServerException(String keyCode, String message) {
        super(keyCode, message);
    }

    public MovilityRPCServerException(String message, Throwable cause, String keyCode) {
        super(message, cause, keyCode);
    }

    public MovilityRPCServerException(Throwable cause, String keyCode, String message) {
        super(cause, keyCode, message);
    }
}
