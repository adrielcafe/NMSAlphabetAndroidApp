package cafe.adriel.nmsalphabet.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.ads.AdView;
import com.kobakei.ratethisapp.RateThisApp;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.material_design_iconic_typeface_library.MaterialDesignIconic;

import butterknife.BindView;
import butterknife.ButterKnife;
import cafe.adriel.nmsalphabet.App;
import cafe.adriel.nmsalphabet.BuildConfig;
import cafe.adriel.nmsalphabet.Constant;
import cafe.adriel.nmsalphabet.R;
import cafe.adriel.nmsalphabet.billing.IabHelper;
import cafe.adriel.nmsalphabet.billing.IabResult;
import cafe.adriel.nmsalphabet.billing.Inventory;
import cafe.adriel.nmsalphabet.billing.Purchase;
import cafe.adriel.nmsalphabet.util.AdUtil;
import cafe.adriel.nmsalphabet.util.Util;
import pl.tajchert.nammu.Nammu;

public class MainActivity extends BaseActivity implements
        IabHelper.QueryInventoryFinishedListener, IabHelper.OnIabPurchaseFinishedListener, IabHelper.OnIabSetupFinishedListener {

    private static Activity instance;
    private static MenuItem proUpgradeMenuItem;
    private static boolean backPressed;

    private IabHelper billingHelper;

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

        init();
        initBilling();

        RateThisApp.onStart(this);
        RateThisApp.showRateDialogIfNeeded(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (billingHelper!= null) {
            billingHelper.dispose();
            billingHelper = null;
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        proUpgradeMenuItem = menu.findItem(R.id.pro_upgrade).setIcon(new IconicsDrawable(this)
                .icon(MaterialDesignIconic.Icon.gmi_shopping_basket)
                .color(Color.WHITE)
                .sizeDp(20));
        menu.findItem(R.id.settings).setIcon(new IconicsDrawable(this)
                .icon(MaterialDesignIconic.Icon.gmi_tune)
                .color(Color.WHITE)
                .sizeDp(20));
        updateUi();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.pro_upgrade:
                proUpgrade();
                break;
            case R.id.settings:
                openSettings();
                break;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (billingHelper != null && !billingHelper.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Nammu.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onIabSetupFinished(IabResult result) {
        if (billingHelper != null && result.isSuccess()) {
            billingHelper.queryInventoryAsync(MainActivity.this);
        }
    }

    @Override
    public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
        if(result.isSuccess()){
            Purchase purchase = inventory.getPurchase(Constant.SKU_PRO_UPGRADE);
            if(purchase != null && Util.matchesBillingPayload(purchase.getDeveloperPayload())) {
                App.setIsPro(this, true);
                updateUi();
            }
        }
    }

    @Override
    public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
        if(result.isSuccess()) {
            if (purchase != null && purchase.getSku().equals(Constant.SKU_PRO_UPGRADE)
                    && Util.matchesBillingPayload(purchase.getDeveloperPayload())) {
                App.setIsPro(this, true);
                updateUi();
            }
        } else if(result.getResponse() == IabHelper.BILLING_RESPONSE_RESULT_ITEM_ALREADY_OWNED){
            App.setIsPro(this, true);
            updateUi();
        }
    }

    public static Activity getInstance(){
        return instance;
    }

    @Override
    protected void init() {
        AdUtil.initBannerAd(this, adView);
    }

    private void initBilling() {
        billingHelper = new IabHelper(this, getString(R.string.play_public_key));
        billingHelper.enableDebugLogging(BuildConfig.DEBUG);
        billingHelper.startSetup(this);
    }

    private void proUpgrade() {
        billingHelper.launchPurchaseFlow(this, Constant.SKU_PRO_UPGRADE, Constant.REQUEST_PRO_UPGRADE, this, Constant.BILLING_PAYLOAD);
    }

    private void openSettings(){
        startActivity(new Intent(this, SettingsActivity.class));
    }

    private void updateUi(){
        if(App.isPro(this)){
            adView.setVisibility(View.GONE);
            if(proUpgradeMenuItem != null) {
                proUpgradeMenuItem.setVisible(false);
            }
        } else {
            adView.setVisibility(View.VISIBLE);
            if(proUpgradeMenuItem != null) {
                proUpgradeMenuItem.setVisible(true);
            }
        }
    }
}