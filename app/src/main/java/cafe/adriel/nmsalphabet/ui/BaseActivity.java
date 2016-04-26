package cafe.adriel.nmsalphabet.ui;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.mikepenz.iconics.context.IconicsLayoutInflater;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import cafe.adriel.nmsalphabet.R;
import cafe.adriel.nmsalphabet.Util;

public abstract class BaseActivity extends AppCompatActivity {
    protected SystemBarTintManager tintManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LayoutInflaterCompat.setFactory(getLayoutInflater(), new IconicsLayoutInflater(getDelegate()));
        super.onCreate(savedInstanceState);
        tintBars();
        Util.updateLanguage(this);
    }

    protected abstract void init();

    @TargetApi(19)
    protected void setTranslucentStatusAndNavigationBar() {
        Window win = getWindow();
        win.setFlags(
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        win.setFlags(
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
    }

    public void tintBars(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatusAndNavigationBar();
        }
        tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setNavigationBarTintEnabled(true);
        tintManager.setTintColor(getResources().getColor(R.color.colorPrimary));
    }
}