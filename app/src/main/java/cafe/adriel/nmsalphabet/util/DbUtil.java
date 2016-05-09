package cafe.adriel.nmsalphabet.util;

import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

import cafe.adriel.nmsalphabet.model.AlienRace;
import cafe.adriel.nmsalphabet.model.AlienWord;
import cafe.adriel.nmsalphabet.model.AlienWordTranslation;
import cafe.adriel.nmsalphabet.model.User;

public class DbUtil {
    public static int PAGE_SIZE_RACE = 100;
    public static int PAGE_SIZE_WORD = 50;
    public static int PAGE_SIZE_TRANSLATION = 5;

    private static List<AlienRace> races;

    private static void loadCachedRaces(){
        if(races == null){
            try {
                races = ParseQuery.getQuery(AlienRace.class)
                        .addAscendingOrder("name")
                        .fromLocalDatastore()
                        .find();
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public static void cacheRaces(){
        try {
            races = ParseQuery.getQuery(AlienRace.class)
                    .addAscendingOrder("name")
                    .setLimit(PAGE_SIZE_RACE)
                    .find();
            AlienRace.unpinAllInBackground(new DeleteCallback() {
                @Override
                public void done(ParseException e) {
                    AlienRace.pinAllInBackground(races);
                }
            });
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static AlienRace getRaceByName(String name){
        loadCachedRaces();
        for(AlienRace race : races){
            if(race.getName().toUpperCase().equals(name.toUpperCase())){
                return race;
            }
        }
        return null;
    }

    public static AlienRace getRaceById(String id){
        loadCachedRaces();
        for(AlienRace race : races){
            if(race.getObjectId().equals(id)){
                return race;
            }
        }
        return null;
    }

    public static int getRacePosition(String id){
        loadCachedRaces();
        for(int i = 0; i < races.size(); i++){
            if(races.get(i).getObjectId().equals(id)){
                return i;
            }
        }
        return -1;
    }

    public static List<String> getRacesName(){
        loadCachedRaces();
        List<String> racesName = new ArrayList<>();
        for(AlienRace race : races){
            racesName.add(race.getName());
        }
        return racesName;
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
                .addAscendingOrder("word")
                .setLimit(PAGE_SIZE_TRANSLATION)
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

    public static void getUserTranslations(User user, AlienRace race, AlienWord word, FindCallback<AlienWordTranslation> callback){
        ParseQuery.getQuery(AlienWordTranslation.class)
                .whereEqualTo("users", user)
                .whereEqualTo("race", race)
                .whereEqualTo("word", word)
                .addAscendingOrder("word")
                .addDescendingOrder("_created_at")
                .findInBackground(callback);
    }

    public static AlienWordTranslation getUserTranslation(User user, String language, AlienWord word, AlienRace race){
        try {
            return ParseQuery.getQuery(AlienWordTranslation.class)
                    .whereEqualTo("users", user)
                    .whereEqualTo("race", race)
                    .whereEqualTo("word", word)
                    .whereEqualTo("language", language)
                    .orderByDescending("_created_at")
                    .getFirst();
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
}