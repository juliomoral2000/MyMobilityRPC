/**
 * Copyright 2016 Julio morales
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.enroquesw.mcs.comm.mobilityRPC.services.impl.caller;

import com.enroquesw.mcs.comm.mobilityRPC.services.ServicesBaseExecutor;
import com.enroquesw.mcs.comm.mobilityRPC.services.parameter.ProcessParameter;
import com.enroquesw.mcs.comm.mobilityRPC.MyMovilityRPCComm;
import com.enroquesw.mcs.comm.mobilityRPC.enums.SystemName;
import com.enroquesw.mcs.comm.mobilityRPC.services.callable.CallerOfProcess;
import com.enroquesw.mcs.comm.mobilityRPC.services.factory.CallerRegister;
import com.enroquesw.mcs.comm.mobilityRPC.services.factory.ProcessorRegister;

import java.util.List;

/**
 * la clase ServicesFactory_Callers representa la implementacion de los callers
 * al servicio comun para todos los clientes
 * referente a los registros de los callers y los processors
 * registrados en el ServicesFactory remoto
 *
 * @author Julio Morales
 */
public class ServicesFactory_Callers <P extends ProcessParameter>{
    /**
     *
     * @param remoteSystemName
     * @return
     * @throws Exception
     */
    public static List<ProcessorRegister> fetchProcessorRegistersFromServer(SystemName remoteSystemName) throws Exception {
        return ServicesBaseExecutor.executeCalling(FetchProcessorRegistersFromCaller.class, MyMovilityRPCComm.getServerSystemName(), remoteSystemName);
    }

    /**
     *
     * @param remoteSystemName
     * @return
     * @throws Exception
     */
    public static List<CallerRegister> fetchCallerRegistersFromServer(SystemName remoteSystemName) throws Exception {
        return ServicesBaseExecutor.executeCalling(FetchCallerRegistersFromServer.class, MyMovilityRPCComm.getServerSystemName(), remoteSystemName);
    }

    /**
     *
     */
    public static class FetchProcessorRegistersFromCaller extends CallerOfProcess<FetchProcessorRegistersFromCaller, SystemName, List<ProcessorRegister>> {
        public FetchProcessorRegistersFromCaller(SystemName parameter) throws Exception {
            super(parameter);
        }
    }

    /**
     *
     */
    public static class FetchCallerRegistersFromServer extends CallerOfProcess<FetchCallerRegistersFromServer, SystemName, List<CallerRegister>>{
        public FetchCallerRegistersFromServer(SystemName parameter) throws Exception {
            super(parameter);
        }
    }
}
