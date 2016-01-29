package com.enroquesw.mcs.comm.mobilityRPC.services.callable;

import java.net.InetAddress;
import java.util.Properties;
import java.util.concurrent.Callable;

/**
 * Created by Julio on 19/01/2016.
 */
public class BoomerangObject implements Callable<BoomerangObject> {

    private Properties someData;
    private InetAddress someOtherData;

    public BoomerangObject call() throws Exception {
        someData = System.getProperties();
        someOtherData = InetAddress.getLocalHost();
        return this;
    }

    public Properties getSomeData() {
        return someData;
    }

    public InetAddress getSomeOtherData() {
        return someOtherData;
    }
}