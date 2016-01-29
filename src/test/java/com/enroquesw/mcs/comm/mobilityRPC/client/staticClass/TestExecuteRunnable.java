package com.enroquesw.mcs.comm.mobilityRPC.client.staticClass;

import com.enroquesw.mcs.comm.mobilityRPC.services.callable.BoomerangObject;
import com.enroquesw.mcs.comm.mobilityRPC.MyMovilityRPCComm;
import com.enroquesw.mcs.comm.mobilityRPC.client.MyMovilityRPCClientTest;
import com.googlecode.mobilityrpc.lib.com.esotericsoftware.minlog.Log;
import com.googlecode.mobilityrpc.network.ConnectionId;
import com.googlecode.mobilityrpc.protocol.pojo.ExecutionMode;
import com.googlecode.mobilityrpc.quickstart.QuickTask;
import com.googlecode.mobilityrpc.session.MobilitySession;

import javax.swing.*;
import java.awt.*;

/**
 * Clase con metodos estaticos de Pruebas.
 */
public class TestExecuteRunnable {

    public static void submitRunnable_External(MobilitySession session, ConnectionId remoteEndpointId) {
        //Test 3 : OK perfecto lo hace
        session.execute(remoteEndpointId, ExecutionMode.RETURN_RESPONSE,
                new Runnable() {
                    //@Override
                    public void run() {
                        System.out.println("Hello World !! ");
                        JFrame window = new JFrame("Hello Remote World");
                        window.setSize(600, 400);
                        JLabel label = new JLabel("<html><center><font size=+6><b>This is a test</b></font></center></html>");
                        window.getContentPane().setLayout(new BorderLayout());
                        window.getContentPane().add(label, BorderLayout.CENTER);
                        label.setHorizontalAlignment(SwingConstants.CENTER);
                        window.setLocationRelativeTo(null);
                        window.setVisible(true);
                        window.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
                        /*try {
                            Thread.sleep(100000000);
                        } catch (InterruptedException ignore) {
                        }
                        window.dispose();*/
                    }
                }
        );
    }

    public static void submitRunnable_RegularObjectMigration_QuickTask(ConnectionId remoteEndpointId) {
        // OK pasando el remoteEndpointId a un nuevo controller y una nueva session (eso lo hace el QuickTask)
        final MyMovilityRPCClientTest.RegularObject regularObject = new MyMovilityRPCClientTest.RegularObject();
        Runnable runnable = new Runnable() {
            public void run() {
                regularObject.printDetails();
            }
        };
        QuickTask.execute(remoteEndpointId, runnable);  // Este crea un nuevo controlador y crea una nueva session, al final lo destruye
    }

    public static void submitRunnable_RegularObjectMigration(MobilitySession session, ConnectionId remoteEndpointId) {
        if(!MyMovilityRPCComm.checkEndpoint(remoteEndpointId)){
            Log.debug("No se pudo conectar a "+remoteEndpointId);
            return;
        }
        // ok seleccionando una nueva session desde el controller y pasando directamente al runnable
        final MyMovilityRPCClientTest.RegularObject regularObject = new MyMovilityRPCClientTest.RegularObject();
        Runnable runnable = new Runnable() {
            public void run() {
                regularObject.printDetails();
            }
        };
        session.execute(remoteEndpointId, runnable);
    }

    public static void submitCallable_BenchmarkMultithreaded() {
        //BenchmarkMultithreaded.init();
    }

    public static void submitCallable_BoomerangPattern(MobilitySession session, ConnectionId remoteEndpointId) {

        BoomerangObject boomerangObject = session.execute(remoteEndpointId, ExecutionMode.RETURN_RESPONSE, new BoomerangObject());
        //BoomerangObject boomerangObject = QuickTask.execute( remoteEndpointId, new BoomerangObject());
        System.out.println(boomerangObject.getSomeData());
        System.out.println(boomerangObject.getSomeOtherData());

    }
    public static void submitCallable_BoomerangPattern_Products(MobilitySession session, ConnectionId remoteEndpointId) {
        BoomerangObject boomerangObject = session.execute(remoteEndpointId, ExecutionMode.RETURN_RESPONSE, new BoomerangObject());
        //BoomerangObject boomerangObject = QuickTask.execute( remoteEndpointId, new BoomerangObject());
        System.out.println(boomerangObject.getSomeData());
        System.out.println(boomerangObject.getSomeOtherData());

    }
}
