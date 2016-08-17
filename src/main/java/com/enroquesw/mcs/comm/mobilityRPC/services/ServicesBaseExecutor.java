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
package com.enroquesw.mcs.comm.mobilityRPC.services;

import com.enroquesw.mcs.comm.mobilityRPC.MyMovilityRPCComm;
import com.enroquesw.mcs.comm.mobilityRPC.enums.CallType;
import com.enroquesw.mcs.comm.mobilityRPC.enums.SystemName;
import com.enroquesw.mcs.comm.mobilityRPC.services.callable.CallerOfProcess;
import com.enroquesw.mcs.comm.mobilityRPC.services.exception.ServiceBaseException;
import com.enroquesw.mcs.comm.mobilityRPC.services.factory.ProcessorRegister;
import com.enroquesw.mcs.comm.mobilityRPC.services.factory.ServicesFactory;
import com.enroquesw.mcs.comm.mobilityRPC.services.impl.result.ErrorResponseImpl;
import com.enroquesw.mcs.comm.mobilityRPC.services.parameter.ProcessParameter;
import com.enroquesw.mcs.comm.mobilityRPC.services.parameter.VoidParameter;
import com.sun.istack.internal.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Logger;

/**
 * La clase <code>ServicesBaseExecutor</code> es la encargada de ejecutar a los Callers y Processors
 *
 * @author Julio Morales
 *
 * @see com.enroquesw.mcs.comm.mobilityRPC.services.processor.CallProcessor
 * @see CallerOfProcess
 */
public class ServicesBaseExecutor<V extends CallerOfProcess, Y extends ProcessParameter, T /*FIXME_JULIO: extends ProcessResponse*/> {
    private static final String preProcessCall = "preProcessCall";
    private static final String postProcessCall = "postProcessCall";
    private static final Logger log = Logger.getLogger(ServicesBaseExecutor.class.getName());


    /**
     * Esto se ejecuta en el Remoto
     * @param codUniqOfService codigo unico de servicio
     * @param parameter parametro de entrada del processor a invocar
     * @param <Y> generico que representa la clase del parametro
     * @param <T> generico que representa la clase de la respuesta
     * @return la respuesta del caller o una instancia de {@link ErrorResponseImpl} al existir algun error
     * @throws Exception a alguna excepcion no capturada
     */
    public static <Y extends ProcessParameter, T extends Object> T executeProcessor(String codUniqOfService, Y parameter) throws Exception {
        ProcessorRegister register = ServicesFactory.getProcessorRegister(codUniqOfService);
        if (register == null) return (T) new ErrorResponseImpl("RPC-001", "El ProcessorRegister , no existe en el Servidor Remoto ".concat(MyMovilityRPCComm.getServerSystemName().getSystemName()));
        Class aClass = register.getProcessorClass();
        try {
            Method m = register.getMethod();
            int numParam = m.getParameterTypes().length;
            Object[] arrayParam = getArrayParam(register, m, numParam, parameter);
            callXXXProcess(aClass, /*arrayParam,*/ preProcessCall);
            T invoke = (T) m.invoke(null, arrayParam);
            callXXXProcess(aClass, /*arrayParam,*/ postProcessCall);
            return invoke;
        } catch (ServiceBaseException ex){
            return (T) new ErrorResponseImpl(ex.getKeyCode(), ex.getMessage());
        } catch (Exception e){
            T outExcp = null;
            if(e instanceof InvocationTargetException && ((InvocationTargetException) e).getTargetException() instanceof ServiceBaseException){
                ServiceBaseException te = ((ServiceBaseException)((InvocationTargetException) e).getTargetException());
                outExcp = (T) new ErrorResponseImpl(te.getKeyCode(), te.getMessage());
            }else if(e instanceof InvocationTargetException){
                outExcp = (T) new ErrorResponseImpl("RPC-999", ((InvocationTargetException) e).getTargetException().getMessage());
            }else{
                outExcp = (T) new ErrorResponseImpl("RPC-999", "Excepcion en la ejecucion del metodo ["+register.getMethodName()+"], en la clase ["+ aClass.getName()+"], mensaje "+e.getMessage());
            }
            return outExcp;
        }
    }
    /**
     * Este metodo crea y invoca al CallerOfProcess indicado
     * @param callerClass clase CallerOfProcess a utilizar
     * @param parameter parametro a utilizar
     * @param remote    SystemName remoto a llamar
     * @param <V>   clase que implementa a CallerOfProcess
     * @param <Y>   clase que implementa a ProcessParameter
     * @param <T>   clase que implementa a Object
     * @return  la respuesta del Servidor que es la clase que implementa a Object
     * @throws Exception a alguna excepcion no capturada
     */
    public static <V extends CallerOfProcess, Y extends ProcessParameter, T extends Object> T executeCalling(Class<V> callerClass, Y parameter, SystemName remote) throws ServiceBaseException {
        return (T) executeCalling(callerClass, parameter, remote, false);
    }
    /**
     * Este metodo crea y invoca al CallerOfProcess indicado
     * @param callerClass clase CallerOfProcess a utilizar
     * @param parameter parametro a utilizar
     * @param remote    SystemName remoto a llamar
     * @param isBase    Indica que es un Caller que es base para todos los sistemas
     * @param <V>   clase que implementa a CallerOfProcess
     * @param <Y>   clase que implementa a ProcessParameter
     * @param <T>   clase que implementa a Object
     * @return  la respuesta del Servidor que es la clase que implementa a Object
     * @throws Exception a alguna excepcion no capturada
     */
    public static <V extends CallerOfProcess, Y extends ProcessParameter, T extends Object> T executeCalling(Class<V> callerClass, Y parameter, @Nullable SystemName remote, boolean isBase) throws ServiceBaseException {
        return (T) CallerOfProcess.executeCalling(callerClass, parameter, remote, isBase);
    }

