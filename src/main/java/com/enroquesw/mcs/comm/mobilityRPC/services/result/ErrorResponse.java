package com.enroquesw.mcs.comm.mobilityRPC.services.result;

/**
 * Created by Julio on 24/01/2016.
 */
public interface ErrorResponse extends ObjectResponse{
    String getCodError();
    String getMessage();
}
