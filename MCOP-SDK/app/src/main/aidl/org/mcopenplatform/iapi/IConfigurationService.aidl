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

interface IConfigurationService {

    /*** Error handling ********************************************************/

    /**
     * Get current error code. After an MCOP service function returns an error,
     * you can use this to query for the exact error reason.
     *
     * @return  An integer error code
     * @see     Constants.Common.Error
     * @see     Constants.Configuration.Error
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
     * @see     Constants.Configuration.Capabilities
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
     * @see     Constants.Configuration.Capabilities
     */
    String[] checkCapabilityList(int cap);


    /*** Configuration / provisioning data access ******************************/

    /**
     * Read a configuration file from the SIM or from the ME (Mobile Equipment).
     *
     * @param   storage configuration file storage
     * @param   type    configuration file type
     * @return  an array of byte data, or null if not present
     * @see     Constants.Configuration.Storage
     * @see     Constants.Configuration.FileType
     */
    byte[] readConfigurationFile(int storage, int type);

    /**
     * Write a configuration file to the SIM or to the ME.
     *
     * @param   storage configuration file storage
     * @param   type    configuration file type
     * @param   data    an array of byte data to write
     * @return  true if write was successful, false otherwise
     * @see     Constants.Configuration.Storage
     * @see     Constants.Configuration.FileType
     */
    boolean writeConfigurationFile(int storage, int type, in byte[] data);


    /*** Security and authentication *******************************************/

    /**
     * Check if the given package is trusted by ConfigurationService.
     *
     * @param  pkgname Full name of the package
     * @return true if trusted, false otherwise
     */
    boolean checkPackageAuth(String pkgname);

    /**
     * Check if the given UID is trusted by ConfigurationService.
     *
     * @param  uid UID to check
     * @return true if trusted, false otherwise
     */
    boolean checkUidAuth(int uid);
}
