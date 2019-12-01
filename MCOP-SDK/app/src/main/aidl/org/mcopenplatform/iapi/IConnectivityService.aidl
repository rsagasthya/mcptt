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

interface IConnectivityService {

    /*** Error handling ********************************************************/

    /**
     * Get current error code. After an MCOP service function returns an error,
     * you can use this to query for the exact error reason.
     *
     * @return  An integer error code
     * @see     Constants.Common.Error
     * @see     Constants.Connectivity.Error
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
     * @see     Constants.Connectivity.Capabilities
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
     * @see     Constants.Connectivity.Capabilities
     */
    String[] checkCapabilityList(int cap);


    /*** Connectivity **********************************************************/


    /**
     * Create a MC PTT APN. If the APN can not be created for any reason
     * an error is returned. You can query the list of acceptable APN
     * types via checkCapabilityList.
     *
     * <p>If your program crashes or for some other reason disconnects from the
     * underlying MCOP service all created APNs will be deleted automatically.
     *
     * <p>Note that this call may take some time, so do not call from a thread
     * that can not afford to block.
     *
     * @param  name  Name of the new APN
     * @param  type  Type of the new APN, f.ex. "ims"
     * @return       true if creation was successful, false otherwise
     */
    boolean createAPN(String name, String type);

    /**
     * Delete a previously created MC PTT APN. You can only delete APNs that
     * you previously have created via createAPN.
     *
     * @param  name  Name of APN to delete
     * @return       true if APN was deleted, false otherwise
     */
    boolean deleteAPN(String name);

    /**
     * Activate a previously created MC PTT APN. After this call returns
     * successfully the APN will be fully established and ready to carry data.
     *
     * @param  name  Name of the APN to activate
     * @return       true if APN was succesfully activated, false otherwise
     */
    boolean activateAPN(String name);

    /**
     * Control what non-MC calls are allowed. If your application disconnects
     * from the MCOP service the state is automatically set to NONE.
     *
     * @param block  One of the BLOCK_CALLS integer constants.
     * @see   Constants.Connectivity.CallBlock
     */
    void blockNonMCCalls(int block);
}
