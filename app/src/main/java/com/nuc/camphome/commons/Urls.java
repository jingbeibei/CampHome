package com.nuc.camphome.commons;

/**
 * Created by 景贝贝 on 2016/7/14.
 */
public interface Urls {
    public final static int PAZE_SIZE = 10;
    public final static String BaseUrl = "http://202.207.177.28:88/API/Interface/";

    public final static String RegisterUrl = BaseUrl + "Register?";
    public final static String GetLastFlashPicture = BaseUrl + "GetLastFlashPicture?";
    public final static String ValidateUserURL = BaseUrl + "ValidateUser?";
    public final static String GetPsersonnelURL = BaseUrl + "GetPersonnel?";
    public final static String GetReportsForAdminURL = BaseUrl + "GetReportsForAdmin?";
    //    public final static String PostAuditReportURL = BaseUrl + "AuditReport?";
//    public final static String PostReportURL = BaseUrl + "PostReport?";
    public final static String UpdatePersonnelURL = BaseUrl + "UpdatePersonnel?";
    public final static String ChangePasswordURL = BaseUrl + "ChangePassword?";
    public final static String GetLastAPPURL = BaseUrl + "GetLastAPP?";
    public final static String GetPictureNewsURL = BaseUrl + "GetPictureNews?";

}
