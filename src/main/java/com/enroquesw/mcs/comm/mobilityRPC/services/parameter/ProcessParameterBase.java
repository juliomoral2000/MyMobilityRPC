package com.enroquesw.mcs.comm.mobilityRPC.services.parameter;

/**
 * <code>ProcessParameterBase</code> es la Clase Abstracta que define que esta implementacion es un parametro usado
 * por algun CallProcessor como parametro de entrada e recibido por los Processor en el Remoto (es el que contiene el requestid utilizado en el proceso de llamado remoto)
 *
 * @author Julio Morales
 */
public abstract class ProcessParameterBase implements ProcessParameter{
    private String requestId; //FIXME_JULIO: La idea es cada vez que setee en CallerOfProcess se resetea nuevamente

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }


}
