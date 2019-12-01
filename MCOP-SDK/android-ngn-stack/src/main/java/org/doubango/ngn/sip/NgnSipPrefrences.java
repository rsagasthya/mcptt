/*
* Copyright (C) 2017, University of the Basque Country (UPV/EHU)
*  Contact for licensing options: <licensing-mcpttclient(at)mcopenplatform(dot)com>
*
* The original file was part of Open Source IMSDROID
*  Copyright (C) 2010-2011, Mamadou Diop.
*  Copyright (C) 2011, Doubango Telecom.
*
*
* Contact: Mamadou Diop <diopmamadou(at)doubango(dot)org>
*
* This file is part of Open Source Doubango Framework.
*
* This is free software: you can redistribute it and/or modify it under the terms of
* the GNU General Public License as published by the Free Software Foundation, either version 3
* of the License, or (at your option) any later version.
*
* This is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
* without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
* See the GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License along
* with this program; if not, write to the Free Software Foundation, Inc.,
* 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
*/
package org.doubango.ngn.sip;


import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import org.doubango.ngn.BuildConfig;
import org.doubango.ngn.datatype.ms.cms.mcpttUserProfile.ExtensibleType;
import org.doubango.utils.Utils;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


@Root(strict=false, name = "NgnSipPrefrences")
public class NgnSipPrefrences {
	private final static String TAG = Utils.getTAG(NgnSipPrefrences.class.getCanonicalName());

	/**
	 * Display name.
	 */
	@Element(required = false , name = "DisplayName")
	private String displayName;

	@Attribute(required = false,name = "name") private String name;

	/**
	 * Password
	 */
	@Element(name="Password" ,required = false) 	private String mPassword;
	@Element(name="Presence" ,required = false) 	private Boolean mPresence;
	@Element(name="XcapEnabled" ,required = false) 	private Boolean mXcapEnabled;
	@Element(name="PresenceRLS" ,required = false) 	private Boolean mPresenceRLS;
	@Element(name="PresencePub" ,required = false) 	private Boolean mPresencePub;
    @Element(name="PresenceSub" ,required = false) 	private Boolean mPresenceSub;
    @Element(name="MWI" ,required = false) 	private Boolean mMWI;

	/**
	 * IP Multimedia Private Identity.
	 */
	@Element(name="IMPI" ,required = false) 	private String mIMPI;

	/**
	 *  IP Multimedia Public Identity.
	 */
    @Element(name="IMPU" ,required = false) 	private String mIMPU;

	/**
	 * domain.
	 */
	@Element(name="Realm" ,required = false) 	private String mRealm;

	/**
	 * Host name or ip address of the P-CSCF (Proxy-Call Session Control Function).
	 */
    @Element(name="PcscfHost" ,required = false) 	private String mPcscfHost;

	/**
	 * Port number of the P-CSCF.
	 */
	@Element(name="PcscfPort" ,required = false) 	private int mPcscfPort=-1;

	/**
	 * Transport protocol.
	 */
	@Element(name="Transport" ,required = false) 	private String mTransport;

    @Element(name="IPVersion" ,required = false) 	private String mIPVersion;
    @Element(name="IPsecSecAgree" ,required = false) 	private Boolean mIPsecSecAgree;
    @Element(name="LocalIP" ,required = false) 	private String mLocalIP;
    @Element(name="HackAoR" ,required = false) 	private Boolean mHackAoR;


	/**
	 * International Mobile Subscriber Identity from SIM
	 */
	@Element(name="imsi" ,required = false) 	private String mImsi;

	/**
	 * International Mobile Station Equipment Identity from device
	 */
	@Element(name="imei" ,required = false) 	private String mImei;




	/**
	 * Public Service Identity for Private calls.
	 */
	@Element(name="McpttPsiCallPrivate" ,required = false) 	private String mMcpttPsiCallPrivate;

	/**
	 * Public Service Identity for Group Calls.
	 */
	@Element(name="McpttPsiCallGroup" ,required = false) 	private String mMcpttPsiCallGroup;

	/**
	 * Public Service Identity for Preestablished Calls.
	 */
	@Element(name="McpttPsiCallPreestablished" ,required = false) 	private String mMcpttPsiCallPreestablished;

	/**
	 * Public Service Identity for CMS
	 */
	@Element(name="McpttPsiCMS" ,required = false) 	private String mMcpttPsiCMS;

	/**
	 * "true" for Subcription CMS, and "false" for no Subcription CMS.
	 */
	@Element(name="McpttEnableSubcriptionCMS" ,required = false) 	private Boolean mMcpttEnableSubcriptionCMS;



	/**
	 * Public Service Identity for GMS
	 */
	@Element(name="McpttPsiGMS" ,required = false) 	private String mMcpttPsiGMS;

	/**
	 * "true" for Subcription GMS, and "false" for no Subcription GMS.
	 */
	@Element(name="McpttEnableSubcriptionGMS" ,required = false) 	private Boolean mMcpttEnableSubcriptionGMS;


	/**
	 * MCPTT identifier.
	 */
	@Element(name="McpttId" ,required = false) 	private String mMcpttId;

	/**
	 * MCPTT Client identifier.
	 */
	@Element(name="McpttClientId" ,required = false) 	private String mMcpttClientId;

	/**
	 * Priority level for Private or Group MCPTT calls. From 1 (lowest), to 15 (highest).
	 */
	@Element(name="McpttPriority" ,required = false) 	private int mMcpttPriority=-1;

	/**
	 * To insert IMPLICIT in the Invite message.
	 */
	@Element(name="McpttImplicit" ,required = false) 	private Boolean mMcpttImplicit;

	/**
	 * To insert GRANTED in the Invite message.
	 */
	@Element(name="McpttGranted" ,required = false) 	private Boolean mMcpttGranted;

	/**
	 * "true" for automatic answer mode, and "false" for manual answer mode.
	 */
	@Element(name="McpttPrivAnswerMode" ,required = false) 	private Boolean mMcpttPrivAnswerMode;

	/**
	 * "true" for auto answer mode, and "false" for manual answer mode.
	 */
	@Element(name="McpttAnswerMode" ,required = false) 	private Boolean mMcpttAnswerMode;

	/**
	 * "true" insert NameSpace for XML file, and "false" no insert NameSpace for XML file.
	 */
	@Element(name="McpttNameSpace" ,required = false) 	private Boolean mMcpttNameSpace;

	/**
	 * Public Service Identity for Affiliations.
	 */
	@Element(name="McpttPsiAffiliation" ,required = false) 	private String mMcpttPsiAffiliation;

	/**
	 *  Public Service Identity for Authentication.
	 */
	@Element(name="McpttPsiAuthentication" ,required = false) 	private String mMcpttPsiAuthentication;

