package cafe.adriel.nmsalphabet;

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

    public static final int REQUEST_PRO_UPGRADE = 0;

    public static final String SKU_PRO_UPGRADE = "pro_upgrade";
    public static final String BILLING_PAYLOAD = "d0872fa8-a955-4137-9aae-f1c5e8af9941";

    public static final String EXTRA_IMAGE_PATH = "imageUri";

    public static final String SETTINGS_GENERAL_LANGUAGE        = "general_language";
    public static final String SETTINGS_GENERAL_THEME           = "general_theme";
    public static final String SETTINGS_STATISTICS_ATLAS_PATH   = "statistics_atlas_path";
    public static final String SETTINGS_STATISTICS_GEK          = "statistics_gek";
    public static final String SETTINGS_STATISTICS_KORVAX       = "statistics_korvax";
    public static final String SETTINGS_STATISTICS_VYKEEN       = "statistics_vykeen";
    public static final String SETTINGS_ABOUT_TRANSLATORS       = "about_translators";
    public static final String SETTINGS_ABOUT_FEEDBACK          = "about_feedback";
    public static final String SETTINGS_ABOUT_SHARE             = "about_share";
    public static final String SETTINGS_ABOUT_RATE              = "about_rate";
    public static final String SETTINGS_ABOUT_VERSION	        = "about_version";
    public static final String SETTINGS_IS_PRO                  = "is_pro";

    public static final Map<String, String> ALIEN_RACES = new HashMap<String, String>() {{
        put("atlaspath", "Atlas Path");
        put("gek", "Gek");
        put("korvax", "Korvax");
        put("vykeen", "Vy'keen");
    }};
    
    public static final Map<String, String> TRANSLATION_LANGUAGES = new HashMap<String, String>() {{
        put("en", "English");
        put("pt", "Português");
        put("de", "Deutsch");
        put("it", "Italiano");
        put("fr", "Français");
        put("es", "Español");
        put("nl", "Nederlandse");
        put("ru", "Pусский");
        put("ja", "日本語");
        put("ko", "한국말");
    }};

    public static final List<Triple<String, String, String>> TRANSLATORS = Arrays.asList(
        new Triple<>(LanguageUtil.LANGUAGE_EN, "Adriel Café", "@adrielcafe"),
        new Triple<>(LanguageUtil.LANGUAGE_EN, "Natascha Brell", ""),
        new Triple<>(LanguageUtil.LANGUAGE_PT, "Adriel Café", "@adrielcafe"),
        new Triple<>(LanguageUtil.LANGUAGE_DE, "Natascha Brell", "")
    );
}