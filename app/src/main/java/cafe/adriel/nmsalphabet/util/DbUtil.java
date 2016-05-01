package cafe.adriel.nmsalphabet.util;

import com.parse.FindCallback;
import com.parse.ParseQuery;

import java.util.List;

import cafe.adriel.nmsalphabet.model.AlienRace;
import cafe.adriel.nmsalphabet.model.AlienWord;
import cafe.adriel.nmsalphabet.model.AlienWordTranslation;
import cafe.adriel.nmsalphabet.model.User;

public class DbUtil {
    public static int PAGE_SIZE = 10;

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
                .findInBackground(callback);
    }

    public static void getWordsByRace(AlienRace race, int page, FindCallback<AlienWord> callback){
        ParseQuery.getQuery(AlienWord.class)
                .whereEqualTo("race", race)
                .addAscendingOrder("word")
                .addAscendingOrder("_updated_at")
                .setLimit(PAGE_SIZE)
                .setSkip(PAGE_SIZE * page)
                .findInBackground(callback);
    }

    public static void getWordsByUser(User user, int page, FindCallback<AlienWord> callback){
        ParseQuery.getQuery(AlienWord.class)
                .whereEqualTo("users", user)
                .addAscendingOrder("word")
                .addAscendingOrder("_updated_at")
                .setLimit(PAGE_SIZE)
                .setSkip(PAGE_SIZE * page)
                .findInBackground(callback);
    }

    public static AlienWord getWord(AlienRace race, String word){
        try {
            return ParseQuery.getQuery(AlienWord.class)
                    .whereEqualTo("race", race)
                    .getFirst();
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static void getTranslations(AlienRace race, AlienWord word, String language, int page, FindCallback<AlienWord> callback){
        ParseQuery.getQuery(AlienWord.class)
                .whereEqualTo("race", race)
                .whereEqualTo("word", word)
                .whereEqualTo("language", language)
                .orderByDescending("_updated_at")
                .setLimit(PAGE_SIZE)
                .setSkip(PAGE_SIZE * page)
                .findInBackground(callback);
    }

    public static AlienWordTranslation getTranslation(AlienRace race, AlienWord word, String language){
        try {
            return ParseQuery.getQuery(AlienWordTranslation.class)
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