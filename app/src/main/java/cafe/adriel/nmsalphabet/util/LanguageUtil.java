package cafe.adriel.nmsalphabet.util;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;

import java.util.Locale;

import cafe.adriel.nmsalphabet.Constant;
import cafe.adriel.nmsalphabet.R;

public class LanguageUtil {

    public static final String LANGUAGE_PT = "pt";
    public static final String LANGUAGE_EN = "en";
    public static final String LANGUAGE_DE = "de";

    public static String getCurrentLanguage(Context context){
        String language = Util.getSettings(context).getString(Constant.SETTINGS_ACCOUNT_LANGUAGE, "");
        if(Util.isEmpty(language)) {
            Configuration conf = context.getResources().getConfiguration();
            switch (conf.locale.getLanguage()){
                case LANGUAGE_PT:
                    language = LANGUAGE_PT;
                    break;
                case LANGUAGE_DE:
                    language = LANGUAGE_DE;
                    break;
                default:
                    language = LANGUAGE_EN;
            }
            Util.getSettings(context)
                    .edit()
                    .putString(Constant.SETTINGS_ACCOUNT_LANGUAGE, language)
                    .commit();
        }
        return language;
    }

    public static void updateLanguage(Context context){
        String language = getCurrentLanguage(context);
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