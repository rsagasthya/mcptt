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

import android.content.Context;
import android.location.Location;
import android.util.Log;

import org.doubango.ngn.BuildConfig;
import org.doubango.ngn.datatype.location.LocationDataDegree;
import org.doubango.ngn.datatype.mcpttloc.LocationInfo;
import org.doubango.ngn.datatype.mcpttloc.ProtectionType;
import org.doubango.ngn.datatype.mcpttloc.TCoordinateType;
import org.doubango.ngn.datatype.mcpttloc.TPointCoordinate;
import org.doubango.utils.Utils;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.convert.AnnotationStrategy;
import org.simpleframework.xml.core.Persister;
import org.simpleframework.xml.strategy.Strategy;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

class LocationUtils{
    public final static String TAG = Utils.getTAG(LocationUtils.class.getCanonicalName());


    protected static final int TWO_ELEVATED_TO_TWENTY_THREE=8388608;
    protected static final int TWO_ELEVATED_TO_TWENTY_FOUR=16777216;
    protected static final int EARTH_RADIU_KM = 6371;
    protected static final int ALL_DEGREE=360;


    //INIT Geolocate

    //Latitude +90º -90º
    //Longitude +180º -180º

    protected static org.doubango.ngn.datatype.mcpttloc.TPointCoordinate locationDegreeTo3gppIanos(Location location){
        if(location==null)return null;
        Object pointCoordinate=locationDegreeTo3gppIanos(new LocationDataDegree(location.getLatitude(),location.getLongitude()),false);
        if(pointCoordinate instanceof org.doubango.ngn.datatype.mcpttloc.TPointCoordinate)
        return (org.doubango.ngn.datatype.mcpttloc.TPointCoordinate)pointCoordinate;
        return null;
    }

    protected static org.doubango.ngn.datatype.mcpttlocOld.TPointCoordinate locationDegreeTo3gppIanosOld(Location location){
        if(location==null)return null;
        Object pointCoordinate=locationDegreeTo3gppIanos(new LocationDataDegree(location.getLatitude(),location.getLongitude()),true);
        if(pointCoordinate instanceof org.doubango.ngn.datatype.mcpttlocOld.TPointCoordinate)
            return (org.doubango.ngn.datatype.mcpttlocOld.TPointCoordinate)pointCoordinate;
        return null;

    }

    protected static Object locationDegreeTo3gppIanos(Location location,boolean oldVersion){
        if(location==null)return null;
        return locationDegreeTo3gppIanos(new LocationDataDegree(location.getLatitude(),location.getLongitude()),oldVersion);
    }


    protected static Object locationDegreeTo3gppIanos(LocationDataDegree location,boolean oldVersion){

        if(location==null)return null;
        double latitude=location.getLatitude();

        double dataLatitude=latitude*TWO_ELEVATED_TO_TWENTY_THREE/90;
        if(latitude<0){
            dataLatitude=(dataLatitude*(-1))+(TWO_ELEVATED_TO_TWENTY_THREE-1);
        }
        int dataLatitudeInteger=(int) (dataLatitude + 0.5);
        //Log.d(TAG,"Latitude:"+Integer.toBinaryString(dataLatitudeInteger));

        double longitude=location.getLongitude();
        if(longitude<0)longitude+=360;
        double dataLongitude=longitude*TWO_ELEVATED_TO_TWENTY_FOUR/360;

        int dataLongitudeInteger=(int)(dataLongitude + 0.5);

        if(oldVersion){
            return new org.doubango.ngn.datatype.mcpttlocOld.TPointCoordinate(dataLatitudeInteger,dataLongitudeInteger);
        }else{
            org.doubango.ngn.datatype.mcpttloc.TCoordinateType dataLatitudeType=new org.doubango.ngn.datatype.mcpttloc.TCoordinateType();
            dataLatitudeType.setThreebytes(dataLatitudeInteger);
            dataLatitudeType.setType(org.doubango.ngn.datatype.mcpttloc.ProtectionType.Normal);
            org.doubango.ngn.datatype.mcpttloc.TCoordinateType dataLongitudeType=new org.doubango.ngn.datatype.mcpttloc.TCoordinateType();
            dataLongitudeType.setThreebytes(dataLongitudeInteger);
            dataLongitudeType.setType(org.doubango.ngn.datatype.mcpttloc.ProtectionType.Normal);
            return new org.doubango.ngn.datatype.mcpttloc.TPointCoordinate(dataLatitudeType,dataLongitudeType);
        }

    }

