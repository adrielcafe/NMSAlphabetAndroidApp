package cafe.adriel.nmsalphabet.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.telephony.TelephonyManager;
import android.view.inputmethod.InputMethodManager;

import java.util.Locale;

import cafe.adriel.nmsalphabet.Constant;
import cafe.adriel.nmsalphabet.R;

public class Util {
    public enum Language {
        ENGLISH,
        PORTUGUESE,
        GERMAN
    }

    private static final Handler ASYNC_HANDLER = new Handler();

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

    public static void shareText(Context context, String text){
        Intent i = new Intent();
        i.setAction(Intent.ACTION_SEND);
        i.putExtra(Intent.EXTRA_TEXT, text);
        i.setType("text/plain");
        context.startActivity(i);
    }

    public Language getLanguage(Context context){
        String language = PreferenceManager.getDefaultSharedPreferences(context).getString(Constant.SETTINGS_ACCOUNT_LANGUAGE, "en");
        switch (language){
            case "pt":
                return Language.PORTUGUESE;
            case "de":
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
        Intent i = new Intent(activity, activity.getClass());
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.finish();
        activity.startActivity(i);
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