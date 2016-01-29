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

import com.googlecode.mobilityrpc.protocol.pojo.ExecutionMode;

/**
 * El enumerador CallType indica el tipo de llamado a realizar al Servidor.<p>
 * <lu>
 *     <li>RETURN_RESPONSE : Es una llamada sincrona, en la cliente espera por una respuesta del servidor (este es el valor por defecto) </li>
 *     <li>FIRE_AND_FORGET: Es una llamada asincrona, en la cliente no espera por respuesta del servidor</li>
 * </lu>
 * </p>
 *
 * @author Julio Morales
 */
public enum CallType {
    /**
     * Es una llamada sincrona, en la cliente espera por una respuesta del servidor
     */
    RETURN_RESPONSE(ExecutionMode.RETURN_RESPONSE),
    /**
     * Es una llamada asincrona, en la cliente no espera por respuesta del servidor
     */
    FIRE_AND_FORGET(ExecutionMode.FIRE_AND_FORGET);

    private final ExecutionMode callType;

    CallType(ExecutionMode returnResponse) {
        callType = returnResponse;
    }

    public ExecutionMode getCallType() {
        return callType;
    }
}
