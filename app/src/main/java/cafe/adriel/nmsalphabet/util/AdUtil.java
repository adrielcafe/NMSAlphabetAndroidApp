package cafe.adriel.nmsalphabet.util;

import android.content.Context;
import android.view.View;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import cafe.adriel.nmsalphabet.App;
import cafe.adriel.nmsalphabet.BuildConfig;

public class AdUtil {

    public static void initBannerAd(Context context, AdView adView){
        if(App.isPro(context) || BuildConfig.DEBUG){
            adView.setVisibility(View.GONE);
        } else {
            adView.loadAd(getAdRequest(context));
        }
    }

    private static AdRequest getAdRequest(Context context){
        AdRequest.Builder request = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR);
        if(BuildConfig.DEBUG) {
            request.addTestDevice(Util.getDeviceId(context));
        }
        return request.build();
    }

}