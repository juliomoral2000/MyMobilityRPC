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
import com.enroquesw.mcs.comm.mobilityRPC.services.processor.CallProcessor;
import com.enroquesw.mcs.comm.mobilityRPC.services.processor.ExternalCallProcessor;
import com.enroquesw.mcs.comm.mobilityRPC.services.result.ProcessResponse;

import java.lang.reflect.Method;

/**
 * La clase ProcessorRegister representa un registro de los CallProcessor o Processor validos [sus datos requeridos para procesar la llamada y retornar una respuesta] para esta dupla {caller/listener} <p>
 * @param <W> Clase que implementa {@link ProcessorRegister}      - [Local/Servidor]==[Remoto/Cliente]
 * @param <Y> Clase que implementa {@link ProcessParameter}     - [Ambos]
 * @param <T> Clase que implementa {@link ProcessResponse}      - [Ambos]
 *
 * @author Julio Morales
 *           FIXME_JULIO: Adicional podria agregarle una lista de Listener que sean externos al Core/Base para ser invocados dentro del processor
 */
public class ProcessorRegister<W extends CallProcessor, Y extends ProcessParameter, T /*FIXME_JULIO: extends ProcessResponse*/, Z extends ExternalCallProcessor> implements ProcessResponse {
    String codUniqOfService;        // Codigo que identifica a este registro de Processor [Metodo/verbo/operacion]- [Ambos] el mismo utilizado en el CallerRegister
    SystemName remoteCaller;        // Sistema que crea la peticion/llamada     - SystemName del [Remoto/Cliente] esta en [Ambos] - SystemName.ALL es para invocar/procesar metodos de ServicesFactory desde cualquier Maquina
    SystemName localLister;         // Sistema que procesa la peticion/llamada  - SystemName del [local/Servidor] esta en [Ambos] - SystemName.ALL es para invocar/procesar metodos de ServicesFactory desde cualquier Maquina
    String methodName;              // Metodo[verbo/operacion] del CallProcessor a Invocar - implementacion en el [local/Servidor] este valor lo deben tener [Ambos]
    Class<W> processorClass;        // Clase que implementa a CallProcessor - implementacion en el [local/Servidor]
    Class<Y> parameterClass;        // Clase que implementa ProcessParameter y se recibira en la llamada (es el parametro de entrada del metodo [verbo/operacion] del CallProcessor) - implementacion en [Ambos]
    Class<T> resultClass;           // Clase [que implementa a ProcessResponse] y es la respuesta a la llamada (es el parametro de salida del metodo [verbo/operacion] del CallProcessor) - implementacion en [Ambos]
    Method method;                  // Objeto Metodo cacheado para ser invocado una vez llegue una llamada
    /********************************************************************************************************/
    Z externalCallProcessor;        // Objeto Implementado por el usuario que sera invocado externamente desde el Processor definido en la libreria

    public ProcessorRegister(SystemName remoteCaller, SystemName localLister, String methodName, Class<W> processorClass, Class<Y> parameterClass, Class<T> resultClass) throws Exception {
        this.remoteCaller = remoteCaller;
        this.localLister = localLister;
        this.methodName = methodName;
        this.processorClass = processorClass;
        this.parameterClass = parameterClass;
        this.resultClass = resultClass;
        setCodUniqOfService();
        setMethod();
    }
    public ProcessorRegister(SystemName remoteCaller, SystemName localLister, String methodName, Class<W> processorClass, Class<Y> parameterClass, Class<T> resultClass, Z externalCallProcessor) throws Exception {
        this(remoteCaller, localLister, methodName, processorClass, parameterClass, resultClass);
        this.externalCallProcessor = externalCallProcessor;
    }

    private void setMethod() throws Exception {
        Method[] methods = processorClass.getDeclaredMethods();
        for (Method m : methods) if(methodName.equals(m.getName())) this.method = m;
        if(this.method == null) throw new Exception("El Metodo "+methodName+" No existe en la clase "+processorClass.getName());
    }

    private void setCodUniqOfService() {
        // Para la dupla {Systemcaller/Systemlisterner} posee n procesorClass, un processorClass puede tener m metodos cada uno con o sin parametros, y m resultados
        this.codUniqOfService = Util.sha256(new StringBuilder().
                append(remoteCaller.getSystemId()).append(ServicesFactory.CHAR_SEP).
                append(localLister.getSystemId()).append(ServicesFactory.CHAR_SEP).
                append(methodName).append(ServicesFactory.CHAR_SEP).
                append(processorClass.getSimpleName()).append(ServicesFactory.CHAR_SEP).
                append(parameterClass.getSimpleName()).append(ServicesFactory.CHAR_SEP).
                append(resultClass.getSimpleName()).append(ServicesFactory.CHAR_SEP).
                toString());
    }

    /**
     *
     * @param remoteCaller
     * @param localLister
     * @param methodName
     * @param processorClass
     * @param parameterClass
     * @param resultClass
     * @param <W>
     * @param <Y>
     * @param <T>
     * @return
     * @deprecated Es solo de prueba no deberia utilizarlo
     */
    private static <W extends CallProcessor, Y extends ProcessParameter, T /*FIXME_JULIO: extends ProcessResponse*/> String getCodUniqOfService(SystemName remoteCaller, SystemName localLister, String methodName, Class<W> processorClass, Class<Y> parameterClass, Class<T> resultClass) {
        return new StringBuilder(remoteCaller.getSystemId()).append(ServicesFactory.CHAR_SEP).
                append(localLister.getSystemId()).append(ServicesFactory.CHAR_SEP).
                append(methodName).append(ServicesFactory.CHAR_SEP).
                append(processorClass.getSimpleName()).append(ServicesFactory.CHAR_SEP).
                append(parameterClass.getSimpleName()).append(ServicesFactory.CHAR_SEP).
                append(resultClass.getSimpleName()).append(ServicesFactory.CHAR_SEP).
                toString();
    }

    public String getCodUniqOfService() {
        return codUniqOfService;
    }

    public SystemName getRemoteCaller() {
        return remoteCaller;
    }

    public SystemName getLocalLister() {
        return localLister;
    }

    public String getMethodName() {
        return methodName;
    }

    public Class<W> getProcessorClass() {
        return processorClass;
    }

    public Class<Y> getParameterClass() {
        return parameterClass;
    }

    public Class<T> getResultClass() {
        return resultClass;
    }

    public Method getMethod() {
        return method;
    }

    public Z getExternalCallProcessor() {
        return (Z) externalCallProcessor;
    }

    public void setExternalCallProcessor(Z externalCallProcessor) { //Si se desea cambiar en tiempo de Ejecucion
        this.externalCallProcessor = externalCallProcessor;
    }

    @Override
    public String toString() {
        return "ProcessorRegister{" +
                "codUniqOfService='" + codUniqOfService + '\'' +
                ", Caller=" + remoteCaller +
                ", Proccessor=" + localLister +
                ", methodName='" + methodName + '\'' +
                ", processorClass=" + processorClass +
                ", parameterClass=" + parameterClass +
                ", resultClass=" + resultClass +
                '}';
    }
}
