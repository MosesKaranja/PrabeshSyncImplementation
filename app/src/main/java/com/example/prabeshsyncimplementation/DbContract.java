package com.example.prabeshsyncimplementation;

public class DbContract {
    public static final int SYNC_STATUS_OK = 0;
    public static final int SYNC_STATUS_FAILED = 1;

    public static final String DATABASE_NAME = "contacsdb";
    public static final String TABLE_NAME = "contactinfo";
    public static final String UI_UPDATE_BROADCAST = "com.example.prabeshsyncimplementation.uiupdatebroadcast";

    public static final String NAME = "name";
    public static final String SYNC_STATUS = "syncstatus";

    public static final String SERVER_URL="https://10.0.2.2/AndroidSyncBackendPrabesh/SyncInfo.php";

}
