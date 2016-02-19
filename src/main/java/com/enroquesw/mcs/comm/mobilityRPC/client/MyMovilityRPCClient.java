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
package com.enroquesw.mcs.comm.mobilityRPC.client;

import com.enroquesw.mcs.comm.mobilityRPC.server.MyMovilityRPCServer;
import com.googlecode.mobilityrpc.controller.MobilityController;
import com.googlecode.mobilityrpc.lib.com.esotericsoftware.minlog.Log;
import com.googlecode.mobilityrpc.network.Connection;
import com.googlecode.mobilityrpc.network.ConnectionId;
import com.googlecode.mobilityrpc.network.ConnectionManager;
import com.googlecode.mobilityrpc.network.impl.tcp.TCPConnection;
import com.googlecode.mobilityrpc.session.MobilityContext;
import com.googlecode.mobilityrpc.session.MobilitySession;
import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;

import java.lang.reflect.Field;
import java.net.Socket;
import java.util.Map;

/**
 * Clase para administrar las conexiones remotas (Salidas),
 * utilizando la misma instancia de {@link MobilityController} contenida en {@link MyMovilityRPCServer},
 * almacena un Mapa con referencia a las conexiones salientes ({@link MyMovilityRPCClient#remoteMap})
 * la cual se fija al invocar {@link MyMovilityRPCClient#init(MobilityController, Map)},
 * adicional define la instancia de {@link MobilitySession} a ser utilizado para las todas las [conexiones salientes/invocaciones remotas].
 *
 * @author Julio Morales
 */
public class MyMovilityRPCClient {
    /** Controlador a ser utilizado en la comunicacion (Local(ServidorLocal)/Remoto(ServidorRemoto)) - El mismo del MyMovilityRPCServer (ver {@link MobilityController}) */
    static MobilityController controller = null;
    /** Mapa por "Nombre personalizado de conexion" de Identificadores de EndPoints para una conexion (ver {@link ConnectionId}) */
    static Map<String, ConnectionId> remoteMap;
    /** Sesion a ser utilizada por las invocaciones a las maquinas Remotas o Servidores Remotos (ver {@link MobilitySession})*/
    static MobilitySession session = null;

    /**
     * Inicia un nuevo contexto de ejecucion del MyMovilityRPCClient (Conexion remotas - invocacion a Remotos)
     * Mantiene una referencia a la instancia de {@link MobilityController}.
     * Verifica la conexion con los Servidores Remotos.
     * Obtiene una Referencia a una instancia de {@link MobilitySession} a ser utilizado para realizar las invocaciones Remotas
     *
     * @param controllerServer instancia de {@link MobilityController} a utilizar
     * @param endPointsToCall  Mapa por "Nombre personalizado de conexion" de Identificadores de EndPoints a utilizar (ver {@link ConnectionId})
     * @throws Exception
     */
    public static synchronized void init(@NotNull MobilityController controllerServer, @NotNull Map<String, ConnectionId> endPointsToCall)  throws Exception {
        controller = controllerServer;
        remoteMap = endPointsToCall;
        checkEndpoints(remoteMap);
        session = controller.newSession();
    }

    /**
     * Verifica la conexion con las conexiones indicadas
     * @param remoteMap <code>Map<String, ConnectionId></code>, mapa de conexiones remotas por nombre de Sistema Remoto
     */
    public static void checkEndpoints(@NotNull Map<String, ConnectionId> remoteMap) {
        StringBuilder msg = new StringBuilder();
        for (Map.Entry<String, ConnectionId> entry : remoteMap.entrySet()) {
            Connection outConn = checkEndpoint(entry.getValue(), msg);
            if (outConn != null) {
                int localPort = getLocalPort(outConn);
                Log.info("Ok! testing conecction to ".concat(entry.getKey()).concat(" connectionId: ").concat(entry.getValue().toString()).concat(" localPort :").concat(String.valueOf(localPort)));
                if(outConn != null) ((TCPConnection) outConn).destroy();
            } else {
                Log.info("FAILED! testing conecction to ".concat(entry.getKey()).concat(" connectionId: ").concat(entry.getValue().toString()).concat(", cause: ").concat(msg.toString()));
            }
        }
    }

    /**
     * Verifica la conexion con la conexion indicada, si se estable retorna la {@link Connection} asociada
     * en caso contrario el mensaje de error se agregara a la variable <code>msg</code>.
     * @param endPoint <code>ConnectionId</code> del Sistema Remoto
     * @param msg contendra el mensaje de error si no se pudo establecer la conexion
     * @return <code>Connection</code> or <code>null</code>
     */
    public static Connection checkEndpoint(@NotNull ConnectionId endPoint, @NotNull StringBuilder msg) {
        try {
            ConnectionManager connM = controller.getConnectionManager();              // El administrador de Conexiones
            Connection outConn = connM.getConnection(endPoint);
            Log.info("OK! checking endPoint ".concat(" connectionId: ").concat(endPoint.toString()).concat(" localPort :").concat(String.valueOf(getLocalPort(outConn))));
            return outConn;
        } catch (Exception e) { //if(e.getCause().getClass().equals(java.net.ConnectException.class)){
            Log.info("FAILED! checking endPoint ".concat(" connectionId: ").concat(endPoint.toString()).concat(", cause: ").concat(e.getMessage()));
            msg.append(e.getCause().getMessage());
        }
        return null;
    }

    /**
     * <p>Obtiene el {@link ConnectionId} asociado al nombre del sistema Remoto, si se encuentra registrado en este contexto de comunicacion</p>
     * @param remoteName Nombre del Sistema Remoto
     * @return <code>ConnectionId</code> o <code>null</code>
     *
     * @see com.enroquesw.mcs.comm.mobilityRPC.enums.SystemName
     */
    public static ConnectionId getEndPointByRemoteName(@Nullable String remoteName){
        if(remoteMap == null || !remoteMap.containsKey(remoteName)) return null;
        return remoteMap.get(remoteName);
    }

    /**
     * Retorna la {@link MobilitySession} asociado a este contexto de Comunicacion
     * @return <code>MobilitySession</code>
     */
    public static MobilitySession getSession() {
        return session;
    }

    /**
     * Retorna la conexion Local asociado a una conexion saliente
     * @param connOut <code>Connection</code> la conexion saliente
     * @return el numero de puerto local asociado
     */
    public static int getLocalPort(@NotNull Connection connOut)  {
        try {
            TCPConnection connInner = (TCPConnection) connOut;
            Field socket = connInner.getClass().getDeclaredField("socket");
            socket.setAccessible(true);
            Socket o = (Socket) socket.get(connInner);
            return o.getLocalPort();
        } catch (Exception e) {
            Log.error(e.getMessage());
        }
        return 0;
    }

    /**
     * Destruye el Contexto del MyMovilityRPCClient
     */
    public static void destroy(){
        remoteMap = null;
        controller = null;
        if(session != null){
            session.release();
            session = null;
        }
        Log.info("\nMobility-RPC Client stopped ");
    }

    /**
     * Clase utilitaria para pruebas
     */
    public static class SessionReleasingRunnable implements Runnable {
        private final Runnable wrapped;
        SessionReleasingRunnable(Runnable wrapped) { this.wrapped = wrapped; }
        @Override public void run() { try { wrapped.run(); } finally { MobilityContext.getCurrentSession().release();} }
    }
}
