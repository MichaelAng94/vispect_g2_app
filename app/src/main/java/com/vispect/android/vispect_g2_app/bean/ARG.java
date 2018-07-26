package com.vispect.android.vispect_g2_app.bean;

/**
 * 文字配置
 * Created by xu on 15/1/8.
 */
public class ARG {

    public static final int WAIT = 10086;

    public static final int UPDATE_TYPE_FIRMWARE = 1;
    public static final int UPDATE_TYPE_SOFTWARE_V = 2;
    public static final int UPDATE_TYPE_SYSTEM = 3;
    public static final int UPDATE_TYPE_MYAPP = 4;
    public static final int UPDATE_TYPE_SOFTWARE_S = 5;
    public static final int UPDATE_TYPE_MYAPP_IOS = 6;
    public static final int UPDATE_TYPE_OBD = 9;

    //1：V固件  2：V系统软件 3：操作系统 4：Android App 5：S系统软件 6：IOS App 9 ：OBD


    public static final String VERIFY_CODE_OP_SIGIN = "signin";
    public static final String VERIFY_CODE_OP_FIND_PASSWORD = "find_password";
    public static final String VERIFY_CODE_OP_MODIFY_PASSWORD = "modify_password";
    public static final String VERIFY_CODE_OP_MODIFY_MOBILE = "modify_mobile";

    public static final String SMS_CONTEXT = "SMS_CONTEXT";

    public static final String HAS_CONTACTS = "HAS_CONTACTS";

    public static final String USER_ID = "USER_ID";

    public static final String MSG_TOAST_CONTENT = "MSG_TOAST_CONTENT";

    public static final String CONTACTS_INFO = "CONTACTS_INFO";
    public static final String CONTACTS_ID = "CONTACTS_ID";
    public static final String PEOPLE_INFO = "PEOPLE_INFO";
    public static final String PEOPLE_LIST = "PEOPLE_LIST";
    public static final String PEOPLE_ID = "PEOPLE_ID";
    public static final String CLUB_INFO = "CLUB_INFO";

    public static final String GATHER_MESSAGE_ID = "GATHER_MESSAGE_ID";

    public static final String GROUP_ID = "GROUP_ID";
    public static final String IS_FROM_GROUP_LIST = "IS_FROM_GROUP_LIST";

    public static final String IS_OWNER = "IS_OWNER";

    //    public static final int NETWORK_RESULT_CODE_SUCCESS = 1000;
    public static final int NETWORK_RESULT_CODE_SUCCESS = 1000;
    public static final int NETWORK_RESULT_CODE_GENERAL_ERROR = 1001;
    public static final int NETWORK_RESULT_CODE_TOKEN_EXPIRED = 1002;
    public static final int NETWORK_RESULT_CODE_NO_PERMISSIONS = 1003;
    public static final int NETWORK_RESULT_CODE_PERMISSIONS_CHANGE = 1004;

    public static final String MSG_ACTI_CREATE_SUCCESS = "MSG_ACTI_CREATE_SUCCESS";
    public static final String MSG_PK_CREATE_SUCCESS = "MSG_PK_CREATE_SUCCESS";
    public static final String MSG_CLUB_CREATE_SUCCESS = "MSG_CLUB_CREATE_SUCCESS";

    public static final String MSG_REFRESH = "MSG_REFRESH";
    public static final String CONTACT_ADD_SUCCESS = "CONTACT_ADD_SUCCESS";
    public static final String GROUP_ADD_SUCCESS = "GROUP_ADD_SUCCESS";
    public static final String MSG_UNREAD_COUNT = "MSG_UNREAD_COUNT";

    public static final String MSG_SEND_MESSAGE_SUCCESS = "MSG_SEND_MESSAGE_SUCCESS";

    public static final String MULTIPLE_VALUE = "MULTIPLE_VALUE";

    public static final String MANY_SEND_TYPE = "MANY_SEND_TYPE";

    public static final int MSG_TIME_COUNT = 123;
    public static final int MSG_TOAST = 11;

    public static final int MANY_SEND_TYPE_PUSH = 0;
    public static final int MANY_SEND_TYPE_SMS = 1;

    public static final int IMPORT_FROM_MY_PHONE_SUCCESS = 8801;

    public static final int MULTIPLE_DELETE = 9001;
    public static final int MULTIPLE_MOVE = 9002;
    public static final int MULTIPLE_PICK = 9000;
    public static final int MORE_SCAN_TAG = 9901;

    public static final int RESULT_GROUP_PICK = 11001;
    public static final int RESULT_PEOPLE_PICK = 11002;

    public static final int TIME_REFRESH = 20001;

    public static final String SENT_SMS_ACTION = "SENT_SMS_ACTION";

    public static final String FORM_TOP_NOTIFICATION = "FORM_TOP_NOTIFICATION";

    public static final String NAME_MOD_SUCCESS = "NAME_MOD_SUCCESS";
    public static final String TRANSFER_SUCCESS = "TRANSFER_SUCCESS";

    public static final String GET_NEW_MESSAGE = "GET_NEW_MESSAGE";

    public static final int RESULT_SUCCESS = 666666;
    public static final int RESULT_FAIL = 555555;

    public static final int RESULT_COURT = 123;
    public static final int RESULT_CLUB = 124;
    public static final int MSG_COURT_SELECTED = 19001;
    public static final String COURT_SELECTED = "COURT_SELECTED";
    public static final String CLUB_SELECTED = "CLUB_SELECTED";

