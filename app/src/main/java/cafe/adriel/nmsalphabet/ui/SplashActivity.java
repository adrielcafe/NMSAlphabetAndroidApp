package cafe.adriel.nmsalphabet.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.github.ybq.android.spinkit.SpinKitView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cafe.adriel.nmsalphabet.App;
import cafe.adriel.nmsalphabet.R;
import cafe.adriel.nmsalphabet.util.CacheUtil;
import cafe.adriel.nmsalphabet.util.Util;

public class SplashActivity extends BaseActivity {

    @BindView(R.id.atlas)
    ImageView atlasView;
    @BindView(R.id.loading)
    SpinKitView loadingView;
    @BindView(R.id.enter)
    Button enterView;
    @BindView(R.id.app_version)
    TextView appVersionView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        tintManager.setTintColor(Color.BLACK);
        ButterKnife.bind(this);
        init();
    }

    @Override
    protected void init() {
        Glide.with(this)
                .load(R.drawable.bg_splash)
                .asGif()
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(atlasView);

        appVersionView.setText(Util.getAppVersionName(this));

        enter(null);
    }

    @OnClick(R.id.enter)
    public void enter(View v){
        setLoading(true);
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                if(Util.isConnected(SplashActivity.this, false)){
                    enterWithInternet();
                } else {
                    enterWithoutInternet();
                }
            }
        });
    }

    private void enterWithInternet(){
        if (!App.forceUpdate(SplashActivity.this)) {
            if(Util.isConnected(SplashActivity.this, false)) {
                CacheUtil.cacheData();
            }
            if(CacheUtil.hasCachedData()) {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
            } else {
                setLoading(false);
                Util.isConnected(SplashActivity.this, true);
            }
        } else {
            startActivity(new Intent(SplashActivity.this, UpdateActivity.class));
            finish();
        }
    }

    private void enterWithoutInternet(){
        if(CacheUtil.hasCachedData()) {
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            finish();
        } else {
            setLoading(false);
            Util.isConnected(SplashActivity.this, true);
        }
    }

    private void setLoading(final boolean loading){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (loading) {
                    loadingView.setVisibility(View.VISIBLE);
                    enterView.setVisibility(View.GONE);
                } else {
                    loadingView.setVisibility(View.GONE);
                    enterView.setVisibility(View.VISIBLE);
                }
            }
        });
    }
}