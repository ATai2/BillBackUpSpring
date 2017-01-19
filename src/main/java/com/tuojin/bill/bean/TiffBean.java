package com.tuojin.bill.bean;

/**
 * Created by Administrator on 2017/1/16.
 */
public class TiffBean {

    private String sVoucherKey;
    private String sUoucherNo;
    private String sFilePahtName;
    private String destination;

    public String getsFilePahtName() {
        return sFilePahtName;
    }

    public void setsFilePahtName(String sFilePahtName) {
        this.sFilePahtName = sFilePahtName;
    }

    public String getsVoucherKey() {
        return sVoucherKey;
    }

    public void setsVoucherKey(String sVoucherKey) {
        this.sVoucherKey = sVoucherKey;
    }

    public String getsUoucherNo() {
        return sUoucherNo;
    }

    public void setsUoucherNo(String sUoucherNo) {
        this.sUoucherNo = sUoucherNo;
    }


    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }
}
