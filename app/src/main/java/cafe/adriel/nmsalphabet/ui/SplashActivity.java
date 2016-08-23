package cafe.adriel.nmsalphabet.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.easyandroidanimations.library.FadeInAnimation;
import com.easyandroidanimations.library.FadeOutAnimation;
import com.github.ybq.android.spinkit.SpinKitView;

import butterknife.BindView;
import butterknife.ButterKnife;
import cafe.adriel.nmsalphabet.App;
import cafe.adriel.nmsalphabet.R;
import cafe.adriel.nmsalphabet.util.DbUtil;
import cafe.adriel.nmsalphabet.util.Util;

public class SplashActivity extends BaseActivity {

    @BindView(R.id.atlas)
    ImageView atlasView;
    @BindView(R.id.load)
    SpinKitView loadView;
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

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                if(Util.isConnected(SplashActivity.this)) {
                    if (!App.forceUpdate(SplashActivity.this)) {
                        setLoading(true);
                        afterSignIn();
                    } else {
                        finish();
                        startActivity(new Intent(SplashActivity.this, UpdateActivity.class));
                    }
                } else {
                    setLoading(false);
                }
            }
        });
    }

    private void afterSignIn(){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                DbUtil.cacheData();
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
            }
        });
    }

    private void setLoading(final boolean load){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (load) {
                    if(loadView.getVisibility() != View.VISIBLE) {
                        new FadeInAnimation(loadView).animate();
                    }
                } else {
                    new FadeOutAnimation(loadView).animate();
                }
            }
        });
    }
}