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
package com.enroquesw.mcs.comm.mobilityRPC.services.impl.processor;

import com.enroquesw.mcs.comm.mobilityRPC.enums.SystemName;
import com.enroquesw.mcs.comm.mobilityRPC.services.factory.CallerRegister;
import com.enroquesw.mcs.comm.mobilityRPC.services.factory.ProcessorRegister;
import com.enroquesw.mcs.comm.mobilityRPC.services.factory.ServicesFactory;
import com.enroquesw.mcs.comm.mobilityRPC.services.processor.CallProcessor;

import java.util.List;

/**
 * La clase <code>ServicesFactory_Proccesors</code> representa la implementacion del CallProcessor
 * de las llamadas realizada por los callers remotos
 * al servicio comun para todos los clientes
 * referente a los registros de los callers y los processors
 * registrados en el ServicesFactory local
 *
 *
 * @author Julio Morales
 */
public class ServicesFactory_Proccesors extends CallProcessor/*<ServicesFactory_Proccesors, ProcessParameter, Object>*/ {
    public static List<ProcessorRegister> fetchProcessorRegistersFromServer(SystemName systemName){
        return ServicesFactory.fetchProcessorRegistersFromServer(systemName);
    }
    public static List<CallerRegister> fetchCallerRegistersFromServer(SystemName systemName){
        return ServicesFactory.fetchCallerRegistersFromServer(systemName);
    }

    /*public static void preProcessCall(ProcessParameter parameter) { }

    public static void postProcessCall(ProcessParameter parameter) { }*/
}
