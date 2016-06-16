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
package com.enroquesw.mcs.comm.mobilityRPC.services.callable;

import com.enroquesw.mcs.comm.mobilityRPC.client.MyMovilityRPCClient;
import com.enroquesw.mcs.comm.mobilityRPC.enums.CallType;
import com.enroquesw.mcs.comm.mobilityRPC.enums.SystemName;
import com.enroquesw.mcs.comm.mobilityRPC.services.exception.ServiceBaseException;
import com.enroquesw.mcs.comm.mobilityRPC.services.factory.CallerRegister;
import com.enroquesw.mcs.comm.mobilityRPC.services.parameter.ProcessParameter;
import com.enroquesw.mcs.comm.mobilityRPC.services.result.ErrorResponse;
import com.enroquesw.mcs.comm.mobilityRPC.services.result.ProcessResponse;
import com.googlecode.mobilityrpc.network.ConnectionId;
import com.googlecode.mobilityrpc.protocol.pojo.ExecutionMode;
import com.googlecode.mobilityrpc.session.impl.MobilitySessionRPCImpl;
import com.sun.istack.internal.Nullable;

import java.lang.reflect.Constructor;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeoutException;

import static com.enroquesw.mcs.comm.mobilityRPC.services.ServicesBaseExecutor.executeProcessor;
import static com.enroquesw.mcs.comm.mobilityRPC.services.factory.ServicesFactory.getCallerRegister;
/**
 * La clase abstracta <code>CallerOfProcess</code> define el creador de llamadas:
 * <p>
 *     <lu>
 *         <li>V la clase que implementa la interfaz {@link CallerOfProcess} [Requerido Local/Cliente]</li>
 *         <li>Y la clase que implementa la interfaz {@link ProcessParameter} el cual es la entrada del CallProcessor, es Opcional tanto para el CallProcessor en la Maquina Remota como para el CallerOfProcess en la Maquina local [Opcional pero en Ambos - Crear una Implentacion Para void]</li>
 *         <li>T la clase [que implementa la interfaz {@link ProcessResponse}] el cual es la respuesta del CallProcessor, es Requerido tanto para el CallProcessor en la Maquina Remota como para el CallerOfProcess en la Maquina local [Requerido en Ambos]</li>
 *     </lu>
 *     <p>
 * @see <a href="com.googlecode.mobilityrpc.examples.BoomerangPattern">ejemplo BoomerangPattern</a>
 *
 * @author Julio Morales
 */
public abstract class CallerOfProcess<V extends CallerOfProcess, Y extends ProcessParameter, T /*extends Object FIXME_JULIO: extends ProcessResponse*/> implements Callable<V /*FIXME_JULIO: Aqui podria ser T asi no tendria que guardarlo como un campo y quitaria getResult()*/> {
    private Y parameter;
    private T result;
    private ErrorResponse error;
    private String codUniqOfService;
    private SystemName remote; // Esto para los casos que se tenga mas de un remoto manejado por el mismo caller/processor x ejemplos los comunes a todos los sistemas

    public CallerOfProcess(Y parameter) throws Exception {
        this.parameter = parameter;
        codUniqOfService = getCodUniqOfService(null);
    }

    public void setParameter(Y parameter){
        this.parameter = parameter;
    }

    public T getResult(){
        return this.result;
    }

    @SuppressWarnings({"unchecked"})
    public String getCodUniqOfService(SystemName remote) throws Exception {
        CallerRegister callerRegister = remote == null? getCallerRegister(this.getClass()) : getCallerRegister(remote, this.getClass()) ;
        if(callerRegister == null) {
            StringBuilder msg = new StringBuilder();
            if(MyMovilityRPCClient.checkEndpoint(MyMovilityRPCClient.getEndPointByRemoteName(remote.getSystemName()), msg) != null) throw new Exception(" El Caller "+this.getClass().getName()+" no esta Registrado ");
            throw new Exception("El Caller "+this.getClass().getName()+" no pudo ser invocado, msg : "+msg.toString());
        }
        return callerRegister.getCodUniqOfService();
    }

    public void setRemote(SystemName remote) throws Exception {
        this.remote = remote;
        this.codUniqOfService = getCodUniqOfService(remote);
    }

    public ErrorResponse getError() {
        return error;
    }

    @SuppressWarnings({"unchecked"})
    @Override
    public V call() throws Exception {
        Object o = executeProcessor(this.codUniqOfService, this.parameter);
        if(o instanceof ErrorResponse) this.error = (ErrorResponse) o;
        else this.result = (T) o;
        return (V) this;
    }

