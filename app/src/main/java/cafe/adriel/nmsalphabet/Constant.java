package cafe.adriel.nmsalphabet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cafe.adriel.nmsalphabet.util.LanguageUtil;
import cafe.adriel.nmsalphabet.util.Triple;

public class Constant {
    public static final String CONTACT_EMAIL    = "me@adriel.cafe";
    public static final String GOOGLE_PLAY_URL  = "https://play.google.com/store/apps/details?id=";
    public static final String MARKET_URI       = "market://details?id=";

    public static final String EXTRA_IMAGE_PATH = "imageUri";

    public static final String SETTINGS_GENERAL             = "general";
    public static final String SETTINGS_GENERAL_LANGUAGE    = "general_language";
    public static final String SETTINGS_GENERAL_THEME       = "general_theme";
    public static final String SETTINGS_GENERAL_UPGRADE_PRO = "general_upgrade_pro";
    public static final String SETTINGS_ABOUT_TRANSLATORS   = "about_translators";
    public static final String SETTINGS_ABOUT_FEEDBACK      = "about_feedback";
    public static final String SETTINGS_ABOUT_SHARE         = "about_share";
    public static final String SETTINGS_ABOUT_RATE          = "about_rate";
    public static final String SETTINGS_ABOUT_VERSION	    = "about_version";

    public static final String STATE_LOADING        = "loading";
    public static final String STATE_EMPTY          = "empty";
    public static final String STATE_NO_INTERNET    = "noInternet";

    public static final List<Triple<String, String, String>> TRANSLATORS = new ArrayList<>(Arrays.asList(
        new Triple<>(LanguageUtil.LANGUAGE_EN, "Adriel Café", "@adrielcafe"),
        new Triple<>(LanguageUtil.LANGUAGE_EN, "Natascha Brell", ""),
        new Triple<>(LanguageUtil.LANGUAGE_PT, "Adriel Café", "@adrielcafe"),
        new Triple<>(LanguageUtil.LANGUAGE_DE, "Natascha Brell", "")
    ));
}