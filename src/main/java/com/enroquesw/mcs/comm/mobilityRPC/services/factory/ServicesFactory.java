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

import com.enroquesw.mcs.comm.mobilityRPC.MyMovilityRPCComm;
import com.enroquesw.mcs.comm.mobilityRPC.enums.SystemName;
import com.enroquesw.mcs.comm.mobilityRPC.server.MyMovilityRPCCommRunner;
import com.enroquesw.mcs.comm.mobilityRPC.services.callable.CallerOfProcess;
import com.enroquesw.mcs.comm.mobilityRPC.services.impl.caller.ServicesFactory_Callers;
import com.enroquesw.mcs.comm.mobilityRPC.services.impl.result.ErrorResponseImpl;
import com.enroquesw.mcs.comm.mobilityRPC.services.parameter.ProcessParameter;
import com.enroquesw.mcs.comm.mobilityRPC.services.processor.CallProcessor;
import com.enroquesw.mcs.comm.mobilityRPC.services.result.ProcessResponse;
import com.googlecode.mobilityrpc.lib.com.esotericsoftware.minlog.Log;
import com.enroquesw.mcs.comm.mobilityRPC.services.impl.processor.ServicesFactory_Proccesors;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * La Clase <code>ServicesFactory</code> contiene el registro de los datos requeridos por
 * las implementaciones de los:<p>
 *     <lu>
 *         <li>{@link CallerOfProcess}</li>
 *         <li>{@link CallProcessor}</li>
 *     </lu>
 * </p>
 * <p>
 *     Es el registro central de los servicios.
 * </p>
 * @author Julio Morales
 */
public class ServicesFactory {
    public static final char CHAR_SEP = '|';
    private static ConcurrentMap<String, ProcessorRegister> processorsRegistry = new ConcurrentHashMap<String, ProcessorRegister>();   // registro de datos requeridos x el Processors (es el que procesa la llamada (Requets/invocacion Remota) en la Maquina Local y retorna un Valor o respuesta - En pocas palabras el servicio-metodo ) (Clases en la maquina Local que implementan al CallProcessor)
    private static ConcurrentMap<String, CallerRegister> callersRegistry = new ConcurrentHashMap<String, CallerRegister>();   // registro de datos requeridos x el Callers (es el que crea o realiza la llamada (Requets/invocacion Remota) en la Maquina local con cierto Parametro y captura la respuesta remotamente (contiene Result)) (Clases en la maquina Local que implementan al CallerOfProcess)

    public static void registerProcessor(ProcessorRegister register) throws Exception {
        if(processorsRegistry.containsKey(register.codUniqOfService)){
            Log.error(" Ya existe un registro con este Identificador " + register.codUniqOfService);
            throw new Exception(" Ya existe un registro con este Identificador "+register.codUniqOfService);
        }
        processorsRegistry.put(register.codUniqOfService, register);
        Log.info("Registrado: \n"+register.toString());
    }

    public static void registerCaller(CallerRegister register) throws Exception {
        if(callersRegistry.containsKey(register.codUniqOfService)){
            Log.error(" Ya existe un registro con este Identificador " + register.codUniqOfService);
            throw new Exception(" Ya existe un registro con este Identificador "+register.codUniqOfService);
        }
        callersRegistry.put(register.codUniqOfService, register);
        Log.info("Registrado: \n" + register.toString());
    }

    public static CallerRegister getCallerRegister(String codUniqOfService){
        return callersRegistry.get(codUniqOfService);
    }

    public static <V extends CallerOfProcess> CallerRegister getCallerRegister(Class<V> callerClass){
        List<CallerRegister> list = getCallerRegisterList(callerClass);
        if(list.isEmpty()) return null;
        if(list.size() > 1) Log.info("Existe mas de un registro para el caller "+callerClass.getName()+" se retornara el primero que se consigue");
        return list.get(0);
    }

