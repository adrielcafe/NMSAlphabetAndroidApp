package cafe.adriel.nmsalphabet.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.easyandroidanimations.library.Animation;
import com.easyandroidanimations.library.AnimationListener;
import com.easyandroidanimations.library.FadeInAnimation;
import com.easyandroidanimations.library.FadeOutAnimation;
import com.facebook.AccessToken;
import com.github.ybq.android.spinkit.SpinKitView;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

import butterknife.BindView;
import butterknife.ButterKnife;
import cafe.adriel.nmsalphabet.Constant;
import cafe.adriel.nmsalphabet.R;
import cafe.adriel.nmsalphabet.util.AnalyticsUtil;
import cafe.adriel.nmsalphabet.util.SocialUtil;
import cafe.adriel.nmsalphabet.util.Util;

public class SplashActivity extends BaseActivity {

    @BindView(R.id.atlas)
    ImageView atlasView;
    @BindView(R.id.signin_layout)
    LinearLayout signInLayout;
    @BindView(R.id.facebook_signin)
    RelativeLayout facebookSignInLayout;
    @BindView(R.id.anonymous_signin)
    RelativeLayout anonymousSignInLayout;
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void init() {
        Glide.with(this)
                .load(R.drawable.atlas)
                .asGif()
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(atlasView);

        facebookSignInLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLoading(true);
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        facebookSignIn();
                    }
                });
            }
        });
        anonymousSignInLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLoading(true);
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        anonymousSignIn();
                    }
                });
            }
        });
        appVersionView.setText(Util.getAppVersionName(this));

        if (hasSignedIn() && Util.isConnected(this)) {
            if(hasSignedInWithFacebook()){
                facebookSignIn();
            } else {
                anonymousSignIn();
            }
        }
    }

    private void facebookSignIn(){
        if(Util.isConnected(this)) {
            setLoading(true);
            if(hasSignedInWithFacebook()){
                ParseFacebookUtils.logInInBackground(AccessToken.getCurrentAccessToken(), new LogInCallback() {
                    @Override
                    public void done(ParseUser user, ParseException e) {
                        if (user == null) {
                            setLoading(false);
                        } else {
                            AnalyticsUtil.signInEvent("Facebook");
                            AsyncTask.execute(new Runnable() {
                                @Override
                                public void run() {
                                    afterSignIn();
                                }
                            });
                        }
                    }
                });
            } else {
                ParseFacebookUtils.logInWithReadPermissionsInBackground(this, Constant.FACEBOOK_PERMISSIONS, new LogInCallback() {
                    @Override
                    public void done(ParseUser user, ParseException err) {
                        if (user == null) {
                            setLoading(false);
                        } else {
                            if (user.isNew()) {
                                AnalyticsUtil.signUpEvent("Facebook");
                            } else {
                                AnalyticsUtil.signInEvent("Facebook");
                            }
                            AsyncTask.execute(new Runnable() {
                                @Override
                                public void run() {
                                    SocialUtil.updateFacebookProfile();
                                    SocialUtil.updateFabricProfile();
                                    afterSignIn();
                                }
                            });
                        }
                    }
                });
            }
        }
    }

    private void anonymousSignIn(){
        if(Util.isConnected(this)) {
            setLoading(true);
            AnalyticsUtil.signInEvent("Anonymous");
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    afterSignIn();
                }
            });
        }
    }

    private void afterSignIn(){
        // TODO load and CACHE races, words and translations
        Util.getSettings(this).edit().putBoolean(Constant.SETTINGS_HAS_SIGNED_IN, true).commit();
        startActivity(new Intent(SplashActivity.this, MainActivity.class));
        finish();
    }

    private boolean hasSignedIn(){
        boolean hasSignedIn = Util.getSettings(this).getBoolean(Constant.SETTINGS_HAS_SIGNED_IN, false);
        return hasSignedIn;
    }

    private boolean hasSignedInWithFacebook(){
        ParseUser parseUser = ParseUser.getCurrentUser();
        return parseUser != null && parseUser.isAuthenticated() && parseUser.isLinked("facebook");
    }

    private void setLoading(final boolean load){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
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
        });
    }
}