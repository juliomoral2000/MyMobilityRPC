package com.googlecode.mobilityrpc.session.impl;

import com.googlecode.mobilityrpc.controller.impl.MobilityControllerInternal;
import com.googlecode.mobilityrpc.network.ConnectionId;
import com.googlecode.mobilityrpc.protocol.pojo.*;
import com.googlecode.mobilityrpc.serialization.Serializer;
import com.googlecode.mobilityrpc.session.MobilitySession;

import java.lang.reflect.Field;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

/**
 * <code>MobilitySessionRPCImpl</code> es la implementacion Personaliza de {@link MobilitySession}.
 */
public class MobilitySessionRPCImpl extends MobilitySessionImpl {
    private static final long EXECUTION_RESPONSE_TIMEOUT_MILLIS_RPC = 120000; // 120 seg. o 2 min
    private UUID sessionId;
    private MobilityControllerInternal mobilityController;
    private SessionClassLoader sessionClassLoader;
    private Serializer defaultSerializerSon;
    private ConcurrentMap<RequestIdentifier, FutureExecutionResponse> futureExecutionResponsesSon;

    public MobilitySessionRPCImpl(UUID sessionId, MobilityControllerInternal mobilityController) {
        super(sessionId, mobilityController);
        this.sessionId = sessionId;
        this.mobilityController = mobilityController;
        this.sessionClassLoader = super.getSessionClassLoader();
        this.defaultSerializerSon =  getDefaultSerializerFromSuper()/*new KryoSerializer(sessionClassLoader)*/;
        this.futureExecutionResponsesSon = getFutureExecutionResponsesFromSuper();
        //this.defaultSerializationFormat = SerializationFormat.KRYO;
    }

    @Override
    public <T> T execute(ConnectionId connectionId, ExecutionMode executionMode, Callable<T> callable) {
        return execute( connectionId, executionMode, callable, null);
    }

    public <T> T execute(ConnectionId connectionId, ExecutionMode executionMode, Callable<T> callable, Long timeOutMilis) {
        final byte[] serializedExecutableObject = serialize(callable, getDefaultSerializationFormat()/*defaultSerializationFormat*/);  // Serialize the object...
        RequestIdentifier requestIdentifier = new RequestIdentifier(this.getSessionId(), UUID.randomUUID(), null);  // Prepare an ExecutionRequest object which we will send to remote machine...
        ExecutionRequest outgoingRequest = new ExecutionRequest( serializedExecutableObject, getDefaultSerializationFormat()/*defaultSerializationFormat*/, executionMode, requestIdentifier );
        switch (executionMode) {
            case FIRE_AND_FORGET: // No need to block waiting for response. Send execution request to remote machine, and then return without blocking...
                try {
                    mobilityController.sendOutgoingMessage(connectionId, outgoingRequest);
                } catch (Exception e) { // This exception is unlikely, should only occur if our outgoing queue to machine specified is full...
                    throw new IllegalStateException("Failed to submit Callable object in FIRE_AND_FORGET mode for execution on remote machine: " + connectionId, e);
                }
                return null;
            case RETURN_RESPONSE: // Send request and block waiting for response from remote machine.
                final ExecutionResponse executionResponse;
                try {
                    // Register a FutureExecutionResponse object in the map, which the thread processing a response to this
                    // request can later look up to notify this thread of the outcome of executing the request...
                    FutureExecutionResponse futureExecutionResponse = new FutureExecutionResponse(requestIdentifier);
                    futureExecutionResponsesSon.put(requestIdentifier, futureExecutionResponse);
                    mobilityController.sendOutgoingMessage(connectionId, outgoingRequest);  // Send the execution request to the remote machine...
                    executionResponse = futureExecutionResponse.getResponse(timeOutMilis == null ? EXECUTION_RESPONSE_TIMEOUT_MILLIS_RPC : timeOutMilis, TimeUnit.MILLISECONDS);  // Now block this thread until we get a response, or we time out...
                }
                catch (Exception e) {
                    throw new IllegalStateException("Failed to receive response for execution request sent to remote machine in RETURN_RESPONSE mode for request identifier: " + requestIdentifier + ", connection id: " + connectionId, e);
                }
                // Decipher the execution response and return control normally to the client, or throw exception as necessary...
                final ExecutionResponse.ExecutionOutcome executionOutcome = executionResponse.getExecutionOutcome();
                // Indicate to the class loader that should this thread require classes when deserializing
                // the response that they can be obtained from this remote machine...
                sessionClassLoader.setThreadLocalConnectionId(connectionId);
                try {
                    switch (executionOutcome) {
                        case VOID_RETURNED: // Return normally...
                            return null;
                        case FAILURE:   // The code threw an exception on the remote machine. Deserialize the exception and throw it to the caller on this machine...
                            Object throwable = deserialize(executionResponse.getSerializedReturnObject(), executionResponse.getSerializationFormat());
                            // Sanity check to validate that indeed an exception was serialized as expected...
                            if (!(throwable instanceof Throwable)) {
                                throw new IllegalStateException("Unexpected response object returned for execution outcome FAILURE: " + throwable);
                            }
                            throw new IllegalStateException("An exception was thrown by the Callable object when executed on the remote machine: " + connectionId, (Throwable)throwable);
                        case VALUE_RETURNED:    // The callable returned an object when executed on the remote machine, return it to the caller of this method...
                            @SuppressWarnings({"unchecked", "UnnecessaryLocalVariable"}) T objectReturned = (T) deserialize(executionResponse.getSerializedReturnObject(), executionResponse.getSerializationFormat());
                            return objectReturned;
                        default:
                            throw new IllegalStateException("Unexpected ExecutionOutcome returned: " + executionMode);
                    }
                } finally {
                    // Null-out the connection id for this calling thread, now that response has been deserialized...
                    sessionClassLoader.setThreadLocalConnectionId(null);
                }
            default:
                throw new IllegalStateException("Unexpected ExecutionMode specified: " + executionMode);
        }
    }

