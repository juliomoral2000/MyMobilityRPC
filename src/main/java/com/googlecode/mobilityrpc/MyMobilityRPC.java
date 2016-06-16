package com.googlecode.mobilityrpc;

import com.googlecode.mobilityrpc.controller.MobilityController;
import com.googlecode.mobilityrpc.controller.impl.MobilityControllerRPCImpl;

/**
 * <code>MyMobilityRPC</code> es la implementacion Personaliza de {@link MobilityRPC}.
 * @author Julio Morales
 */
public class MyMobilityRPC {
    /**
     * Returns a new {@link MobilityController} instance.
     * <p/>
     * Usually the application will want to hold on to this instance so that it can interact with the library
     * via the same controller.
     *
     * @return A new {@link MobilityController} instance
     */
    public static MobilityController newController() {
        return new MobilityControllerRPCImpl(); // Return the custom implementation...
    }

    /**
     * Private constructor, not used.
     */
    MyMobilityRPC() { }
}
