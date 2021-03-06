/**
 * Copyright 2016 Julio morales
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.enroquesw.mcs.comm.mobilityRPC.services.processor;

import com.enroquesw.mcs.comm.mobilityRPC.enums.SystemName;
import com.enroquesw.mcs.comm.mobilityRPC.services.exception.ServiceBaseException;
import com.enroquesw.mcs.comm.mobilityRPC.services.factory.ProcessorRegister;
import com.enroquesw.mcs.comm.mobilityRPC.services.factory.ServicesFactory;
import com.enroquesw.mcs.comm.mobilityRPC.services.parameter.ProcessParameter;
import com.enroquesw.mcs.comm.mobilityRPC.services.result.ProcessResponse;

import java.util.List;

/**
 * La Interface <code>CallProcessor</code> define el procesador de llamadas :
 * <p>
 *     <ul>
 *         <li>W la clase que implementa la interfaz {@link CallProcessor}  [Requerido Local/Servidor]</li>
 *         <li>Y la clase que implementa la interfaz {@link ProcessParameter} (Requerido por el CallProcessor en la Maquina Remota/Servidor) [Opcional pero en Ambos - Crear una Implentacion Para void]</li>
 *         <li>T la clase [que implementa la interfaz {@link ProcessResponse} el cual es la respuesta del CallProcessor, es Requerido tanto para el CallProcessor en la Maquina Local/Servidor como para el CallerOfProcess en la Maquina Remota/Cliente [Requerido en Ambos]</li>
 *     </ul>
 * </p>
 * <p>
 * @see <a href="com.googlecode.mobilityrpc.examples.BoomerangPattern">ejemplo BoomerangPattern</a>
 * @author Julio Morales
 */
public abstract class CallProcessor<W extends CallProcessor, Y extends ProcessParameter, T , Z extends ExternalCallProcessor> {

    public static <W extends CallProcessor, Y extends ProcessParameter, T> T invokeExternalProcessor(SystemName remote, Class<W> callProcessorClass, String methodName, Y parameter) throws ServiceBaseException {
        List<ProcessorRegister> list = ServicesFactory.getProcessorRegister(remote, callProcessorClass, methodName);
        if(!list.isEmpty()) return (T) list.get(0).getExternalCallProcessor().processCall(parameter);
        return (T) null;
    }

}
