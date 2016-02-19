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
package com.enroquesw.mcs.comm.mobilityRPC;

import com.enroquesw.mcs.comm.mobilityRPC.client.MyMovilityRPCClient;
import com.enroquesw.mcs.comm.mobilityRPC.enums.SystemName;
import com.enroquesw.mcs.comm.mobilityRPC.server.MyMovilityRPCServer;
import com.enroquesw.mcs.comm.mobilityRPC.services.factory.CallerRegister;
import com.enroquesw.mcs.comm.mobilityRPC.services.factory.ProcessorRegister;
import com.googlecode.mobilityrpc.controller.MobilityController;
import com.googlecode.mobilityrpc.lib.com.esotericsoftware.minlog.Log;
import com.googlecode.mobilityrpc.network.Connection;
import com.googlecode.mobilityrpc.network.ConnectionId;
import com.googlecode.mobilityrpc.network.impl.ConnectionListenerInternal;
import com.googlecode.mobilityrpc.network.impl.ConnectionManagerImpl;
import com.googlecode.mobilityrpc.network.impl.tcp.TCPConnectionListener;
import com.googlecode.mobilityrpc.quickstart.EmbeddedMobilityServer;
import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

/**
 * <p>La Clase <code>MyMovilityRPCComm</code> centraliza la administracion</p>
 * <p>de la comunicacion de la Maquina Local con las Maquinas Remotas (Contexto de Comunicacion).</p>
 * <p>
 * Inicia las clases:
 * <lu>
 *     <li>{@link MyMovilityRPCServer}</li>
 *     <li>{@link MyMovilityRPCClient}</li>
 * </lu>
 * </p>
 *
 *
 * @author Julio Morales
 *
 */
public class MyMovilityRPCComm {
    static Map<String, ConnectionId> clients;
    static List<ProcessorRegister> proccesors = new ArrayList<ProcessorRegister>();
    static List<CallerRegister> callers = new ArrayList<CallerRegister>();
    static Thread myStartThread;
    static {
        Log.DEBUG = System.getProperty("debug.log.mobilityRPC") != null && Boolean.getBoolean(System.getProperty("debug.log.mobilityRPC"))? true : false;
    }

    /**
     * Inicializa al {@link MyMovilityRPCServer} y {@link MyMovilityRPCClient} y
     * estable la lista de Maquinas Remotas a utilizar en este contexto de Comunicacion
     *
     * @param hostIp Ip de la maquina Local a utilizar para escuchar o null para escuchar en Todas las Interfaces de Red
     * @param port  puerto a utilizar o null para utilizar {@link EmbeddedMobilityServer#DEFAULT_PORT}
     * @param clientsToCall Mapa por "Nombre personalizado de conexion" de Identificadores de EndPoints a utilizar (ver {@link ConnectionId})
     * @param serverSystemName El {@link SystemName} de la Maquina Local.
     * @param proccesorsX Lista de {@link ProcessorRegister} a registrar en la Maquina Local [Opcional]
     * @param callersX Lista de {@link CallerRegister} a registrar en la Maquina Local [Opcional]
     * @param myStartThreadIn {@link Thread} que inicio a este contexto de comunicacion
     * @throws Exception
     */
    public static void init(@Nullable String hostIp, @Nullable Integer port, @Nullable Map<String, ConnectionId> clientsToCall, @NotNull SystemName serverSystemName, @NotNull List<ProcessorRegister> proccesorsX, @NotNull List<CallerRegister> callersX, @Nullable Thread myStartThreadIn) throws Exception{
        try {
            proccesors = proccesorsX;
            callers = callersX;
            clients = clientsToCall;
            MyMovilityRPCServer.init(hostIp, port, serverSystemName);
            MyMovilityRPCClient.init(MyMovilityRPCServer.getController(), clientsToCall);
            myStartThread = myStartThreadIn;
        } catch (Exception e){
            Log.error("Error!!!!! ", e);
            throw e;
        }
    }

