package cafe.adriel.nmsalphabet.util;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;

import java.util.Locale;

import cafe.adriel.nmsalphabet.Constant;
import cafe.adriel.nmsalphabet.R;

public class LanguageUtil {

    public static final String LANGUAGE_PT = "pt";
    public static final String LANGUAGE_EN = "en";
    public static final String LANGUAGE_DE = "de";

    public static String getCurrentLanguage(Context context){
        String language = PreferenceManager.getDefaultSharedPreferences(context).getString(Constant.SETTINGS_ACCOUNT_LANGUAGE, LANGUAGE_EN);
        return language;
    }

    public static void updateLanguage(Context context){
        String language = PreferenceManager.getDefaultSharedPreferences(context).getString(Constant.SETTINGS_ACCOUNT_LANGUAGE, "en");
        Resources res = context.getResources();
        Configuration conf = res.getConfiguration();
        conf.locale = new Locale(language.toLowerCase());
        res.updateConfiguration(conf, res.getDisplayMetrics());
    }

    public static Drawable getLanguageFlagDrawable(Context context, String language){
        switch (language){
            case LANGUAGE_PT:
                return context.getResources().getDrawable(R.drawable.flag_brazil);
            case LANGUAGE_DE:
                return context.getResources().getDrawable(R.drawable.flag_germany);
            default:
                return context.getResources().getDrawable(R.drawable.flag_uk);
        }
    }
}