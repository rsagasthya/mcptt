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
package org.doubango.ngn.services.impl.profiles;

import android.content.Context;
import android.util.Log;

import org.doubango.ngn.BuildConfig;
import org.doubango.ngn.NgnApplication;
import org.doubango.ngn.NgnEngine;
import org.doubango.ngn.datatype.profiles.Profiles;
import org.doubango.ngn.services.impl.preference.PreferencesManager;
import org.doubango.ngn.services.preference.IPreferencesManager;
import org.doubango.ngn.services.profiles.IMyProfilesService;
import org.doubango.ngn.sip.NgnSipPrefrences;
import org.doubango.ngn.utils.NgnConfigurationEntry;
import org.doubango.utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class MyProfilesService implements IMyProfilesService {
    private final static String TAG = Utils.getTAG(MyProfilesService.class.getCanonicalName());
    private static Profiles profilesNow;
    private static Map<String,NgnSipPrefrences> profilesMap;
    private static NgnSipPrefrences profileNow;
    private static IPreferencesManager preferencesManager;
    private OnSetProfileListener mOnSetProfileListener;
    private static final String IMPORTED_PROFILES_SAVED="IMPORTED_PROFILES_SAVED"+TAG;

    @Override
    public boolean start() {
        return true;
    }

    @Override
    public boolean stop() {
        return true;
    }

    private boolean readProfile(Context context){
        try {
            if(context==null)return false;
            if(profilesNow==null)profilesNow=new Profiles();
            profilesNow=getProfiles(context);
            if(profilesNow==null)return false;
            if(profilesMap==null)profilesMap=new TreeMap<>();
            for(NgnSipPrefrences profile:profilesNow.getProfiles()){
                if(profile.getName()!=null){
                    profilesMap.put(profile.getName(),profile);
                }
            }
            return true;
        }catch (NoSuchMethodException e) {
            if(BuildConfig.DEBUG)Log.e(TAG,"Error reading profiles. No access method:"+e.toString());
        } catch (Exception e1) {
            if(BuildConfig.DEBUG)Log.e(TAG,"Error reading profiles:"+e1.toString());
        }
        return false;
    }

    public NgnSipPrefrences getProfile(Context context, String name,boolean forceReadPofile){
        if(profilesNow==null || profilesNow.isEmpty() || forceReadPofile){
            if(!readProfile(context))return null;
        }
        if(profilesMap!=null){
            loadDataCMS(context,profilesMap.get(name));
            return profilesMap.get(name);
        }
        return null;
    }

    public ArrayList<String> getProfilesNames(Context context){
        if(profilesNow==null || profilesNow.isEmpty()){
            if(!readProfile(context))return null;
        }
        if(profilesMap!=null){
            String[] strings= profilesMap.keySet().toArray(new String[profilesMap.keySet().size()]);
            return new ArrayList<>(Arrays.asList(strings));
        }
        return null;
    }

    public boolean setProfileNow(Context context,String nameProfile){
        if(nameProfile==null){
            if(BuildConfig.DEBUG)Log.e(TAG,"Error in setProfileNow. nameProfile is null");
            return false;
        }
        if(profilesMap==null){
            if(BuildConfig.DEBUG)Log.w(TAG,"Warning in setProfileNow. profilesMap is null");
            return false;
        }
        NgnSipPrefrences profileNow=profilesMap.get(nameProfile);
        if(profileNow==null)return false;
        if(MyProfilesService.profileNow==null) MyProfilesService.profileNow =profileNow;
        if(MyProfilesService.profileNow.getName()!=null && profileNow.getName()!=null) {
            if (MyProfilesService.profileNow.getName().compareTo(profileNow.getName()) == 0) {
                if(BuildConfig.DEBUG)Log.d(TAG, "The same profile is selected");
            } else {

                if(BuildConfig.DEBUG) Log.d(TAG, "The same profile isn´t selected");
                //Now remote all data from CMS for the last profile
                deleteDataCMS(context);
            }
        }
        MyProfilesService.profileNow=profileNow;
        NgnEngine.getInstance().getConfigurationService().putString(NgnConfigurationEntry.PROFILE_USE,nameProfile);
        NgnEngine.getInstance().getConfigurationService().commit();
        //This is used by NGNsipService to load data from default profile.
        if(mOnSetProfileListener!=null)mOnSetProfileListener.onSetProfile();
        return true;
    }

    public void setProfileNow(NgnSipPrefrences profileNow){
        if(profileNow==null)return;
        this.profileNow=profileNow;
        return;
    }
    public NgnSipPrefrences getProfileNow(Context context){
        if(profileNow==null){
            String name=NgnEngine.getInstance().getConfigurationService().getString(NgnConfigurationEntry.PROFILE_USE,NgnConfigurationEntry.DEFAULT_PROFILE_USE);
            if(NgnConfigurationEntry.DEFAULT_PROFILE_USE.compareTo(name)!=0){
                profileNow=getProfile(context,name,false);
            }

        }

        return profileNow;
    }

    public boolean invalidProfile(Context context){
        if(profilesNow!=null){
            String name=NgnEngine.getInstance().getConfigurationService().getString(NgnConfigurationEntry.PROFILE_USE,NgnConfigurationEntry.DEFAULT_PROFILE_USE);
            if(!NgnConfigurationEntry.DEFAULT_PROFILE_USE.equals(name)){
                profileNow=getProfile(context,name,true);
                if(profileNow!=null)return true;
            }
        }

        return false;
    }
    private boolean loadDataCMS(Context context,NgnSipPrefrences ngnSipPrefrences){

        //To load configuration that the device downloaded from the CMS.
        if(ngnSipPrefrences!=null){
            if(NgnEngine.getInstance().getCMSService().configureAllProfile(context,ngnSipPrefrences)){
                if(BuildConfig.DEBUG)Log.d(TAG,"Load data to CMS: OK");
                return false;
            }else{
                if(BuildConfig.DEBUG)Log.i(TAG,"Error loading data to CMS.");
            }
        }
        return false;

    }

    private boolean deleteDataCMS(Context context){
    if(NgnEngine.getInstance().getCMSService().deleteAllProfile(context)){
        if(BuildConfig.DEBUG)Log.d(TAG,"delete ok data CMS");
        return true;
    }else {
        if(BuildConfig.DEBUG)Log.i(TAG, "Error in delete data CMS");
    }
    return false;
    }
    //Used by NGNsipService to load data from default profile.


    public void setOnSetProfileListener(OnSetProfileListener mOnSetProfileListener){
        this.mOnSetProfileListener=mOnSetProfileListener;
    }

    public void setProfiles(List<NgnSipPrefrences> profiles) {
        if(profiles!=null && !profiles.isEmpty()){
            if(profilesNow!=null){

            }else{
                profilesNow=new Profiles();
            }
            profilesNow.setProfiles(profiles);
        }
    }

    public boolean importProfiles(String profiles,Context context){
        boolean result=false;
        if(profiles==null || profiles.isEmpty()){
            if(BuildConfig.DEBUG)Log.e(TAG,"in importProfiles profile or context is null");
            return false;
        }
        try {
            Profiles profilesNews=ProfilesUtils.getProfiles(profiles);

            if(profilesNews!=null && !profilesNews.isEmpty()){

                result=saveProfiles(profilesNews,context);
                if(result && profilesNews.size()==1){
                    //Select this profile
                    return setProfileNow(context,profilesNews.getProfiles().get(0).getName());
                }else if(!result){
                    if(BuildConfig.DEBUG)Log.e(TAG,"Error in saveProfile");
                }
            }else{
                if(BuildConfig.DEBUG)Log.e(TAG,"Error importing profiles");
            }
        } catch (Exception e) {
            if(BuildConfig.DEBUG) Log.e(TAG,"Error importing profiles "+e.toString());
        }
        return result;
    }

    private  boolean saveProfiles(Profiles profiles, Context context){
        if(profiles!=null && !profiles.isEmpty()){

            preferencesManager=new PreferencesManager(IMPORTED_PROFILES_SAVED);
            try {
                String profileString=ProfilesUtils.getStringOfProfiles(context,profiles);
                if(preferencesManager.putString(context,IMPORTED_PROFILES_SAVED,profileString)){
                    profilesNow=null;
                    profilesMap=null;
                    return readProfile(context);
                }else{
                    if(BuildConfig.DEBUG)Log.e(TAG,"Error in save profiles");
                }
            } catch (Exception e) {
                if(BuildConfig.DEBUG)Log.e(TAG,"Error importing profiles "+e.toString());
            }
            return false;
        }else{
            if(BuildConfig.DEBUG)Log.e(TAG,"Error in import profile. the new profiles is not valid.");
        }
        return false;
    }

    protected static Profiles getProfiles(Context context) throws Exception {
        if(context==null)return null;
        preferencesManager=new PreferencesManager(IMPORTED_PROFILES_SAVED);
        String profiles=preferencesManager.getString(context,IMPORTED_PROFILES_SAVED);
        if(profiles!=null && !profiles.isEmpty() && !profiles.equalsIgnoreCase(PreferencesManager.STRING_DEFAULT)){
            if(BuildConfig.DEBUG)Log.w(TAG,"Device has imported profiles");
            return ProfilesUtils.getProfiles(profiles);
        }else{
            if(BuildConfig.DEBUG)Log.w(TAG,"Device doesn´t have imported profiles.");
        }
        return ProfilesUtils.getProfiles(context);
    }
    @Override
    public boolean clearService(){
        return true;
    }
}
