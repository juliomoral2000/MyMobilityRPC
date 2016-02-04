package com.enroquesw.mcs.comm.mobilityRPC.client;

import com.enroquesw.mcs.comm.mobilityRPC.client.staticClass.TestExecuteRunnable;
import com.enroquesw.mcs.comm.mobilityRPC.enums.SystemName;
import com.enroquesw.mcs.comm.mobilityRPC.server.MyMovilityRPCCommRunner;
import com.enroquesw.mcs.comm.mobilityRPC.services.callable.BoomerangObject;
import com.enroquesw.mcs.comm.mobilityRPC.services.impl.caller.ServicesFactory_Callers;
import com.esotericsoftware.minlog.Log;
import com.googlecode.mobilityrpc.network.ConnectionId;
import com.googlecode.mobilityrpc.quickstart.QuickTask;
import com.googlecode.mobilityrpc.session.MobilitySession;
import com.enroquesw.mcs.comm.mobilityRPC.MyMovilityRPCComm;
import com.enroquesw.mcs.comm.mobilityRPC.MyMovilityRPCCommTest;
import com.enroquesw.mcs.comm.mobilityRPC.services.factory.CallerRegister;
import com.enroquesw.mcs.comm.mobilityRPC.services.factory.ProcessorRegister;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Pruebas Unitarias de invocaciones a una Maquina Remoto o Servidor Remoto.
 *
 * @author Julio Morales
 */
public class MyMovilityRPCClientTest {
    private transient static final Logger log = Logger.getLogger(MyMovilityRPCClientTest.class.getName());
    private transient MobilitySession session;
    private transient ConnectionId remoteEndpointId;
    private transient String hostIp;
    private transient Integer hostPort;
    private transient Map<String, ConnectionId> mapClients = new Hashtable<String, ConnectionId>();
    private transient boolean isDebugEnabled;
    private SystemName serverSystemName;

    @Before
    public void setUp() throws Exception {
        mapClients = MyMovilityRPCCommTest.getMapClients();
        hostIp = "127.0.0.1";   // LocalIp
        hostPort = 5749;        //LocalPort
        isDebugEnabled = true;
        serverSystemName = SystemName.COTIZADOR;
        /****************************************************/
        /*MyMovilityRPCCommRunner thread = */
        MyMovilityRPCCommRunner.startMyMovilityRPCCommRunner(hostIp, hostPort, mapClients, isDebugEnabled, serverSystemName);
        while(!MyMovilityRPCComm.isServerRunning()) MyMovilityRPCComm.sleep(1); // Esperamos x que inicie el servidor
        remoteEndpointId = MyMovilityRPCClient.getEndPointByRemoteName("Acsel-e");
        /****************************************************/
        /****************************************************/
        session = MyMovilityRPCClient.controller.newSession();
        /****************************************************/
    }

    @After
    public void tearDown() throws Exception {
        if(MyMovilityRPCComm.isServerRunning()) MyMovilityRPCComm.destroy();

    }

    @Test
    public void testMain() throws Exception {
        //TestExecuteRunnable.submitCallable_BoomerangPattern(session, remoteEndpointId);
        //test_BoomerangPattern();
        test_ServicesFactoryProccesor();
        //MyMovilityRPCComm.sleep(10);
        log.log(Level.INFO, "Parate Aqui");
    }

    private void test_ServicesFactoryProccesor() throws Exception {
        List<CallerRegister> list = ServicesFactory_Callers.fetchCallerRegistersFromServer(null);
        Log.debug("" + list.size());
        List<ProcessorRegister> lis_t = ServicesFactory_Callers.fetchProcessorRegistersFromServer(null);
        Log.debug("" + lis_t.size());
    }

    private void test_BoomerangPattern() {
        BoomerangObject boomerangObject = QuickTask.execute(hostIp, new BoomerangObject());
        System.out.println(boomerangObject.getSomeData());
        System.out.println(boomerangObject.getSomeOtherData());
    }

    /****************************************************************/
    public static class RegularObject {
        private String name = "Julio Morales";
        private String address = "Camino Real 1030";

        public void printDetails() {
            System.out.println(name);
            System.out.println(address);
        }
    }


}
    /*@Test
    public void testMain() throws Exception {
        //TestExecuteRunnable.submitRunnable_RegularObjectMigration_QuickTask(remoteEndpointId);
        //MyMovilityRPCComm.sleep(10);
        //TestExecuteRunnable.submitRunnable_RegularObjectMigration(session, remoteEndpointId);
        //TestExecuteRunnable.submitRunnable_External(session, remoteEndpointId);
        //TestExecuteRunnable.submitCallable_BenchmarkMultithreaded();
        TestExecuteRunnable.submitCallable_BoomerangPattern(session, remoteEndpointId);
        //test_BoomerangPattern();
        MyMovilityRPCComm.sleep(10);
        log.log(Level.INFO, "Parate Aqui");
    }*/