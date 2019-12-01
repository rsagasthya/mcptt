package org.doubango.ngn.datatype.ms.cms;

import android.util.Log;

import org.doubango.ngn.BuildConfig;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.ArrayList;
import java.util.List;

@Root(strict=false, name="CMSDatas")
public class CMSDatas {
    private final static String TAG = CMSDatas.class.getCanonicalName();
    @ElementList(required=false, inline=true)
    private List<CMSData> cmsDataList;

    public CMSDatas() {
    }

    public CMSDatas(CMSData cmsData) {
        addNewCMSData(cmsData);
    }

    public CMSDatas(List<CMSData> cmsDataList) {
        this.cmsDataList = cmsDataList;
    }



    public List<CMSData> getCmsDataList() {
        return cmsDataList;
    }

    public void setCmsDataList(List<CMSData> cmsDataList) {
        this.cmsDataList = cmsDataList;
    }

    public boolean addNewCMSData(CMSData cmsData){
        int con=-1;
        if(cmsData==null)return false;
        if(cmsDataList==null){
            cmsDataList=new ArrayList<>();
            cmsDataList.add(cmsData);
        }else if(isExistTAG(cmsData.getEtag())<0 ){
            if((con=isExist(cmsData.getPath()))>=0){
                cmsDataList.set(con,cmsData);
            }else{
                cmsDataList.add(cmsData);
            }
        }else{
            if(BuildConfig.DEBUG) Log.w(TAG,cmsData.getPath()+" etag="+cmsData.getEtag()+" exist now");
        }

        return true;
    }

    public int lengthCMSData(){
        if(cmsDataList!=null && !isEmpty())
            return cmsDataList.size();
        return -1;
    }

    public int isExistTAG(String tag){
        String etag=tag.replace('"',' ').trim();
        if(tag==null || tag.isEmpty() || cmsDataList==null || isEmpty())return -1;
        for(int con=0;con<cmsDataList.size();con++){
            if(cmsDataList!=null && cmsDataList.get(con).getEtag()!=null){
                String etagSaved=cmsDataList.get(con).getEtag().replace('"',' ').trim();
                if(etagSaved.compareTo(etag)==0){
                    if(BuildConfig.DEBUG)Log.d(TAG,"isExistTAG");
                    return con;
                }
            }
        }
        return -1;
    }

    public int isExist(String path){
        if(path==null || path.isEmpty() || cmsDataList==null || isEmpty())return -1;
        for(int con=0;con<cmsDataList.size();con++){
            if(cmsDataList!=null && cmsDataList.get(con).getPath()!=null && cmsDataList.get(con).getPath().replace('"',' ').trim().compareTo(path.replace('"',' ').trim())==0)return con;
        }
        return -1;
    }

    public boolean isEmpty(){
        if(cmsDataList==null)return true;
        return cmsDataList.isEmpty();
    }
}