    private static <V extends CallerOfProcess> List<CallerRegister> getCallerRegisterList(Class<V> callerClass){
        List<CallerRegister> list = new ArrayList<CallerRegister>();
        for (CallerRegister register : callersRegistry.values()) {
            if(register.getCallerClass().equals(callerClass)) list.add(register);
        }
        return list;
    }

    public static <V extends CallerOfProcess> CallerRegister getCallerRegister(SystemName remoteSystemName, Class<V> callerClass){
        List<CallerRegister> list = getCallerRegisterList(callerClass);
        if(list.isEmpty()) return null;
        for (CallerRegister register : list) {
            if(register.getRemoteLister().equals(remoteSystemName)) return register;
        }
        return null;
    }

    public static ProcessorRegister getProcessorRegister(String codUniqOfService){
        return processorsRegistry.get(codUniqOfService);
    }

    public static <W extends CallProcessor> List<ProcessorRegister> getProcessorRegister(Class<W> processorClass){
        List<ProcessorRegister> list = new ArrayList<ProcessorRegister>();
        for (ProcessorRegister register : processorsRegistry.values()) {
            if(register.getProcessorClass().equals(processorClass)) list.add(register);
        }
        return list;
    }

    public static <W extends CallProcessor> List<ProcessorRegister> getProcessorRegister(SystemName remoteSystemName, Class<W> processorClass){
        if(SystemName.ALL.equals(remoteSystemName)) return fetchProcessorRegistersFromServer(remoteSystemName);
        List<ProcessorRegister> list = new ArrayList<ProcessorRegister>();
        for (ProcessorRegister r : processorsRegistry.values()) {
            if(r.getRemoteCaller().equals(remoteSystemName) && r.getProcessorClass().equals(processorClass)) list.add(r);
        }
        return list;
    }

    public static <W extends CallProcessor> List<ProcessorRegister> getProcessorRegister(SystemName remoteSystemName, Class<W> processorClass, String methodName){
        if(SystemName.ALL.equals(remoteSystemName)) return fetchProcessorRegistersFromServer(remoteSystemName);
        List<ProcessorRegister> list = new ArrayList<ProcessorRegister>();
        for (ProcessorRegister r : processorsRegistry.values()) {
            if(r.getRemoteCaller().equals(remoteSystemName)
                    && r.getProcessorClass().equals(processorClass)
                    && r.getMethodName().equals(methodName)) list.add(r);
        }
        return list;
    }

    /**
     * Retornara la lista de ProcessorRegister que se encuentran registrado en la maquina Local/Servidor
     * asociados al sistema en la Maquina Remota/Cliente
     * [ lista los processors con sus metodos asociados asociados al sistema remoto/cliente].
     * @param remoteCaller Sistema Remoto/Cliente que llama para obtener los processors con sus metodos asociados registrados en la Maquina Local/Servidor
     * @return lista de ProcessorRegister
     */
    public static List<ProcessorRegister> fetchProcessorRegistersFromServer(SystemName remoteCaller){
        List<ProcessorRegister> out = new ArrayList<ProcessorRegister>();
        for (ProcessorRegister o : processorsRegistry.values()) {
            SystemName systemName = o.getRemoteCaller();
            if((systemName.compareTo(remoteCaller) == 0)|| (SystemName.ALL.equals(systemName))) out.add(o);
        }
        // NOTA los que se hallan registrado ALL son parte del Framework por lo tanto no los incluyo
        return out;
    }

    /**
     * Retornara la lista de CallerRegister que se encuentran implementados en la maquina Remota/Cliente
     * y en la Maquina Local/Servidor [en Ambas Maquinas - logica interseccion].
     * @param remoteCaller sistema remoto que llama para obtener los Caller en comun con el sistema local
     * @return lista de CallerRegister
     */
    public static <Y extends ProcessParameter> List<CallerRegister> fetchCallerRegistersFromServer (SystemName remoteCaller){
        List<CallerRegister> out = new ArrayList<CallerRegister>();
        for (CallerRegister o : callersRegistry.values()) {
            SystemName systemName = o.getRemoteLister();
            if((systemName.compareTo(remoteCaller) == 0) || (SystemName.ALL.equals(systemName))) out.add(o);
        }
        return out;
    }

