package cafe.adriel.nmsalphabet.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.gigamole.library.NavigationTabBar;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.material_design_iconic_typeface_library.MaterialDesignIconic;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import cafe.adriel.nmsalphabet.App;
import cafe.adriel.nmsalphabet.R;
import cafe.adriel.nmsalphabet.Util;
import cafe.adriel.nmsalphabet.ui.view.ViewPagerFadeTransformer;

public class MainActivity extends BaseActivity {

    private WordsFragment homeFrag;
    private TranslateFragment translateFrag;
    private WordsFragment profileFrag;
    private SettingsFragment settingsFrag;

    @BindView(R.id.tabs)
    NavigationTabBar tabView;
    @BindView(R.id.pager)
    ViewPager pagerView;
    @BindView(R.id.fab)
    FloatingActionButton fabView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        checkPermissions();
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
        pagerView.setOffscreenPageLimit(4);
        pagerView.setAdapter(new TabPagerAdapter(getSupportFragmentManager()));
        pagerView.setPageTransformer(false, new ViewPagerFadeTransformer());
        initTabs();
        initFab();
    }

    private void initTabs(){
        int tabColor = getResources().getColor(R.color.colorPrimaryDark);
        ArrayList<NavigationTabBar.Model> models = new ArrayList<>();
        models.add(new NavigationTabBar.Model(getResources().getDrawable(R.drawable.tab_home), tabColor, getString(R.string.home)));
        models.add(new NavigationTabBar.Model(getResources().getDrawable(R.drawable.tab_translation), tabColor, getString(R.string.translate)));
        if(App.isLoggedIn) {
            models.add(new NavigationTabBar.Model(getResources().getDrawable(R.drawable.tab_profile), tabColor, getString(R.string.profile)));
        }
        models.add(new NavigationTabBar.Model(getResources().getDrawable(R.drawable.tab_settings), tabColor, getString(R.string.settings)));
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
                startActivity(new Intent(MainActivity.this, AddTranslationActivity.class));
            }
        });
    }

    private void checkPermissions(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_WIFI_STATE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.INTERNET,
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.ACCESS_WIFI_STATE,
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
            }, 0);
        }
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
                    if(App.isLoggedIn) {
                        profileFrag = WordsFragment.newInstance(WordsFragment.Type.PROFILE);
                        return profileFrag;
                    } else {
                        settingsFrag = new SettingsFragment();
                        return settingsFrag;
                    }
                case 3:
                    if(App.isLoggedIn) {
                        settingsFrag = new SettingsFragment();
                        return settingsFrag;
                    } else {
                        return null;
                    }
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return App.isLoggedIn ? 4 : 3;
        }

    }

}