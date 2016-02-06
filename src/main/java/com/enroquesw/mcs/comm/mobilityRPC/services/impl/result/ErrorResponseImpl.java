package com.enroquesw.mcs.comm.mobilityRPC.services.impl.result;

import com.enroquesw.mcs.comm.mobilityRPC.services.result.ErrorResponse;

/**
 * la clase <code>ErrorResponseImpl</code> implementa la interfaz ErrorResponse
 */
public class ErrorResponseImpl implements ErrorResponse{

    private String codError;
    private String message;

    public ErrorResponseImpl(String codError, String message) {
        this.codError = codError;
        this.message = message;
    }

    @Override
    public String getCodError() {
        return codError;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
