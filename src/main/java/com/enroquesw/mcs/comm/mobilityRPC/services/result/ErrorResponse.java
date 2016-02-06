package com.enroquesw.mcs.comm.mobilityRPC.services.result;

/**
 * la interfaz <code>ErrorResponse</code> define el contracto de los Errores a recibir o enviar dado una  excepcion <code>ServiceBaseException</code>
 */
public interface ErrorResponse extends ObjectResponse{
    String getCodError();
    String getMessage();
}
