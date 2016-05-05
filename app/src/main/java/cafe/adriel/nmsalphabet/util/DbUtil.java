package cafe.adriel.nmsalphabet.util;

import com.parse.FindCallback;
import com.parse.ParseQuery;

import java.util.List;

import cafe.adriel.nmsalphabet.model.AlienRace;
import cafe.adriel.nmsalphabet.model.AlienWord;
import cafe.adriel.nmsalphabet.model.AlienWordTranslation;
import cafe.adriel.nmsalphabet.model.User;

public class DbUtil {
    public static int PAGE_SIZE_WORD = 50;
    public static int PAGE_SIZE_TRANSLATION = 5;

    public static List<AlienRace> getRaces(){
        try {
            return ParseQuery.getQuery(AlienRace.class)
                    .addAscendingOrder("name")
                    .find();
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static void getWords(int page, FindCallback<AlienWord> callback){
        ParseQuery.getQuery(AlienWord.class)
                .addAscendingOrder("word")
                .addAscendingOrder("_updated_at")
                .setLimit(PAGE_SIZE_WORD)
                .setSkip(PAGE_SIZE_WORD * page)
                .findInBackground(callback);
    }

    public static void getWordsByRace(AlienRace race, int page, FindCallback<AlienWord> callback){
        ParseQuery.getQuery(AlienWord.class)
                .whereEqualTo("race", race)
                .addAscendingOrder("word")
                .addAscendingOrder("_updated_at")
                .setLimit(PAGE_SIZE_WORD)
                .setSkip(PAGE_SIZE_WORD * page)
                .findInBackground(callback);
    }

    public static void getWordsByUser(User user, int page, FindCallback<AlienWord> callback){
        ParseQuery.getQuery(AlienWord.class)
                .whereEqualTo("users", user)
                .addAscendingOrder("word")
                .addAscendingOrder("_updated_at")
                .setLimit(PAGE_SIZE_WORD)
                .setSkip(PAGE_SIZE_WORD * page)
                .findInBackground(callback);
    }

    public static AlienWord getWord(AlienRace race, String word){
        try {
            return ParseQuery.getQuery(AlienWord.class)
                    .whereEqualTo("race", race)
                    .whereEqualTo("word", word)
                    .getFirst();
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static void getTranslations(AlienRace race, AlienWord word, String language, FindCallback<AlienWordTranslation> callback){
        ParseQuery.getQuery(AlienWordTranslation.class)
                .whereEqualTo("race", race)
                .whereEqualTo("word", word)
                .whereEqualTo("language", language)
                .addDescendingOrder("usersCount")
                .addDescendingOrder("word")
                .setLimit(PAGE_SIZE_TRANSLATION)
                .findInBackground(callback);
    }

    public static void getUserTranslations(AlienRace race, AlienWord word, User user, FindCallback<AlienWordTranslation> callback){
        ParseQuery.getQuery(AlienWordTranslation.class)
                .whereEqualTo("race", race)
                .whereEqualTo("word", word)
                .whereEqualTo("users", user)
                .orderByAscending("word")
                .findInBackground(callback);
    }

    public static AlienWordTranslation getTranslation(String translation, String language, AlienWord word, AlienRace race){
        try {
            return ParseQuery.getQuery(AlienWordTranslation.class)
                    .whereEqualTo("translation", translation)
                    .whereEqualTo("race", race)
                    .whereEqualTo("word", word)
                    .whereEqualTo("language", language)
                    .getFirst();
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

}