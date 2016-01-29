package com.enroquesw.mcs.comm.mobilityRPC.client.staticClass;

/**
 * Created by Julio on 19/01/2016.
 */
public class BenchmarkMultithreaded {
/*
    public static final transient int NUM_THREADS = 1;
    public static final transient int NUM_REQUESTS_PER_THREAD = 1; //100000;
    public static final transient int REQUEST_SIZE = 1;                  // number of objects to send in each request
*/

    /*public static void init(){
        MobilityBenchmark.main(null);
        //Log.debug("parate Aqui");
    }
    @SuppressWarnings("unchecked")
    public static <T extends Comparable> Collection<T> processRemotelyViaMobility(final Collection<T> input, MobilitySession session, ConnectionId connectionId) {
        Collection<T> execute = session.execute(connectionId, ExecutionMode.RETURN_RESPONSE,
                new Callable<Collection<T>>() {
                    public Collection<T> call() throws Exception {
                        return ServerServicesLogic.processRequest(input);
                    }
                }
        );
        return execute;
    }*/
}
