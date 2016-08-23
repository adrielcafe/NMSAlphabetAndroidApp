package cafe.adriel.nmsalphabet.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cafe.adriel.nmsalphabet.Constant;
import okhttp3.Request;
import okhttp3.Response;

public class TranslationUtil {
    private static final String BASE_URL = "https://raw.githubusercontent.com/adrielcafe/NMSAlphabetAndroidApp/dev/alienwords";
    private static final String RACE_WORDS_URL = BASE_URL + "/%s.txt";
    private static final String TRANSLATIONS_URL = BASE_URL + "/translations/%s.txt";

    public static List<String> getSupportedLanguages(){
        List<String> languages = new ArrayList<>();
        // TODO
        return null;
    }

    public static Map<String, List<Triple<String, String, Integer>>> getAllWords(){
        Map<String, List<Triple<String, String, Integer>>> words = new HashMap<>();
        for(String race : Constant.ALIEN_RACES.keySet()){
            words.put(Constant.ALIEN_RACES.get(race), getWords(race));
        }
        return words;
    }

    private static List<Triple<String, String, Integer>> getWords(String race){
        List<Triple<String, String, Integer>> wordsTriple = new ArrayList<>();
        String url = String.format(RACE_WORDS_URL, race);
        List<String> words = getListFromUrl(url);
        for(String word : words){
            String[] wordWithTranslationIndex = word.split(" ");
            int translationIndex = Integer.parseInt(wordWithTranslationIndex[0]) - 1;
            wordsTriple.add(new Triple<>(race, wordWithTranslationIndex[1], translationIndex));
        }
        return wordsTriple;
    }

    public static Map<String, List<String>> getAllTranslations(){
        Map<String, List<String>> translations = new HashMap<>();
        for(Triple<String, String, Integer> language : Constant.LANGUAGES){
            translations.put(language.getA(), getTranslations(language.getA()));
        }
        return translations;
    }

    private static List<String> getTranslations(String language){
        String url = String.format(TRANSLATIONS_URL, language);
        return getListFromUrl(url);
    }

    private static List<String> getListFromUrl(String url){
        try {
            Request request = new Request.Builder()
                    .url(url)
                    .header("Content-Type", "text/plain")
                    .build();
            Response response = Util.getHttpClient().newCall(request).execute();
            if (response.isSuccessful()) {
                return Arrays.asList(response.body().string().split("\n"));
            } else {
                return new ArrayList<>();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

}