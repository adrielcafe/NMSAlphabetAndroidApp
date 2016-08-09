package cafe.adriel.nmsalphabet.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.mikepenz.iconics.context.IconicsLayoutInflater;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.tsengvn.typekit.TypekitContextWrapper;

import cafe.adriel.nmsalphabet.R;
import cafe.adriel.nmsalphabet.util.LanguageUtil;
import cafe.adriel.nmsalphabet.util.ThemeUtil;
import cafe.adriel.nmsalphabet.util.Util;

public abstract class BaseActivity extends AppCompatActivity {
    protected SystemBarTintManager tintManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LayoutInflaterCompat.setFactory(getLayoutInflater(), new IconicsLayoutInflater(getDelegate()));
        super.onCreate(savedInstanceState);
        ThemeUtil.setCustomTheme(this);
        LanguageUtil.updateLanguage(this);
        tintBars();
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

    private void tintBars(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatusAndNavigationBar();
        }
        tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setNavigationBarTintEnabled(true);
        tintManager.setTintColor(ThemeUtil.getPrimaryColor(this));
    }

    protected void adjustMarginAndPadding(){
        FrameLayout contentLayout = (FrameLayout) findViewById(R.id.content_layout);
        if(contentLayout != null){
            if(Build.VERSION.SDK_INT >= 23) {
                contentLayout.setPaddingRelative(0, contentLayout.getPaddingTop(), 0, Util.getNavigationBarHeight(this));
            } else if(Build.VERSION.SDK_INT == 18){
                contentLayout.setPaddingRelative(0, 0, 0, 0);
                if(this instanceof TranslationEditorActivity){
                    LinearLayout childView = (LinearLayout) findViewById(R.id.form_layout);
                    FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) childView.getLayoutParams();
                    params.topMargin = 0;
                    childView.setLayoutParams(params);
                } else if(this instanceof SettingsActivity){
                    LinearLayout childView = (LinearLayout) contentLayout.getChildAt(0);
                    FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) childView.getLayoutParams();
                    params.topMargin = params.bottomMargin;
                    childView.setLayoutParams(params);
                }
            }
        }
    }
}