    public static void registerProcessorsAndCallersBase() throws Exception {
        SystemName all = SystemName.ALL;
        /* Registro los Procesor de esta maquina que es para todos */
        ProcessorRegister regproc = new ProcessorRegister(all, all, "fetchProcessorRegistersFromServer", ServicesFactory_Proccesors.class, SystemName.class, List.class );
        registerProcessor(regproc);
        regproc = new ProcessorRegister(all, all, "fetchCallerRegistersFromServer", ServicesFactory_Proccesors.class, SystemName.class, List.class);
        registerProcessor(regproc);
        //... FIXME_JULIO: agregar los Otros
        //ServicesFactory.registerCaller() - esto no tiene mucho sentido invocarlo remotamentamente a menos que sea comun a todos el caller a registrar
        //ServicesFactory.registerProcessor() - esto no tiene mucho sentido invocarlo remotamentamente a menos que sea comun a todos el processor a registrar

        /* Registro los Caller de esta maquina */
        CallerRegister regCall = new CallerRegister(all, all, "fetchProcessorRegistersFromServer", "ServicesFactory_Proccesors", SystemName.class, List.class, ServicesFactory_Callers.FetchProcessorRegistersFromCaller.class );
        registerCaller(regCall);
        regCall = new CallerRegister(all, all, "fetchCallerRegistersFromServer", "ServicesFactory_Proccesors", SystemName.class, List.class, ServicesFactory_Callers.FetchCallerRegistersFromServer.class );
        registerCaller(regCall);
        //... FIXME_JULIO: agregar los Otros
        //ServicesFactory.registerCaller() - esto no tiene mucho sentido invocarlo remotamentamente a menos que sea comun a todos el caller a registrar
        //ServicesFactory.registerProcessor() - esto no tiene mucho sentido invocarlo remotamentamente a menos que sea comun a todos el processor a registrar
    }

    public static void registerProcessorsAndCallersfromList(List<ProcessorRegister> processors,List<CallerRegister> callers) throws Exception {
        for (ProcessorRegister register : processors) {
            try { registerProcessor(register); } catch (Exception e) { MyMovilityRPCCommRunner.warnings.put("WMMRCR-002","Error registrando ProcessorRegister: "+register.getMethodName()+", "+e.getMessage()); Log.debug("Fallo el registro !!!!"); }
        }
        for (CallerRegister register : callers) {
            try { registerCaller(register); } catch (Exception e) { MyMovilityRPCCommRunner.warnings.put("WMMRCR-003","Error registrando CallerRegister: "+register.getMethodName()+", "+e.getMessage()); Log.debug("Fallo el registro !!!!"); }
        }
    }

    public static void destroy() {
        processorsRegistry = new ConcurrentHashMap<String, ProcessorRegister>();
        callersRegistry = new ConcurrentHashMap<String, CallerRegister>();
        Log.info("\nServiceFactory destroy. ");
    }

    public static <W extends CallProcessor> ProcessResponse validateRemoteSystemName(SystemName remote, Class<W> processorsClass, String methoName) {
        List<ProcessorRegister> list = getProcessorRegister(remote, processorsClass, methoName);
        if(list.size() == 0) return new ErrorResponseImpl("RPC-001", "El ProcessorRegister del metodo "+methoName+", no existe en el Servidor Remoto "+ MyMovilityRPCComm.getServerSystemName().getSystemName()+" para el Cliente "+remote.getSystemName());
        if(list.size() > 1) return new ErrorResponseImpl("RPC-002", "Existe mas de un ProcessorRegister para el metodo "+methoName+", en el Servidor Remoto "+ MyMovilityRPCComm.getServerSystemName().getSystemName()+" para el Cliente "+remote.getSystemName());
        return list.get(1);
    }

    //FIXME_JULIO: Implementar
    /*public static void registerProcessorsAndCallersfromXML() throws Exception {

    }*/
}
