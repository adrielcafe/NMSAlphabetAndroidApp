package cafe.adriel.nmsalphabet.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.widget.Toast;

import com.gigamole.library.ntb.NavigationTabBar;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.kobakei.ratethisapp.RateThisApp;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.material_design_iconic_typeface_library.MaterialDesignIconic;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cafe.adriel.nmsalphabet.App;
import cafe.adriel.nmsalphabet.BuildConfig;
import cafe.adriel.nmsalphabet.Constant;
import cafe.adriel.nmsalphabet.R;
import cafe.adriel.nmsalphabet.util.AdUtil;
import cafe.adriel.nmsalphabet.util.ThemeUtil;
import cafe.adriel.nmsalphabet.util.Util;
import co.mobiwise.materialintro.animation.MaterialIntroListener;
import pl.tajchert.nammu.Nammu;

public class MainActivity extends BaseActivity {

    private static Activity instance;
    private static InterstitialAd interstitialAd;
    private static WordsFragment homeFrag;
    private static WordsFragment profileFrag;
    private static TranslateFragment translateFrag;
    private static boolean backPressed;

    @BindView(R.id.tabs)
    NavigationTabBar tabView;
    @BindView(R.id.pager)
    ViewPager pagerView;
    @BindView(R.id.fab)
    FloatingActionButton fabView;
    @BindView(R.id.ad)
    AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainActivity.instance = this;
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        RateThisApp.onStart(this);
        RateThisApp.showRateDialogIfNeeded(this);
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(pagerView != null && fabView != null) {
            if (pagerView.getCurrentItem() == 1 || pagerView.getCurrentItem() == 3) {
                fabView.hide();
            } else {
                fabView.show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if(backPressed){
            super.onBackPressed();
        } else {
            backPressed = true;
            Toast.makeText(this, R.string.press_back_again, Toast.LENGTH_SHORT).show();
            Util.asyncCall(3000, new Runnable() {
                @Override
                public void run() {
                    backPressed = false;
                }
            });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Nammu.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @OnClick(R.id.fab)
    public void addTranslation(){
        if(Util.isConnected(this)) {
            if(App.isPro(this) || BuildConfig.DEBUG){
                openTranslationEditor();
            } else {
                AdUtil.showInterstitialAd(this, interstitialAd);
            }
        }
    }

    @Override
    protected void init() {
        Drawable fabIcon = new IconicsDrawable(this)
                .icon(MaterialDesignIconic.Icon.gmi_plus)
                .color(Color.WHITE)
                .sizeDp(50);
        fabView.setImageDrawable(fabIcon);
        pagerView.setOffscreenPageLimit(2);
        pagerView.setAdapter(new TabPagerAdapter(getSupportFragmentManager()));
        interstitialAd = AdUtil.initInterstitialAd(this, new Runnable() {
            @Override
            public void run() {
                openTranslationEditor();
            }
        });
        initTabs();
        AdUtil.initBannerAd(this, adView, fabView);
        Util.asyncCall(500, new Runnable() {
            @Override
            public void run() {
                try {
                    Util.showShowcase(MainActivity.this, Constant.INTRO_HOME, R.string.intro_home, homeFrag.headerHomeLayout, new MaterialIntroListener() {
                        @Override
                        public void onUserClicked(String s) {
                            Util.showShowcase(MainActivity.this, Constant.INTRO_ADD_TRANSLATION, R.string.intro_add_translation, fabView, null);
                        }
                    });
                } catch (Exception e){ }
            }
        });
    }

    public static Activity getInstance(){
        return instance;
    }

    private void initTabs(){
        String userGender = App.isSignedIn() ? App.getUser().getGender() : Constant.GENDER_MALE;
        int tabColor = ThemeUtil.getPrimaryDarkColor(this);
        ArrayList<NavigationTabBar.Model> models = new ArrayList<>();
        models.add(new NavigationTabBar.Model
                .Builder(getResources().getDrawable(R.drawable.tab_home), tabColor)
                .title(getString(R.string.home)).build());
        models.add(new NavigationTabBar.Model
                .Builder(getResources().getDrawable(R.drawable.tab_translation), tabColor)
                .title(getString(R.string.translate)).build());
        models.add(new NavigationTabBar.Model
                .Builder(getResources().getDrawable(userGender.equals(Constant.GENDER_FEMALE) ? R.drawable.tab_profile_female : R.drawable.tab_profile_male), tabColor)
                .title(getString(R.string.profile)).build());
        tabView.setModels(models);
        tabView.setViewPager(pagerView);
        tabView.setOnTabBarSelectedIndexListener(new NavigationTabBar.OnTabBarSelectedIndexListener() {
            @Override
            public void onStartTabSelected(NavigationTabBar.Model model, int index) {

            }
            @Override
            public void onEndTabSelected(NavigationTabBar.Model model, int index) {
                Util.hideSoftKeyboard(MainActivity.this);
                switch (index){
                    case 0:
                        fabView.show();
                        break;
                    case 1:
                        fabView.hide();
                        try {
                            Util.showShowcase(MainActivity.this, Constant.INTRO_TRANSLATE, R.string.intro_translate, translateFrag.controlsLayout, null);
                        } catch (Exception e){ }
                        break;
                    case 2:
                        fabView.show();
                        try {
                            Util.showShowcase(MainActivity.this, Constant.INTRO_PROFILE, R.string.intro_profile, profileFrag.wordsView, null);
                        } catch (Exception e){ }
                        break;
                }
            }
        });
    }

    private void openTranslationEditor(){
        if (App.isSignedIn()) {
            startActivity(new Intent(MainActivity.this, TranslationEditorActivity.class));
        } else {
            showSignInDialog(MainActivity.this);
        }
    }

    public static void showSignInDialog(final Activity activity){
        new AlertDialog.Builder(activity)
                .setTitle(R.string.new_translation)
                .setMessage(R.string.signin_to_add_translations)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.signin, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Util.getSettings(activity).edit()
                                .putBoolean(Constant.SETTINGS_HAS_SIGNED_IN, false)
                                .commit();
                        dialog.dismiss();
                        activity.finish();
                        activity.startActivity(new Intent(activity, SplashActivity.class));
                    }
                })
                .show();
    }

    private class TabPagerAdapter extends FragmentPagerAdapter {

        public TabPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    homeFrag = WordsFragment.newInstance(WordsFragment.Type.HOME);
                    return homeFrag;
                case 1:
                    translateFrag = new TranslateFragment();
                    return translateFrag;
                case 2:
                    profileFrag = WordsFragment.newInstance(WordsFragment.Type.PROFILE);
                    return profileFrag;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 3;
        }

    }

}