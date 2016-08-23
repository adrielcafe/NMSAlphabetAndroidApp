package cafe.adriel.nmsalphabet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public static final Map<String, String> ALIEN_RACES = new HashMap<String, String>() {{
        put("atlaspath", "Atlas Path");
        put("gek", "Gek");
        put("korvax", "Korvax");
        put("vikeen", "Vi'keen");
    }};
    
    public static final List<Triple<String, String, Integer>> LANGUAGES = Arrays.asList(
        new Triple<>("en", "English", R.drawable.flag_en_small),
        new Triple<>("pt", "Português", R.drawable.flag_pt_small),
        new Triple<>("de", "Deutsch", R.drawable.flag_de_small),
        new Triple<>("it", "Italiano", R.drawable.flag_it_small),
        new Triple<>("fr", "Français", R.drawable.flag_fr_small),
        new Triple<>("es", "Español", R.drawable.flag_es_small),
        new Triple<>("nl", "Nederlandse", R.drawable.flag_nl_small),
        new Triple<>("ru", "Pусский", R.drawable.flag_ru_small),
        new Triple<>("ja", "日本語", R.drawable.flag_ja_small),
        new Triple<>("ko", "한국말", R.drawable.flag_ko_small)
    );

    public static final List<Triple<String, String, String>> TRANSLATORS = Arrays.asList(
        new Triple<>(LanguageUtil.LANGUAGE_EN, "Adriel Café", "@adrielcafe"),
        new Triple<>(LanguageUtil.LANGUAGE_EN, "Natascha Brell", ""),
        new Triple<>(LanguageUtil.LANGUAGE_PT, "Adriel Café", "@adrielcafe"),
        new Triple<>(LanguageUtil.LANGUAGE_DE, "Natascha Brell", "")
    );
}