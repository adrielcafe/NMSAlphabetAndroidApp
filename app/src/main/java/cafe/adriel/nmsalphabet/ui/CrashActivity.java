package cafe.adriel.nmsalphabet.ui;

import android.content.Intent;
import android.os.Bundle;

import butterknife.ButterKnife;
import butterknife.OnClick;
import cafe.adriel.nmsalphabet.R;
import cat.ereza.customactivityoncrash.CustomActivityOnCrash;

public class CrashActivity extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crash);
        ButterKnife.bind(this);
        tintManager.setTintColor(getResources().getColor(R.color.colorAccent));
        init();
    }

    @OnClick(R.id.restart_app)
    public void restartApp(){
        Intent intent = new Intent(this, SplashActivity.class);
        CustomActivityOnCrash.restartApplicationWithIntent(this, intent, CustomActivityOnCrash.getEventListenerFromIntent(getIntent()));
    }

    @Override
    protected void init() {

    }

}