	/**
	 * To enable MCPTT affiliation features.
	 */
	@Element(name="McpttIsEnableAffiliation" ,required = false) 	private Boolean mMcpttIsEnableAffiliation;
	@Element(name="McpttIsSelfAffiliation" ,required = false) 	private Boolean mMcpttIsSelfAffiliation;




	@Element(name="McpttLocationInfoVersionOld" ,required = false) 	private Boolean mMcpttLocationInfoVersionOld;
	/**
	 * To enable MBMS (Multimedia Broadcast Multicast Services).
	 */
	@Element(name="McpttEnableMbms" ,required = false) 	private Boolean mMcpttEnableMbms;


	//Allows UserProfile
	@Element(name="AllowsUserProfile" ,required = false)private org.doubango.ngn.datatype.ms.cms.mcpttUserProfile.ExtensibleType mAllowsUserProfile;
	@Element(name="IndexUserProfile" ,required = false)private Short mIndexUserProfile;


	//Idms Authentication
	@Element(name="McpttIsSelfAuthentication" ,required = false) 	private Boolean mMcpttIsSelfAuthentication;

	/**
	 * Client Id for Authentication IDMS
	 */
	@Element(name="McpttSelfAuthenticationClientId" ,required = false) private String mMcpttSelfAuthenticationClientId;

	/**
	 * Issuer Uri for Authentication IDMS
	 */
	@Element(name="McpttSelfAuthenticationIssuerUri" ,required = false) private String mMcpttSelfAuthenticationIssuerUri;

	/**
	 * Redirect Uri for Authentication IDMS
	 */
	@Element(name="McpttSelfAuthenticationRedirectUri" ,required = false) private String mMcpttSelfAuthenticationRedirectUri;//REDIRECT_URI

	/**
	 * It allows you to send a token not valid for testing
	 */
	@Element(name="McpttSelfAuthenticationSendTokenFail" ,required = false) private Boolean mMcpttSelfAuthenticationSendTokenFail;

	/**
	 * "true"  It sends the token in the Register, "false" It sends the token in the Publish
	 */
	@Element(name="McpttSelfAuthenticationSendTokenRegister" ,required = false) private Boolean mMcpttSelfAuthenticationSendTokenRegister;

	/**
	 * "true" Use Issuer Uri for IDMS
	 */
	@Element(name="McpttUseIssuerUriIdms" ,required = false) private Boolean mMcpttUseIssuerUriIdms;

	/**
	 * Auth End point for IDMS
	 */
	@Element(name="IdmsAuthEndpoint" ,required = false) private String mIdmsAuthEndpoint;

	/**
	 * Token End point for IDMS
	 */
	@Element(name="IdmsTokenEndPoint" ,required = false) private String mIdmsTokenEndPoint;

	/**
	 * "true" Enable Use CMS/GMS/IDMS, "flase" only use IDMS
	 */
	@Element(name="McpttEnableCMS" ,required = false) private Boolean mMcpttEnableCMS;


	/**
	 * MCPTT UE identifier.
	 */
	@Element(name="McpttUEId" ,required = false) private String mMcpttUEId;

	//Init CMS DATA from EUInitConfig


	@Element(required = false , name = "T100")
	private short t100;
	@Element(required = false , name = "T101")
	private short t101;
	@Element(required = false , name = "T103")
	private short t103;
	@Element(required = false , name = "T104")
	private short t104;
	@Element(required = false , name = "T132")
	private short t132;

	/**
	 *  Public Service Identity for KMS.
	 */
    @Element(name="Kms" ,required = false) private String mKms;

	/**
	 *  XCAP Root URI for CMS.
	 */
	@Element(name="CMSXCAPRootURI" ,required = false) private String mCMSXCAPRootURI;

	/**
	 *  XCAP Root URI for GMS.
	 */
	@Element(name="GMSXCAPRootURI" ,required = false) private String mGMSXCAPRootURI;


	//End CMS DATA from EUInitConfig

	//Init CMS DATA from EUConfig

	//This leaf node indicates the maximum number of private calls. Values: 0-255 Element contains an integer indicating the maximum number of simultaneous calls (N10) allowed for an on-network or off-network private call with floor control
	@Element(required = false , name = "MaxSimulCallN10")
	private BigInteger mMaxSimulCallN10;
	//This leaf node indicates the maximum number of simultaneous group calls. Values: 0-255. element contains an integer indicating the number of simultaneous calls (N4) allowed for an on-network or off-network group call
	@Element(required = false , name = "MaxSimulCallN4")
	private BigInteger mMaxSimulCallN4;
	//This leaf node indicates the maximum number of transmissions in a group. Values: 0-255. element contains an integer indicating the maximum number of allowed simultaneous transmissions for an on-network or off-network group call
	@Element(required = false , name = "MaxSimulTransN5")
	private BigInteger mMaxSimulTransN5;
	//This interior node is a placeholder for the prioritized MCPTT group call configuration.
	@ElementList(inline = true,required = false,entry = "MCPTTGroupPriority")
	private List<MCPTTGroupPriority> mcpttGroupPriority;

	/*
	a)	if the UE has both IPv4 and IPv6 host configuration::
		i)	if IPv6Preferred is set to true then the UE shall use IPv6 for all on network signalling and media; otherwise
		ii)	if IPv6Preferred is set to false then the UE shall use IPv4 for all on network signalling and media;
	b)	if the UE has only IPv4 host configuration then the UE shall use IPv4 for all on network signalling and media; and
	c)	if the UE has only IPv6 host configuration then the UE shall use IPv6 for all on network signalling and media; and
	 */
	@Element(required = false , name = "IPv6Preferred")
	private Boolean iPv6Preferred;
	//element is set to "true" the MCPTT UE is allowed to offer a relay service, and if set to "false" the MCPTT UE is not allowed to offer relay service
	@Element(required = false , name = "RelayService")
	private Boolean relayService;
	@Element(required = false , name = "RelayedMCPTTGroup")
	private RelayedMCPTTGroup relayedMCPTTGroup;




	//End CMS DATA from EUConfig

	//Init CMS DATA from UserProfile


	@Element(required = false , name = "PrivateCallList")
	private Map<String,EntryType> privateCallList;


	//element is of type "positiveInteger" and indicates the maximum number of simultaneously received MCPTT group calls
	@Element(required = false , name = "MaxSimultaneousCallsN6")
	private BigInteger maxSimultaneousCallsN6;
	//element is of type "nonNegativeInteger", and indicates to the MCPTT server the maximun number of MCPTT groups that the MCPTT user is authorised to affiliate with
	@Element(required = false , name = "MaxAffiliationsN2")
	private BigInteger maxAffiliationsN2;

