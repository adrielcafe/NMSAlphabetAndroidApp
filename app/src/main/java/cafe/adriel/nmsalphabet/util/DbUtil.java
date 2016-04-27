package cafe.adriel.nmsalphabet.util;

import com.parse.FindCallback;
import com.parse.ParseQuery;

import cafe.adriel.nmsalphabet.model.AlienRace;
import cafe.adriel.nmsalphabet.model.AlienWord;
import cafe.adriel.nmsalphabet.model.User;

public class DbUtil {
    public static int PAGE_SIZE = 10;

    public static void getRaces(FindCallback<AlienRace> callback){
        ParseQuery.getQuery(AlienRace.class)
                .findInBackground(callback);
    }

    public static void getWords(int page, FindCallback<AlienWord> callback){
        ParseQuery.getQuery(AlienWord.class)
                .findInBackground(callback);
    }

    public static void getWordsByRace(AlienRace race, int page, FindCallback<AlienWord> callback){
        ParseQuery.getQuery(AlienWord.class)
                .whereEqualTo("race", race)
                .setLimit(PAGE_SIZE)
                .setSkip(PAGE_SIZE * page)
                .findInBackground(callback);
    }

    public static void getWordsByUser(User user, int page, FindCallback<AlienWord> callback){
        ParseQuery.getQuery(AlienWord.class)
                .whereEqualTo("user", user)
                .setLimit(PAGE_SIZE)
                .setSkip(PAGE_SIZE * page)
                .findInBackground(callback);
    }

    public static void searchTranslation(String word, AlienRace race, int page, FindCallback<AlienWord> callback){
        ParseQuery.getQuery(AlienWord.class)
                .whereEqualTo("word", word)
                .whereEqualTo("race", race)
                .setLimit(PAGE_SIZE)
                .setSkip(PAGE_SIZE * page)
                .findInBackground(callback);
    }

}