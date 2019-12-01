/*

 *  Copyright (C) 2017, University of the Basque Country (UPV/EHU)
 *
 * Contact for licensing options: <licensing-mcpttclient(at)mcopenplatform(dot)com>
 *
 * This file is part of MCOP MCPTT Client
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

package org.doubango.ngn.services.impl.location;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.CellIdentityLte;
import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.doubango.ngn.BuildConfig;
import org.doubango.ngn.NgnApplication;
import org.doubango.ngn.datatype.location.DataCell;
import org.doubango.ngn.datatype.location.DataReport;
import org.doubango.ngn.datatype.location.InfoReport;
import org.doubango.ngn.datatype.location.LocationDataDegree;
import org.doubango.ngn.datatype.location.TypeMcpttSignallingEvent;
import org.doubango.ngn.datatype.mcpttloc.LocationInfo;
import org.doubango.ngn.datatype.mcpttloc.LocationType;
import org.doubango.ngn.datatype.mcpttloc.ProtectionType;
import org.doubango.ngn.datatype.mcpttloc.TCellChange;
import org.doubango.ngn.datatype.mcpttloc.TConfigurationType;
import org.doubango.ngn.datatype.mcpttloc.TEllipsoidArcType;
import org.doubango.ngn.datatype.mcpttloc.TEmptyType;
import org.doubango.ngn.datatype.mcpttloc.TGeographicalAreaChange;
import org.doubango.ngn.datatype.mcpttloc.TGeographicalAreaDef;
import org.doubango.ngn.datatype.mcpttloc.TIntegerAttributeType;
import org.doubango.ngn.datatype.mcpttloc.TPlmnChangeType;
import org.doubango.ngn.datatype.mcpttloc.TPlmnIdentity;
import org.doubango.ngn.datatype.mcpttloc.TPointCoordinate;
import org.doubango.ngn.datatype.mcpttloc.TPolygonAreaType;
import org.doubango.ngn.datatype.mcpttloc.TRequestType;
import org.doubango.ngn.datatype.mcpttloc.TRequestedLocationType;
import org.doubango.ngn.datatype.mcpttloc.TSignallingEventType;
import org.doubango.ngn.datatype.mcpttloc.TSpecificAreaType;
import org.doubango.ngn.datatype.mcpttloc.TSpecificCellType;
import org.doubango.ngn.datatype.mcpttloc.TTrackingAreaChangeType;
import org.doubango.ngn.datatype.mcpttloc.TTrackingAreaIdentity;
import org.doubango.ngn.datatype.mcpttloc.TriggeringCriteriaType;
import org.doubango.utils.Utils;

import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class LocationServer implements android.location.LocationListener, LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private final static String TAG = Utils.getTAG(LocationServer.class.getCanonicalName());

    public static final String ACTION_CONFIGURE = TAG + ".ACTION_CONFIGURE";

    private static final int NUM_CHARS_ECI = 28;
    private static final int NUM_CHARS_TAC = 16;
    private static final int NUM_CHARS_MCC = 3;
    private static final int NUM_CHARS_MNC = 3;
    private static final int MAX_NUM_POINT_POLYGON = 20;
    private static final int MIN_NUM_POINT_POLYGON = 3;
    private static final int MIN_INTERVAL_LOCATION_MSEG = 1000;
    private com.google.android.gms.location.FusedLocationProviderClient mFusedLocationClient;
    private static final boolean USE_NEW_VERSION_LOCATION = true;


    private Context context;
    private boolean isStart;

    private DataReport dataReportEmergency;
    private DataReport dataReportNoEmergency;

    private LocationInfo locationInfo;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Set<TypeMcpttSignallingEvent> currentTypeMcpttSignallingEvents;
    private Map<String, List<LocationDataDegree>> polynomPoints;


    //CurrentData
    private Location currientLocation;
    private Location lastLocation = null;
    private DataCell lastDataCell;
    private Calendar lastCalendar;
    private OnReportListener onReportListener;
    private Handler handlerService;
    private long TIME_INTERVAL = 10000;//10seg
    private long TIME_INTERVAL_MIN = 5000;//5seg
    private boolean isStartLoclization;
    private PendingResult<Status> statusPendingResultLocation;
    private static LocationServer mLocationServer;
    private Runnable runnableService;
    private boolean isOldVersion = false;
    private LocationCallback mLocationCallback;


    public LocationServer() {
    }

    protected static LocationServer getInstance(Context context, Intent intent, boolean isOldVersion) {
        if (mLocationServer == null || intent != null) {
            mLocationServer = new LocationServer(context, intent, isOldVersion);
        }
        return mLocationServer;
    }


    protected LocationServer(Context context, Intent intent, boolean oldVersion) {
        Log.d(TAG, "Start service location.");
        if (context == null || intent == null) return;
        polynomPoints = new HashMap<>();
        this.context = context;
        byte[] datasConfigure = intent.getByteArrayExtra(ACTION_CONFIGURE);
        Boolean isConfigure = configure(datasConfigure);
        isStartLoclization = false;
        isOldVersion = oldVersion;
        if (isConfigure) {
            if (BuildConfig.DEBUG) Log.d(TAG, "Location is Configure");


            //Init Calendar
            lastCalendar = Calendar.getInstance();
            //Location start
            // Create an instance of GoogleAPIClient.


            if (mGoogleApiClient == null) {
                if (BuildConfig.DEBUG) Log.d(TAG, "Start new version Google API.");
                mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
                startLocationUpdates(USE_NEW_VERSION_LOCATION);
            }

            if (mGoogleApiClient == null) {
                if (BuildConfig.DEBUG) Log.d(TAG, "Start Google API.");
                mGoogleApiClient = new GoogleApiClient.Builder(context)
                        .addConnectionCallbacks(this)
                        .addOnConnectionFailedListener(this)
                        .addApi(LocationServices.API)
                        .build();
                mGoogleApiClient.connect();
            }
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                configureLastLocation(USE_NEW_VERSION_LOCATION);
            }

            lastDataCell = getCurrentDataCell(context);

            isStart = true;
            loop();
            if (onReportListener != null) onReportListener.onConfiguration(true);

            return;

        } else {
            if (BuildConfig.DEBUG) Log.e(TAG, "Location No configuration.");
            if (onReportListener != null) onReportListener.onConfiguration(false);
        }


    }



    protected static boolean isRequestReport(Context context, byte[] datasReport) {
        return getLocationInfoRequestReport(context, datasReport) != null;

    }


    private static LocationInfo getLocationInfoRequestReport(Context context, byte[] datasReport) {
        if (datasReport == null || datasReport.length == 0) return null;
        try {
            //Log.d(TAG,"receive new data info for location: "+new String(datasReport));
            LocationInfo locationInfoRequest = LocationUtils.getLocationInfo(datasReport, context);
            TRequestType requestType;
            if ((requestType = locationInfoRequest.getRequest()) != null && locationInfoRequest.getConfiguration() == null) {
                if (requestType != null) {
                    return locationInfoRequest;
                }
            }
        } catch (Exception e) {

            Log.e(TAG, "Error in Request XML:" + e.getMessage() + " " + datasReport);
        }
        return null;

    }


    protected boolean sendRequestNow(Context context, byte[] dataRequest) {
        try {
            if (dataRequest == null || dataRequest.length == 0 || context == null) return false;
            LocationInfo locationInfoRequest = getLocationInfoRequestReport(context, dataRequest);
            if (locationInfoRequest == null || dataReportNoEmergency == null) return false;
            Object locationInfoReport = createReport(
                    dataReportNoEmergency,
                    null,
                    locationInfoRequest.getRequest().getRequestId(),
                    context,
                    onReportListener != null ? onReportListener.isEmergency() : false,
                    isOldVersion);
            if (isStart() && onReportListener != null)
                onReportListener.onReport(LocationUtils.getLocationInfoToString(locationInfoReport, context));
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Error send report 1:" + e.getMessage());
        }
        return false;
    }

    private boolean sendRequestNow(Context context, String requestId, boolean oldVersion) {
        try {
            if (requestId == null || requestId.isEmpty() || context == null || dataReportNoEmergency == null)
                return false;
            Object locationInfoReport = createReport(dataReportNoEmergency,
                    null,
                    requestId,
                    context,
                    onReportListener != null ? onReportListener.isEmergency() : false,
                    oldVersion);
            if (onReportListener != null)
                onReportListener.onReport(LocationUtils.getLocationInfoToString(locationInfoReport, context));
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Error send report:" + e.getMessage());
        }
        return false;
    }

    private void loop() {
        if (dataReportNoEmergency == null) {
            Log.d(TAG, "No dataReportNoEmergency configured.");
            return;
        }
        handlerService = new Handler();
        runnableService = new Runnable() {
            @Override
            public void run() {
                if (isStart) {
                    //TriggeringCriteria
                    if (locationInfo != null && dataReportNoEmergency != null) {
                        List<String> triggersNow = isExecuteTriggers(context);
                        if (triggersNow != null && triggersNow.size() > 0) {
                            sendReportNow(triggersNow);
                        }
                    }
                    loop();
                }
            }
        };

        long intervalTimemseg = MIN_INTERVAL_LOCATION_MSEG;
        if (dataReportNoEmergency != null && (dataReportNoEmergency.getMinimumIntervalLength() * 1000) >= MIN_INTERVAL_LOCATION_MSEG) {
            intervalTimemseg = (dataReportNoEmergency.getMinimumIntervalLength() * 1000);
        }
        handlerService.postDelayed(runnableService
                , intervalTimemseg);
    }

    protected boolean sendReportNow(List<String> triggersNow) {
        //SendReport

        Object locationInfo = createReport(dataReportNoEmergency,
                triggersNow,
                context,
                onReportListener != null ? onReportListener.isEmergency() : false,
                isOldVersion);
        if (locationInfo != null) try {
            Log.d(TAG, "Triggers set.");
            String reportString = null;
            if (onReportListener != null)
                onReportListener.onReport(LocationUtils.getLocationInfoToString(locationInfo, context));

        } catch (Exception e) {
            Log.e(TAG, "Error generating XML Report:" + e.toString());
            return false;
        }
        return true;
    }


    /**
     * Is the location service started?
     * @return
     */
    public boolean isStart() {
        return isStart;
    }

    //INIT event for send report
    public interface OnReportListener {
        void onReport(String xmlReport);

        void onConfiguration(Boolean isConfiguration);

        void errorLocation(String error, int code);

        boolean isEmergency();
    }

    public void setOnClickItemAddListener(OnReportListener onReportListener) {
        this.onReportListener = onReportListener;
    }

    //END event for send report


    protected String createReport(Context context) {
        Object locationInfoReport = createReport(dataReportNoEmergency,
                null,
                context,
                onReportListener != null ? onReportListener.isEmergency() : false,
                isOldVersion);
        try {
            return locationInfoReport != null ? LocationUtils.getLocationInfoToString(locationInfoReport, context) : null;
        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.e(TAG, "Error in parse report location: " + e.getMessage());
        }
        return null;
    }


    private Object createReport(DataReport dataReport, List<String> triggers, Context context, boolean emergencyAlert, boolean oldVersion) {
        return createReport(dataReport, triggers, null, context, emergencyAlert, oldVersion);
    }

    private Object createReport(DataReport dataReport, List<String> triggers, String reportID, Context context, boolean emergencyAlert, boolean oldVersion) {
        if (BuildConfig.DEBUG) Log.d(TAG, "createReport location");
        if (oldVersion) {
            org.doubango.ngn.datatype.mcpttlocOld.LocationInfo locationInfo = new org.doubango.ngn.datatype.mcpttlocOld.LocationInfo();
            org.doubango.ngn.datatype.mcpttlocOld.TReportType tReportType = new org.doubango.ngn.datatype.mcpttlocOld.TReportType();
            (locationInfo).setReport(tReportType);
            if (triggers != null)
                (tReportType).setTriggerId(triggers);
            String reportType = "NonEmergency";
            if (emergencyAlert) reportType = "Emergency";
            (tReportType).setReportType(reportType);
            if (reportID != null && !reportID.isEmpty()) (tReportType).setReportID(reportID);
            if (dataReport.getInfoReport() != null) {
                org.doubango.ngn.datatype.mcpttlocOld.TCurrentLocationType currentLocationType = new org.doubango.ngn.datatype.mcpttlocOld.TCurrentLocationType();
                (tReportType).setCurrentLocation((currentLocationType));
                if (dataReport.getInfoReport().contains(InfoReport.GEOGRAPHICALCORDINATE)) {
                    if (currientLocation != null) {
                        org.doubango.ngn.datatype.mcpttlocOld.TPointCoordinate tPointCoordinate = LocationUtils.locationDegreeTo3gppIanosOld(currientLocation);
                        if (BuildConfig.DEBUG)
                            Log.d(TAG, "current location:" + tPointCoordinate.getLatitude() + "," + tPointCoordinate.getLongitude());
                        (currentLocationType).setCurrentCoordinate(tPointCoordinate);
                    } else {
                        if (BuildConfig.DEBUG)
                            Log.d(TAG, "currientLocation is null. And it can not send the current location");
                    }
                }
                if (dataReport.getInfoReport().contains(InfoReport.MBMSSAID)) {
                    (currentLocationType).setMbmsSaId(2);//TODO: android API does not support it
                }
                if (dataReport.getInfoReport().contains(InfoReport.MBSFNAREA)) {
                    (currentLocationType).setMbsfnArea(1);//TODO: android API does not support it
                }
                if (dataReport.getInfoReport().contains(InfoReport.SERVICEECGI)) {
                    DataCell currentDataCell = getCurrentDataCell(context);
                    if (currentDataCell != null) {
                        (currentLocationType).setCurrentServingEcgi(currentDataCell.getECGI());
                    }

                }
                if (dataReport.getInfoReport().contains(InfoReport.NEIGHBOURINGECGI)) {
                    List<DataCell> dataCells = getDataCell(context);
                    ArrayList<String> neighbouringEcgis = new ArrayList<>();
                    if (dataCells != null)
                        for (int con1 = 0, con = 0; con < dataCells.size() && con1 < dataReport.getNumNeighbour(); con++) {
                            if (!dataCells.get(con).isRegister()) {
                                con1++;
                                Log.d(TAG, dataCells.size() + " con: " + con);
                                //New
                                neighbouringEcgis.add(dataCells.get(con).getECGI());
                            }
                        }
                    (currentLocationType).setNeighbouringEcgi(neighbouringEcgis);
                }

            }
            return locationInfo;
        } else {
            org.doubango.ngn.datatype.mcpttloc.LocationInfo locationInfo = new org.doubango.ngn.datatype.mcpttloc.LocationInfo();
            org.doubango.ngn.datatype.mcpttloc.TReportType tReportType = new org.doubango.ngn.datatype.mcpttloc.TReportType();
            (locationInfo).setReport(tReportType);
            if (triggers != null)
                (tReportType).setTriggerId(triggers);
            String reportType = "NonEmergency";
            if (emergencyAlert) reportType = "Emergency";
            (tReportType).setReportType(reportType);
            if (reportID != null && !reportID.isEmpty()) (tReportType).setReportID(reportID);
            if (dataReport.getInfoReport() != null) {
                org.doubango.ngn.datatype.mcpttloc.TCurrentLocationType currentLocationType = new org.doubango.ngn.datatype.mcpttloc.TCurrentLocationType();
                (tReportType).setCurrentLocation((currentLocationType));
                if (dataReport.getInfoReport().contains(InfoReport.GEOGRAPHICALCORDINATE)) {
                    if (currientLocation != null) {
                        org.doubango.ngn.datatype.mcpttloc.TPointCoordinate tPointCoordinate = LocationUtils.locationDegreeTo3gppIanos(currientLocation);
                        if (BuildConfig.DEBUG)
                            Log.d(TAG, "current location:" + tPointCoordinate.getLatitude() + "," + tPointCoordinate.getLongitude());
                        (currentLocationType).setCurrentCoordinate(tPointCoordinate);
                    } else {
                        if (BuildConfig.DEBUG)
                            Log.d(TAG, "currientLocation is null. And it can not send the current location");
                    }
                } else {
                    if (BuildConfig.DEBUG)
                        Log.d(TAG, "it do not contains" + " GEOGRAPHICALCORDINATE");
                }
                if (dataReport.getInfoReport().contains(InfoReport.MBMSSAID)) {
                    //New
                    LocationType locationType = new LocationType();
                    locationType.setType(ProtectionType.Normal);
                    locationType.setSaId(2);
                    (currentLocationType).setMbmsSaId(locationType);//TODO: android API does not support it

                } else {
                    if (BuildConfig.DEBUG) Log.d(TAG, "it do not contains" + " MBMSSAID");
                }
                if (dataReport.getInfoReport().contains(InfoReport.MBSFNAREA)) {
                    //New
                    LocationType locationType = new LocationType();
                    locationType.setType(ProtectionType.Normal);
                    locationType.setMbsfnAreaId(1);
                    (currentLocationType).setMbsfnArea(locationType);//TODO: android API does not support it
                } else {
                    if (BuildConfig.DEBUG) Log.d(TAG, "it do not contains" + " MBSFNAREA");
                }
                if (dataReport.getInfoReport().contains(InfoReport.SERVICEECGI)) {
                    DataCell currentDataCell = getCurrentDataCell(context);
                    if (currentDataCell != null) {
                        LocationType locationType = new LocationType();
                        locationType.setType(ProtectionType.Normal);
                        locationType.setEcgi(currentDataCell.getECGI());
                        (currentLocationType).setCurrentServingEcgi(locationType);
                    }

                } else {
                    if (BuildConfig.DEBUG) Log.d(TAG, "it do not contains" + " SERVICEECGI");
                }
                if (dataReport.getInfoReport().contains(InfoReport.NEIGHBOURINGECGI)) {
                    List<DataCell> dataCells = getDataCell(context);
                    ArrayList<LocationType> neighbouringEcgis = new ArrayList<>();
                    if (dataCells != null)
                        for (int con1 = 0, con = 0; con < dataCells.size() && con1 < dataReport.getNumNeighbour(); con++) {
                            if (!dataCells.get(con).isRegister()) {
                                con1++;
                                Log.d(TAG, dataCells.size() + " con: " + con);
                                //New
                                LocationType locationType = new LocationType();
                                locationType.setType(ProtectionType.Normal);
                                locationType.setEcgi(dataCells.get(con).getECGI());
                                neighbouringEcgis.add(locationType);
                            }
                        }
                    (currentLocationType).setNeighbouringEcgi(neighbouringEcgis);
                } else {
                    if (BuildConfig.DEBUG) Log.d(TAG, "it do not contains" + " NEIGHBOURINGECGI");
                }

            }
            return locationInfo;
        }

    }

    private List<String> isExecuteTriggers(Context context) {
        TConfigurationType configure = locationInfo.getConfiguration();
        ArrayList<String> triggers = new ArrayList<>();
        TriggeringCriteriaType triggersCriterial;
        if (configure != null && (triggersCriterial = configure.getTriggeringCriteria()) != null) {
            //CellChange
            DataCell currientDataCell = getCurrentDataCell(context);

            //ECGI
            TCellChange cellChange;
            if ((cellChange = triggersCriterial.getCellChange()) != null) {
                // Any Change ECGI
                if (cellChange.getAnyCellChange() != null) {
                    if (currientDataCell != null) {
                        if (lastDataCell == null || !currientDataCell.getECGI().equals(lastDataCell.getECGI())) {
                            triggers.add(cellChange.getAnyCellChange().getTriggerId());
                        }
                    }
                }
                //Enter cell ECGI
                List<TSpecificCellType> specificCellTypes;
                if ((specificCellTypes = cellChange.getEnterSpecificCell()) != null) {
                    if (currientDataCell != null)
                        for (TSpecificCellType specificCellType : specificCellTypes) {
                            if (currientDataCell.getECGI().equals(specificCellType.getValue().trim())) {
                                triggers.add(specificCellType.getTriggerId());
                            }
                        }
                }
                //Exit Cell ECGI
                if ((specificCellTypes = cellChange.getExitSpecificCell()) != null) {

                    if (lastDataCell != null)
                        for (TSpecificCellType specificCellType : specificCellTypes) {
                            if (lastDataCell.getECGI().equals(specificCellType.getValue().trim())) {
                                triggers.add(specificCellType.getTriggerId());
                            }
                        }
                }
            }

            //Tracking Area
            TTrackingAreaChangeType trackingAreaChangeType;
            if ((trackingAreaChangeType = triggersCriterial.getTrackingAreaChange()) != null) {
                // Any Change TrackingArea
                if (trackingAreaChangeType.getAnyTrackingAreaChange() != null) {
                    if (currientDataCell != null) {
                        if (lastDataCell == null || !currientDataCell.getTrackingArea().equals(lastDataCell.getTrackingArea())) {
                            triggers.add(trackingAreaChangeType.getAnyTrackingAreaChange().getTriggerId());
                        }
                    }
                }
                //Enter Tracking Area
                List<TTrackingAreaIdentity> trackingAreaIdentities;
                if ((trackingAreaIdentities = trackingAreaChangeType.getEnterSpecificTrackingArea()) != null) {
                    if (currientDataCell != null)
                        for (TTrackingAreaIdentity trackingAreaIdentity : trackingAreaIdentities) {
                            if (currientDataCell.getTrackingArea().equals(trackingAreaIdentity.getValue().trim())) {
                                triggers.add(trackingAreaIdentity.getTriggerId());
                            }
                        }
                }
                //Exit Tracking Area
                if ((trackingAreaIdentities = trackingAreaChangeType.getExitSpecificTrackingArea()) != null) {
                    if (lastDataCell != null)
                        for (TTrackingAreaIdentity trackingAreaIdentity : trackingAreaIdentities) {
                            if (lastDataCell.getTrackingArea().equals(trackingAreaIdentity.getValue().trim())) {
                                triggers.add(trackingAreaIdentity.getTriggerId());
                            }
                        }
                }
            }

            //PLMN ID
            TPlmnChangeType plmnChangeType;
            if ((plmnChangeType = triggersCriterial.getPlmnChange()) != null) {
                // Any Change PLMN ID
                if (plmnChangeType.getAnyPlmnChange() != null) {
                    if (currientDataCell != null) {
                        //All change
                        if (lastDataCell == null || !currientDataCell.getPLMNId().equals(lastDataCell.getPLMNId())) {
                            triggers.add(plmnChangeType.getAnyPlmnChange().getTriggerId());
                        }
                    }
                }
                //Enter PLMN ID
                List<TPlmnIdentity> plmnIdentities;
                if ((plmnIdentities = plmnChangeType.getEnterSpecificPlmn()) != null) {
                    if (currientDataCell != null)
                        for (TPlmnIdentity plmnIdentity : plmnIdentities) {
                            if (currientDataCell.getPLMNId().equals(plmnIdentity.getValue().trim())) {
                                triggers.add(plmnIdentity.getTriggerId());
                            }
                        }
                }
                //Exit PLMN ID
                if ((plmnIdentities = plmnChangeType.getExitSpecificPlmn()) != null) {
                    if (lastDataCell != null) for (TPlmnIdentity plmnIdentity : plmnIdentities) {
                        if (lastDataCell.getPLMNId().equals(plmnIdentity.getValue().trim())) {
                            triggers.add(plmnIdentity.getTriggerId());
                        }
                    }
                }
            }
            /*
            IMPORTANT: MBMS SA and MBSF AREA not supported by android
             */

            //Periodic Report
            TIntegerAttributeType integerAttributeType;
            if ((integerAttributeType = triggersCriterial.getPeriodicReport()) != null) {
                if (integerAttributeType.getValue() > 0 &&
                        (integerAttributeType.getValue() * 1000) <
                                (Calendar.getInstance().getTimeInMillis() - lastCalendar.getTimeInMillis())) {
                    triggers.add(integerAttributeType.getTriggerId());
                }
            }

            //Travelled Distance
            if ((integerAttributeType = triggersCriterial.getTravelledDistance()) != null) {
                double distanceTravel = LocationUtils.distance(lastLocation, currientLocation);
                if (integerAttributeType.getValue() > 0 && distanceTravel > 0 && integerAttributeType.getValue() <= distanceTravel) {
                    triggers.add(integerAttributeType.getTriggerId());
                }
            }

            //Mcptt Signalling Event
            TSignallingEventType signallingEventType;
            if ((signallingEventType = triggersCriterial.getMcpttSignallingEvent()) != null &&
                    currentTypeMcpttSignallingEvents != null &&
                    !currentTypeMcpttSignallingEvents.isEmpty()) {
                //Init all sessions
                if (signallingEventType.getInitialLogOn() != null && currentTypeMcpttSignallingEvents.contains(TypeMcpttSignallingEvent.LOG_ON)) {
                    triggers.add(signallingEventType.getInitialLogOn().getTriggerId());
                }
                //Init group call no emergency
                if (signallingEventType.getGroupCallNonEmergency() != null && currentTypeMcpttSignallingEvents.contains(TypeMcpttSignallingEvent.GROUP_CALL_NON_EMERGENCY)) {
                    triggers.add(signallingEventType.getGroupCallNonEmergency().getTriggerId());
                }
                //Init private call no emergency
                if (signallingEventType.getPrivateCallNonEmergency() != null && currentTypeMcpttSignallingEvents.contains(TypeMcpttSignallingEvent.PRIVATE_CALL_NON_EMERGENCY)) {
                    triggers.add(signallingEventType.getPrivateCallNonEmergency().getTriggerId());
                }
                //Init all sessions
                if (signallingEventType.getLocationConfigurationReceived() != null && currentTypeMcpttSignallingEvents.contains(TypeMcpttSignallingEvent.LOCATION_CONFIGURATION_RECIVED)) {
                    triggers.add(signallingEventType.getLocationConfigurationReceived().getTriggerId());

                }
            }


            //Geographical Area Change
            TGeographicalAreaChange geographicalAreaChange;
            if ((geographicalAreaChange = triggersCriterial.getGeographicalAreaChange()) != null) {
                //Enter Specific Area Type
                int contain = triggers.size();
                TSpecificAreaType specificAreaType;
                if ((specificAreaType = geographicalAreaChange.getEnterSpecificAreaType()) != null) {
                    triggers = compareSpecificAreaType(specificAreaType, triggers, true);
                }
                //Exit Specific Area Type
                if ((specificAreaType = geographicalAreaChange.getExitSpecificAreaType()) != null) {
                    triggers = compareSpecificAreaType(specificAreaType, triggers, false);
                }
                //Any Area change
                if (geographicalAreaChange.getAnyAreaChange() != null && triggers.size() > contain) {
                    triggers.add(geographicalAreaChange.getAnyAreaChange().getTriggerId());
                }

            }


        }
        return triggers;
    }

    private ArrayList<String> compareSpecificAreaType(TSpecificAreaType specificAreaType, ArrayList<String> triggers, Boolean control) {
        TGeographicalAreaDef geographicalAreaDef;
        if ((geographicalAreaDef = specificAreaType.getGeographicalArea()) != null) {
            TPolygonAreaType polygonAreaType;
            TEllipsoidArcType ellipsoidArcType;
            if ((polygonAreaType = geographicalAreaDef.getPolygonArea()) != null) {
                //Polygon
                List<TPointCoordinate> polygonAreaTypeCorner;
                if ((polygonAreaTypeCorner = polygonAreaType.getCorner()) != null && polygonAreaTypeCorner.size() >= MIN_NUM_POINT_POLYGON && polygonAreaTypeCorner.size() <= MAX_NUM_POINT_POLYGON) {
                    List<LocationDataDegree> locationDataDegrees;
                    if (polynomPoints != null && specificAreaType.getTriggerId() != null && polynomPoints.get(specificAreaType.getTriggerId()) != null) {
                        locationDataDegrees = polynomPoints.get(specificAreaType.getTriggerId());
                    } else {
                        locationDataDegrees = LocationUtils.location3gppIanosToDegrees(polygonAreaTypeCorner);
                    }

                    if (currientLocation != null && lastLocation != null && locationDataDegrees != null) {
                        if (LocationUtils.isEnterOrExitOfPolygon(lastLocation, currientLocation, locationDataDegrees) == control) {
                            Log.d(TAG, "Enter Trigger or exit from polygon.");
                            triggers.add(specificAreaType.getTriggerId());
                        }
                    }
                } else if (polygonAreaTypeCorner != null) {
                    Log.e(TAG, "Invalid Num points: " + polygonAreaTypeCorner.size());
                }
            } else if ((ellipsoidArcType = geographicalAreaDef.getEllipsoidArcArea()) != null) {
                TPointCoordinate pointCoordinateCenter = ellipsoidArcType.getCenter();
                LocationDataDegree locationDataDegreeCenter;
                double radiuElip = ellipsoidArcType.getRadius();
                double offAngle = ellipsoidArcType.getOffsetAngle();
                double includeAngle = ellipsoidArcType.getIncludedAngle();
                if (pointCoordinateCenter != null && radiuElip > 0 && (locationDataDegreeCenter = LocationUtils.location3gppIanosToDegree(pointCoordinateCenter)) != null && offAngle > 0 && includeAngle > 0 && currientLocation != null) {
                    radiuElip = (radiuElip * 5 / 1000);
                    offAngle *= 2;
                    includeAngle *= 2;
                    if (LocationUtils.isEnterOrExitOfElipti(lastLocation, currientLocation, locationDataDegreeCenter, radiuElip, offAngle, includeAngle) == control) {
                        triggers.add(specificAreaType.getTriggerId());
                    }
                }

            }
        }
        return triggers;
    }


    //INIT Mcptt Signalling Event

    public void onLocationConfigurationReceived() {
        addNewTypeMcpttSignalllingEvent(TypeMcpttSignallingEvent.LOCATION_CONFIGURATION_RECIVED);
    }

    public void onCallPrivateNonEmergent() {
        addNewTypeMcpttSignalllingEvent(TypeMcpttSignallingEvent.PRIVATE_CALL_NON_EMERGENCY);
    }

    public void onCallGroupNonEmergent() {
        addNewTypeMcpttSignalllingEvent(TypeMcpttSignallingEvent.GROUP_CALL_NON_EMERGENCY);
    }

    public void onEventMcptt(TypeMcpttSignallingEvent typeMcpttSignallingEvent) {
        if (typeMcpttSignallingEvent == null) return;
        addNewTypeMcpttSignalllingEvent(typeMcpttSignallingEvent);
    }

    public void onInitialLogOn() {
        addNewTypeMcpttSignalllingEvent(TypeMcpttSignallingEvent.LOG_ON);
    }

    private void addNewTypeMcpttSignalllingEvent(TypeMcpttSignallingEvent typeMcpttSignallingEvent) {
        if (currentTypeMcpttSignallingEvents == null) {
            currentTypeMcpttSignallingEvents = EnumSet.of(typeMcpttSignallingEvent);
        } else {
            currentTypeMcpttSignallingEvents.add(typeMcpttSignallingEvent);
        }
    }

    //END Mcptt Signalling Event


    //INIT Configure


    private boolean configure(byte[] bytes) {
        if (bytes == null) {
            Log.e(TAG, "Configure is null 1");
            return false;
        }
        try {
            //Log.d(TAG,"receive new data info for location: "+new String(bytes));
            LocationInfo locationInfo = LocationUtils.getLocationInfo(bytes, context);
            if (locationInfo != null && locationInfo.getConfiguration() != null) {
                return configure(locationInfo);
            }
        } catch (Exception e) {
            Log.e(TAG, "Incorrect configuration " + e.toString());
            return false;
        }
        return false;
    }

    private boolean configure(LocationInfo locationInfo) {
        if (locationInfo == null) {
            Log.e(TAG, "Null locationInfo.");
            return false;
        }
        this.locationInfo = locationInfo;
        return configure(locationInfo.getConfiguration());
    }

    private boolean configure(TConfigurationType configure) {
        if (configure == null) {
            Log.e(TAG, "Configure is null.");
            return false;
        }

        dataReportEmergency = setDataReport(configure.getEmergencyLocationInformation());
        dataReportNoEmergency = setDataReport(configure.getNonEmergencyLocationInformation());


        return true;
    }

    //END Configure


    //INIT DataType and Utils


    private static Set<InfoReport> addInfoReport(Set<InfoReport> infoReports, InfoReport infoReport) {
        if (infoReport == null) return null;
        if (infoReports == null) {
            return EnumSet.of(infoReport);
        } else {
            infoReports.add(infoReport);
        }
        return infoReports;
    }


    private DataCell getCurrentDataCell(Context context) {
        List<DataCell> dataCells = getDataCell(context, true);
        if (dataCells == null || dataCells.isEmpty()) return null;
        return dataCells.get(0);
    }

    private static List<DataCell> getDataCell(Context context) {
        return getDataCell(context, false);
    }

    public static List<DataCell> getDataCell(Context context, boolean onlyCurrent) {
        if (context == null) {
            Log.e(TAG, "Context is null");
            return null;
        }
        if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "Location service needs location permission.");
            return null;
        }
        TelephonyManager mTelephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        List<CellInfo> cellInfos = mTelephony.getAllCellInfo();
        ArrayList<DataCell> dataCells = new ArrayList<>();
        if (cellInfos != null && !cellInfos.isEmpty()) {
            String string = new String();
            for (final CellInfo cellInfo : cellInfos) {
                if (cellInfo instanceof CellInfoLte) {
                    String eci = "0";
                    String mcc = "0";
                    String mnc = "0";
                    String tac = "0";
                    CellInfoLte cellInfoLte = (CellInfoLte) cellInfo;
                    CellIdentityLte cellIdentityLte = cellInfoLte.getCellIdentity();
                    int data = cellIdentityLte.getCi();
                    if (data > 0 && data < Integer.MAX_VALUE) {
                        DecimalFormat decimalFormat = new DecimalFormat(String.format("%0" + NUM_CHARS_ECI + "d", 0));
                        eci = decimalFormat.format(new BigInteger(Integer.toBinaryString(data)));
                    }
                    data = cellIdentityLte.getTac();
                    if (data > 0 && data < Integer.MAX_VALUE) {
                        DecimalFormat decimalFormat = new DecimalFormat(String.format("%0" + NUM_CHARS_TAC + "d", 0));
                        tac = decimalFormat.format(new BigInteger(Integer.toBinaryString(data)));
                    }
                    data = cellIdentityLte.getMcc();
                    if (data > 0 && data < Integer.MAX_VALUE) {
                        mcc = String.format("%0" + NUM_CHARS_MCC + "d", data);
                    }
                    data = cellIdentityLte.getMnc();
                    if (data > 0 && data < Integer.MAX_VALUE) {
                        mnc = String.format("%0" + NUM_CHARS_MNC + "d", data);
                    }

                    //insert new dataCell
                    DataCell dataCellNow = new DataCell(eci, mcc, mnc, tac, cellInfo.isRegistered());
                    if (onlyCurrent && dataCellNow.isRegister()) {
                        dataCells = new ArrayList<>();
                        dataCells.add(dataCellNow);
                        return dataCells;
                    } else {

                        dataCells.add(dataCellNow);
                    }


                } else if (cellInfo instanceof CellInfoGsm) {
                    // Log.d(TAG, "cellinfo type:"+"GSM"+" isRegister:"+cellInfo.isRegistered());
                } else if ((cellInfo instanceof CellInfoCdma)) {
                    //Log.d(TAG, "cellinfo type:"+"CDMA"+" isRegister:"+cellInfo.isRegistered());
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    if ((cellInfo instanceof CellInfoWcdma)) {
                        // Log.d(TAG, "cellinfo type:"+"WCDMA"+" isRegister:"+cellInfo.isRegistered());
                    }
                }

            }
        } else {
            Log.d(TAG, "Cellinfos is null or empty.");
        }
        return dataCells;
    }

    private static DataReport setDataReport(TRequestedLocationType locationInformation) {
        if (BuildConfig.DEBUG) Log.d(TAG, "setDataReport");
        if (locationInformation != null) {
            long minimumIntervalLength = locationInformation.getMinimumIntervalLength();
            if (minimumIntervalLength <= 0) {
                Log.e(TAG, "Invalid minimum interval: " + minimumIntervalLength);
                return null;
            }

            DataReport dataReport = new DataReport(minimumIntervalLength);
            Set<InfoReport> infoReports = null;
            if (locationInformation.getGeographicalCordinate() != null) {
                if (BuildConfig.DEBUG) Log.d(TAG, "it add info report " + "GEOGRAPHICALCORDINATE");
                infoReports = addInfoReport(infoReports, InfoReport.GEOGRAPHICALCORDINATE);
            } else {
                if (BuildConfig.DEBUG)
                    Log.d(TAG, "it do not add info report " + "GEOGRAPHICALCORDINATE");
            }
            if (locationInformation.getMbmsSaId() != null) {
                if (BuildConfig.DEBUG) Log.d(TAG, "it add info report " + "MBMSSAID");
                infoReports = addInfoReport(infoReports, InfoReport.MBMSSAID);
            } else {
                if (BuildConfig.DEBUG) Log.d(TAG, "it do not add info report " + "MBMSSAID");
            }
            if (locationInformation.getMbsfnArea() != null) {
                if (BuildConfig.DEBUG) Log.d(TAG, "it add info report " + "MBSFNAREA");

                infoReports = addInfoReport(infoReports, InfoReport.MBSFNAREA);
            } else {
                if (BuildConfig.DEBUG) Log.d(TAG, "it do not add info report " + "MBSFNAREA");
            }
            if (locationInformation.getServingEcgi() != null) {
                if (BuildConfig.DEBUG) Log.d(TAG, "it add info report " + "SERVICEECGI");

                infoReports = addInfoReport(infoReports, InfoReport.SERVICEECGI);
            } else {
                if (BuildConfig.DEBUG) Log.d(TAG, "it do not add info report " + "SERVICEECGI");
            }
            if (locationInformation.getNeighbouringEcgi() != null) {
                if (BuildConfig.DEBUG) Log.d(TAG, "it add info report " + "NEIGHBOURINGECGI");

                infoReports = addInfoReport(infoReports, InfoReport.NEIGHBOURINGECGI);
                List<TEmptyType> list = locationInformation.getNeighbouringEcgi();
                if (list != null) {
                    dataReport.setNumNeighbour(list.size());
                }
            } else {
                if (BuildConfig.DEBUG) Log.d(TAG, "no add info report " + "NEIGHBOURINGECGI");
            }

            dataReport.setInfoReport(infoReports);

            return dataReport;
        }
        return null;
    }


    //END DataType and Utils


    //INIT Location GPS

    private void configureLastLocation(boolean newVersion) {
        if(newVersion){
            if(mFusedLocationClient!=null)
                try {
                    mFusedLocationClient.getLastLocation()
                            .addOnCompleteListener(new OnCompleteListener<Location>() {
                                @Override
                                public void onComplete(@NonNull Task<Location> task) {
                                    if (task.isSuccessful() && task.getResult() != null) {
                                            updateLocation(task.getResult());
                                    } else {
                                        if(BuildConfig.DEBUG)Log.w(TAG, "Failed to get location.");
                                    }
                                }
                            });
                } catch (SecurityException unlikely) {
                    Log.e(TAG, "Lost location permission." + unlikely);
                }
        }else{
            if (mGoogleApiClient != null)
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    updateLocation(LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient));
                }
        }

    }

    private boolean startLocationUpdates(boolean newVersion) {
        Log.d(TAG, "Start Location Update.");
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_LOW_POWER)
                .setInterval(TIME_INTERVAL)
                .setFastestInterval(TIME_INTERVAL_MIN);
        if (newVersion) {
            return startLocationUpdatesNew();
        } else {
            return startLocationUpdatesOld();
        }

    }

    private boolean startLocationUpdatesNew() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {


            mLocationCallback=new LocationCallback(){
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    if (locationResult == null) {
                        if(BuildConfig.DEBUG)Log.w(TAG,"the onLocationResult is null");
                        return;
                    }
                    if(locationResult.getLocations().size()>1){
                        if(BuildConfig.DEBUG)Log.w(TAG,"The device receive a lot of location");
                    }
                    for (Location location : locationResult.getLocations()) {
                        updateLocation(location);
                    }
                };
            };

            mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                    mLocationCallback,
                    null /* Looper */);
            return true;
        }
        return false;

    }

    private boolean startLocationUpdatesOld() {

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Log.e(TAG, "No permissions to access location.");
            return false;
        } else {
            isStartLoclization = true;
            statusPendingResultLocation = LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

            return true;
        }
    }


    private boolean startLocationGPSorNetwork(Context context){
        Log.d(TAG,"Start location with GPS or Network");
        // Acquire a reference to the system Location Manager
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);


        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Log.e(TAG, "No permissions to access location.");
            return false;
        } else {
            // Define a listener that responds to location updates
            // Register the listener with the Location Manager to receive location updates
            if(!isStartLoclization)isStartLoclization=true;
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0, 0,this);
            return true;
        }
    }

    private void stopLocationUpdates(boolean newVersion) {
        Log.d(TAG, "Stop Location Update.");
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_LOW_POWER)
                .setInterval(TIME_INTERVAL)
                .setFastestInterval(TIME_INTERVAL_MIN);
        if (newVersion) {
             stopLocationUpdatesNew();
        } else {
             stopLocationUpdatesOld();
        }

        //Stop loop
        try{
            if(handlerService!=null && runnableService!=null){
                handlerService.removeCallbacks(runnableService);
                handlerService=null;
                runnableService=null;
            }
        }catch (Exception e){
            Log.e(TAG,"Error in Location:"+e.getMessage());
        }

    }

    private void stopLocationUpdatesNew() {
        Log.d(TAG, "Stop location update.");
        isStartLoclization = false;
        if (mFusedLocationClient == null) return;
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }

    private void stopLocationUpdatesOld() {
        Log.d(TAG, "Stop location update.");
        isStartLoclization = false;
        if (mGoogleApiClient == null || !mGoogleApiClient.isConnected()) return;
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }


    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "Change position " + location.getLatitude() + " " + location.getLongitude());
        updateLocation(location);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        String providerString=null;
        if(provider.compareTo(LocationManager.NETWORK_PROVIDER)==0){
            providerString="Network";
        }else if(provider.compareTo(LocationManager.GPS_PROVIDER)==0){
            providerString="GPS";
        }
        Log.d(TAG,"Change status in provider of location: "+providerString);
    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    private void updateLocation(Location location){
        if(location!=null){
            if(currientLocation==null || currientLocation.getTime()<location.getTime()){
                Log.d(TAG,"New location received.");
                currientLocation=location;
                if (lastLocation == null) {
                    //Init Location;
                    lastLocation = location;
                }
            }else{
                Log.w(TAG,"Old location received.");
            }

        }else{
           if(BuildConfig.DEBUG) Log.w(TAG,"Location isn´t logic 1");
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if(BuildConfig.DEBUG)Log.d(TAG,"onConnected location.");

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling


            Log.e(TAG,"No location permission.");

        }else{
            if(BuildConfig.DEBUG)Log.d(TAG,"init updateLocation");
            configureLastLocation(USE_NEW_VERSION_LOCATION);

            if(currientLocation!=null){
                if(BuildConfig.DEBUG)Log.d(TAG,"Last locations: "+"Lat="+currientLocation.getLatitude()+" Lon="+currientLocation.getLongitude());
            }else{
                if(BuildConfig.DEBUG)Log.e(TAG,"Invalid last location.");
            }
        }

        if(!isStartLoclization){
            Log.d(TAG,"Service location started.");
            startLocationUpdates(USE_NEW_VERSION_LOCATION);
        }else{
            Log.d(TAG,"Service location started.");
        }

    }


    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG,"Service location suspended.");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(TAG,"Error connecting Google API: "+connectionResult.getErrorMessage()+" Code error:"+connectionResult.getErrorCode());
        startLocationGPSorNetwork(NgnApplication.getContext());
        switch (connectionResult.getErrorCode()){
            case ConnectionResult.API_UNAVAILABLE:
                Log.e(TAG,"Error connecting Google API. "+"API UNAVAILABLE");
                if(onReportListener!=null)onReportListener.errorLocation("API UNAVAILABLE",connectionResult.getErrorCode());
                break;
            case ConnectionResult.CANCELED:
                Log.e(TAG,"Error connecting Google API. "+"CANCELED");
                if(onReportListener!=null)onReportListener.errorLocation("API UNAVAILABLE",connectionResult.getErrorCode());
                break;
            case ConnectionResult.DEVELOPER_ERROR:
                Log.e(TAG,"Error connecting Google API. "+"DEVELOPER ERROR");
                if(onReportListener!=null)onReportListener.errorLocation("API UNAVAILABLE",connectionResult.getErrorCode());
                break;
            case ConnectionResult.INTERNAL_ERROR:
                Log.e(TAG,"Error connecting Google API. "+"INTERNAL ERROR");
                if(onReportListener!=null)onReportListener.errorLocation("INTERNAL ERROR",connectionResult.getErrorCode());
                break;
            case ConnectionResult.INTERRUPTED:
                Log.e(TAG,"Error connecting Google API. "+"INTERRUPTED");
                if(onReportListener!=null)onReportListener.errorLocation("INTERRUPTED",connectionResult.getErrorCode());
                break;
            case ConnectionResult.INVALID_ACCOUNT:
                Log.e(TAG,"Error connecting Google API. "+"INVALID ACCOUNT");
                if(onReportListener!=null)onReportListener.errorLocation("INVALID ACCOUNT",connectionResult.getErrorCode());
                break;
            case ConnectionResult.LICENSE_CHECK_FAILED:
                Log.e(TAG,"Error connecting Google API. "+"LICENSE CHECK FAILED");
                if(onReportListener!=null)onReportListener.errorLocation("LICENSE CHECK FAILED",connectionResult.getErrorCode());
                break;
            case ConnectionResult.NETWORK_ERROR:
                Log.e(TAG,"Error connecting Google API. "+"NETWORK ERROR");
                if(onReportListener!=null)onReportListener.errorLocation("NETWORK ERROR",connectionResult.getErrorCode());
                break;
            case ConnectionResult.RESOLUTION_REQUIRED:
                Log.e(TAG,"Error connecting Google API. "+"RESOLUTION REQUIRED");
                if(onReportListener!=null)onReportListener.errorLocation("RESOLUTION REQUIRED",connectionResult.getErrorCode());
                break;
            case ConnectionResult.RESTRICTED_PROFILE:
                Log.e(TAG,"Error connecting Google API. "+"RESTRICTED PROFILE");
                if(onReportListener!=null)onReportListener.errorLocation("RESTRICTED PROFILE",connectionResult.getErrorCode());
                break;
            case ConnectionResult.SERVICE_DISABLED:
                Log.e(TAG,"Error connecting Google API. "+"SERVICE DISABLED");
                if(onReportListener!=null)onReportListener.errorLocation("SERVICE DISABLED",connectionResult.getErrorCode());
                break;
            case ConnectionResult.SERVICE_MISSING:
                Log.e(TAG,"Error connecting Google API. "+"SERVICE MISSING");
                if(onReportListener!=null)onReportListener.errorLocation("SERVICE MISSING",connectionResult.getErrorCode());
                break;
            case ConnectionResult.SERVICE_INVALID:
                Log.e(TAG,"Error connecting Google API. "+"SERVICE INVALID");
                if(onReportListener!=null)onReportListener.errorLocation("SERVICE INVALID",connectionResult.getErrorCode());
                break;
            case ConnectionResult.SERVICE_MISSING_PERMISSION:
                Log.e(TAG,"Error connecting Google API. "+"SERVICE MISSING PERMISSION");
                if(onReportListener!=null)onReportListener.errorLocation("SERVICE MISSING PERMISSION",connectionResult.getErrorCode());

                break;
            case ConnectionResult.SERVICE_UPDATING:
                Log.e(TAG,"Error connecting Google API. "+"SERVICE UPDATING");
                if(onReportListener!=null)onReportListener.errorLocation("SERVICE UPDATING",connectionResult.getErrorCode());
                break;
            case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED:
                Log.e(TAG,"Error connecting Google API. "+"SERVICE VERSION UPDATE REQUIRED");
                if(onReportListener!=null)onReportListener.errorLocation("SERVICE VERSION UPDATE REQUIRED",connectionResult.getErrorCode());
                break;
            case ConnectionResult.SUCCESS:
                Log.e(TAG,"Error connecting Google API. "+"SUCCESS");
                if(onReportListener!=null)onReportListener.errorLocation("SUCCESS",connectionResult.getErrorCode());
                break;
            case ConnectionResult.SIGN_IN_FAILED:
                Log.e(TAG,"Error connecting Google API. "+"SIGN IN FAILED");
                if(onReportListener!=null)onReportListener.errorLocation("SIGN IN FAILED",connectionResult.getErrorCode());
                break;
            case ConnectionResult.SIGN_IN_REQUIRED:
                Log.e(TAG,"Error connecting Google API. "+"SIGN IN REQUIRED");
                if(onReportListener!=null)onReportListener.errorLocation("SIGN IN REQUIRED",connectionResult.getErrorCode());
                break;
            case ConnectionResult.TIMEOUT:
                Log.e(TAG,"Error connecting Google API. "+"TIMEOUT");
                if(onReportListener!=null)onReportListener.errorLocation("TIMEOUT",connectionResult.getErrorCode());
                break;
        }
        mGoogleApiClient=null;
    }




    //END Location GPS


    public void onDestroy(){
        Log.d(TAG,"Location service stopped.");
        stopLocationUpdates(USE_NEW_VERSION_LOCATION);
        isStart=false;
    }

}