    public static final String ACTI_ID = "ACTI_ID";
    public static final String ACTI_SHARE_BUTTON = "ACTI_SHARE_BUTTON";

    public static final String MSG_SCORE_1 = "MSG_SCORE_1";
    public static final String MSG_SCORE_2 = "MSG_SCORE_2";
    public static final String MSG_SCORE_3 = "MSG_SCORE_3";

    public static final String MEMBER_LIST = "MEMBER_LIST";

    public static final int PHOTO_PICKED_WITH_DATA = 3021;
    public static final int CAMERA_WITH_DATA = 3023;

    public static final int RESULT_IMAGE_PHOTO = 201;
    public static final int RESULT_IMAGE_ALBUMS = 202;
    public static final int RESULT_IMAGE_ALBUMS_KITKAT_ABOVE = 2021;


    public static final int CAR_LENGTH = 3001;
    public static final int CAR_WIDTH = 3002;
    public static final int CAR_HEIGTH = 3003;

    public static final String CAR_LENGTH_STR = "CAR_LENGTH";
    public static final String CAR_WIDTH_STR = "CAR_WIDTH";
    public static final String CAR_HEIGTH_STR = "CAR_HEIGTH";

    public static final String EDITTEXT_TITLE = "EDITTEXT_TITLE";
    public static final String EDITTEXT_VALUE = "EDITTEXT_VALUE";

    public static final String EDITTEXT_ONLY_INPUT_TYPE = "EDITTEXT_ONLY_INPUT_TYPE";

    public static final String EDITTEXT_LENGTH_FILTER = "EDITTEXT_LENGTH_FILTER";

    public static final int EDITTEXT_CODE = 3000;


    public static final String DISCOVERY_TOGGLE = "DISCOVERY_TOGGLE";
    public static final int DISCOVERY_TOGGLE_MAP = 0;
    public static final int DISCOVERY_TOGGLE_LIST = 1;

    public static final String MSG_LOCATION = "MSG_LOCATION";


    public static final String SHOW_BACKGROUND_CHECK = "SHOW_BACKGROUND_CHECK";

    public static final String VIDEO_LIST = "VIDEO_LIST";

    public static final int DOWNLOAD_PROGRESS = 9000;
    public static final String DOWNLOAD_DONE = "DOWNLOAD_DONE";

    public static final String IS_FROM_HELP_BUTTON = "IS_FROM_HELP_BUTTON";

    public static final String GET_TRACKLIST_SUCCESS = "GET_TRACKLIST_SUCCESS";

    public static final String CARCORDER_FILE_PATH = "CARCORDER_FILE_PATH";

    /**
     * 返回值
     */
    public final static int EDIT_BLE_PAS = 1004;
    public final static int EDIT_WIFI_PAS = 1005;
    public final static int EDIT_BLE_NAME = 1006;
    public final static int EDIT_WIFI_NAME = 1007;
    public final static int EDIT_GPS_INTERVAL = 1008;
    public final static int EDIT_ADAS_INTERVAL = 1009;
    public final static int BLE_LOGIN = 1010;

    public static final String ISADJUSTMENTS  = "ISADJUSTMENTS";



    /**
     * 上传至服务端的报警代码
     */
    public final static int DLD = 10;    //左车道偏离_虚线报警
    public final static int DLF = 11;    // 左车道偏离_实线报警
    public final static int DRD = 12;    //右车道偏离_虚线报警
    public final static int DRF = 13;    // 右车道偏离_实线报警
    public final static int CCH = 14;    // 前方车辆碰撞_危险级别
    public final static int CCL = 15;    // 前方车辆碰撞_小心级别
    public final static int CPH = 16;    // 前方行人碰撞_危险级别
    public final static int CPL = 17;    // 前方行人碰撞_小心级别

    /**
     * 等待结果的超时时间
     *
     * SET_VALUE_TIMEOUT :设置参数的超时时间
     * OPEN_WIFI_TIMEOUT :打开WIFI等待返回账号名密码的超时时间
     *
     */
    public static final int SET_VALUE_TIMEOUT = 10000;
    public static final int OPEN_WIFI_TIMEOUT = 10000;

    /**
     * 请求查看的文档的类型
     *
     */
    public static final String DOCTYPE = "DOCTYPE";
    public static final String DEVICETYPE = "DEVICETYPE";


    /**
     * 请求调整界面的类型
     *
     */
    public static final int SHOWTYPE_USERGUIDE = 10001;
    public static final int SHOWTYPE_CALIBRATION = 10002;

    /**
     * 车的品牌
     */
    public static final String CARBRAND = "CARBRAND";
    public static final int REQUEST_CARBRAND = 10012;

    /**
     * 需要破解的数据
     * OBD_CRACK_VALUE_POST ： key
     * LIGHT_LEFT           ： 左转向
     * LIGHT_RIGHT          ： 右转向
     */
    public static final String OBD_CRACK_VALUE_POST = "OBDCRACKVALUEPOST";
    public static final String LIGHT_LEFT = "LIGHTLEFT";
    public static final String LIGHT_RIGHT= "LIGHTRIGHT";
    /**
     * 校准中心点
     *CORRECTING_PROGRESS : 校正进度
     * STARTCORRECTING:     开始校正的结果
     */
    public static final String CORRECTING_PROGRESS= "CORRECTINGPROGRESS";
    public static final String START_CORRECTING= "STARTCORRECTING";


}
