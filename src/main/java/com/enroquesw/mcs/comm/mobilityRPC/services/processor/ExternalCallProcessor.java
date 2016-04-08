package com.enroquesw.mcs.comm.mobilityRPC.services.processor;

import com.enroquesw.mcs.comm.mobilityRPC.services.exception.ServiceBaseException;
import com.enroquesw.mcs.comm.mobilityRPC.services.parameter.ProcessParameter;
import com.enroquesw.mcs.comm.mobilityRPC.services.result.ProcessResponse;

/**
 * La Interface <code>ExternalCallProcessor</code> define el un procesador externo de llamadas remotas [el cual debe ser registrado por el cliente en su aplicacion] :
 * <p>
 *     <ul>
 *         <li>W la clase que implementa la interfaz {@link CallProcessor}  para la cual se define este procesador externo [Requerido]</li>
 *         <li>Y la clase que implementa la interfaz {@link ProcessParameter} (Requerido por el ExternalCallProcessor y el cual es proporcionado por CallProcessor de la libreria en la Maquina Local) </li>
 *         <li>T la clase [que implementa la interfaz {@link ProcessResponse} el cual es la respuesta del ExternalCallProcessor y enviado de retorno al CallProcessor de la libreria, es Requerido tanto para el CallProcessor en la Maquina Local/Servidor como para el CallerOfProcess en la Maquina Remota/Cliente [Requerido en Ambos]</li>
 *     </ul>
 * </p>
 * <p>
 * @author Julio Morales
 */
public interface ExternalCallProcessor<W extends CallProcessor, Y extends ProcessParameter, T> {
    T processCall(Y parameter) throws ServiceBaseException;
    //W getParentCallProccessor();
}
