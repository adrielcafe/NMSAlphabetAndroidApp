package cafe.adriel.nmsalphabet.util;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;

import com.jaredrummler.materialspinner.MaterialSpinner;

import java.util.Locale;

import cafe.adriel.nmsalphabet.Constant;
import cafe.adriel.nmsalphabet.R;

public class LanguageUtil {

    public static final String LANGUAGE_PT = "pt";
    public static final String LANGUAGE_EN = "en";
    public static final String LANGUAGE_DE = "de";

    public static String getCurrentLanguageCode(Context context){
        String languageCode = Util.getSettings(context).getString(Constant.SETTINGS_ACCOUNT_LANGUAGE, null);
        if(Util.isEmpty(languageCode)) {
            Configuration conf = context.getResources().getConfiguration();
            switch (conf.locale.getLanguage()){
                case LANGUAGE_PT:
                    languageCode = LANGUAGE_PT;
                    break;
                case LANGUAGE_DE:
                    languageCode = LANGUAGE_DE;
                    break;
                default:
                    languageCode = LANGUAGE_EN;
            }
            Util.getSettings(context)
                    .edit()
                    .putString(Constant.SETTINGS_ACCOUNT_LANGUAGE, languageCode)
                    .commit();
        }
        return languageCode;
    }

    public static String languageCodeToLanguage(Context context, String languageCode){
        switch (languageCode){
            case LANGUAGE_EN:
                return context.getString(R.string.english);
            case LANGUAGE_PT:
                return context.getString(R.string.portuguese);
            case LANGUAGE_DE:
                return context.getString(R.string.german);
            default:
                return null;
        }
    }

    public static void updateLanguage(Context context){
        String language = getCurrentLanguageCode(context);
        Resources res = context.getResources();
        Configuration conf = res.getConfiguration();
        conf.locale = new Locale(language.toLowerCase());
        res.updateConfiguration(conf, res.getDisplayMetrics());
    }

    public static String updateLanguageFlag(Context context, MaterialSpinner languageView, String language){
        String languageCode = null;
        String english = context.getString(R.string.english);
        String portuguese = context.getString(R.string.portuguese);
        String german = context.getString(R.string.german);
        int flagResId = -1;
        try {
            if(language.equals(english) || language.equals(LANGUAGE_EN)){
                languageCode = LANGUAGE_EN;
                flagResId = R.drawable.flag_uk_small;
            } else if(language.equals(portuguese) || language.equals(LANGUAGE_PT)){
                languageCode = LANGUAGE_PT;
                flagResId = R.drawable.flag_brazil_small;
            } else if(language.equals(german) || language.equals(LANGUAGE_DE)){
                languageCode = LANGUAGE_DE;
                flagResId = R.drawable.flag_germany_small;
            }
            languageView.setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(flagResId), null, languageView.getCompoundDrawables()[2], null);
        } catch (Exception e){ }
        return languageCode;
    }

    public static Drawable getLanguageFlagDrawable(Context context, String language){
        switch (language){
            case LANGUAGE_PT:
                return context.getResources().getDrawable(R.drawable.flag_brazil_small);
            case LANGUAGE_DE:
                return context.getResources().getDrawable(R.drawable.flag_germany_small);
            default:
                return context.getResources().getDrawable(R.drawable.flag_uk_small);
        }
    }

}