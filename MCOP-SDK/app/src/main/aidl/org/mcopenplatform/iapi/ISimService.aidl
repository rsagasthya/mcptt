/*
Copyright 2018 Bittium Wireless Ltd.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package org.mcopenplatform.iapi;

import org.mcopenplatform.iapi.McopMessenger;

interface ISimService {

    /*** Error handling ********************************************************/

    /**
     * Get current error code. After an MCOP service function returns an error,
     * you can use this to query for the exact error reason.
     *
     * @return  An integer error code
     * @see     Constants.Common.Error
     * @see     Constants.Sim.Error
     */
    int getErrorCode();

    /**
     * Get a string representation of the current error. Useful for debugging,
     * but note that this string may not be localized for the user.
     *
     * @return  A string describing the last error
     */
    String getErrorStr();


    /*** Notifications *********************************************************/

    /**
     * Register a messenger instance to receive notifications from the MCOP
     * service. Note that you may receive a notification even while this function
     * is still executing.
     *
     * <p><b>This function is platform-specific.</b> On Android, McopMessenger
     * is just a wrapped instance of Messenger. You must do the relevant setup to
     * receive messages yourself.
     *
     * @param m  Messenger instance to receive the notifications
     */
    void registerNotificationReceiver(in McopMessenger m);


    /*** Capability checks *****************************************************/

    /**
     * Check the service implementation for a specific capability. This
     * version checks for on/off and integer types.
     *
     * @param   cap  Capability to check.
     * @return  capability value, or -1 if the capability is invalid;
     *          for boolean capabilities 0 for unsupported, 1 for supported
     * @see     Constants.Sim.Capabilities
     */
    int checkCapability(int cap);

    /**
     * Check the service implementation for a specific capability. This
     * version checks for list type capabilities (constants starting with
     * LIST). If the capability is completely unsupported an empty list
     * will be returned.
     *
     * @param   cap  Capability to check.
     * @return  list describing the requested capability, an empty list if
     *          unsupported, or null if the capability is invalid
     * @see     Constants.Sim.Capabilities
     */
    String[] checkCapabilityList(int cap);


    /*** SIM Access ************************************************************/

    /**
     * Get the device identity (IMEI).
     *
     * @param   slotId  UICC card slot number
     * @return  the IMEI, or null if not present
     * @see     Constants.Sim.SimSlot
     */
    String getDeviceIdentity(int slotId);

    /**
     * Get the subscriber identity (IMSI).
     *
     * @param   slotId  UICC card slot number
     * @return  the IMSI, or null if not present
     * @see     Constants.Sim.SimSlot
     */
    String getSubscriberIdentity(int slotId);

    /**
     * Get the IMS private user identity (IMPI) that was loaded from the ISIM.
     *
     * @param   slotId  UICC card slot number
     * @return  the IMPI, or null if not present
     * @see     Constants.Sim.SimSlot
     */
    String getImpi(int slotId);

    /**
     * Get the IMS user identities (IMPU) that was loaded from the ISIM.
     *
     * @param   slotId  UICC card slot number
     * @return  an array of IMPU strings, with one IMPU per string,
     *          or null if not present
     * @see     Constants.Sim.SimSlot
     */
    String[] getImpu(int slotId);

    /**
     * Get the IMS home network domain name that was loaded from the ISIM.
     *
     * @param   slotId  UICC card slot number
     * @return  the IMS domain name, or null if not present
     * @see     Constants.Sim.SimSlot
     */
    String getDomain(int slotId);

    /**
     * Get the IMS Proxy Call Session Control Function (PCSCF) that were loaded
     * from the ISIM.
     *
     * @param   slotId  UICC card slot number
     * @return  an array of PCSCF strings with one PCSCF per string where first
     *          byte of the string will describe address type of the PCSCF,
     *          or null if not present.
     *          See 3GPP TS 31.103 4.2.8 for more details.
     * @see     Constants.Sim.SimSlot
     */
    String[] getPcscf(int slotId);

    /**
     * Get PCSCF address from PCO after data connection is established or modified.
     *
     * @param   type  Type of the previously created MC PTT APN
     * @return  an array of PCSCF strings with one PCSCF per string,
     *          or null if not present
     */
    String[] getPcscfPco(String type);

    /**
     * Get the response of SIM authentication.
     *
     * @param   slotId   UICC card slot number
     * @param   appType  Application type
     * @param   authType Authentication type
     * @param   data     Authentication challenge data, base64 encoded.
     *                   See 3GPP TS 31.102/31.103 7.1.2 for more details.
     * @return  the response of SIM authentication, base64 encoded,
     *          or null if not available.
     *          See 3GPP TS 31.102/31.103 7.1.2 for more details.
     * @see     Constants.Sim.SimSlot
     * @see     Constants.Sim.SimApp
     * @see     Constants.Sim.SimAuth
     */
    String getAuthentication(int slotId, int appType, int authType, String data);
}
