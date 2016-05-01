package cafe.adriel.nmsalphabet.util;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.Spanned;

import cafe.adriel.nmsalphabet.Constant;
import cafe.adriel.nmsalphabet.R;
import cafe.adriel.nmsalphabet.ui.MainActivity;
import cafe.adriel.nmsalphabet.ui.SettingsActivity;
import cafe.adriel.nmsalphabet.ui.TranslationEditorActivity;

public class ThemeUtil {
    public static final String THEME_1 = "theme1";
    public static final String THEME_2 = "theme2";
    public static final String THEME_3 = "theme3";
    public static final String THEME_4 = "theme4";
    public static final String THEME_5 = "theme5";

    private static String currentTheme = THEME_1;

    public static String getCurrentTheme(Context context){
        try {
            currentTheme = PreferenceManager.getDefaultSharedPreferences(context)
                    .getString(Constant.SETTINGS_ACCOUNT_THEME, ThemeUtil.THEME_1);
            return currentTheme;
        } catch (Exception e) {
            e.printStackTrace();
            currentTheme = THEME_1;
            return currentTheme;
        }
    }

    public static void setCustomTheme(Context context){
        switch (getCurrentTheme(context)){
            case THEME_2:
                if(context instanceof MainActivity){
                    context.setTheme(R.style.AppTheme2_NoActionBar);
                } else if(context instanceof TranslationEditorActivity || context instanceof SettingsActivity){
                    context.setTheme(R.style.AppTheme2);
                }
                break;
            case THEME_3:
                if(context instanceof MainActivity){
                    context.setTheme(R.style.AppTheme3_NoActionBar);
                } else if(context instanceof TranslationEditorActivity || context instanceof SettingsActivity){
                    context.setTheme(R.style.AppTheme3);
                }
                break;
            case THEME_4:
                if(context instanceof MainActivity){
                    context.setTheme(R.style.AppTheme4_NoActionBar);
                } else if(context instanceof TranslationEditorActivity || context instanceof SettingsActivity){
                    context.setTheme(R.style.AppTheme4);
                }
                break;
            case THEME_5:
                if(context instanceof MainActivity){
                    context.setTheme(R.style.AppTheme5_NoActionBar);
                } else if(context instanceof TranslationEditorActivity || context instanceof SettingsActivity){
                    context.setTheme(R.style.AppTheme5);
                }
                break;
            default:
                if(context instanceof MainActivity){
                    context.setTheme(R.style.AppTheme1_NoActionBar);
                } else if(context instanceof TranslationEditorActivity || context instanceof SettingsActivity){
                    context.setTheme(R.style.AppTheme1);
                }
        }
    }

    public static int getPrimaryColor(Context context){
        return getPrimaryColor(context, currentTheme);
    }

    public static int getPrimaryColor(Context context, String theme){
        switch (theme){
            case THEME_2:
                return context.getResources().getColor(R.color.colorPrimaryTheme2);
            case THEME_3:
                return context.getResources().getColor(R.color.colorPrimaryTheme3);
            case THEME_4:
                return context.getResources().getColor(R.color.colorPrimaryTheme4);
            case THEME_5:
                return context.getResources().getColor(R.color.colorPrimaryTheme5);
            default:
                return context.getResources().getColor(R.color.colorPrimaryTheme1);
        }
    }

    public static int getPrimaryDarkColor(Context context){
        return getPrimaryDarkColor(context, currentTheme);
    }

    public static int getPrimaryDarkColor(Context context, String theme){
        switch (theme){
            case THEME_2:
                return context.getResources().getColor(R.color.colorPrimaryDarkTheme2);
            case THEME_3:
                return context.getResources().getColor(R.color.colorPrimaryDarkTheme3);
            case THEME_4:
                return context.getResources().getColor(R.color.colorPrimaryDarkTheme4);
            case THEME_5:
                return context.getResources().getColor(R.color.colorPrimaryDarkTheme5);
            default:
                return context.getResources().getColor(R.color.colorPrimaryDarkTheme1);
        }
    }

    public static int getAccentColor(Context context){
        return getAccentColor(context, currentTheme);
    }

    public static int getAccentColor(Context context, String theme){
        switch (theme){
            case THEME_2:
                return context.getResources().getColor(R.color.colorAccentTheme2);
            case THEME_3:
                return context.getResources().getColor(R.color.colorAccentTheme3);
            case THEME_4:
                return context.getResources().getColor(R.color.colorAccentTheme4);
            case THEME_5:
                return context.getResources().getColor(R.color.colorAccentTheme5);
            default:
                return context.getResources().getColor(R.color.colorAccentTheme1);
        }
    }

    public static Spanned getThemeCircles(Context context, String theme){
        String primaryDarkColor = Util.toHexColor(getPrimaryDarkColor(context, theme));
        String primaryColor = Util.toHexColor(getPrimaryColor(context, theme));
        String accentColor = Util.toHexColor(getAccentColor(context, theme));
        String html = String.format(
                "<font color='%s'>●</font>" +
                "<font color='%s'>●</font>" +
                "<font color='%s'>●</font>",
                primaryDarkColor, primaryColor, accentColor);
        return Html.fromHtml(html);
    }

    public static Drawable getHeaderControlDrawable(Context context){
        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.RECTANGLE);
        shape.setColor(ThemeUtil.getPrimaryDarkColor(context));
        shape.setCornerRadius(250);
        return shape;
    }

    public static Drawable getWordRaceTitleDrawable(Context context){
        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.RECTANGLE);
        shape.setColor(ThemeUtil.getAccentColor(context));
        shape.setCornerRadii(new float [] {0, 0, 250, 250, 250, 250, 0, 0});
        return shape;
    }
}