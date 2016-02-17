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
package com.enroquesw.mcs.comm.mobilityRPC.enums;

import com.enroquesw.mcs.comm.mobilityRPC.services.parameter.ProcessParameter;

/**
 * El enumerador SystemName Representan los Sistemas permitidos o registrados
 * a ser utilizados por :<p>
 *     <lu>
 *         <li>{@link com.enroquesw.mcs.comm.mobilityRPC.MyMovilityRPCComm}</li>
 *         <li>el registro {@link com.enroquesw.mcs.comm.mobilityRPC.services.factory.CallerRegister}</li>
 *         <li>el registro {@link com.enroquesw.mcs.comm.mobilityRPC.services.factory.ProcessorRegister}</li>
 *     </lu>
 *
 * @author Julio Morales
 */
public enum SystemName implements ProcessParameter {
    ACSELE(1, "Acsel-e"), COTIZADOR(15, "Cotizador"), SAPN(14, "SAPN"), CRM(3, "CRM Vida Individual"), OTHER(-1, "Other"), ALL(0, "All");
    int systemId;
    String systemName;

    SystemName(int systemId, String systemName) {
        this.systemId = systemId;
        this.systemName = systemName;
    }

    /**
     * Retorna el Identificador del Sistema
     * @return int
     */
    public int getSystemId() {
        return systemId;
    }

    /**
     * Retorna el Nombre del Sistema
     * @return String
     */
    public String getSystemName() {
        return systemName;
    }

    /**
     * Retorna el SystemName dado el nombre
     * @param name
     * @return SystemName
     */
    public static SystemName getByName(String name){
        for (SystemName systemName : values()) {
            if(systemName.systemName.equals(name)) return systemName;
        }
        return OTHER;
    }

    /**
     * Retorna el SystemName dado el id
     * @param systemId
     * @return SystemName
     */
    public static SystemName getById(int systemId){
        for (SystemName systemName : values()) {
            if(systemName.systemId == systemId) return systemName;
        }
        return OTHER;
    }


}
