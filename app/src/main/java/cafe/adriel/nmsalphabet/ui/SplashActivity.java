package cafe.adriel.nmsalphabet.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.easyandroidanimations.library.Animation;
import com.easyandroidanimations.library.AnimationListener;
import com.easyandroidanimations.library.FadeInAnimation;
import com.easyandroidanimations.library.FadeOutAnimation;
import com.github.ybq.android.spinkit.SpinKitView;

import butterknife.BindView;
import butterknife.ButterKnife;
import cafe.adriel.nmsalphabet.App;
import cafe.adriel.nmsalphabet.R;
import cafe.adriel.nmsalphabet.Util;

public class SplashActivity extends BaseActivity {

    @BindView(R.id.atlas)
    ImageView atlasView;
    @BindView(R.id.signin_layout)
    LinearLayout signInLayout;
    @BindView(R.id.google_signin)
    RelativeLayout googleSignInView;
    @BindView(R.id.anonymous_signin)
    RelativeLayout anonymousSignInView;
    @BindView(R.id.load)
    SpinKitView loadView;

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
                .load(R.drawable.atlas)
                .asGif()
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(atlasView);

        googleSignInView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLoading(true);
                Util.asyncCall(4000, new Runnable() {
                    @Override
                    public void run() {
                        App.isLoggedIn = true;
                        finish();
                        startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    }
                });
            }
        });
        anonymousSignInView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLoading(true);
                Util.asyncCall(4000, new Runnable() {
                    @Override
                    public void run() {
                        App.isLoggedIn = false;
                        finish();
                        startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    }
                });
            }
        });
    }

    private void setLoading(boolean load){
        final View fadeInView;
        final View fadeOutView;
        if(load){
            fadeInView = loadView;
            fadeOutView = signInLayout;
        } else {
            fadeInView = signInLayout;
            fadeOutView = loadView;
        }
        new FadeOutAnimation(fadeOutView).setListener(new AnimationListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                new FadeInAnimation(fadeInView).animate();
            }
        }).animate();
    }
}