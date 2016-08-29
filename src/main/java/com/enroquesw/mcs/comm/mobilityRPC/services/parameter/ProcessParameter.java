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
package com.enroquesw.mcs.comm.mobilityRPC.services.parameter;

/**
 * <code>ProcessParameter</code> es la Clase Abstracta (Interface antes) que define que esta implementacion es un parametro usado
 * por algun CallProcessor como parametro de entrada e recibido por los Processor en el Remoto
 * @author Julio Morales
 */
public interface ProcessParameter /* extends Cloneable */{
    Long getTimeOutMax();
    String getRequestId();
    void setRequestId(String requestId);
}
