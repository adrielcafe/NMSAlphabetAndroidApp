package cafe.adriel.nmsalphabet.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.ads.AdView;
import com.kobakei.ratethisapp.RateThisApp;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.material_design_iconic_typeface_library.MaterialDesignIconic;

import butterknife.BindView;
import butterknife.ButterKnife;
import cafe.adriel.nmsalphabet.R;
import cafe.adriel.nmsalphabet.util.AdUtil;
import cafe.adriel.nmsalphabet.util.Util;
import pl.tajchert.nammu.Nammu;

public class MainActivity extends BaseActivity {

    private static Activity instance;
    private static boolean backPressed;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.ad)
    AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainActivity.instance = this;
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        RateThisApp.onStart(this);
        RateThisApp.showRateDialogIfNeeded(this);
        init();
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.findItem(R.id.settings).setIcon(new IconicsDrawable(this)
                .icon(MaterialDesignIconic.Icon.gmi_settings)
                .color(Color.WHITE)
                .sizeDp(20));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.settings:
                openSettings();
                break;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Nammu.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void init() {
        AdUtil.initBannerAd(this, adView);
    }

    public static Activity getInstance(){
        return instance;
    }

    private void openSettings(){
        startActivity(new Intent(this, SettingsActivity.class));
    }
}