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

public class Constants {
    private Constants() { }

    public class Common {
        private Common() { }

        /** Constants for errors common to all plugins / services */
        public class Error {
            private Error() { }

            /** No error / success */
            static public final int NO_ERROR = 0;

            /** Capability is unknown, or used for incorrect type (int vs list) */
            static public final int UNKNOWN_CAPABILITY = 10001;

            /** Permission denied */
            static public final int PERMISSION_DENIED = 10002;

            /** User authentication failed. Your app does not have carrier privileges
             and/or you could not be authenticated against the internal whitelist. */
            static public final int AUTH_FAILED = 10003;

            /** Invalid parameter */
            static public final int INVALID_PARAMETER = 10004;

            /** Internal error */
            static public final int INTERNAL_ERROR = 10005;
        }

        /** Constants for notifications common to all plugins / services */
        public class Notification {
            private Notification() { }

            /**
             * Notification for SIM state change.
             *
             * <p><b>Parameters:</b>
             * <br>arg1 - sim slot
             * <br>arg2 - sim state
             * @see Constants.Sim.SimSlot
             * @see Constants.Sim.SimState
             */
            static public final int SIM_UPDATED = 20001;
        }
    }

    public class Connectivity {
        private Connectivity() { }

        /** Constants for Connectivity capabilities */
        public class Capability {
            private Capability() { }

            /** List of supported MC APN types */
            static public final int LIST_MC_APN_TYPES = 0;

            /** List of supported QCI classes */
            static public final int LIST_QCI_CLASSES = 1;

            /** List of reserved MC APN names. Depending on platform
             security settings, it may not be possible to create these
             APNs the normal way, only via MCOP interfaces. */
            static public final int LIST_MC_APN_NAMES = 2;
        }

        /** Constants for getErrorCode */
        public class Error {
            private Error() { }

        }

        /** Constants for blocking non-MC calls */
        public class CallBlock {
            private CallBlock() { }

            /** Do not block any calls */
            static public final int NONE = 0;

            /** Block incoming (MT) non-MC calls */
            static public final int INCOMING = 1;

            /** Block outgoing (MO) non-MC calls */
            static public final int OUTGOING = 2;

            /** Block all non-MC calls */
            static public final int ALL = 3;
        }

        /** Constants for connectivity notifications */
        public class Notification {
            private Notification() { }

            /**
             * Notification for MC APN/network state change.
             *
             * <p><b>Parameters:</b>
             * <br>obj  - (android.net.Network) Network object
             * <br>arg1 - 0 = network lost, 1 = network available
             */
            static public final int NETWORK_STATE_CHANGED = 30001;
        }
    }

    public class Sim {
        private Sim() { }

        /** Constants for SIM capabilities */
        public class Capability {
            private Capability() { }
        }

        /** Constants for getErrorCode */
        public class Error {
            private Error() { }
        }

        /** Constants for SIM slot id's */
        public class SimSlot {
            private SimSlot() { }

            /** SIM which is default subscription */
            static public final int SLOT_ID_DEFAULT = 0;

            /** SIM slot ID 1 */
            static public final int SLOT_ID_1 = 1;

            /** SIM slot ID 2 */
            static public final int SLOT_ID_2 = 2;
        }

        /** Constants for SIM application types */
        public class SimApp {
            private SimApp() { }

            /** SIM application type USIM */
            static public final int USIM = 0;

            /** SIM application type ISIM */
            static public final int ISIM = 1;
        }

        /** Constants for SIM authentication types */
        public class SimAuth {
            private SimAuth() { }

            /** Authentication type for UICC challenge is EAP SIM. See RFC 4186 for details. */
            static public final int SIM = 0;

            /** Authentication type for UICC challenge is EAP AKA. See RFC 4187 for details. */
            static public final int AKA = 1;
        }

        /** Constants for SIM state types */
        public class SimState {
            private SimState() { }

            /** SIM is absent */
            static public final int ABSENT = 0;

            /** SIM is ready */
            static public final int READY = 1;

            /** SIM is updated due to SIM refresh */
            static public final int UPDATED = 2;
        }
    }

    public class Configuration {
        private Configuration() { }

        /** Constants for configuration and provisioning capabilities */
        public class Capability {
            private Capability() { }

            /** MCPTT UE Configuration Data Management Object */
            static public final int MO_UE_CONFIGURATION_DATA = 0;

            /** MCPTT User Profile Configuration Data Management Object */
            static public final int MO_USER_CONFIGURATION_DATA = 1;

            /** MCS Group Configuration Data Management Object */
            static public final int MO_GROUP_CONFIGURATION_DATA = 2;

            /** MCPTT Service Configuration Data Management Object */
            static public final int MO_SERVICE_CONFIGURATION_DATA = 3;

            /** MCS UE Initial Configuration Data Management Object */
            static public final int MO_UE_INITIAL_CONFIGURATION_DATA = 4;
        }

        /** Constants for getErrorCode */
        public class Error {
            private Error() { }

            /** The configuration data is invalid (either malformed or
             too large for the storage target) */
            static public final int INVALID_DATA = 2;

            /** Failed to read/write the configuration data to storage */
            static public final int IO_FAILURE = 3;
        }

        /** Constants for configuration and provisioning notifications */
        public class Notification {
            private Notification() { }

            /**
             * Notification for configuration file update.
             *
             * <p><b>Parameters:</b>
             * <br>arg1 - configuration file storage
             * <br>arg2 - configuration file type
             * @see Constants.Configuration.Storage
             * @see Constants.Configuration.FileType
             */
            static public final int FILE_UPDATED = 1;
        }

        /** Constants for storage types */
        public class Storage {
            private Storage() { }

            /** Storage type is SIM which is default subscription */
            static public final int SIM_DEFAULT = 0;

            /** Storage type is SIM in SIM slot 1 */
            static public final int SIM_1 = 1;

            /** Storage type is SIM in SIM slot 2 */
            static public final int SIM_2 = 2;

            /** Storage type is ME (Mobile Equipment) */
            static public final int ME = 3;

            /** Use a storage decided by the ConfigurationService.
             This storage type can only be used for reads. */
            static public final int AUTO = 4;
        }

        /** Constants for MC configuration files */
        public class FileType {
            private FileType() { }

            /** MCPTT UE Configuration Management Object file */
            static public final int PTT_MO_UE = 0;

            /** MCPTT User Profile Configuration Management Object file */
            static public final int PTT_MO_USER = 1;

            /** MCS Group Configuration Management Object file */
            static public final int PTT_MO_GROUP = 2;

            /** MCPTT Service Configuration Management Object file */
            static public final int PTT_MO_SERVICE = 3;

            /** MCS UE Initial Configuration Management Object file */
            static public final int PTT_MO_UE_INITIAL = 4;

            /** Device management certificate list for MCOP access control */
            static public final int MDM_CERTIFICATE_LIST = 10001;

            /** Only for developing */
            static public final int PTT_PROFILE_SERVICE = -1000;
        }
    }
}

