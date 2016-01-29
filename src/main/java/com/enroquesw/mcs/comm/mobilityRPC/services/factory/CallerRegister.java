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
package com.enroquesw.mcs.comm.mobilityRPC.services.factory;

import com.enroquesw.mcs.comm.mobilityRPC.enums.SystemName;
import com.enroquesw.mcs.comm.mobilityRPC.services.factory.util.Util;
import com.enroquesw.mcs.comm.mobilityRPC.services.parameter.ProcessParameter;
import com.enroquesw.mcs.comm.mobilityRPC.services.result.ProcessResponse;
import com.enroquesw.mcs.comm.mobilityRPC.services.callable.CallerOfProcess;

/**
 * La clase CallerRegister representa un registro de los CallerOfProcess o Caller validos [sus datos requeridos para procesar la llamada] para esta dupla {caller/listener} <p>
 * @param <V> Clase que implementa {@link CallerOfProcess}      - [Local/Cliente]==[Remoto/Servidor]
 * @param <Y> Clase que implementa {@link ProcessParameter}     - [Ambos]
 * @param <T> Clase que implementa {@link ProcessResponse}      - [Ambos]
 *
 * @author Julio Morales
 */
public class CallerRegister<V extends CallerOfProcess, Y extends ProcessParameter, T /*FIXME_JULIO: extends ProcessResponse*/ >  implements ProcessResponse {
    String codUniqOfService;          // Codigo que identifica a este registro de Caller[Servicio/LLamada] a un Metodo en el Remoto - [Ambos] el mismo utilizado en el ProcessorRegister
    SystemName localCaller;         // Sistema que crea la peticion/llamada     - SystemName del [Local|Cliente] esta en [Ambos] - SystemName.ALL es para invocar/procesar metodos de ServicesFactory desde cualquier Maquina
    SystemName remoteLister;        // Sistema que procesa la peticion/llamada  - SystemName del [Remoto|Servidor] esta en [Ambos] - SystemName.ALL es para invocar/procesar metodos de ServicesFactory desde cualquier Maquina
    String methodName;              // Metodo[verbo/operacion] del CallProcessor a Invocar - implementacion en el [Remoto|Servidor] este valor lo deben tener [Ambos]
    String processorClassName;      // Nombre de la Clase que implementa a CallProcessor a Invocar en la Maquina Remota - implementacion en el [Remoto|Servidor] y este valor lo deben tener [Local|Cliente] por lo tanto en el remoto Class.forName(processorClassName).equals(CallProcessor.class) debe ser true.
    Class<Y> parameterClass;        // Clase que implementa a ProcessParameter y se enviara en la llamada (es el parametro de entrada del metodo [verbo/operacion] del CallProcessor) - implementacion en [Ambos]
    Class<T> resultClass;           // Clase [que implementa a ProcessResponse] y es la respuesta a la llamada (es el parametro de salida del metodo [verbo/operacion] del CallProcessor) - implementacion en [Ambos]
    Class<V> callerClass;           // Clase que implementa CallerOfProcess     - implementacion en el [Local|Cliente]

    public CallerRegister(SystemName localCaller, SystemName remoteLister, String methodName, String processorClassName, Class<Y> parameterClass, Class<T> resultClass, Class<V> callerClass) {
        this.localCaller = localCaller;
        this.remoteLister = remoteLister;
        this.methodName = methodName;
        this.processorClassName = processorClassName;
        this.parameterClass = parameterClass;
        this.resultClass = resultClass;
        this.callerClass = callerClass;
        setCodUniqOfService();
    }

    private void setCodUniqOfService() {
    // La dupla {Systemcaller/Systemlisterner} posee n procesorClass,
    // un processorClass puede tener m metodos cada uno con o sin parametros
    // - de esos m solo n estan asignados a la dupla y poseen un caller,
    // entonces existen n caller, n result y n parametros
        this.codUniqOfService = Util.sha256(new StringBuilder().
                append(localCaller.getSystemId()).append(ServicesFactory.CHAR_SEP).
                append(remoteLister.getSystemId()).append(ServicesFactory.CHAR_SEP).
                append(methodName).append(ServicesFactory.CHAR_SEP).
                append(processorClassName).append(ServicesFactory.CHAR_SEP).
                append(parameterClass.getSimpleName()).append(ServicesFactory.CHAR_SEP). //Crear implementacio de Void o Nulo
                append(resultClass.getSimpleName()).append(ServicesFactory.CHAR_SEP).
                toString());
    }

    /**
     *
     * @param localCaller
     * @param remoteLister
     * @param methodName
     * @param processorClassName
     * @param parameterClass
     * @param resultClass
     * @param callerClass
     * @param <V>
     * @param <Y>
     * @param <T>
     * @return
     * @deprecated Es solo de prueba no deberia utilizarlo
     */
    private static <V extends CallerOfProcess, Y extends ProcessParameter, T /*FIXME_JULIO: extends ProcessResponse*/> String  getCodUniqOfService(SystemName localCaller, SystemName remoteLister, String methodName, String processorClassName, Class<Y> parameterClass, Class<T> resultClass, Class<V> callerClass) {
        return new StringBuilder(localCaller.getSystemId()).append(ServicesFactory.CHAR_SEP).
                append(remoteLister.getSystemId()).append(ServicesFactory.CHAR_SEP).
                append(methodName).append(ServicesFactory.CHAR_SEP).
                append(processorClassName).append(ServicesFactory.CHAR_SEP).
                append(parameterClass.getSimpleName()).append(ServicesFactory.CHAR_SEP).
                append(resultClass.getSimpleName()).append(ServicesFactory.CHAR_SEP).
                toString();
    }

    public String getCodUniqOfService() {
        return codUniqOfService;
    }

    public SystemName getLocalCaller() {
        return localCaller;
    }

    public SystemName getRemoteLister() {
        return remoteLister;
    }

    public String getMethodName() {
        return methodName;
    }

    public String getProcessorClassName() {
        return processorClassName;
    }

    public Class<Y> getParameterClass() {
        return parameterClass;
    }

    public Class<T> getResultClass() {
        return resultClass;
    }

    public Class<V> getCallerClass() {
        return callerClass;
    }

    @Override
    public String toString() {
        return "CallerRegister{" +
                "codUniqOfService='" + codUniqOfService + '\'' +
                ", localCaller=" + localCaller +
                ", remoteLister=" + remoteLister +
                ", methodName='" + methodName + '\'' +
                ", processorClassName='" + processorClassName + '\'' +
                ", parameterClass=" + parameterClass +
                ", resultClass=" + resultClass +
                ", callerClass=" + callerClass +
                '}';
    }
}