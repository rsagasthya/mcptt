package org.mcopenplatform.muoapi.datatype;

public class ClientSIM{
    private String[] impu;
    private String impi;
    private String domain;
    private String[] pcscf;
    private int[] pcscfPort;
    private String imsi;
    private String imei;

    public ClientSIM(String[] impu, String impi, String domain, String[] pcscf, int[] pcscfPort, String imsi, String imei) {
        this.impu = impu;
        this.impi = impi;
        this.domain = domain;
        this.pcscf = pcscf;
        this.pcscfPort = pcscfPort;
        this.imsi = imsi;
        this.imei = imei;
    }

    public String[] getImpu() {
        return impu;
    }

    public void setImpu(String[] impu) {
        this.impu = impu;
    }

    public String getImpi() {
        return impi;
    }

    public void setImpi(String impi) {
        this.impi = impi;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String[] getPcscf() {
        return pcscf;
    }

    public void setPcscf(String[] pcscf) {
        this.pcscf = pcscf;
    }

    public int[] getPcscfPort() {
        return pcscfPort;
    }

    public void setPcscfPort(int[] pcscfPort) {
        this.pcscfPort = pcscfPort;
    }

    public String getImsi() {
        return imsi;
    }

    public void setImsi(String imsi) {
        this.imsi = imsi;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }
}