    private Object deserialize(byte[] serializedObject, SerializationFormat serializationFormat) {
        try {
            switch (serializationFormat) {
                case KRYO: // Note: we only support one serialization format now, however the protocol allows for others in future...
                    return /*defaultSerializer*/getDefaultSerializer().deserialize(serializedObject);
                default:
                    throw new IllegalStateException("Unsupported serialization format: " + serializationFormat);
            }
        } catch (Exception e) { throw new IllegalStateException("Exception deserializing object from " + serializedObject.length + " bytes data in " + serializationFormat + " format", e); }
    }

    private byte[] serialize(Object object, SerializationFormat serializationFormat) {
        try {
            switch (serializationFormat) {
                case KRYO: return /*defaultSerializer*/getDefaultSerializer().serialize(object);
                default: throw new IllegalStateException("Unsupported serialization format: " + serializationFormat);
            }
        } catch (Exception e) {
            throw new IllegalStateException("Exception serializing object to " + serializationFormat + " format: " + object, e);
        }
    }

    private Serializer getDefaultSerializerFromSuper() {
        try {
            Field field = MobilitySessionImpl.class.getDeclaredField("defaultSerializer");
            field.setAccessible(true);
            Object value = field.get(this);
            field.setAccessible(false);

            if (value == null) {
                return null;
            } else {
                @SuppressWarnings({"unchecked"})
                final Serializer out = (Serializer) value;
                return out;
            }
            //throw new RuntimeException("Wrong value");
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private ConcurrentMap<RequestIdentifier, FutureExecutionResponse> getFutureExecutionResponsesFromSuper() {
        try {
            Field field = MobilitySessionImpl.class.getDeclaredField("futureExecutionResponses");
            field.setAccessible(true);
            Object value = field.get(this);
            field.setAccessible(false);

            if (value == null) {
                return null;
            } else {
                @SuppressWarnings({"unchecked"})
                final ConcurrentMap<RequestIdentifier, FutureExecutionResponse> out = (ConcurrentMap<RequestIdentifier, FutureExecutionResponse>) value;
                return out;
            }
            //throw new RuntimeException("Wrong value");
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public SerializationFormat getDefaultSerializationFormat(){
        return SerializationFormat.KRYO;
    }

    public Serializer getDefaultSerializer(){
         return this.defaultSerializerSon;
    }

    public long getDefaultExecutionResponseTimeout(){
        return EXECUTION_RESPONSE_TIMEOUT_MILLIS_RPC;
    }
}
