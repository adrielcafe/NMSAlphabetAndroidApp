package cafe.adriel.nmsalphabet.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cafe.adriel.nmsalphabet.Constant;
import io.paperdb.Paper;

public class DbUtil {
    private static final String BOOK_NAME = "nmsalphabet";
    private static final String BOOK_KEY_WORDS = "alienWords";
    private static final String BOOK_KEY_TRANSLATIONS = "alienWordsTranslations";

    private static List<String> alienRaces;
    private static Map<String, Integer> alienWords;
    private static Map<String, List<String>> alienWordsTranslations;

    public static void cacheData(){
        Paper.book().destroy();
        Paper.book(BOOK_NAME)
                .write(BOOK_KEY_WORDS, TranslationUtil.getAllRaceWords());
        Paper.book(BOOK_NAME)
                .write(BOOK_KEY_TRANSLATIONS, TranslationUtil.getAllTranslations());
    }

    private static void loadCachedData(){
        if(alienRaces == null) {
            alienRaces = new ArrayList<>();
            for (String race : Constant.ALIEN_RACES.keySet()){
                alienRaces.add(Constant.ALIEN_RACES.get(race));
            }
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

    public static Map<String, String> translateWords(List<String> words, String race, String language){
        List<String> wordsTranslations = alienWordsTranslations.get(language);
        Map<String, String> wordsTranslated = new HashMap<>();
        for(String word : words){
            if(alienWords.keySet().contains(word)){
                wordsTranslated.put(word, wordsTranslations.get(alienWords.get(word)));
            }
        }
        return wordsTranslated;
    }
}