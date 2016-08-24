package cafe.adriel.nmsalphabet.util;

import android.content.Context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cafe.adriel.nmsalphabet.Constant;
import cafe.adriel.nmsalphabet.R;
import io.paperdb.Paper;

public class CacheUtil {
    private static final String BOOK_NAME = "nmsalphabet";
    private static final String BOOK_KEY_WORDS = "alienWords";
    private static final String BOOK_KEY_TRANSLATIONS = "alienWordsTranslations";

    private static List<String> alienRaces;
    private static Map<String, List<Triple<String, String, Integer>>> alienWords;
    private static Map<String, List<String>> alienWordsTranslations;

    public static void cacheData(){
        Paper.book().destroy();
        Paper.book(BOOK_NAME)
                .write(BOOK_KEY_WORDS, TranslationUtil.getAllWords());
        if(!Paper.book(BOOK_NAME).exist(BOOK_KEY_TRANSLATIONS)) {
            Paper.book(BOOK_NAME)
                    .write(BOOK_KEY_TRANSLATIONS, TranslationUtil.getAllTranslations());
        }
    }

    public static boolean hasCachedData(){
        return Paper.book(BOOK_NAME).exist(BOOK_KEY_WORDS) && Paper.book(BOOK_NAME).exist(BOOK_KEY_TRANSLATIONS);
    }

    private static void loadCachedData(){
        if(alienRaces == null) {
            alienRaces = new ArrayList<>();
            for (String race : Constant.ALIEN_RACES.keySet()){
                alienRaces.add(Constant.ALIEN_RACES.get(race));
            }
            Collections.sort(alienRaces);
        }
        if(alienWords == null){
            alienWords = Paper.book(BOOK_NAME).read(BOOK_KEY_WORDS);
        }
        if(alienWordsTranslations == null){
            alienWordsTranslations = Paper.book(BOOK_NAME).read(BOOK_KEY_TRANSLATIONS);
        }
    }

    public static List<String> getRaces(){
        loadCachedData();
        return alienRaces;
    }

    public static String getRaceByPosition(int position){
        loadCachedData();
        if(alienRaces.size() < position){
            return alienRaces.get(position);
        } else {
            return null;
        }
    }

    public static Map<String, String> translateWords(List<String> queryWords, String race, String languageCode){
        List<Triple<String, String, Integer>> words = alienWords.get(race);
        List<String> wordsTranslations = alienWordsTranslations.get(languageCode);
        Map<String, String> wordsTranslated = new HashMap<>();
        if(words != null) {
            for (String word : queryWords) {
                for (Triple<String, String, Integer> w : words) {
                    if (w.getB().equalsIgnoreCase(word)) {
                        wordsTranslated.put(word, wordsTranslations.get(w.getC()));
                    }
                }
            }
        }
        return wordsTranslated;
    }

    public static String countWords(Context context, String race){
        return alienWords.get(race).size() + " " + context.getString(R.string.known_words);
    }
}