    protected static LocationDataDegree location3gppIanosToDegree(org.doubango.ngn.datatype.mcpttloc.TPointCoordinate tPointCoordinate){

        if((tPointCoordinate)==null ||
                tPointCoordinate.getLongitude()==null ||
                tPointCoordinate.getLatitude()==null)return null;
        long latitudeInteger=-1;
        if(tPointCoordinate.getLatitude() instanceof org.doubango.ngn.datatype.mcpttloc.TCoordinateType ){
            //new version
            if(((org.doubango.ngn.datatype.mcpttloc.TCoordinateType)tPointCoordinate.getLatitude())!=null)
                latitudeInteger=((org.doubango.ngn.datatype.mcpttloc.TCoordinateType)tPointCoordinate.getLatitude()).getThreebytes();
        }else{
            //old version
            latitudeInteger=tPointCoordinate.getLatitudeLong();


        }


        if(latitudeInteger>=TWO_ELEVATED_TO_TWENTY_THREE){
            latitudeInteger-=(TWO_ELEVATED_TO_TWENTY_THREE-1);
            latitudeInteger=-latitudeInteger;
        }

        double latitudeDouble=((double)latitudeInteger*90)/TWO_ELEVATED_TO_TWENTY_THREE;
        long longitudeInteger=-1;
        if(tPointCoordinate.getLongitude() instanceof org.doubango.ngn.datatype.mcpttloc.TCoordinateType ){
            //new version
            if(((org.doubango.ngn.datatype.mcpttloc.TCoordinateType)tPointCoordinate.getLongitude())!=null)
                longitudeInteger=((org.doubango.ngn.datatype.mcpttloc.TCoordinateType)tPointCoordinate.getLongitude()).getThreebytes();
        }else{
            //old version
            longitudeInteger=tPointCoordinate.getLongitudeLong();


        }

        double longitudeDouble=((double)longitudeInteger*360)/TWO_ELEVATED_TO_TWENTY_FOUR;

        if(longitudeDouble>=180)longitudeDouble-=360;


        return new LocationDataDegree(latitudeDouble,longitudeDouble);


    }




    protected static List<LocationDataDegree> location3gppIanosToDegrees(List<org.doubango.ngn.datatype.mcpttloc.TPointCoordinate> tPointCoordinates){
        ArrayList<LocationDataDegree> dataDegrees=new ArrayList<>();
        if(tPointCoordinates==null)return dataDegrees;
        for(org.doubango.ngn.datatype.mcpttloc.TPointCoordinate TPointCoordinate:tPointCoordinates){
            dataDegrees.add(location3gppIanosToDegree(TPointCoordinate));
        }
        return dataDegrees;
    }

    /**
     *
     * @param lastPoint
     * @param nowPoint
     * @param vertices
     * @return if true=enter, if false=exist, if null=no area change
     */
    protected static Boolean isEnterOrExitOfPolygon(LocationDataDegree lastPoint,LocationDataDegree nowPoint, List<LocationDataDegree> vertices) {

        boolean isEntryLast=isPointInPolygon(lastPoint,vertices);
        boolean isEntryNow=isPointInPolygon(nowPoint,vertices);
        if(isEntryLast && !isEntryNow){
            //Exit
            return false;
        }else if(!isEntryLast && isEntryNow){
            //Enter
            return true;
        }
        return null;
    }


    protected static Boolean isEnterOrExitOfPolygon(Location lastPoint,Location nowPoint, List<LocationDataDegree> vertices) {
        if(lastPoint==null ||nowPoint==null )return null;



        return isEnterOrExitOfPolygon(new LocationDataDegree(lastPoint.getLatitude(),lastPoint.getLongitude()),
                new LocationDataDegree(nowPoint.getLatitude(),nowPoint.getLongitude()),vertices);
    }


