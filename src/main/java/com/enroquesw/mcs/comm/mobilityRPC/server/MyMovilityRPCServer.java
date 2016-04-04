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
package com.enroquesw.mcs.comm.mobilityRPC.server;

import com.enroquesw.mcs.comm.mobilityRPC.MyMovilityRPCComm;
import com.enroquesw.mcs.comm.mobilityRPC.enums.SystemName;
import com.enroquesw.mcs.comm.mobilityRPC.exceptions.MovilityRPCServerException;
import com.enroquesw.mcs.comm.mobilityRPC.services.factory.ServicesFactory;
import com.googlecode.mobilityrpc.MobilityRPC;
import com.googlecode.mobilityrpc.controller.MobilityController;
import com.googlecode.mobilityrpc.lib.com.esotericsoftware.minlog.Log;
import com.googlecode.mobilityrpc.network.ConnectionId;
import com.googlecode.mobilityrpc.quickstart.EmbeddedMobilityServer;
import com.googlecode.mobilityrpc.quickstart.util.NetworkUtil;
import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase que Representa La Maquina Local o Servidor Local,
 * crea y almacena una instancia de {@link MobilityController},
 * crea el Listener o conexion entrante (ver {@link com.googlecode.mobilityrpc.network.impl.tcp.TCPConnectionListener});
 * que por defecto escucha en todos las interfaces de red local y por el puerto {@link EmbeddedMobilityServer#DEFAULT_PORT},
 * o por la ip y puerto indicados al invocar {@link #init(String, Integer, SystemName)} los cuales sean diferentes de nulo o vacio.
 *
 * @author Julio Morales
 */
public class MyMovilityRPCServer {
    static MobilityController controller;
    static List<String> bindAddresses = new ArrayList<String>();
    static Thread myShutdownHook;
    public static SystemName serverSystemName;
    public static boolean isInit = false;

    /**
     * <p>Inicia un nuevo contexto de ejecucion del MovilityRPC (Servidor Local - Listener local)
     * Crea una nueva instancia de {@link MobilityController}.
     * Abre un oyente en el equipo local para recibir conexiones entrantes desde otras máquinas
     * que por defecto escucha en todos las interfaces de red local
     * y por el puerto 5739 , o si se indica la dirección (host/port) podría ser
     * localhost para recibir sólo las conexiones desde otros procesos en la máquina local,
     * o podría ser el dirección IP o nombre de host del equipo local en su red.
     * <p/>
     *
     * @param host  null or Ip
     * @param port  null or puerto
     * @param ssn "Nombre del Sistema" que representara este servidor.
     * @return Una nueva instancia de {@link MobilityController}
     * @throws Exception
     */
    public static MobilityController init(@Nullable String host, @Nullable Integer port, @NotNull SystemName ssn) throws Exception {
        if (controller == null) {
            serverSystemName = ssn;
            createController(host, port);
            return controller;
        } else {
            MyMovilityRPCCommRunner.warnings.put("WMMRCR-004","Server is already running");
            throw new IllegalStateException("Server is already running");
        }
    }

    private static List<String> getBindAddressesByHost(@Nullable String hostIp) throws Exception {
        List<String> list = NetworkUtil.getAllNetworkInterfaceAddresses();
        if (hostIp == null || hostIp.length() == 0) return list;
        if (!list.contains(hostIp))
            throw new MovilityRPCServerException("MMRCR-001", "Don't exist in List of Network Interface Addresses , " + hostIp);
        ArrayList<String> returno = new ArrayList<String>();
        returno.add(hostIp);
        return returno;
    }

    private static MobilityController createController(@Nullable String hostIp, @Nullable Integer port) throws Exception {
        if (controller != null) throw new IllegalStateException("Server is already running");
        MobilityController mobilityController = MobilityRPC.newController(); // Create a new MobilityController...
        int innerport = port == null || port == 0 ? EmbeddedMobilityServer.DEFAULT_PORT : port;
        bindAddresses = getBindAddressesByHost(hostIp);
        // bind to network interfaces...
        for (String networkAddress : bindAddresses) {
            mobilityController.getConnectionManager().bindConnectionListener(new ConnectionId(networkAddress, innerport));
        }
        controller = mobilityController;
        createShutdownHook(controller);
        Log.info("Mobility-RPC Server started, listening on port " + innerport + " on the following addresses:");
        for (String networkAddress : bindAddresses) Log.info(networkAddress);
        //MyMovilityRPCCommRunner.isRun = true;
        try {
            ServicesFactory.registerProcessorsAndCallersBase(); // Igual registro los servicios aunque el cliente no pueda contactar a todos los servidores
            ServicesFactory.registerProcessorsAndCallersfromList(MyMovilityRPCComm.getProccesors(), MyMovilityRPCComm.getCallers()); //registro los definidos por el usuario/sistema externo
        } catch (Exception e) {
            Log.error("Error registrando los servicios Base !!!!", e);
            MyMovilityRPCCommRunner.warnings.put("WMMRCR-001","Error registrando los servicios Base: "+e.getMessage());
        }
        isInit = true;
        return controller;
    }

    private static void createShutdownHook(@NotNull final MobilityController controller) {
        Log.debug("creating ShutdownHook");
        if (myShutdownHook == null) {
            myShutdownHook = new Thread() {
                @Override
                public void run() {
                    //System.out.println("\nMobility-RPC Server stopped / System.out.println");
                    if (controller != null) MyMovilityRPCServer.destroy();
                    else Log.info("\nMobility-RPC Server stopped ");
                }
            };
            Runtime.getRuntime().addShutdownHook(myShutdownHook);
        }
    }

    /**
     * Retorna el controlador utilizado en este contexto de Comunicacion
     * @return <code>MobilityController</code>
     */
    public static MobilityController getController() {
        return controller;
    }

    /**
     * Retorna la lista de ips de las interfaces de Red utilizadas por este Servidor (escuchas)
     * @return <code>List<String></code>
     */
    public static List<String> getBindAddresses() {
        return bindAddresses;
    }

    /**
     * Destruye el Contexto del MyMovilityRPCServer
     */
    public static synchronized void destroy() {
        MobilityController mobilityController = controller;
        if (mobilityController != null) {
            ServicesFactory.destroy();
            mobilityController.destroy();
            controller = null;
            bindAddresses = new ArrayList<String>();
            MyMovilityRPCCommRunner.clean();
            Log.info("\nMobility-RPC Server stopped ");
        }
    }

    /**
     * Verifica si el Servidor esta en ejecucion
     * @return <code>true</code> or <code>false</code>
     */
    public static synchronized boolean isRunning() {
        return controller != null && MyMovilityRPCServer.isInit/*&& controller.getConnectionManager() != null*/;
    }
}

