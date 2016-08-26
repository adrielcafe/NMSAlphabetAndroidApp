package cafe.adriel.nmsalphabet.util;

import android.content.Context;

import com.jaredrummler.materialspinner.MaterialSpinner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cafe.adriel.nmsalphabet.Constant;
import cafe.adriel.nmsalphabet.R;
import okhttp3.Request;
import okhttp3.Response;

public class TranslationUtil {
    private static final String BASE_URL = "https://raw.githubusercontent.com/adrielcafe/NMSAlphabetAndroidApp/master/alienwords";
    private static final String RACE_WORDS_URL = BASE_URL + "/%s.txt";
    private static final String TRANSLATIONS_URL = BASE_URL + "/translations/%s.txt";

    public static List<String> getLanguages(){
        List<String> languages = new ArrayList<>(Constant.TRANSLATION_LANGUAGES.values());
        Collections.sort(languages);
        return languages;
    }

    public static String updateLanguageFlag(Context context, MaterialSpinner languageView, String language){
        String languageCode = null;
        int flagResId = -1;
        try {
            if(language.equals("en") || language.equals("English")){
                languageCode = "en";
                flagResId = R.drawable.flag_en_small;
            } else if(language.equals("pt") || language.equals("Português")){
                languageCode = "pt";
                flagResId = R.drawable.flag_pt_small;
            } else if(language.equals("de") || language.equals("Deutsch")){
                languageCode = "de";
                flagResId = R.drawable.flag_de_small;
            } else if(language.equals("it") || language.equals("Italiano")){
                languageCode = "it";
                flagResId = R.drawable.flag_it_small;
            } else if(language.equals("fr") || language.equals("Français")){
                languageCode = "fr";
                flagResId = R.drawable.flag_fr_small;
            } else if(language.equals("es") || language.equals("Español")){
                languageCode = "es";
                flagResId = R.drawable.flag_es_small;
            } else if(language.equals("nl") || language.equals("Nederlandse")){
                languageCode = "nl";
                flagResId = R.drawable.flag_nl_small;
            } else if(language.equals("ru") || language.equals("Pусский")){
                languageCode = "ru";
                flagResId = R.drawable.flag_ru_small;
            } else if(language.equals("ja") || language.equals("日本語")){
                languageCode = "ja";
                flagResId = R.drawable.flag_ja_small;
            } else if(language.equals("ko") || language.equals("한국말")){
                languageCode = "ko";
                flagResId = R.drawable.flag_ko_small;
            }
            languageView.setCompoundDrawablesWithIntrinsicBounds(
                    context.getResources().getDrawable(flagResId), null, languageView.getCompoundDrawables()[2], null);
        } catch (Exception e){ }
        return languageCode;
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
            try {
                String[] wordWithTranslationIndex = word.split(" ");
                int translationIndex = Integer.parseInt(wordWithTranslationIndex[0]) - 1;
                wordsTriple.add(new Triple<>(race, wordWithTranslationIndex[1], translationIndex));
            } catch (Exception e){ }
        }
        return wordsTriple;
    }

    public static Map<String, List<String>> getAllTranslations(){
        Map<String, List<String>> translations = new HashMap<>();
        for(String language : Constant.TRANSLATION_LANGUAGES.keySet()){
            translations.put(language, getTranslations(language));
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