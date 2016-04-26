package cafe.adriel.nmsalphabet;

import android.net.Uri;

public class Constant {
    public static final String PACKAGE_NAME     		= "cafe.adriel.nmsalphabet";
    public static final String CONTACT_EMAIL    		= "me@adriel.cafe";
    public static final String GOOGLE_PLAY_URL     		= "https://play.google.com/store/apps/details?id=" + PACKAGE_NAME;
    public static final Uri    MARKET_URI          		= Uri.parse("market://details?id=" + PACKAGE_NAME);

    public static final String EXTRA_TYPE = "type";

    public static final String SETTINGS_ACCOUNT_STATUS      = "account_status";
    public static final String SETTINGS_ACCOUNT_LANGUAGE    = "account_language";
    public static final String SETTINGS_ABOUT_FEEDBACK      = "about_feedback";
    public static final String SETTINGS_ABOUT_SHARE         = "about_share";
    public static final String SETTINGS_ABOUT_RATE          = "about_rate";
    public static final String SETTINGS_ABOUT_VERSION	    = "about_version";

}