    protected static boolean intersectPoint2(LocationDataDegree point,LocationDataDegree vectA,LocationDataDegree vectB){

        double aY = vectA.getLatitude();
        double bY = vectB.getLatitude();
        double aX = vectA.getLongitude();
        double bX = vectB.getLongitude();
        double pY = point.getLatitude();
        double pX = point.getLongitude();


        //change X axis;
        if(aX<0)aX+=360;
        if(bX<0)bX+=360;
        if(pX<0)pX+=360;

        //move point to {0,0}
        aY-=pY;
        bY-=pY;
        pY-=pY;
        aX-=pX;
        bX-=pX;
        pX-=pX;





        if((aY>pY && bY>pY) ||
                (aY<pY && bY<pY) ||
                (aX<pX && bX<pX)){
            //System.out.println("invalid for intersection");
            return false;//invalid for intersection
        }
        //Calcule  intersection point.
        double d=(bX-aX)/(aY-bY);
        double c=aX-d*aY;
        //System.out.println("d:"+d);



        double intersectX=c+d*pY;
        //System.out.println("intersectX:"+intersectX+" pX:"+pX);

        if(intersectX>pX){
            return true;
        }else return false;

    }

    protected static boolean isPointInPolygon(LocationDataDegree point, List<LocationDataDegree> locationDataDegrees){
        if(point==null || locationDataDegrees==null){
            return false;
        }
        int numIntersection=0;
        for(int con=0;con<locationDataDegrees.size();con++){
            if(con==(locationDataDegrees.size()-1)){
                if(intersectPoint2(point,locationDataDegrees.get(con),locationDataDegrees.get(0)))
                numIntersection++;
            }else if(intersectPoint2(point,locationDataDegrees.get(con),locationDataDegrees.get(con+1))){
                numIntersection++;
            }
        }
        if((numIntersection%2)==1){
            return true;
        }else
            return false;

    }



    protected static double radiuMeterToDegree(double radiuMeter){
        double diameterEarth=EARTH_RADIU_KM*Math.PI*2;
        return ALL_DEGREE*radiuMeter/diameterEarth;
    }


    protected static Boolean isEnterOrExitOfElipti(Location lastPoint,Location nowPoint,LocationDataDegree center,double KiloMeter,double offRadiu,double interRadio) {
        if(lastPoint==null || nowPoint==null)return null;
        LocationDataDegree lastPointDegree = new LocationDataDegree(lastPoint);
        LocationDataDegree nowPointDegree = new LocationDataDegree(nowPoint);

        return isEnterOrExitOfElipti(lastPointDegree,nowPointDegree,center,KiloMeter,offRadiu,interRadio);
    }

    protected static Boolean isEnterOrExitOfElipti(LocationDataDegree lastPoint,LocationDataDegree nowPoint,LocationDataDegree center,double KiloMeter,double offRadiu,double interRadio) {

        boolean isEntryLast=isContainPointInCircle(lastPoint,center,KiloMeter,offRadiu,interRadio);
        boolean isEntryNow=isContainPointInCircle(nowPoint,center,KiloMeter,offRadiu,interRadio);
        if(isEntryLast && !isEntryNow){
            //Exit
            return false;
        }else if(!isEntryLast && isEntryNow){
            //Enter
            return true;
        }
        return null;
    }


    protected static boolean isContainPointInCircle(LocationDataDegree nowLocation,
                                                 LocationDataDegree center,
                                                 double radioKm,
                                                 double offsetAngleDegree,
                                                 double includedAngleDegree){
        if((offsetAngleDegree+includedAngleDegree)>ALL_DEGREE)return false;



        //http://math.stackexchange.com/questions/830413/calculating-the-arc-length-of-a-circle-segment

        double distanceBetweenCenterAndPoint=distanceRad(nowLocation,center);
        //System.out.println("distanceBetweenCenterAndPoint:"+distanceBetweenCenterAndPoint);

        double radiuDegreeEarth=radiuMeterToDegree(radioKm);
        if(distanceBetweenCenterAndPoint>radiuDegreeEarth){
            //System.out.println("No contain");
            return false;
        }

        LocationDataDegree locationNort=new LocationDataDegree(nowLocation);
        locationNort.setLatitude(nowLocation.getLatitude()+distanceBetweenCenterAndPoint);

        double distanceBetweenNortAndPoint=distanceRad(locationNort,center);
        //System.out.println("distanceBetweenNortAndPoint:"+distanceBetweenNortAndPoint);

        double interm=(Math.pow(distanceBetweenNortAndPoint, 2))/(2*Math.pow(distanceBetweenCenterAndPoint, 2));
        //System.out.println("interm:"+interm);

        double anglePoint= Math.acos(1-interm);
        //System.out.println("anglePoint:"+anglePoint);

        anglePoint*=(360/(2*Math.PI));

        if(center.getLongitude()>nowLocation.getLongitude()){
            anglePoint=360-anglePoint;
            //System.out.println("Change the angle:"+anglePoint);
        }else{
            //System.out.println("Do not change the angle:"+anglePoint);

        }

        if(anglePoint>offsetAngleDegree && anglePoint<(includedAngleDegree+offsetAngleDegree)){
            return true;
        }
        return false;
    }


