package com.enroquesw.mcs.comm.mobilityRPC;

import com.enroquesw.mcs.comm.mobilityRPC.enums.SystemName;
import com.enroquesw.mcs.comm.mobilityRPC.server.MyMovilityRPCCommRunner;
import com.googlecode.mobilityrpc.network.ConnectionId;
import com.googlecode.mobilityrpc.quickstart.EmbeddedMobilityServer;
import org.junit.Before;
import org.junit.Test;

import java.util.Hashtable;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Julio on 18/01/2016.
 */
public class MyMovilityRPCCommTest {

    private final Logger log = Logger.getLogger(getClass().getName());
    private String hostIp;
    private Integer hostPort;
    private Map<String, ConnectionId> mapClients = new Hashtable<String, ConnectionId>();
    private boolean isDebugEnabled;
    private SystemName serverSystemName;

    @Before
    public void setUp() throws Exception {
        /****************************************************/
        hostIp = "127.0.0.1";
        hostPort = 5749;
        mapClients = getMapClients();
        isDebugEnabled = true;
        serverSystemName = SystemName.COTIZADOR;
        /****************************************************/
    }

    // @Test
    public void testMain() throws Exception {
        Thread t = Thread.currentThread();
        MyMovilityRPCCommRunner thread = MyMovilityRPCCommRunner.startMyMovilityRPCCommRunner(hostIp, hostPort, mapClients, isDebugEnabled, serverSystemName);
        log.log(Level.INFO, "Espero por la finalizacion del Hilo "+thread.getName());
        thread.join();
        System.out.println("[" + t.getName() + "] All threads have finished.");
    }

    public static Map<String, ConnectionId> getMapClients() {
        Map<String, ConnectionId> clients = new Hashtable<String, ConnectionId>();
        clients.put(SystemName.ACSELE.getSystemName(), new ConnectionId("127.0.0.1", EmbeddedMobilityServer.DEFAULT_PORT));
        return clients;
    }
}