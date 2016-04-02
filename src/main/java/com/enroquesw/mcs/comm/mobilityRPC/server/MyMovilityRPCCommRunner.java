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
import com.enroquesw.mcs.comm.mobilityRPC.exceptions.MovilityRPCException;
import com.enroquesw.mcs.comm.mobilityRPC.services.factory.CallerRegister;
import com.enroquesw.mcs.comm.mobilityRPC.services.factory.ProcessorRegister;
import com.googlecode.mobilityrpc.lib.com.esotericsoftware.minlog.Log;
import com.googlecode.mobilityrpc.network.ConnectionId;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * La clase MyMovilityRPCCommRunner es el Thread encargado de Iniciar la ejecucion del {@link MyMovilityRPCComm}
 * Con los valores indicados de Direccion Local, Puerto Local, Lista de Maquinas Remotas
 * , el nivel de depuracion , el SystemName de la maquina local, la lista de ProcessorRegister y la lista de CallerRegister a utilizar en esta Maquina .
 *
 * @author Julio Morales
 */
public class MyMovilityRPCCommRunner extends Thread {
    Integer serverPort;
    Map<String, ConnectionId> clientsToCall;
    boolean activeDebug;
    SystemName serverSystemName;
    String serverIp;
    private List<ProcessorRegister> proccesors = new ArrayList<ProcessorRegister>();
    private List<CallerRegister> callers = new ArrayList<CallerRegister>();
    public static boolean isInit = false;           // Se inicio el proceso de Arranque del Servidor
    public static boolean isFail = false;           // Fallo el Arranque del Servidor
    public static boolean isFailClients = false;    // Fallo la comunicacion con alguno de las maquinas remotas
    public static boolean isRun = false;            // Inicio correctamente el servidor
    public static Map<String, String> errors = new HashMap<String, String>(); // Mapa que contiene los errores capturados en el proceso de Arranque del Servidor
    public static Map<String, String> warnings = new HashMap<String, String>(); // Mapa que contiene las advertencias capturados en el proceso de Arranque del Servidor y comunicacion con las maquinas remotas

    /**
     *
     * @param serverIp
     * @param serverPort
     * @param clientsToCall
     * @param activeDebug
     * @param serverSystemName
     * @return MyMovilityRPCCommRunner
     * @throws Exception
     */
    public static MyMovilityRPCCommRunner startMyMovilityRPCCommRunner(String serverIp, Integer serverPort, Map<String, ConnectionId> clientsToCall, boolean activeDebug, SystemName serverSystemName) throws Exception {
        return startMyMovilityRPCCommRunner(serverIp, serverPort, clientsToCall, activeDebug, serverSystemName, new ArrayList<ProcessorRegister>(), new ArrayList<CallerRegister>());
    }

    /**
     *
     * @param serverIp
     * @param serverPort
     * @param clientsToCall
     * @param activeDebug
     * @param serverSystemName
     * @param proccesors
     * @param callers
     * @return MyMovilityRPCCommRunner
     * @throws Exception
     */
    public static MyMovilityRPCCommRunner startMyMovilityRPCCommRunner(String serverIp, Integer serverPort, Map<String, ConnectionId> clientsToCall, boolean activeDebug, SystemName serverSystemName, List<ProcessorRegister> proccesors, List<CallerRegister> callers) throws Exception {
        try {
            MyMovilityRPCCommRunner running = new MyMovilityRPCCommRunner(serverIp, serverPort, clientsToCall, activeDebug, serverSystemName, proccesors, callers);
            running.start();
            return running;
        } catch (Exception e) {
            isFail = true;
            String code = "MMRCR-999"; String msg = "Error running MyMovilityRPCComm: "+e.getMessage();
            errors.put(code, msg);
            throw new Exception("Cant start de MovilityRPCServer ", e);
        }
    }

    private MyMovilityRPCCommRunner(String serverIp, Integer serverPort, Map<String, ConnectionId> clientsToCall, boolean activeDebug, SystemName serverSystemName, List<ProcessorRegister> proccesors, List<CallerRegister> callers) {
        super();
        System.setProperty("debug.log.mobilityRPC", String.valueOf(activeDebug));
        Log.DEBUG = activeDebug;
        this.serverIp = serverIp;this.serverPort = serverPort;this.clientsToCall = new ConcurrentHashMap<String, ConnectionId>(clientsToCall);this.activeDebug = activeDebug; this.serverSystemName=serverSystemName;this.proccesors=proccesors;this.callers=callers;
    }

    @Override
    public void run() {
        try {
            Thread t = Thread.currentThread();
            t.setName("MyMovilityRPCCommThread");
            Log.info("[MyMovilityRPCCommRunner] Inicio la ejecucion del MyMovilityRPCCommRunner, hilo ".concat(t.getName()));
            isInit = true;
            MyMovilityRPCComm.init(serverIp, serverPort, clientsToCall, serverSystemName, proccesors, callers, t);
            synchronized (t) {
                try {
                    while (!MyMovilityRPCServer.isInit || MyMovilityRPCServer.isRunning()) {
                        Thread.sleep(Long.MAX_VALUE);
                    }
                } catch (InterruptedException e) {
                    clean();
                    Log.info("Someone invoked destroy.....");
                }
            }
            Log.info("[MyMovilityRPCCommRunner] Aqui termino la ejecucion del MyMovilityRPCCommRunner ".concat(t.getName()));
        } catch (Exception e) {
            isFail = true;
            String code = "MMRCR-999";
            String msg = "Error running MyMovilityRPCComm: "+e.getMessage();
            if(e instanceof MovilityRPCException){
                code = ((MovilityRPCException)e).getKeyCode(); msg = ((MovilityRPCException)e).getMessage();
            }
            errors.put(code, msg);
            Log.error("Error running MyMovilityRPCComm: "+code+" - "+msg, e);
        }
    }


    public static void clean() {
        isInit = false;
        isFail = false;
        isRun = false;
        errors = new HashMap<String, String>();
        warnings = new HashMap<String, String>();
    }
}