package cafe.adriel.nmsalphabet.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.view.inputmethod.InputMethodManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import cafe.adriel.nmsalphabet.Constant;
import cafe.adriel.nmsalphabet.R;

public class Util {
    public static final String LANGUAGE_EN = "en";
    public static final String LANGUAGE_PT = "pt";
    public static final String LANGUAGE_DE = "de";

    public enum Language {
        ENGLISH,
        PORTUGUESE,
        GERMAN
    }

    private static final Handler ASYNC_HANDLER = new Handler();
    private static final String[] PERMISSIONS = {
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private static ConnectivityManager connectivityManager;

    public static boolean isEmpty(final CharSequence cs) {
        return cs == null || cs.length() == 0;
    }

    public static boolean isNotEmpty(final CharSequence cs) {
        return !isEmpty(cs);
    }

    public static void asyncCall(int delay, Runnable runnable){
        ASYNC_HANDLER.postDelayed(runnable, delay);
    }

    public static void shareText(Activity activity, String text){
        ShareCompat.IntentBuilder.from(activity)
                .setText(text)
                .setType("text/plain")
                .startChooser();
    }

    public static void askForPermissions(Activity context){
        List<String> missingPermissions = new ArrayList<>();
        for(int i = 0; i < PERMISSIONS.length; i++){
            if(ContextCompat.checkSelfPermission(context, PERMISSIONS[i]) != PackageManager.PERMISSION_GRANTED){
                missingPermissions.add(PERMISSIONS[i]);
            }
        }
        if(!missingPermissions.isEmpty()){
            ActivityCompat.requestPermissions(context, missingPermissions.toArray(new String[]{}), 0);
        }
    }

    public Language getLanguage(Context context){
        String language = PreferenceManager.getDefaultSharedPreferences(context).getString(Constant.SETTINGS_ACCOUNT_LANGUAGE, LANGUAGE_EN);
        switch (language){
            case LANGUAGE_PT:
                return Language.PORTUGUESE;
            case LANGUAGE_DE:
                return Language.GERMAN;
            default:
                return Language.ENGLISH;
        }
    }

    public static void updateLanguage(Context context){
        String language = PreferenceManager.getDefaultSharedPreferences(context).getString(Constant.SETTINGS_ACCOUNT_LANGUAGE, "en");
        Resources res = context.getResources();
        Configuration conf = res.getConfiguration();
        conf.locale = new Locale(language.toLowerCase());
        res.updateConfiguration(conf, res.getDisplayMetrics());
    }

    public static void restartActivity(Activity activity){
        if(activity != null) {
            activity.recreate();
        }
    }

    public static void hideSoftKeyboard(Activity activity) {
        if(activity.getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        }
    }

    public static String getAppVersionName(Context context) {
        try {
            return "v" + context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (Exception e) {
            return "";
        }
    }

    public static boolean isConnected(Context context, CoordinatorLayout coordinatorLayout){
        if(connectivityManager == null) {
            connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        }
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        boolean isConnected = (info != null && info.isConnected() && isConnectionFast(info.getType(), info.getSubtype()));
        if(!isConnected && coordinatorLayout != null){
            Snackbar.make(coordinatorLayout, R.string.connect_internet, Snackbar.LENGTH_SHORT).show();
        }
        return isConnected;
    }

    private static boolean isConnectionFast(int type, int subType){
        if(type == ConnectivityManager.TYPE_WIFI){
            return true;
        } else if (type == ConnectivityManager.TYPE_MOBILE){
            switch (subType){
                case TelephonyManager.NETWORK_TYPE_1xRTT:
                    return false; // ~ 50-100 kbps
                case TelephonyManager.NETWORK_TYPE_CDMA:
                    return false; // ~ 14-64 kbps
                case TelephonyManager.NETWORK_TYPE_EDGE:
                    return false; // ~ 50-100 kbps
                case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    return true; // ~ 400-1000 kbps
                case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    return true; // ~ 600-1400 kbps
                case TelephonyManager.NETWORK_TYPE_GPRS:
                    return false; // ~ 100 kbps
                case TelephonyManager.NETWORK_TYPE_HSDPA:
                    return true; // ~ 2-14 Mbps
                case TelephonyManager.NETWORK_TYPE_HSPA:
                    return true; // ~ 700-1700 kbps
                case TelephonyManager.NETWORK_TYPE_HSUPA:
                    return true; // ~ 1-23 Mbps
                case TelephonyManager.NETWORK_TYPE_UMTS:
                    return true; // ~ 400-7000 kbps
                case TelephonyManager.NETWORK_TYPE_EHRPD:
                    return true; // ~ 1-2 Mbps
                case TelephonyManager.NETWORK_TYPE_EVDO_B:
                    return true; // ~ 5 Mbps
                case TelephonyManager.NETWORK_TYPE_HSPAP:
                    return true; // ~ 10-20 Mbps
                case TelephonyManager.NETWORK_TYPE_IDEN:
                    return false; // ~25 kbps
                case TelephonyManager.NETWORK_TYPE_LTE:
                    return true; // ~ 10+ Mbps
                case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                default:
                    return false;
            }
        } else {
            return false;
        }
    }
}