	//> list element of the <OnNetwork> element indicates an MCPTT group ID of an MCPTT group that the MCPTT user is authorised to affiliate with during on-network operation and corresponds to the "MCPTTGroupID"
	@Element(required = false , name = "MCPTTGroupInfo")
	private Map<String,EntryType> mCPTTGroupInfo;
	//list element indicates the name of of an MCPTT group that the MCPTT user is implicitly affiliated with and corresponds to the "DisplayName" element of subclause 5.2.48C5 in 3GPP TS 24.483
	@Element(required = false , name = "ImplicitAffiliations")
	private Map<String,EntryType> implicitAffiliations;


	//End CMS DATA from UserProfile

	//Init CMS DATA from Service
	@Element(required = false , name = "EmergencyCallServiceConf")
	private EmergencyCallType emergencyCall;
	@Element(required = false , name = "PrivateCallServiceConf")
	private PrivateCallType privateCall;
	@Element(required = false , name = "FloorControlQueueServiceConf")
	private FloorControlQueueType floorControlQueue;
	@Element(required = false , name = "FcTimersCountersServiceConf")
    protected FcTimersCountersType fcTimersCounters;
	@Element(required = false , name = "SignallingProtectionServiceConf")
	protected SignallingProtectionType signallingProtection;

	//End CMS DATA from Service


	//GUI
	@Element(name="McpttPlayerSound" ,required = false) private Boolean mMcpttPlayerSound;
	@Element(name="McpttSelfRegistration" ,required = false) private Boolean mMcpttSelfRegistration;//MCPTT_SELF_REGISTRATION


	public NgnSipPrefrences(){
    	
    }



	public Boolean isMcpttPlayerSound() {
		return mMcpttPlayerSound;
	}

	public void setMcpttPlayerSound(Boolean mMcpttPlayerSound) {
		this.mMcpttPlayerSound = mMcpttPlayerSound;
	}

	public String getImsi() {
		return mImsi;
	}

	public void setImsi(String mImsi) {
		this.mImsi = mImsi;
	}

	public String getImei() {
		return mImei;
	}

