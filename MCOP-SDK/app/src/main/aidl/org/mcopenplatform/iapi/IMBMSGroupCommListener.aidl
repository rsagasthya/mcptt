// IMBMSGroupComListener.aidl
package org.mcopenplatform.iapi;

interface IMBMSGroupCommListener {

	/* Received regularly */
	void notifySaiList(in int[] sai);

	/* Received regularly */
	void notifyCellInfo(int mcc, int mns, int eci);
	
	/* Received when the group comm is monitored */
	void notifyMBMSGroupCommAvailability(long tmgi, int available, int quality); 
	
	/* Received after a request for opening a group comm */
	void notifyOpenMBMSGroupCommResult(long tmgi, int result, String netInterfaceName); 
	
	/* Received after a request for closing a group comm */
	void notifyCloseMBMSGroupCommResult(long tmgi, int result); 

}