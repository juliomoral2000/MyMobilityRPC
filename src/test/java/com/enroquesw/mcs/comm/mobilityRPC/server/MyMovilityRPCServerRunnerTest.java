package com.enroquesw.mcs.comm.mobilityRPC.server;

import com.enroquesw.mcs.comm.mobilityRPC.MyMovilityRPCCommTest;
import com.enroquesw.mcs.comm.mobilityRPC.enums.SystemName;
import com.googlecode.mobilityrpc.network.ConnectionId;
import org.junit.Before;
import org.junit.Test;

import java.util.Hashtable;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Julio on 18/01/2016.
 */
public class MyMovilityRPCServerRunnerTest {
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
        mapClients = MyMovilityRPCCommTest.getMapClients();
        isDebugEnabled = true;
        serverSystemName = SystemName.COTIZADOR;
        /****************************************************/
    }

    // @Test
    public void testMain() throws Exception {
        try {
            /*MyMovilityRPCCommRunner running = MyMovilityRPCCommRunner.startMyMovilityRPCCommRunner(hostIp, hostPort, mapClients, isDebugEnabled, serverSystemName);
            running.start();
            log.log(Level.INFO,"[MyMovilityRPCCommRunner] Inicio el contexto de MyMovilityRPCComm ");
            running.join();*/
        } catch (Exception e) {
            throw new Exception("Cant start de MovilityRPCServer ", e);
        }
    }

}