package cafe.adriel.nmsalphabet.util;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.view.View;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import cafe.adriel.nmsalphabet.App;
import cafe.adriel.nmsalphabet.BuildConfig;
import cafe.adriel.nmsalphabet.Constant;
import cafe.adriel.nmsalphabet.R;

public class AdUtil {

    public static void initBannerAd(Context context, AdView adView, FloatingActionButton fabView){
        if(App.isPro(context) || BuildConfig.DEBUG){
            adView.setVisibility(View.GONE);
        } else {
            adView.loadAd(getAdRequest(context));
            fixFabMarginBottom(adView, fabView);
        }
    }

    public static InterstitialAd initInterstitialAd(Context context, final Runnable onAdClosed){
        final InterstitialAd interstitialAd = new InterstitialAd(context);
        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                onAdClosed.run();
            }
            @Override
            public void onAdFailedToLoad(int errorCode) {
                onAdClosed.run();
            }
            @Override
            public void onAdLoaded() {
                interstitialAd.show();
            }
        });
        interstitialAd.setAdUnitId(context.getString(R.string.interstitial_ad_unit_id));
        return interstitialAd;
    }

    public static void showInterstitialAd(Context context, InterstitialAd interstitialAd){
        interstitialAd.loadAd(getAdRequest(context));
    }

    private static AdRequest getAdRequest(Context context){
        int gender = AdRequest.GENDER_MALE;
        if(App.isSignedIn() && App.getUser().getGender().equals(Constant.GENDER_FEMALE)){
            gender = AdRequest.GENDER_FEMALE;
        }
        AdRequest.Builder request = new AdRequest.Builder()
                .setGender(gender)
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR);
        if(BuildConfig.DEBUG) {
            request.addTestDevice(Util.getDeviceId(context));
        }
        return request.build();
    }

    private static void fixFabMarginBottom(final AdView adView, final FloatingActionButton fabView){
        if(fabView != null) {
            adView.post(new Runnable() {
                @Override
                public void run() {
                    CoordinatorLayout.LayoutParams fabParams = (CoordinatorLayout.LayoutParams) fabView.getLayoutParams();
                    fabParams.bottomMargin = fabParams.bottomMargin + adView.getHeight();
                    fabView.setLayoutParams(fabParams);
                }
            });
        }
    }

}