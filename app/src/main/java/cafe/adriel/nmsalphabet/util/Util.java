package cafe.adriel.nmsalphabet.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Base64;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import cafe.adriel.nmsalphabet.Constant;
import cafe.adriel.nmsalphabet.R;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import pl.tajchert.nammu.Nammu;
import pl.tajchert.nammu.PermissionCallback;

public class Util {

    private static final Handler ASYNC_HANDLER = new Handler();
    private static final OkHttpClient HTTP_CLIENT = new OkHttpClient.Builder()
            .addInterceptor(new HttpLoggingInterceptor())
            .build();
    private static final String[] PERMISSIONS = {
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public static InputFilter alienWordTranslationFilter = new InputFilter() {
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            String chr = source+"";
            if(chr.isEmpty() || chr.equals(" ") || chr.equals("-")){
                return null;
            } else {
                return !Character.isLetterOrDigit(chr.charAt(0)) ? "" : chr.toUpperCase();
            }
        }
    };

    private static ConnectivityManager connectivityManager;
    private static String deviceId;

    public static boolean isEmpty(String str) {
        return str == null || str.length() == 0 || str.toLowerCase().equals("null");
    }

    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    public static boolean isEmpty(List list) {
        return list == null || list.isEmpty();
    }

    public static boolean isNotEmpty(List list) {
        return !isEmpty(list);
    }

    public static String toHexColor(int color){
        return "#" + Integer.toHexString(color).replaceFirst("ff", "").toUpperCase();
    }

    public static byte[] toByteArray(Bitmap image){
        int maxSize = 1024000;
        int quality = 95;
        byte[] bytes;
        do {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, quality, byteArrayOutputStream);
            bytes = byteArrayOutputStream.toByteArray();
            quality -= 10;
            if(quality < 0){
                break;
            }
        } while (bytes.length > maxSize);
        return bytes;
    }

    public static String toBase64(Bitmap image){
        byte[] byteArray = toByteArray(image);
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    public static File toFile(Context context, Bitmap image, String name){
        try {
            File file = File.createTempFile(name, ".jpg", context.getCacheDir());
            byte[] byteArray = toByteArray(image);
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(byteArray);
            fos.flush();
            fos.close();
            return file;
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static String removeSpecialCharacters(String text){
        return text.replaceAll("[^\\w\\s]+", "");
    }

    public static OkHttpClient getHttpClient(){
        return HTTP_CLIENT;
    }

    public static String getDeviceId(Context context){
        if(isEmpty(deviceId)) {
            String androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            String md5 = md5(androidId);
            deviceId = isNotEmpty(md5) ? md5.toUpperCase() : null;
        }
        return deviceId;
    }

    public static void asyncCall(int delay, Runnable runnable){
        ASYNC_HANDLER.postDelayed(runnable, delay);
    }

    public static AlertDialog showLoadingDialog(Context context){
        return new AlertDialog.Builder(context)
                .setView(R.layout.dialog_loading)
                .setCancelable(false)
                .show();
    }

    public static void shareText(Activity activity, String text){
        ShareCompat.IntentBuilder.from(activity)
                .setType("text/plain")
                .setText(text)
                .startChooser();
    }

    public static boolean matchesBillingPayload(String payload){
        return Constant.BILLING_PAYLOAD.equals(payload);
    }

    public static void askForPermissions(final Activity activity, PermissionCallback callback){
        List<String> missingPermissions = new ArrayList<>();
        for(int i = 0; i < PERMISSIONS.length; i++){
            if(!Nammu.checkPermission(PERMISSIONS[i])){
                missingPermissions.add(PERMISSIONS[i]);
            }
        }
        if(missingPermissions.isEmpty()){
            callback.permissionGranted();
        } else {
            Nammu.askForPermission(activity, missingPermissions.toArray(new String[missingPermissions.size()]), callback);
        }
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

    public static InputFilter getTranslationInputFilter(){
        return alienWordTranslationFilter;
    }

    public static SharedPreferences getSettings(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static int getAppVersionCode(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (Exception e) {
            return -1;
        }
    }

    public static String getAppVersionName(Context context) {
        try {
            return "v" + context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (Exception e) {
            return "";
        }
    }

    public static int getNavigationBarHeight(Context context){
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return resources.getDimensionPixelSize(resourceId);
        } else {
            return 0;
        }
    }

    public static String getPackageName(Context context){
        return context.getPackageName();
    }

    public static String getFreePackageName(Context context){
        return getPackageName(context)
                .replace("dev", "free")
                .replace("pro", "free");
    }

    public static String getProPackageName(Context context){
        return getPackageName(context)
                .replace("dev", "pro")
                .replace("free", "pro");
    }

    public static String getGooglePlayUrl(Context context){
        return Constant.GOOGLE_PLAY_URL + getFreePackageName(context);
    }

    public static void openUrl(Activity activity, String url){
        Intent intent= new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        activity.startActivity(intent);
    }

    public static void printAppKeyHash(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(getPackageName(context), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.e("KEY_HASH:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isConnected(final Context context, boolean showToast){
        if(connectivityManager == null) {
            connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        }
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        boolean isConnected = (info != null && info.isConnected() && isConnectionFast(info.getType(), info.getSubtype()));
        if(!isConnected && showToast && context instanceof Activity){
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, R.string.connect_internet, Toast.LENGTH_SHORT).show();
                }
            });
        }
        return isConnected;
    }

    public static String md5(String str) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] array = md.digest(str.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte anArray : array) {
                sb.append(Integer.toHexString((anArray & 0xFF) | 0x100).substring(1, 3));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
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