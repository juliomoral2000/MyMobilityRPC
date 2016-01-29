package com.enroquesw.mcs.comm.mobilityRPC.client.staticClass;

/**
 * Created by Julio on 19/01/2016.
 */
public class MobilityBenchmark {
    /*public static void main(String[] args) {
        try {
            final MobilitySession session = MyMovilityRPCClient.getSession();
            final ConnectionId connectionId = MyMovilityRPCClient.getEndPointByRemoteName("Acsel-e");

            *//*final AtomicLong numIterations = new AtomicLong();
            final AtomicLong numObjectsSent = new AtomicLong();
            final AtomicLong sumOfLatencyNanos = new AtomicLong();*//*


            Future<Collection<? extends Comparable>> result = null;

            // Warm up (run the test code but discard results)...
            ExecutorService executorService = Executors.newFixedThreadPool(1*//*BenchmarkMultithreaded.NUM_THREADS*//*);
            for (int i = 0; i < *//*BenchmarkMultithreaded.NUM_THREADS*//*1; i++) {
                result = executorService.submit(new BenchmarkTask(session, connectionId*//*, numIterations,numObjectsSent, sumOfLatencyNanos*//*));
            }
            executorService.shutdown();
            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);

            // Run test...
            //executorService = Executors.newFixedThreadPool(*//*BenchmarkMultithreaded.NUM_THREADS*//*1);
            *//*numIterations.set(0);
            numObjectsSent.set(0);
            sumOfLatencyNanos.set(0);*//*

            *//*for (int i = 0; i < *//**//*BenchmarkMultithreaded.NUM_THREADS*//**//*1; i++) {
                result = executorService.submit(new BenchmarkTask(session, connectionId*//**//*, numIterations,numObjectsSent, sumOfLatencyNanos*//**//*));
            }

            executorService.shutdown();
            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);*//*
            //mobilityController.destroy();

            System.out.println("Finalizado. El resultado final fue: " + ((result == null) ? null : result.get()));
            *//*System.out.println("Mobility Num Threads\tMobility Request Size\tMobility Requests per sec\tMobility Latency Per Request(ns)");
            System.out.println(*//**//*BenchmarkMultithreaded.NUM_THREADS*//**//*1 + "\t" + (((double)numObjectsSent.get()) / numIterations.get()) + "\t" + (numIterations.get() / (sumOfLatencyNanos.get() / 1000000000.0)) + "\t" + (((double) sumOfLatencyNanos.get()) / numIterations.get()));*//*
        }
        catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }*/
}