    /**
     * Inicializa al {@link MyMovilityRPCServer} y {@link MyMovilityRPCClient} y
     * estable la lista de Maquinas Remotas a utilizar en este contexto de Comunicacion
     *
     * @param hostIp Ip de la maquina Local a utilizar para escuchar o null para escuchar en Todas las Interfaces de Red
     * @param port  puerto a utilizar o null para utilizar {@link EmbeddedMobilityServer#DEFAULT_PORT}
     * @param clientsToCall Mapa por "Nombre personalizado de conexion" (ver : {@link SystemName#getSystemName()}) de Identificadores de EndPoints  a utilizar (ver {@link ConnectionId})
     * @param serverSystemName El {@link SystemName} de la Maquina Local.
     * @param myStartThread {@link Thread} que inicio a este contexto de comunicacion
     * @throws Exception
     */
    public static void init(@Nullable String hostIp, @Nullable Integer port, Map<String, ConnectionId> clientsToCall, @NotNull SystemName serverSystemName, @Nullable Thread myStartThread) throws Exception{
        init(hostIp, port, clientsToCall, serverSystemName, new ArrayList<ProcessorRegister>(), new ArrayList<CallerRegister>(), myStartThread);
    }
    /******************************************************************************************************************/
    // Metodos Utilitarios

    /**
     * Verifica si el Servidor esta en ejecucion
     * @return <code>true</code>: en ejecucion o <code>false</code>: no esta en ejecucion
     */
    public static boolean isServerRunning(){
        return MyMovilityRPCServer.isInit && MyMovilityRPCServer.isRunning();
    }

    /**
     * Obtiene el {@link TCPConnectionListener} asociado al controlador y conexion especificada
     * @param controller controlador
     * @param endPoint Conexion
     * @return <code>TCPConnectionListener</code> o listener de conexion asociado
     */
    public static TCPConnectionListener getConnListener(@NotNull MobilityController controller, @NotNull ConnectionId endPoint) { //Estos se registran en el servidor (escuchan)
        try {
            ConnectionManagerImpl connectionManager = (ConnectionManagerImpl) controller.getConnectionManager();
            Field map = connectionManager.getClass().getDeclaredField("incomingConnectionListeners");
            map.setAccessible(true);
            ConcurrentMap<ConnectionId, ConnectionListenerInternal> o = (ConcurrentMap<ConnectionId, ConnectionListenerInternal>) map.get(connectionManager);
            for (Map.Entry<ConnectionId, ConnectionListenerInternal> entry : o.entrySet()) {
                ConnectionId key = entry.getKey();
                if(key.equals(endPoint)) return (TCPConnectionListener) entry.getValue();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Realiza el Sleep del Thread en la cantidad de segundos indicados
     * @param numSeconds cantidad de segundos a sleep
     */
    public static void sleep(int numSeconds) {
        try {
            TimeUnit.SECONDS.sleep(numSeconds);
        } catch (InterruptedException e) {
            throw new IllegalStateException("Exception while sleeping for " + numSeconds + " seconds", e);
        }
    }

    /**
     * Verifica la conexion con la conexion indicada
     * @param remoteEndpointId El {@link ConnectionId} de la conexion a verificar
     * @return <code>true</code>: exitoso la conexion or <code>false</code>: fallo la conexion
     */
    public static boolean checkEndpoint(@NotNull ConnectionId remoteEndpointId) {
        Connection outConn = MyMovilityRPCClient.checkEndpoint(remoteEndpointId, new StringBuilder());
        boolean isOk = outConn != null;
        return isOk;
    }

    /**
     * Retorna el {@link SystemName} del Sistema Local en este contexto de Comunicacion
     * @return <code>SystemName</code>
     */
    public static SystemName getServerSystemName(){
        return MyMovilityRPCServer.serverSystemName;
    }

    /**
     * Retorna la Lista de Sistemas Remotos registrados en este contexto de Comunicacion
     * @return <code> Map< String, ConnectionId > </code> de los Sistemas Remotos
     *
     * @see SystemName#getSystemName()
     * @see ConnectionId
     */
    public static Map<String, ConnectionId> getClients() { return clients; }

    /**
     * Retorna la lista de Registros de {@link ProcessorRegister} registrados en este contexto de Comunicacion
     * @return <code>List < ProcessorRegister ></code>
     */
    public static List<ProcessorRegister> getProccesors() { return proccesors; }

    /**
     * Retorna la lista de Registros de {@link ProcessorRegister} registrados en este contexto de Comunicacion
     * @return <code>List < CallerRegister > </code>
     */
    public static List<CallerRegister> getCallers() { return callers; }

    /**
     * Destuye este Contexto de Comunicacion
     */
    public static void destroy(){
        MyMovilityRPCClient.destroy();
        MyMovilityRPCServer.destroy();
        callers = new ArrayList<CallerRegister>();
        proccesors = new ArrayList<ProcessorRegister>();
        clients = null;
        if(myStartThread != null) myStartThread.interrupt();
        else Log.info("This thread don't start with MyMovilityRPCCommThread ...");
    }
}
