package com.googlecode.mobilityrpc.network.impl.tcp;

import com.googlecode.mobilityrpc.network.ConnectionId;
import com.googlecode.mobilityrpc.network.impl.ConnectionErrorHandler;
import com.googlecode.mobilityrpc.network.impl.IncomingMessageHandler;

import java.io.InputStream;

/**
 * Created by Julio on 29/06/2016.
 */
public class IncomingByteStreamReaderImpl extends IncomingByteStreamReader {
    /**
     * @param connectionId           Identifies the connection to which the stream belongs
     * @param inputStream            An input stream from which the reader will read messages
     * @param incomingMessageHandler An object to which the reader will supply messages extracted from the stream
     * @param connectionErrorHandler An object which the reader will notify when any exceptions occur
     */
    public IncomingByteStreamReaderImpl(ConnectionId connectionId, InputStream inputStream, IncomingMessageHandler incomingMessageHandler, ConnectionErrorHandler connectionErrorHandler) {
        super(connectionId, inputStream, incomingMessageHandler, connectionErrorHandler);
    }
    /**
     * Reads a specified number of bytes from an input stream. This method allocates a byte array of the specified
     * size up front, then reads from the stream into this byte array without allocating any additional buffers, in
     * as few round-trips to the read method of the stream as possible.
     *
     * @param is An input stream
     * @param numBytesToRead The number of bytes to read from the stream
     * @return A byte array containing the specified amount of data read from the stream
     * @throws StreamClosedException If the stream is closed (i.e. EOF is detected) while reading
     * @throws IllegalStateException If any other error occurs
     */
    static byte[] readBytesFromStream(InputStream is, int numBytesToRead) {
        try {
            byte[] bytes = new byte[numBytesToRead];

            int bytesReadEachIteration, bytesReadTotal = 0;
            while (bytesReadTotal < numBytesToRead && (bytesReadEachIteration = is.read(bytes, bytesReadTotal, numBytesToRead - bytesReadTotal)) != -1) {
                bytesReadTotal += bytesReadEachIteration;
            }
            // FIXME_JULIO: Aqui deberia verificar es si realmente se cerro el InputStream y que no es que no envia/tiene nada el InputStream
            if (bytesReadTotal < numBytesToRead) {
                throw new StreamClosedException("Stream was closed explicitly by remote side, while reading byte " + bytesReadTotal + " of " + numBytesToRead);
            }
            return bytes;
        }
        catch (StreamClosedException e) {
            throw e;
        }
        catch (Exception e) {
            throw new IllegalStateException("Failed to read " + numBytesToRead + " bytes from stream", e);
        }
    }
}