    protected static double distanceRad(LocationDataDegree lastLocation,LocationDataDegree currientLocation) {
        if(lastLocation==null || currientLocation==null)return -1;
        return Math.sqrt(Math.pow(lastLocation.getLatitude()-currientLocation.getLatitude(), 2) + Math.pow(lastLocation.getLongitude()-currientLocation.getLongitude(), 2));
    }


    protected static double distance(Location lastLocation,Location currientLocation) {
        if(lastLocation==null || currientLocation==null)return -1;
        return distance(
                lastLocation.getLatitude(),
                currientLocation.getLatitude(),
                lastLocation.getLongitude(),
                currientLocation.getLongitude(),
                lastLocation.getAltitude(),
                currientLocation.getAltitude());
    }


    /**
     * Get distance between two points
     * @param lat1
     * @param lat2
     * @param lon1
     * @param lon2
     * @param el1
     * @param el2
     * @return
     */
    protected static double distance(double lat1, double lat2, double lon1,
                                  double lon2, double el1, double el2) {
        final int R = EARTH_RADIU_KM; // Earth radius

        Double latDistance = Math.toRadians(lat2 - lat1);
        Double lonDistance = Math.toRadians(lon2 - lon1);
        Double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters

        double height = el1 - el2;

        distance = Math.pow(distance, 2) + Math.pow(height, 2);

        return Math.sqrt(distance);
    }

    //END Geolocate

    //INIT utils xml

    protected static org.doubango.ngn.datatype.mcpttloc.LocationInfo getLocationInfo(String string,Context context){
        return getLocationInfo(string.getBytes(),context);
    }

    protected static org.doubango.ngn.datatype.mcpttloc.LocationInfo getLocationInfo(byte[] bytes,Context context) {
        org.doubango.ngn.datatype.mcpttloc.LocationInfo result= getLocationInfo(new ByteArrayInputStream(bytes),false,context);
        if(result==null)result= getLocationInfo(new ByteArrayInputStream(bytes),true,context);
        if(result==null && bytes!=null && bytes.length>0){
            String data = new String(bytes);
            Log.e(TAG,"Error parsing data: "+data);
        }
        return result;
    }

     private static org.doubango.ngn.datatype.mcpttloc.LocationInfo getLocationInfo(InputStream stream,boolean oldVersion,Context context)  {
         if(stream==null)return null;
         Strategy strategy = new AnnotationStrategy();
         Serializer serializer = new Persister(strategy);
         org.doubango.ngn.datatype.mcpttloc.LocationInfo locationInfo=null;
         if(oldVersion){
             try{
                 org.doubango.ngn.datatype.mcpttlocOld.LocationInfo locationInfoOld;
                 locationInfoOld=serializer.read(org.doubango.ngn.datatype.mcpttlocOld.LocationInfo.class,stream);
                 locationInfo=locationInfoOldToLocationInfo(locationInfoOld,context);
                 if(BuildConfig.DEBUG){
                     String locationInfoString=getStringOfLocationInfo(context,locationInfo);
                     Log.d(TAG,"Translate new version:\n"+locationInfoString);
                 }
             }catch (Exception e){
             }
         }else{
             try{
                 locationInfo=serializer.read(org.doubango.ngn.datatype.mcpttloc.LocationInfo.class,stream);
             }catch (Exception ex){
                 Log.e(TAG,"Error in: "+ex.toString());
             }

         }



         return locationInfo;
     }



