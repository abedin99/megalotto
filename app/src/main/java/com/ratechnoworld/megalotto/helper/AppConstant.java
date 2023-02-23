package com.ratechnoworld.megalotto.helper;

public class AppConstant {

    // Put your api url and file path url
    public static final String API_URL = "https://domain.com/api/";
    public static final String FILE_URL = "https://domain.com/";

    // Put your purchase key
    public static final String PURCHASE_KEY = "1234567890";

    // ************************* Below value ca be change from Admin Panel *************************

    // Put your PayTm production merchant id
    public static String PAYTM_M_ID = "XXXXXXXXXXXXXXXXXXX";

    // Put your PayU production Merchant id & key
    public static String PAYU_M_ID = "XXXXXXXXXXXX";
    public static String PAYU_M_KEY = "XXXXXXXXXXX";

    // Set default country code and sign
    public static String COUNTRY_CODE = "+91";
    public static String CURRENCY_SIGN = "₹";

    // Set default app configuration
    public static int MAINTENANCE_MODE = 0;     // (0 for Off, 1 for On)
    public static int WALLET_MODE =  0;         // (0 for Enable, 1 for Disable)
    public static int MODE_OF_PAYMENT = 0;      // (0 for PayTm, 1 for PayU, 2 for RazorPay)

    // Set withdraw limit (In Amount)
    public static int MIN_WITHDRAW_LIMIT = 10;
    public static int MAX_WITHDRAW_LIMIT = 5000;

    // Set deposit limit (In Amount)
    public static int MIN_DEPOSIT_LIMIT = 10;
    public static int MAX_DEPOSIT_LIMIT = 25000;

    // Set Bonus Uses (In percentage)
    public static int TICKET_BONUS_USED = 5;

    // Set Refer Prize (In Amount)
    public static int APP_SHARE_PRIZE = 50;
    public static int APP_DOWNLOAD_PRIZE = 50;

    // ******************************* Don't change below value  ***********************************

    // PayU Production API Details
    public static final long API_CONNECTION_TIMEOUT = 1201;
    public static final long API_READ_TIMEOUT = 901;
    public static final String SERVER_MAIN_FOLDER = "";

}
