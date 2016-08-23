package cafe.adriel.nmsalphabet.util;

import android.util.Log;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cafe.adriel.nmsalphabet.Constant;
import cafe.adriel.nmsalphabet.R;
import okhttp3.Request;
import okhttp3.Response;

public class TranslationUtil {
    private static final String BASE_URL = "https://raw.githubusercontent.com/adrielcafe/NMSAlphabetAndroidApp/dev/words";
    private static final String RACE_WORDS_URL = BASE_URL + "/%s.txt";
    private static final String TRANSLATIONS_URL = BASE_URL + "/translations/%s.txt";

    public static Map<String, List<String>> getAllRaceWords(){
        Map<String, List<String>> words = new HashMap<>();
        for(String race : Constant.ALIEN_RACES.keySet()){
            words.put(Constant.ALIEN_RACES.get(race), getRaceWords(race));
        }
        return words;
    }

    private static List<String> getRaceWords(String race){
        try {
            Request request = new Request.Builder()
                    .url(String.format(RACE_WORDS_URL, race))
                    .header("Content-Type", "text/plain")
                    .build();
            Log.e("RACE URL", String.format(RACE_WORDS_URL, race));
            Response response = Util.getHttpClient().newCall(request).execute();
            Log.e("RACE WORDS", response.code() +"," +response.body().string());
            if (response.isSuccessful()) {
                return null;
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Map<String, List<String>> getAllTranslations(){
        Map<String, List<String>> translations = new HashMap<>();
        for(Triple<String, String, Integer> language : Constant.LANGUAGES){
            translations.put(language.getB(), getTranslations(language.getA()));
        }
        return translations;
    }

    private static List<String> getTranslations(String language){
        try {
            Request request = new Request.Builder()
                    .url(String.format(TRANSLATIONS_URL, language.toUpperCase()))
                    .header("Content-Type", "text/plain")
                    .build();
            Log.e("TRANSLATIONS URL", String.format(TRANSLATIONS_URL, language.toUpperCase()));
            Response response = Util.getHttpClient().newCall(request).execute();
            Log.e("TRANSLATIONS", response.code() +"," +response.body().string());
            if (response.isSuccessful()) {
                return null;
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}