    /**
     *
     * @param callerClass clase CallerOfProcess a utilizar
     * @param parameter parametro a utilizar
     * @param remote SystemName remoto a llamar
     * @param callType tipo de llamado (sincrono o asincrono)
     * @param <V> clase que implementa a CallerOfProcess
     * @param <Y> clase que implementa a ProcessParameter
     * @param <T> clase que implementa a Object
     * @return la respuesta del Servidor que es la clase que implementa a Object
     * @throws ServiceBaseException
     */
    public static <V extends CallerOfProcess, Y extends ProcessParameter, T> T executeCalling(Class<V> callerClass, Y parameter, SystemName remote, CallType callType) throws ServiceBaseException {
        return (T) CallerOfProcess.executeCalling(callerClass, parameter, remote, callType, false);
    }

    private static void callXXXProcess(Class processorClass, /*Object[] arrayParam,*/ String methodName) {
        try {
            Class[] arrayClass = new Class[0]; //getClassArray(arrayParam); No puedo xq pueden existir mas de metodo en el processor x lo tanto algunos son metodos sin parametro
            Method m = processorClass.getDeclaredMethod(methodName, arrayClass);
            m.invoke(null, /*arrayParam*/new Object[0]);
        } catch (Exception e) {
            /*log.log(Level.INFO, "la clase ".concat(processorClass.getName()).concat(" no posee el metodo ".concat(methodName)).concat(" mensaje:").concat(e.getMessage()));*/
        }
    }

    private static Class[] getClassArray(Object[] arrayParam) {
        return arrayParam.length == 0 ? new Class[0] : new Class[]{arrayParam[0].getClass()};
    }

    private static <Y extends ProcessParameter> Object[] getArrayParam(ProcessorRegister register, Method m, int numParam, Y parameter) {
        boolean isVoid = parameter == null || parameter instanceof VoidParameter;
        if((numParam > 0 && isVoid)
                || (numParam > 1 && !isVoid)
                ||  (numParam > 0 && !m.getParameterTypes()[0].equals(parameter.getClass()))){
            throw new ServiceBaseException("RPC-000", "No se pudo ejecutar reflectivamente el processor [".concat(register.getProcessorClass().getName()).concat("], errado el Parametro enviado con clase [").concat(parameter==null? null : parameter.getClass().getName()).concat("], en el metodo ").concat(register.getMethodName()));
        }
        if(isVoid) return new Object[0];
        return new Object[]{parameter};
    }
}