    @SuppressWarnings({"unchecked"})
    private static <V extends CallerOfProcess, Y extends ProcessParameter> V getInstance(Class<V> callerClass, Y parameter, StringBuffer msg){
        try {
            Constructor<?>[] declaredConstructors = callerClass.getDeclaredConstructors();
            if(declaredConstructors.length < 1 || declaredConstructors.length > 1) return null;
            Constructor ctor = declaredConstructors[0];
            ctor.setAccessible(true);
            Object[] param = {parameter};
            return (V) ctor.newInstance(param);
        } catch (Exception e) {
            msg.append(e.getMessage()==null? e.getCause().getMessage():e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @SuppressWarnings({"unchecked"})
    public static <V extends CallerOfProcess, Y extends ProcessParameter, T /*extends Object*/> T executeCalling(Class<V> callerClass, Y parameter, @Nullable SystemName remoteSystemName, boolean isBase) throws ServiceBaseException {
        return (T) executeCalling(callerClass, parameter, remoteSystemName, null, isBase);
    }

    @SuppressWarnings({"unchecked"})
    public static <V extends CallerOfProcess, Y extends ProcessParameter, T /*extends Object*/> T executeCalling(Class<V> callerClass, Y parameter, @Nullable SystemName remoteSystemName, CallType callType, boolean isBase) throws ServiceBaseException {
        if(remoteSystemName == null) throw new ServiceBaseException("RPC-500", "El Sistema Remoto no puede ser nulo para invocar a ".concat(callerClass.getSimpleName()));
        StringBuffer msg = new StringBuffer();
        V callerInstance = /*(V)*/ getInstance(callerClass, parameter, msg);
        ConnectionId connectionId = null;
        try {
            if(callerInstance == null) throw new Exception(msg.toString());
            if (!isBase && !SystemName.ALL.equals(remoteSystemName)){
                callerInstance.setRemote(remoteSystemName);
            }else if(isBase){
                callerInstance.setRemote(SystemName.ALL);
            }
        }catch (Exception e){
            throw new ServiceBaseException("RPC-501", "Error al obtener el registro del Sistema Remoto : "+ e.getMessage()!= null ? e.getMessage(): e.getCause().toString());
        }
        try {
            connectionId = MyMovilityRPCClient.getEndPointByRemoteName(remoteSystemName.getSystemName());
            ExecutionMode rr = callType != null? callType.getCallType() : ExecutionMode.RETURN_RESPONSE;
            V execute = executeRequest(connectionId, rr, callerInstance);
            if(rr.equals(ExecutionMode.FIRE_AND_FORGET)) return null;
            ErrorResponse error = execute.getError();
            if(error != null) throw new ServiceBaseException(error.getCodError(), error.getMessage());
            return (T) execute.getResult();
        } catch (ServiceBaseException e) {
            throw e;
        } catch (IllegalStateException e) {
            final String connStr = connectionId != null? connectionId.toString() : "connectionId is Null!!!";
            if(e.getCause() != null && e.getCause().getCause() != null && e.getCause().getCause() instanceof TimeoutException) throw new ServiceBaseException("RPC-504", "Se agoto el tiempo de espera para recibir la respuesta a traves de la conexion ".concat(connStr));
            if(e.getCause() != null && e.getCause().getCause() != null && e.getCause().getCause().getMessage() != null && e.getCause().getCause().getMessage().equals("Connection refused: connect")){
                throw new ServiceBaseException("RPC-502", "Conexión rechazada a ".concat(connStr));
            }
            throw new ServiceBaseException("RPC-503","Error desconocido :".concat(e.getMessage()));
        } catch (Exception ex){
            throw new ServiceBaseException("RPC-503","Error desconocido :".concat(ex.getMessage()));
        }
    }

    @SuppressWarnings({"unchecked"})
    private static <V extends CallerOfProcess> V executeRequest(ConnectionId connectionId, ExecutionMode rr, V callerInstance) {
        final Long timeOutMax = callerInstance.parameter.getTimeOutMax();
        return (timeOutMax != null && timeOutMax > 0)? (V) ((MobilitySessionRPCImpl) MyMovilityRPCClient.getSession()).execute(connectionId, rr, callerInstance, timeOutMax) : (V) MyMovilityRPCClient.getSession().execute(connectionId, rr, callerInstance);
    }
}