    private static org.doubango.ngn.datatype.mcpttloc.LocationInfo locationInfoOldToLocationInfo(org.doubango.ngn.datatype.mcpttlocOld.LocationInfo locationInfoOld, Context context){
        if(locationInfoOld==null)return null;
        TPointCoordinate centerEllipsoidEnter=null;
        List<org.doubango.ngn.datatype.mcpttloc.TPointCoordinate> pointCoordinatesAreaEntre=null;
        String triggerIdEntre=null;
        TPointCoordinate centerEllipsoidExit=null;
        List<org.doubango.ngn.datatype.mcpttloc.TPointCoordinate> pointCoordinatesAreaExit=null;
        String triggerIdExit=null;


        if(locationInfoOld.getConfiguration()!=null &&
                locationInfoOld.getConfiguration().getTriggeringCriteria()!=null &&
                locationInfoOld.getConfiguration().getTriggeringCriteria().getGeographicalAreaChange()!=null ){
            if(locationInfoOld.getConfiguration().getTriggeringCriteria().getGeographicalAreaChange().getEnterSpecificAreaType()!=null &&
                    locationInfoOld.getConfiguration().getTriggeringCriteria().getGeographicalAreaChange().getEnterSpecificAreaType().getGeographicalArea()!=null &&
                    locationInfoOld.getConfiguration().getTriggeringCriteria().getGeographicalAreaChange().getEnterSpecificAreaType().getTriggerId()!=null){
                triggerIdEntre=locationInfoOld.getConfiguration().getTriggeringCriteria().getGeographicalAreaChange().getEnterSpecificAreaType().getTriggerId() ;
                if(locationInfoOld.getConfiguration().getTriggeringCriteria().getGeographicalAreaChange().getEnterSpecificAreaType().getGeographicalArea().getEllipsoidArcArea()!=null &&
                        locationInfoOld.getConfiguration().getTriggeringCriteria().getGeographicalAreaChange().getEnterSpecificAreaType().getGeographicalArea().getEllipsoidArcArea().getCenter()!=null){
                    centerEllipsoidEnter=tPointCoordinateOldToTPointCoordinate(locationInfoOld.getConfiguration().getTriggeringCriteria().getGeographicalAreaChange().getEnterSpecificAreaType().getGeographicalArea().getEllipsoidArcArea().getCenter());
                    //SetNull
                    locationInfoOld.getConfiguration().getTriggeringCriteria().getGeographicalAreaChange().getEnterSpecificAreaType().getGeographicalArea().getEllipsoidArcArea().setCenter(null);

                } else if(locationInfoOld.getConfiguration().getTriggeringCriteria().getGeographicalAreaChange().getEnterSpecificAreaType().getGeographicalArea().getPolygonArea()!=null &&
                        locationInfoOld.getConfiguration().getTriggeringCriteria().getGeographicalAreaChange().getEnterSpecificAreaType().getGeographicalArea().getPolygonArea().getCorner()!=null){
                    pointCoordinatesAreaEntre=tPointCoordinateOldToTPointCoordinate(locationInfoOld.getConfiguration().getTriggeringCriteria().getGeographicalAreaChange().getEnterSpecificAreaType().getGeographicalArea().getPolygonArea().getCorner());
                    locationInfoOld.getConfiguration().getTriggeringCriteria().getGeographicalAreaChange().getEnterSpecificAreaType().getGeographicalArea().getPolygonArea().setCorner(null);
                }

            }
            if(locationInfoOld.getConfiguration().getTriggeringCriteria().getGeographicalAreaChange().getExitSpecificAreaType()!=null &&
                    locationInfoOld.getConfiguration().getTriggeringCriteria().getGeographicalAreaChange().getExitSpecificAreaType().getGeographicalArea()!=null &&
                    locationInfoOld.getConfiguration().getTriggeringCriteria().getGeographicalAreaChange().getExitSpecificAreaType().getTriggerId()!=null){
                triggerIdExit=locationInfoOld.getConfiguration().getTriggeringCriteria().getGeographicalAreaChange().getExitSpecificAreaType().getTriggerId() ;
                if(locationInfoOld.getConfiguration().getTriggeringCriteria().getGeographicalAreaChange().getExitSpecificAreaType().getGeographicalArea().getEllipsoidArcArea()!=null &&
                        locationInfoOld.getConfiguration().getTriggeringCriteria().getGeographicalAreaChange().getExitSpecificAreaType().getGeographicalArea().getEllipsoidArcArea().getCenter()!=null){
                    centerEllipsoidExit=tPointCoordinateOldToTPointCoordinate(locationInfoOld.getConfiguration().getTriggeringCriteria().getGeographicalAreaChange().getExitSpecificAreaType().getGeographicalArea().getEllipsoidArcArea().getCenter());
                    //SetNull
                    locationInfoOld.getConfiguration().getTriggeringCriteria().getGeographicalAreaChange().getExitSpecificAreaType().getGeographicalArea().getEllipsoidArcArea().setCenter(null);

                } else if(locationInfoOld.getConfiguration().getTriggeringCriteria().getGeographicalAreaChange().getExitSpecificAreaType().getGeographicalArea().getPolygonArea()!=null &&
                        locationInfoOld.getConfiguration().getTriggeringCriteria().getGeographicalAreaChange().getExitSpecificAreaType().getGeographicalArea().getPolygonArea().getCorner()!=null){
                    pointCoordinatesAreaExit=tPointCoordinateOldToTPointCoordinate(locationInfoOld.getConfiguration().getTriggeringCriteria().getGeographicalAreaChange().getExitSpecificAreaType().getGeographicalArea().getPolygonArea().getCorner());
                    locationInfoOld.getConfiguration().getTriggeringCriteria().getGeographicalAreaChange().getExitSpecificAreaType().getGeographicalArea().getPolygonArea().setCorner(null);
                }

            }

        }
        org.doubango.ngn.datatype.mcpttloc.LocationInfo locationInfo=null;
        if(triggerIdEntre!=null || triggerIdExit!=null){
            try {
                locationInfo=getLocationInfo(getOutputStreamOfLocationInfo(context,locationInfoOld),false,context);
                if(locationInfo!=null){
                    if(triggerIdEntre!=null && (centerEllipsoidEnter!=null|| pointCoordinatesAreaEntre!=null)){
                        locationInfo.getConfiguration().getTriggeringCriteria().getGeographicalAreaChange().getEnterSpecificAreaType().setTriggerId(triggerIdEntre);
                        if(centerEllipsoidEnter!=null){
                            locationInfo.getConfiguration().getTriggeringCriteria().getGeographicalAreaChange().getEnterSpecificAreaType().getGeographicalArea().getEllipsoidArcArea().setCenter(centerEllipsoidEnter);
                        }
                        if(pointCoordinatesAreaEntre!=null){
                            locationInfo.getConfiguration().getTriggeringCriteria().getGeographicalAreaChange().getEnterSpecificAreaType().getGeographicalArea().getPolygonArea().setCorner(pointCoordinatesAreaEntre);
                        }
                    }
                    if(triggerIdExit!=null && (centerEllipsoidExit!=null|| pointCoordinatesAreaExit!=null)){
                        locationInfo.getConfiguration().getTriggeringCriteria().getGeographicalAreaChange().getExitSpecificAreaType().setTriggerId(triggerIdExit);
                        if(centerEllipsoidExit!=null){
                            locationInfo.getConfiguration().getTriggeringCriteria().getGeographicalAreaChange().getExitSpecificAreaType().getGeographicalArea().getEllipsoidArcArea().setCenter(centerEllipsoidExit);
                        }
                        if(pointCoordinatesAreaExit!=null){
                            locationInfo.getConfiguration().getTriggeringCriteria().getGeographicalAreaChange().getExitSpecificAreaType().getGeographicalArea().getPolygonArea().setCorner(pointCoordinatesAreaExit);
                        }
                    }


                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        return locationInfo;
    }

    protected static String getLocationInfoToString(Object locationInfo,Context context) throws Exception {
        String reportString=null;
        if(locationInfo instanceof LocationInfo){
            reportString=LocationUtils.getStringOfLocationInfo(context, (LocationInfo)locationInfo);
        }else if(locationInfo instanceof org.doubango.ngn.datatype.mcpttlocOld.LocationInfo ){
            reportString=LocationUtils.getStringOfLocationInfo(context, (org.doubango.ngn.datatype.mcpttlocOld.LocationInfo)locationInfo);
        }
        return reportString;
    }


     private static List<org.doubango.ngn.datatype.mcpttloc.TPointCoordinate> tPointCoordinateOldToTPointCoordinate(List<org.doubango.ngn.datatype.mcpttlocOld.TPointCoordinate> tPointCoordinateOlds){
         if(tPointCoordinateOlds==null)return null;
         ArrayList<TPointCoordinate> pointCoordinates=new ArrayList<>();
         for(org.doubango.ngn.datatype.mcpttlocOld.TPointCoordinate pointCoordinateOld: tPointCoordinateOlds)
             if(pointCoordinateOld!=null)pointCoordinates.add(tPointCoordinateOldToTPointCoordinate(pointCoordinateOld));
         return pointCoordinates;

     }

    private static org.doubango.ngn.datatype.mcpttloc.TPointCoordinate tPointCoordinateOldToTPointCoordinate(org.doubango.ngn.datatype.mcpttlocOld.TPointCoordinate tPointCoordinateOld){
        if(tPointCoordinateOld==null)return null;
        org.doubango.ngn.datatype.mcpttloc.TPointCoordinate tPointCoordinate=new TPointCoordinate();
        org.doubango.ngn.datatype.mcpttloc.TCoordinateType coordinateTypeLa=new TCoordinateType();
        coordinateTypeLa.setType(ProtectionType.Normal);
        coordinateTypeLa.setThreebytes(tPointCoordinateOld.getLatitude());
        org.doubango.ngn.datatype.mcpttloc.TCoordinateType coordinateTypeLon=new TCoordinateType();
        coordinateTypeLon.setType(ProtectionType.Normal);
        coordinateTypeLon.setThreebytes(tPointCoordinateOld.getLongitude());
        tPointCoordinate.setLatitude(coordinateTypeLa);
        tPointCoordinate.setLongitude(coordinateTypeLon);
        return tPointCoordinate;

    }

    protected static byte[] checkMultipart(byte[] bytes){
        if(bytes.length==0){
            Log.e(TAG,"Empty location.");
            return bytes;
        }
        String str = new String(bytes);
        return str.substring(str.indexOf("<location-info"),str.length()-1).getBytes();
    }


    protected static InputStream getOutputStreamOfLocationInfo(Context context, org.doubango.ngn.datatype.mcpttloc.LocationInfo locationInfo) throws Exception {
        if(locationInfo==null)return null;
        Strategy strategy = new AnnotationStrategy();
        Serializer serializer = new Persister(strategy);
        File outputDir = context.getCacheDir(); // context being the Activity pointer
        File outputFile = File.createTempFile(String.valueOf(Calendar.getInstance().getTimeInMillis()), "txt", outputDir);
        serializer.write(locationInfo,outputFile);
        InputStream inputStream = new FileInputStream(outputFile);

        return inputStream;
    }



    protected static byte[] getBytesOfLocationInfo(Context context,org.doubango.ngn.datatype.mcpttloc.LocationInfo locationInfo) throws Exception {
        InputStream inputStream=getOutputStreamOfLocationInfo(context,locationInfo);
        if(inputStream==null)return null;
        return readBytes(inputStream);
    }

    protected  static String  getStringOfLocationInfo(Context context,org.doubango.ngn.datatype.mcpttloc.LocationInfo locationInfo) throws Exception {
        return new String(getBytesOfLocationInfo(context,locationInfo)).trim();
    }

    private static InputStream getOutputStreamOfLocationInfo(Context context, org.doubango.ngn.datatype.mcpttlocOld.LocationInfo locationInfo) throws Exception {
        if(locationInfo==null)return null;
        Strategy strategy = new AnnotationStrategy();
        Serializer serializer = new Persister(strategy);
        File outputDir = context.getCacheDir(); // context being the Activity pointer
        File outputFile = File.createTempFile(String.valueOf(Calendar.getInstance().getTimeInMillis()), "txt", outputDir);
        serializer.write(locationInfo,outputFile);
        InputStream inputStream = new FileInputStream(outputFile);

        return inputStream;
    }

    private static byte[] getBytesOfLocationInfo(Context context,org.doubango.ngn.datatype.mcpttlocOld.LocationInfo locationInfo) throws Exception {
        InputStream inputStream=getOutputStreamOfLocationInfo(context,locationInfo);
        if(inputStream==null)return null;
        return readBytes(inputStream);
    }

    protected  static String  getStringOfLocationInfo(Context context,org.doubango.ngn.datatype.mcpttlocOld.LocationInfo locationInfo) throws Exception {
        return new String(getBytesOfLocationInfo(context,locationInfo)).trim();
    }




    private static byte[] readBytes(InputStream inputStream) throws IOException {

        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();


        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];


        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }


        return byteBuffer.toByteArray();
    }

    //END utils xml

}