	public void setImei(String mImei) {
		this.mImei = mImei;
	}


	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}




	public SignallingProtectionType getSignallingProtection() {
		if(signallingProtection==null)signallingProtection=new SignallingProtectionType();
		return signallingProtection;
	}

	public void setSignallingProtection(SignallingProtectionType signallingProtection) {
		this.signallingProtection = signallingProtection;
	}

	public FcTimersCountersType getFcTimersCounters() {
		if(fcTimersCounters!=null)fcTimersCounters=new FcTimersCountersType();
		return fcTimersCounters;
	}

	public void setFcTimersCounters(FcTimersCountersType fcTimersCounters) {
		this.fcTimersCounters = fcTimersCounters;
	}

	public FloorControlQueueType getFloorControlQueue() {
		if(floorControlQueue==null)floorControlQueue=new FloorControlQueueType();
		return floorControlQueue;
	}

	public void setFloorControlQueue(FloorControlQueueType floorControlQueue) {
		this.floorControlQueue = floorControlQueue;
	}

	public PrivateCallType getPrivateCall() {
		if(privateCall==null)privateCall=new PrivateCallType();
		return privateCall;
	}

	public void setPrivateCall(PrivateCallType privateCall) {
		this.privateCall = privateCall;
	}

	public EmergencyCallType getEmergencyCall() {
		if(emergencyCall==null)emergencyCall=new EmergencyCallType();
		return emergencyCall;
	}

	public void setEmergencyCall(EmergencyCallType emergencyCall) {
		this.emergencyCall = emergencyCall;
	}



	public Map<String,EntryType> getImplicitAffiliations() {
		return implicitAffiliations;
	}


	public List<EntryType> getImplicitCheckedAffiliations() {
		ArrayList<EntryType> entryTypesChecked=new ArrayList<>();
		if(mCPTTGroupInfo!=null){
			for(EntryType entryType:implicitAffiliations.values()){
				EntryType entryTypeCheck=null;
				if(entryType.getDisplayName()!=null && !entryType.getDisplayName().isEmpty()){
					entryTypeCheck=mCPTTGroupInfo.get(entryType.getDisplayName());
				}else if(entryType.getUriEntry()!=null && !entryType.getUriEntry().isEmpty()){
					entryTypeCheck=mCPTTGroupInfo.get(entryType.getDisplayName());
				}
				if(entryTypeCheck!=null){
					entryTypesChecked.add(entryTypeCheck);
				}

			}
		}
		return entryTypesChecked;
	}

	public List<EntryType> getExplicitCheckedAffiliations() {
		ArrayList<EntryType> entryTypesChecked=new ArrayList<>();
		if(mCPTTGroupInfo!=null && implicitAffiliations!=null){
			entryTypesChecked=new ArrayList<>(mCPTTGroupInfo.values());
			/*for(EntryType entryType:mCPTTGroupInfo.values()){
				EntryType entryTypeCheck=null;
				if(entryType.getDisplayName()!=null && !entryType.getDisplayName().isEmpty()){
					entryTypeCheck=implicitAffiliations.get(entryType.getDisplayName());
				}else if(entryType.getUriEntry()!=null && !entryType.getUriEntry().isEmpty()){
					entryTypeCheck=implicitAffiliations.get(entryType.getDisplayName());
				}
				if(entryTypeCheck==null){
					entryTypesChecked.add(entryType);
				}

			}*/
		}
		return entryTypesChecked;
	}

	public void setImplicitAffiliations(List<EntryType> implicitAffiliations) {
		if(implicitAffiliations!=null && !implicitAffiliations.isEmpty()){
			this.implicitAffiliations=new HashMap<>();
			for(EntryType entryType:implicitAffiliations){
				if(entryType.getDisplayName()!=null){
					this.implicitAffiliations.put(entryType.getDisplayName(),entryType);
				}else if(entryType.getUriEntry()!=null){
					this.implicitAffiliations.put(entryType.getUriEntry(),entryType);
				}

			}
		}
	}

	public Map<String,EntryType> getMCPTTGroupInfo() {
		return mCPTTGroupInfo;
	}

	public void setMCPTTGroupInfo(Map<String,EntryType> mCPTTGroupInfo) {
		this.mCPTTGroupInfo = mCPTTGroupInfo;
	}
	public void setMCPTTGroupInfo(List<EntryType> mCPTTGroupInfo) {
		if(mCPTTGroupInfo!=null && !mCPTTGroupInfo.isEmpty()){
			this.mCPTTGroupInfo=new HashMap<>();
			for(EntryType entryType:mCPTTGroupInfo){
				if(entryType.getDisplayName()!=null && this.mCPTTGroupInfo.get(entryType.getDisplayName())==null){
					this.mCPTTGroupInfo.put(entryType.getDisplayName(),entryType);
				}else if(entryType.getUriEntry()!=null && this.mCPTTGroupInfo.get(entryType.getUriEntry())==null){
					this.mCPTTGroupInfo.put(entryType.getUriEntry(),entryType);
				}else{
					if(BuildConfig.DEBUG)Log.e(TAG,"Error in procced MCPTT GroupInfo");
				}

			}
		}
	}

	public BigInteger getMaxAffiliationsN2() {
		return maxAffiliationsN2;
	}

	public void setMaxAffiliationsN2(BigInteger maxAffiliationsN2) {
		this.maxAffiliationsN2 = maxAffiliationsN2;
	}

	public BigInteger getMaxSimultaneousCallsN6() {
		return maxSimultaneousCallsN6;
	}

	public void setMaxSimultaneousCallsN6(BigInteger maxSimultaneousCallsN6) {
		this.maxSimultaneousCallsN6 = maxSimultaneousCallsN6;
	}

	public Map<String,EntryType> getPrivateCallListMap() {
		return privateCallList;
	}
	public List<EntryType> getPrivateCallList() {
		if(privateCallList==null)return null;
		return new ArrayList<>(privateCallList.values());
	}

	public void setPrivateCallList(List<EntryType> privateCallList) {
		if(privateCallList==null){
			this.privateCallList=null;
			return;
		}
		this.privateCallList=new TreeMap<>();
		for(EntryType entryType:privateCallList){
			if(entryType!=null)
			if(entryType.getDisplayName()!=null && !entryType.getDisplayName().isEmpty()){
				this.privateCallList.put(entryType.getDisplayName(),entryType);
			}else if(entryType.getUriEntry()!=null && !entryType.getUriEntry().isEmpty()){
				this.privateCallList.put(entryType.getUriEntry(),entryType);
			}
		}
	}

	public void setPrivateCallList(Map<String,EntryType> privateCallList) {
		this.privateCallList = privateCallList;
	}



	public List<MCPTTGroupPriority> getMcpttGroupPriority() {
		if(mcpttGroupPriority==null){
			mcpttGroupPriority=new ArrayList<>();
		}
		return mcpttGroupPriority;
	}

	public void setMcpttGroupPriority(List<MCPTTGroupPriority> mcpttGroupPriority) {
		this.mcpttGroupPriority = mcpttGroupPriority;
	}

	public BigInteger getMaxSimulCallN10() {
		return mMaxSimulCallN10;
	}

	public void setMaxSimulCallN10(BigInteger mMaxSimulCallN10) {
		this.mMaxSimulCallN10 = mMaxSimulCallN10;
	}

	public BigInteger getMaxSimulCallN4() {
		return mMaxSimulCallN4;
	}

	public void setMaxSimulCallN4(BigInteger mMaxSimulCallN4) {
		this.mMaxSimulCallN4 = mMaxSimulCallN4;
	}

	public BigInteger getMaxSimulTransN5() {
		return mMaxSimulTransN5;
	}

	public void setMaxSimulTransN5(BigInteger mMaxSimulTransN5) {
		this.mMaxSimulTransN5 = mMaxSimulTransN5;
	}

	public Boolean isMcpttUseIssuerUriIdms() {
		return mMcpttUseIssuerUriIdms;
	}

	public void setMcpttUseIssuerUriIdms(Boolean mMcpttUseIssuerUriIdms) {
		this.mMcpttUseIssuerUriIdms = mMcpttUseIssuerUriIdms;
	}

	public String getIdmsAuthEndpoint() {
		return mIdmsAuthEndpoint;
	}

	public void setIdmsAuthEndpoint(String mIdmsAuthEndpoint) {
		this.mIdmsAuthEndpoint = mIdmsAuthEndpoint;
	}

	public String getIdmsTokenEndPoint() {
		return mIdmsTokenEndPoint;
	}

	public void setIdmsTokenEndPoint(String mIdmsTokenEndPoint) {
		this.mIdmsTokenEndPoint = mIdmsTokenEndPoint;
	}

	public Boolean isMcpttEnableCMS() {
		return mMcpttEnableCMS;
	}

	public void setMcpttEnableCMS(boolean mMcpttEnableCMS) {
		this.mMcpttEnableCMS=mMcpttEnableCMS;
	}


	public ExtensibleType getAllowsUserProfile() {
		return mAllowsUserProfile;
	}

	public void setAllowsUserProfile(ExtensibleType mAllowsUserProfile) {
		this.mAllowsUserProfile = mAllowsUserProfile;
	}

	public Short getIndexUserProfile() {
		return mIndexUserProfile;
	}

	public void setIndexUserProfile(Short mIndexUserProfile) {
		this.mIndexUserProfile = mIndexUserProfile;
	}

	public Boolean isMcpttSelfAuthenticationSendTokenFail() {
		return mMcpttSelfAuthenticationSendTokenFail;
	}

	public void setMcpttSelfAuthenticationSendTokenFail(Boolean mMcpttSelfAuthenticationSendTokenFail) {
		this.mMcpttSelfAuthenticationSendTokenFail = mMcpttSelfAuthenticationSendTokenFail;
	}

	public Boolean isMcpttSelfAuthenticationSendTokenRegister() {
		return mMcpttSelfAuthenticationSendTokenRegister;
	}

	public void setMcpttSelfAuthenticationSendTokenRegister(Boolean mMcpttSelfAuthenticationSendTokenRegister) {
		this.mMcpttSelfAuthenticationSendTokenRegister = mMcpttSelfAuthenticationSendTokenRegister;
	}



	public String getMcpttPsiAuthentication() {
		return mMcpttPsiAuthentication;
	}

	public void setMcpttPsiAuthentication(String mMcpttPsiAuthentication) {
		this.mMcpttPsiAuthentication = mMcpttPsiAuthentication;
	}






	public Boolean isMcpttIsSelfAuthentication() {
		return mMcpttIsSelfAuthentication;
	}

	public void setMcpttIsSelfAuthentication(Boolean mMcpttIsSelfAuthentication) {
		this.mMcpttIsSelfAuthentication = mMcpttIsSelfAuthentication;
	}

	public String getMcpttSelfAuthenticationClientId() {
		return mMcpttSelfAuthenticationClientId;
	}

	public void setMcpttSelfAuthenticationClientId(String mMcpttSelfAuthenticationClientId) {
		this.mMcpttSelfAuthenticationClientId = mMcpttSelfAuthenticationClientId;
	}

	public String getMcpttSelfAuthenticationIssuerUri() {
		return mMcpttSelfAuthenticationIssuerUri;
	}

	public void setMcpttSelfAuthenticationIssuerUri(String mMcpttSelfAuthenticationIssuerUri) {
		this.mMcpttSelfAuthenticationIssuerUri = mMcpttSelfAuthenticationIssuerUri;
	}

	public String getMcpttSelfAuthenticationRedirectUri() {
		return mMcpttSelfAuthenticationRedirectUri;
	}

	public void setMcpttSelfAuthenticationRedirectUri(String mMcpttSelfAuthenticationRedirectUri) {
		this.mMcpttSelfAuthenticationRedirectUri = mMcpttSelfAuthenticationRedirectUri;
	}


	public String getMcpttUEId() {
		return mMcpttUEId;
	}

	public void setMcpttUEId(String mMcpttUEId) {
		this.mMcpttUEId = mMcpttUEId;
	}

	public short getT100() {
		return t100;
	}

	public void setT100(short t100) {
		this.t100 = t100;
	}

	public short getT101() {
		return t101;
	}

	public void setT101(short t101) {
		this.t101 = t101;
	}

	public short getT103() {
		return t103;
	}

	public void setT103(short t103) {
		this.t103 = t103;
	}

	public short getT104() {
		return t104;
	}

	public void setT104(short t104) {
		this.t104 = t104;
	}

	public short getT132() {
		return t132;
	}

	public void setT132(short t132) {
		this.t132 = t132;
	}


	public String getCMSXCAPRootURI() {
		return mCMSXCAPRootURI;
	}

	public void setCMSXCAPRootURI(String mCMSXCAPRootURI) {
		this.mCMSXCAPRootURI = mCMSXCAPRootURI;
	}

	public String getGMSXCAPRootURI() {
		return mGMSXCAPRootURI;
	}

	public void setGMSXCAPRootURI(String mGMSXCAPRootURI) {
		this.mGMSXCAPRootURI = mGMSXCAPRootURI;
	}

	public String getKms() {
		return mKms;
	}

	public void setKms(String kms) {
		this.mKms = kms;
	}

	public Boolean isIPv6Preferred() {
		return iPv6Preferred;
	}

	public void setiPv6Preferred(Boolean iPv6Preferred) {
		this.iPv6Preferred = iPv6Preferred;
	}

	public Boolean isRelayService() {
		return relayService;
	}

	public void setRelayService(Boolean relayService) {
		this.relayService = relayService;
	}

	public RelayedMCPTTGroup getRelayedMCPTTGroup() {
		if(relayedMCPTTGroup==null){
			relayedMCPTTGroup=new RelayedMCPTTGroup();
		}
		return relayedMCPTTGroup;
	}

	public void setRelayedMCPTTGroup(RelayedMCPTTGroup relayedMCPTTGroup) {
		this.relayedMCPTTGroup = relayedMCPTTGroup;
	}



	public Boolean isMcpttEnableMbms() {
		return mMcpttEnableMbms;
	}

	public void setMcpttEnableMbms(Boolean mMcpttEnableMbms) {
		this.mMcpttEnableMbms = mMcpttEnableMbms;
	}


	public Boolean isMcpttLocationInfoVersionOld() {
		return mMcpttLocationInfoVersionOld;
	}

	public void setMcpttLocationInfoVersionOld(Boolean mMcpttLocationInfoVersionOld) {
		this.mMcpttLocationInfoVersionOld = mMcpttLocationInfoVersionOld;
	}
	public Boolean isMcpttIsSelfAffiliation() {
		return mMcpttIsSelfAffiliation;
	}

	public void setMcpttIsSelfAffiliation(Boolean mMcpttIsSelfAffiliation) {
		this.mMcpttIsSelfAffiliation = mMcpttIsSelfAffiliation;
	}

	public String getPassword() {
		return mPassword;
	}

	public void setPassword(String pass) {
		this.mPassword = pass;
	}

	public String getMcpttPsiCallPrivate() {
		return mMcpttPsiCallPrivate;
	}

	public void setMcpttPsiCallPrivate(String mMcpttPsiCallPrivate) {
		this.mMcpttPsiCallPrivate = mMcpttPsiCallPrivate;
	}

	public String getMcpttPsiCallGroup() {
		return mMcpttPsiCallGroup;
	}

	public void setMcpttPsiCallGroup(String mMcpttPsiCallGroup) {
		this.mMcpttPsiCallGroup = mMcpttPsiCallGroup;
	}

	public String getMcpttPsiCallPreestablished() {
		return mMcpttPsiCallPreestablished;
	}

	public void setMcpttPsiCallPreestablished(String mMcpttPsiCallPreestablished) {
		this.mMcpttPsiCallPreestablished = mMcpttPsiCallPreestablished;
	}
	public Boolean isMcpttIsEnableAffiliation() {
		return mMcpttIsEnableAffiliation;
	}

	public void setMcpttIsEnableAffiliation(Boolean mcpttIsEnableAffiliation) {
		this.mMcpttIsEnableAffiliation = mcpttIsEnableAffiliation;
	}

	public String getMcpttPsiAffiliation() {
		return mMcpttPsiAffiliation;
	}


	public void setMcpttPsiAffiliation(String mMcpttPsiAffiliation) {
		this.mMcpttPsiAffiliation = mMcpttPsiAffiliation;
	}

	public String getMcpttId() {
		return mMcpttId;
	}

	public void setMcpttId(String mMcpttId) {
		this.mMcpttId = mMcpttId;
	}

	public int getMcpttPriority() {
		return mMcpttPriority;
	}

	public void setMcpttPriority(int mMcpttPriority) {
		this.mMcpttPriority = mMcpttPriority;
	}

	public Boolean isMcpttImplicit() {
		return mMcpttImplicit;
	}

	public void setMcpttImplicit(Boolean mMcpttImplicit) {
		this.mMcpttImplicit = mMcpttImplicit;
	}

	public Boolean isMcpttGranted() {
		return mMcpttGranted;
	}

	public void setMcpttGranted(Boolean mMcpttGranted) {
		this.mMcpttGranted = mMcpttGranted;
	}




	public Boolean isMcpttPrivAnswerMode() {
		return mMcpttPrivAnswerMode;
	}

	public void setMcpttPrivAnswerMode(Boolean mMcpttPrivAnswerMode) {
		this.mMcpttPrivAnswerMode = mMcpttPrivAnswerMode;
	}

	public Boolean isMcpttAnswerMode() {
		return mMcpttAnswerMode;
	}

	public String getMcpttClientId() {
		return mMcpttClientId;
	}

	public void setMcpttClientId(String mcpttClientId) {
		this.mMcpttClientId = mcpttClientId;
	}

	public Boolean isMcpttNameSpace() {
		return mMcpttNameSpace;
	}

	public void setMcpttNameSpace(Boolean mMcpttNameSpace) {
		this.mMcpttNameSpace = mMcpttNameSpace;
	}

	public void setMcpttAnswerMode(Boolean mMcpttAnswerMode) {
		this.mMcpttAnswerMode = mMcpttAnswerMode;
	}



    
    public void setPresenceEnabled(Boolean enabled) {
		this.mPresence = enabled;
	}

	public Boolean isPresenceEnabled() {
		return mPresence;
	}
	
	public void setXcapEnabled(Boolean xcapEnabled) {
		this.mXcapEnabled = xcapEnabled;
	}
	
	public Boolean isXcapEnabled() {
		return mXcapEnabled;
	}
	
	public void setPresenceRLS(Boolean presenceRLS) {
		this.mPresenceRLS = presenceRLS;
	}
	
	public Boolean isPresenceRLS() {
		return mPresenceRLS;
	}
	
	public void setMWI(Boolean MWI) {
		this.mMWI = MWI;
	}
	
	public Boolean isMWI() {
		return mMWI;
	}
	
	public void setPresencePub(Boolean presencePub) {
		this.mPresencePub = presencePub;
	}
	
	public Boolean isPresencePub() {
		return mPresencePub;
	}
	
	public void setIMPI(String IMPI) {
		this.mIMPI = IMPI;
	}
	
	public String getIMPI() {
		return mIMPI;
	}
	
	public void setIMPU(String IMPU) {
		this.mIMPU = IMPU;
	}
	
	public String getIMPU() {
		return mIMPU;
	}
	
	public void setRealm(String realm) {
		if((mRealm = realm) != null){
			if(!mRealm.contains(":")){
				mRealm="sip:"+mRealm;
			}
		}
	}
	
	public String getRealm() {
		return mRealm;
	}
	public String getRealmWhitoutProtocol() {
		if(mRealm!=null){
			if(mRealm.compareToIgnoreCase("sip:")==0){
				Uri realmUri;
				if((realmUri=Uri.parse(mRealm))!=null){
					return realmUri.getHost();
				}
			}else{
				return mRealm;
			}

		}
		return null;
	}
	
	public void setPresenceSub(Boolean presenceSub) {
		this.mPresenceSub = presenceSub;
	}
	
	public Boolean isPresenceSub() {
		return mPresenceSub;
	}
	
	
	public void setPcscfHost(String pcscfHost) {
		this.mPcscfHost = pcscfHost;
	}
	
	public String getPcscfHost() {
		return mPcscfHost;
	}
	
	public void setPcscfPort(int pcscfPort) {
		this.mPcscfPort = pcscfPort;
	}
	
	public int getPcscfPort() {
		return mPcscfPort;
	}
	
	public void setIPVersion(String IPVersion) {
		this.mIPVersion = IPVersion;
	}
	
	public String getIPVersion() {
		return mIPVersion;
	}
	
	public void setTransport(String mTransport) {
		this.mTransport = mTransport;
	}
	public String getTransport() {
		return mTransport;
	}
	
	public void setIPsecSecAgree(Boolean IPsecSecAgree) {
		this.mIPsecSecAgree = IPsecSecAgree;
	}
	
	public Boolean isIPsecSecAgree() {
		return mIPsecSecAgree;
	}

	public void setLocalIP(String localIP) {
		this.mLocalIP = localIP;
	}

	public String getLocalIP() {
		return mLocalIP;
	}

	public void setHackAoR(Boolean mHackAoR) {
		this.mHackAoR = mHackAoR;
	}

	public Boolean isHackAoR() {
		return mHackAoR;
	}


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}




	public static class MCPTTGroupPriority {
		//element identifying an MCPTT group
		@Element(required = false, name = "mcpttGroupID")
		protected String mcpttGroupID;
		//The group with the lowest MCPTTGroupPriorityHierarchy value shall be considered as
		// the group having the lowest priority among the groups. Element that contains an integer
		// that identifies the relative priority level of that MCPTT group with 0 being the lowest
		// priority and 7 being the highest
		@Element(required = false, name = "groupPriorityHierarchy")
		protected BigInteger groupPriorityHierarchy;


		public MCPTTGroupPriority(String mcpttGroupID, BigInteger groupPriorityHierarchy) {
			this.mcpttGroupID = mcpttGroupID;
			this.groupPriorityHierarchy = groupPriorityHierarchy;
		}

		public String getMcpttGroupID() {
			return mcpttGroupID;
		}

		public void setMcpttGroupID(String mcpttGroupID) {
			this.mcpttGroupID = mcpttGroupID;
		}

		public BigInteger getGroupPriorityHierarchy() {
			return groupPriorityHierarchy;
		}

		public void setGroupPriorityHierarchy(BigInteger groupPriorityHierarchy) {
			this.groupPriorityHierarchy = groupPriorityHierarchy;
		}
	}


	public class RelayedMCPTTGroup {
		//attribute identifying an MCPTT group that is allowed to be used via a relay and
		// corresponds to the "MCPTTGroupID" element of subclause 4.2.20 in 3GPP TS 24.483 [4]
		@Element(required = false, name = "McpttGroupID")
		protected String mcpttGroupID;
		//element as specified in 3GPP TS 24.333 [12] which corresponds to the "RelayServiceCode"
		// element of subclause 4.2.21 in 3GPP TS 24.483 [4].
		@Element(required = false, name = "RelayServiceCode")
		protected String relayServiceCode;



		public RelayedMCPTTGroup() {
		}

		public String getMcpttGroupID() {
			return mcpttGroupID;
		}

		public void setMcpttGroupID(String mcpttGroupID) {
			this.mcpttGroupID = mcpttGroupID;
		}

		public String getRelayServiceCode() {
			return relayServiceCode;
		}

		public void setRelayServiceCode(String relayServiceCode) {
			this.relayServiceCode = relayServiceCode;
		}
	}


	public class EntryType {

		@Element(required = false, name = "UriEntry")
		protected String uriEntry;
		@Element(required = false, name = "DisplayName")
		protected String displayName;

		public EntryType(String uriEntry, String displayName) {
			this.uriEntry = uriEntry;
			this.displayName = displayName;
		}

		public EntryType() {
		}

		public String getUriEntry() {
			return uriEntry;
		}

		public void setUriEntry(String uriEntry) {
			this.uriEntry = uriEntry;
		}

		public String getDisplayName() {
			return displayName;
		}

		public void setDisplayName(String displayName) {
			this.displayName = displayName;
		}
	}

	public class EmergencyCallType {

		@Element(required = false, name = "PrivateCancelTimeout")
		protected Long privateCancelTimeout;
		@Element(required = false, name = "GroupTimeLimit")
		protected Long groupTimeLimit;

		public Long getPrivateCancelTimeout() {
			return privateCancelTimeout;
		}

		public void setPrivateCancelTimeout(Long privateCancelTimeout) {
			this.privateCancelTimeout = privateCancelTimeout;
		}

		public Long getGroupTimeLimit() {
			return groupTimeLimit;
		}

		public void setGroupTimeLimit(Long groupTimeLimit) {
			this.groupTimeLimit = groupTimeLimit;
		}
	}

	public class PrivateCallType {
		//element of the <private-call> element contains the value of the hang timer for
		// on-network private calls
		@Element(required = false, name = "HangTime")
		protected Long hangTime;
		//element of the <private-call> element contains the maximum DURATION allowed for
		// an on-network private call with floor control
		@Element(required = false, name = "MaxDurationWithFloorControl")
		protected Long maxDurationWithFloorControl;
		//element of the <private-call> element contains the maximum duration allowed for
		// an on-network private call without floor control
		@Element(required = false, name = "MaxDurationWithoutFloorControl")
		protected Long maxDurationWithoutFloorControl;


		public PrivateCallType() {return;}

		public Long getHangTime() {
			return hangTime;
		}

		public PrivateCallType(Long hangTime, Long maxDurationWithFloorControl, Long maxDurationWithoutFloorControl) {
			this.hangTime = hangTime;
			this.maxDurationWithFloorControl = maxDurationWithFloorControl;
			this.maxDurationWithoutFloorControl = maxDurationWithoutFloorControl;
		}

		public void setHangTime(Long hangTime) {
			this.hangTime = hangTime;
		}

		public Long getMaxDurationWithFloorControl() {
			return maxDurationWithFloorControl;
		}

		public void setMaxDurationWithFloorControl(Long maxDurationWithFloorControl) {
			this.maxDurationWithFloorControl = maxDurationWithFloorControl;
		}

		public Long getMaxDurationWithoutFloorControl() {
			return maxDurationWithoutFloorControl;
		}

		public void setMaxDurationWithoutFloorControl(Long maxDurationWithoutFloorControl) {
			this.maxDurationWithoutFloorControl = maxDurationWithoutFloorControl;
		}
	}
	public class FloorControlQueueType {
		//element of the <floor-control-queue> element contains the maximum size of the floor
		// control queue
		@Element(required = false, name = "Depth")
		protected Integer depth;
		//element of the <floor-control-queue> element contains the maximum time for a user's floor
		// control request to be queued;
		@Element(required = false, name = "MaxUserRequestTime")
		protected Long maxUserRequestTime;

		public FloorControlQueueType(Integer depth, Long maxUserRequestTime) {
			this();
			this.depth = depth;
			this.maxUserRequestTime = maxUserRequestTime;
		}

		public FloorControlQueueType() {
		}
		//
		public Integer getDepth() {
			return depth;
		}

		public void setDepth(Integer depth) {
			this.depth = depth;
		}

		public Long getMaxUserRequestTime() {
			return maxUserRequestTime;
		}

		public void setMaxUserRequestTime(Long maxUserRequestTime) {
			this.maxUserRequestTime = maxUserRequestTime;
		}
	}
	public class FcTimersCountersType {

		//element of the <fc-timers-counters> element contains the maximum allowed time between
		// RTP media packets;
		@Element(required = false, name = "T1EndOfRtpMedia")
		protected Long t1EndOfRtpMedia;
		//element of the <fc-timers-counters> element contains the maximum time the floor control
		// server shall forward RTP media packets after that the permission to send RTP media is revoked
		@Element(required = false, name = "T3StopTalkingGrace")
		protected Long t3StopTalkingGrace;
		//element of the <fc-timers-counters> element contains the retransmission interval of the
		// Floor Idle message when the floor is idle. The maximum number of times the Floor Idle
		// is retransmitted is controlled by the counter in the <C7-floor-idle> element
		@Element(required = false, name = "T7FloorIdle")
		protected Long t7FloorIdle;
		//element of the <fc-timers-counters> element contains the retransmission interval time of
		// the Floor Revoke message until the Floor Release message is received
		@Element(required = false, name = "T8FloorRevoke")
		protected Long t8FloorRevoke;
		//element of the <fc-timers-counters> element contains the maximum allowed time between RTP
		// media packets for the interrupting participant during dual floor operations;
		@Element(required = false, name = "T11EndOfRTPDual")
		protected Long t11EndOfRTPDual;
		//element of the <fc-timers-counters> element contains the transmit time limit in an
		// on-network group for the interrupting participant during dual floor operations
		@Element(required = false, name = "T12StopTalkingDual")
		protected Long t12StopTalkingDual;
		//element of the <fc-timers-counters> element contains the maximum allowed time of silence
		// in a group session involving an MBMS bearer before the MBMS subchannel shall be released
		@Element(required = false, name = "T15Conversation")
		protected Long t15Conversation;
		//element of the <fc-timers-counters> element contains the retransmission interval of the
		// Map Group To Bearer message
		@Element(required = false, name = "T16MapGroupToBearer")
		protected Long t16MapGroupToBearer;
		//element of the <fc-timers-counters> element contains the retransmission interval of the
		// Unmap Group To Bearer message
		@Element(required = false, name = "T17UnmapGroupToBearer")
		protected Long t17UnmapGroupToBearer;
		//element of the <fc-timers-counters> element contains the time the floor control server
		// shall wait before retransmitting the Floor Granted message until the Floor Request
		// message is received. The number of times the Floor Granted message shall be sent is
		// controlled by the counter in <C20-floor-granted> element;
		@Element(required = false, name = "T20FloorGranted")
		protected Long t20FloorGranted;
		//element of the <fc-timers-counters> element contains the retransmission interval of the
		// Connect message. The number of times the Connect message is retransmitted is controlled
		// by the counter in <C56-disconnect> element;
		@Element(required = false, name = "T55Connect")
		protected Long t55Connect;
		//element of the <fc-timers-counters> element contains the retransmission interval of the
		// Disconnect message. The number of times the Disconnect message is retransmitted is
		// controlled by the counter in <C55-connect> element;
		@Element(required = false, name = "T56Disconnect")
		protected Long t56Disconnect;
		//element of the <fc-timers-counters> element contains the maximum number of times the
		// Floor Idle shall be sent
		@Element(required = false, name = "C7FloorIdle")
		protected int c7FloorIdle;
		//element of the <fc-timers-counters> element contains the retransmission interval of the
		// Unmap Group To Bearer message;
		@Element(required = false, name = "C17UnmapGroupToBearer")
		protected int c17UnmapGroupToBearer;
		//element of the <fc-timers-counters> element contains the maximum times the Floor
		// Granted message shall be retransmitted.
		@Element(required = false, name = "C20FloorGranted")
		protected int c20FloorGranted;
		//element of the <fc-timers-counters> element contains the maximum number of times the
		// Connect message is retransmitted;
		@Element(required = false, name = "C55Connect")
		protected int c55Connect;
		//element of the <fc-timers-counters> element contains the maximum number of times the
		// Disconnect message is retransmitted;
		@Element(required = false, name = "C56Disconnect")
		protected int c56Disconnect;


		public Long getT1EndOfRtpMedia() {
			return t1EndOfRtpMedia;
		}

		public void setT1EndOfRtpMedia(Long t1EndOfRtpMedia) {
			this.t1EndOfRtpMedia = t1EndOfRtpMedia;
		}

		public Long getT3StopTalkingGrace() {
			return t3StopTalkingGrace;
		}

		public void setT3StopTalkingGrace(Long t3StopTalkingGrace) {
			this.t3StopTalkingGrace = t3StopTalkingGrace;
		}

		public Long getT7FloorIdle() {
			return t7FloorIdle;
		}

		public void setT7FloorIdle(Long t7FloorIdle) {
			this.t7FloorIdle = t7FloorIdle;
		}

		public Long getT8FloorRevoke() {
			return t8FloorRevoke;
		}

		public void setT8FloorRevoke(Long t8FloorRevoke) {
			this.t8FloorRevoke = t8FloorRevoke;
		}

		public Long getT11EndOfRTPDual() {
			return t11EndOfRTPDual;
		}

		public void setT11EndOfRTPDual(Long t11EndOfRTPDual) {
			this.t11EndOfRTPDual = t11EndOfRTPDual;
		}

		public Long getT12StopTalkingDual() {
			return t12StopTalkingDual;
		}

		public void setT12StopTalkingDual(Long t12StopTalkingDual) {
			this.t12StopTalkingDual = t12StopTalkingDual;
		}

		public Long getT15Conversation() {
			return t15Conversation;
		}

		public void setT15Conversation(Long t15Conversation) {
			this.t15Conversation = t15Conversation;
		}

		public Long getT16MapGroupToBearer() {
			return t16MapGroupToBearer;
		}

		public void setT16MapGroupToBearer(Long t16MapGroupToBearer) {
			this.t16MapGroupToBearer = t16MapGroupToBearer;
		}

		public Long getT17UnmapGroupToBearer() {
			return t17UnmapGroupToBearer;
		}

		public void setT17UnmapGroupToBearer(Long t17UnmapGroupToBearer) {
			this.t17UnmapGroupToBearer = t17UnmapGroupToBearer;
		}

		public Long getT20FloorGranted() {
			return t20FloorGranted;
		}

		public void setT20FloorGranted(Long t20FloorGranted) {
			this.t20FloorGranted = t20FloorGranted;
		}

		public Long getT55Connect() {
			return t55Connect;
		}

		public void setT55Connect(Long t55Connect) {
			this.t55Connect = t55Connect;
		}

		public Long getT56Disconnect() {
			return t56Disconnect;
		}

		public void setT56Disconnect(Long t56Disconnect) {
			this.t56Disconnect = t56Disconnect;
		}

		public int getC7FloorIdle() {
			return c7FloorIdle;
		}

		public void setC7FloorIdle(int c7FloorIdle) {
			this.c7FloorIdle = c7FloorIdle;
		}

		public int getC17UnmapGroupToBearer() {
			return c17UnmapGroupToBearer;
		}

		public void setC17UnmapGroupToBearer(int c17UnmapGroupToBearer) {
			this.c17UnmapGroupToBearer = c17UnmapGroupToBearer;
		}

		public int getC20FloorGranted() {
			return c20FloorGranted;
		}

		public void setC20FloorGranted(int c20FloorGranted) {
			this.c20FloorGranted = c20FloorGranted;
		}

		public int getC55Connect() {
			return c55Connect;
		}

		public void setC55Connect(int c55Connect) {
			this.c55Connect = c55Connect;
		}

		public int getC56Disconnect() {
			return c56Disconnect;
		}

		public void setC56Disconnect(int c56Disconnect) {
			this.c56Disconnect = c56Disconnect;
		}
	}
	public class SignallingProtectionType {
		//element of the <signalling-protection> element contains a boolean indicating whether
		// confidentiality protection of MCPTT signalling is enabled or disabled between the MCPTT
		// client and MCPTT server;
		@Element(required = false, name = "ConfidentialityProtection")
		protected Boolean confidentialityProtection;
		//element of the <signalling-protection> element contains a boolean indicating whether
		// integrity protection of MCPTT signalling is enabled or disabled between the MCPTT client
		// and MCPTT server;
		@Element(required = false, name = "IntegrityProtection")
		protected Boolean integrityProtection;


		public Boolean getConfidentialityProtection() {
			return confidentialityProtection;
		}

		public void setConfidentialityProtection(Boolean confidentialityProtection) {
			this.confidentialityProtection = confidentialityProtection;
		}

		public Boolean getIntegrityProtection() {
			return integrityProtection;
		}

		public void setIntegrityProtection(Boolean integrityProtection) {
			this.integrityProtection = integrityProtection;
		}
	}



	@Override
	public boolean equals(@NonNull Object other){

		if (other!=null && other==this)return true;
		if (!(other instanceof NgnSipPrefrences))return false;
		return super.equals(other);


	}

	public String getMcpttPsiCMS() {
		return mMcpttPsiCMS;
	}

	public void setMcpttPsiCMS(String mcpttPsiCMS) {
		this.mMcpttPsiCMS = mcpttPsiCMS;
	}

	public Boolean isMcpttEnableSubcriptionCMS() {
		return mMcpttEnableSubcriptionCMS;
	}

	public void setMcpttEnableSubcriptionCMS(Boolean mMcpttEnableSubcriptionCMS) {
		this.mMcpttEnableSubcriptionCMS = mMcpttEnableSubcriptionCMS;
	}


	public String getMcpttPsiGMS() {
		return mMcpttPsiGMS;
	}

	public void setMcpttPsiGMS(String mcpttPsiGMS) {
		this.mMcpttPsiGMS = mcpttPsiGMS;
	}

	public Boolean isMcpttEnableSubcriptionGMS() {
		return mMcpttEnableSubcriptionGMS;
	}

	public void setMcpttEnableSubcriptionGMS(Boolean mMcpttEnableSubcriptionGMS) {
		this.mMcpttEnableSubcriptionGMS = mMcpttEnableSubcriptionGMS;
	}


}
