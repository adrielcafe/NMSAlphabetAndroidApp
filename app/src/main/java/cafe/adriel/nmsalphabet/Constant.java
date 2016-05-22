package cafe.adriel.nmsalphabet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import cafe.adriel.nmsalphabet.util.LanguageUtil;
import cafe.adriel.nmsalphabet.util.Triple;

public class Constant {
    public static final String CONTACT_EMAIL    = "me@adriel.cafe";
    public static final String GOOGLE_PLAY_URL  = "https://play.google.com/store/apps/details?id=";
    public static final String MARKET_URI       = "market://details?id=";

    // TODO Don't use Parse Server until get ready for production
//    public static final String PARSE_SERVER_URL = "https://nmsalphabet.herokuapp.com/api/";

    public static final List<String> FACEBOOK_PERMISSIONS   = Collections.singletonList("public_profile");
    public static final String FACEBOOK_USER_IMAGE_URL      = "https://graph.facebook.com/%s/picture?type=small";

    public static final String GENDER_MALE      = "male";
    public static final String GENDER_FEMALE    = "female";

    public static final String EXTRA_IMAGE_PATH = "imageUri";
    public static final String EXTRA_TYPE       = "type";

    public static final String SETTINGS_ACCOUNT             = "account";
    public static final String SETTINGS_ACCOUNT_STATUS      = "account_status";
    public static final String SETTINGS_ACCOUNT_LANGUAGE    = "account_language";
    public static final String SETTINGS_ACCOUNT_THEME       = "account_theme";
    public static final String SETTINGS_ACCOUNT_UPGRADE_PRO = "account_upgrade_pro";
    public static final String SETTINGS_ABOUT_NEW_RACE      = "about_new_race";
    public static final String SETTINGS_ABOUT_TRANSLATORS   = "about_translators";
    public static final String SETTINGS_ABOUT_FEEDBACK      = "about_feedback";
    public static final String SETTINGS_ABOUT_SHARE         = "about_share";
    public static final String SETTINGS_ABOUT_RATE          = "about_rate";
    public static final String SETTINGS_ABOUT_VERSION	    = "about_version";
    public static final String SETTINGS_HAS_SIGNED_IN	    = "hasSignedIn";
    public static final String SETTINGS_FACEBOOK_USER_ID    = "facebookUserId";

    public static final String STATE_LOADING           = "loading";
    public static final String STATE_EMPTY             = "empty";
    public static final String STATE_NO_INTERNET       = "noInternet";
    public static final String STATE_REQUIRE_SIGN_IN   = "requireSignIn";

    public static final List<Triple<String, String, String>> TRANSLATORS = new ArrayList<>(Arrays.asList(
            new Triple<>(LanguageUtil.LANGUAGE_EN, "Adriel Café", "@adrielcafe"),
            new Triple<>(LanguageUtil.LANGUAGE_EN, "Natascha Brell", ""),
            new Triple<>(LanguageUtil.LANGUAGE_PT, "Adriel Café", "@adrielcafe"),
            new Triple<>(LanguageUtil.LANGUAGE_DE, "Natascha Brell", "")
    ));
}