// IMBMSGroupCom.aidl
package org.mcopenplatform.iapi;

import org.mcopenplatform.iapi.IMBMSGroupCommListener;

interface IMBMSGroupComm {
    void registerApplication(IMBMSGroupCommListener listener);

    void startMBMSGroupCommMonitoring(long tmgi, in int[] sai, in int[] frequencies, int qci);
    void stopMBMSGroupCommMonitoring(long tmgi);

    void openGroupComm(long tmgi, in int[] sai, in int[] frequencies);
    void closeGroupComm(long tmgi);
}