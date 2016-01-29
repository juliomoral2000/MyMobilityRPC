package com.enroquesw.mcs.comm.mobilityRPC.services.callable;

/**
 * Created by Julio on 19/01/2016.
 */
public class BenchmarkTask /*implements Callable<Collection<? extends Comparable>> */{
   /* private final transient MobilitySession session;
    private final transient ConnectionId connectionId;
    *//*private final transient AtomicLong numIterations ;
    private final transient AtomicLong numObjectsSent ;
    private final transient AtomicLong sumOfLatencyNanos ;*//*

    public static BenchmarkTask instance(MobilitySession session, ConnectionId connectionId*//*, AtomicLong numIterations, AtomicLong numObjectsSent, AtomicLong sumOfLatencyNanos*//*){
        return new BenchmarkTask(session, connectionId*//*, numIterations, numObjectsSent, sumOfLatencyNanos*//*);
    }


    public BenchmarkTask(MobilitySession session, ConnectionId connectionId*//*, AtomicLong numIterations, AtomicLong numObjectsSent, AtomicLong sumOfLatencyNanos*//*) {
        super();
        this.session = session;
        this.connectionId = connectionId;
        *//*this.numIterations = numIterations;
        this.numObjectsSent = numObjectsSent;
        this.sumOfLatencyNanos = sumOfLatencyNanos;*//*
    }

    @Override
    public Collection<? extends Comparable> call() {
        Collection<? extends Comparable> input = Util.createCollection(*//*BenchmarkMultithreaded.REQUEST_SIZE*//*1);
        Collection<? extends Comparable> output = null;
        long startTime = System.nanoTime();
        for (int iterationNumber = 0; iterationNumber < *//*BenchmarkMultithreaded.NUM_REQUESTS_PER_THREAD*//*1; iterationNumber++) {
            output = BenchmarkMultithreaded.processRemotelyViaMobility(input, session, connectionId);
        }
        long timeTakenNanos = System.nanoTime() - startTime;
        System.out.println("[BenchmarkTask] timeTakenNanos: "+timeTakenNanos);
        *//*numIterations.addAndGet(BenchmarkMultithreaded.NUM_REQUESTS_PER_THREAD);
        numObjectsSent.addAndGet(BenchmarkMultithreaded.REQUEST_SIZE * BenchmarkMultithreaded.NUM_REQUESTS_PER_THREAD);
        sumOfLatencyNanos.addAndGet(timeTakenNanos);*//*
        return output;
    }*/
}
