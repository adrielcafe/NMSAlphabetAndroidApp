package cafe.adriel.nmsalphabet.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.gigamole.library.NavigationTabBar;
import com.kobakei.ratethisapp.RateThisApp;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.material_design_iconic_typeface_library.MaterialDesignIconic;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import cafe.adriel.nmsalphabet.App;
import cafe.adriel.nmsalphabet.Constant;
import cafe.adriel.nmsalphabet.R;
import cafe.adriel.nmsalphabet.ui.view.ViewPagerFadeTransformer;
import cafe.adriel.nmsalphabet.util.ThemeUtil;
import cafe.adriel.nmsalphabet.util.Util;

public class MainActivity extends BaseActivity {
    private static Activity instance;

    private WordsFragment homeFrag;
    private TranslateFragment translateFrag;
    private WordsFragment profileFrag;

    @BindView(R.id.tabs)
    NavigationTabBar tabView;
    @BindView(R.id.pager)
    ViewPager pagerView;
    @BindView(R.id.fab)
    FloatingActionButton fabView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainActivity.instance = this;
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        RateThisApp.onStart(this);
        RateThisApp.showRateDialogIfNeeded(this);
        Util.askForPermissions(this);
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
    protected void init() {
        pagerView.setOffscreenPageLimit(2);
        pagerView.setAdapter(new TabPagerAdapter(getSupportFragmentManager()));
        pagerView.setPageTransformer(false, new ViewPagerFadeTransformer());
        initTabs();
        initFab();
    }

    public static Activity getInstance(){
        return instance;
    }

    private void initTabs(){
        int tabColor = ThemeUtil.getPrimaryDarkColor(this);
        ArrayList<NavigationTabBar.Model> models = new ArrayList<>();
        models.add(new NavigationTabBar.Model(getResources().getDrawable(R.drawable.tab_home), tabColor, getString(R.string.home)));
        models.add(new NavigationTabBar.Model(getResources().getDrawable(R.drawable.tab_translation), tabColor, getString(R.string.translate)));
        models.add(new NavigationTabBar.Model(getResources().getDrawable(R.drawable.tab_profile), tabColor, getString(R.string.profile)));
        tabView.setModels(models);
        tabView.setViewPager(pagerView);
        tabView.setOnTabBarSelectedIndexListener(new NavigationTabBar.OnTabBarSelectedIndexListener() {
            @Override
            public void onStartTabSelected(NavigationTabBar.Model model, int index) {

            }
            @Override
            public void onEndTabSelected(NavigationTabBar.Model model, int index) {
                Util.hideSoftKeyboard(MainActivity.this);
                if(index == 0 || index == 2){
                    fabView.show();
                } else {
                    fabView.hide();
                }
            }
        });
    }

    private void initFab(){
        Drawable fabIcon = new IconicsDrawable(this)
                .icon(MaterialDesignIconic.Icon.gmi_plus)
                .color(Color.WHITE)
                .sizeDp(50);
        fabView.setImageDrawable(fabIcon);
        fabView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(App.isSignedIn()) {
                    startActivity(new Intent(MainActivity.this, TranslationEditorActivity.class));
                } else {
                    showSignInDialog();
                }
            }
        });
    }

    private void showSignInDialog(){
        new AlertDialog.Builder(this)
                .setTitle(R.string.new_translation)
                .setMessage(R.string.signin_to_add_translations)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.signin, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Util.getSettings(MainActivity.this).edit()
                                .putBoolean(Constant.SETTINGS_HAS_SIGNED_IN, false)
                                .commit();
                        dialog.dismiss();
                        finish();
                        startActivity(new Intent(MainActivity.this, SplashActivity.class));
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