package cafe.adriel.nmsalphabet.util;

import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cafe.adriel.nmsalphabet.App;
import cafe.adriel.nmsalphabet.model.AlienRace;
import cafe.adriel.nmsalphabet.model.AlienWord;
import cafe.adriel.nmsalphabet.model.AlienWordTranslation;
import cafe.adriel.nmsalphabet.model.User;

public class DbUtil {
    public static int PAGE_SIZE_RACE = 100;
    public static int PAGE_SIZE_LIKE_DISLIKE = 100;
    public static int PAGE_SIZE_WORD = 50;
    public static int PAGE_SIZE_TRANSLATION = 5;

    private static List<AlienRace> races;
    private static Set<String> likes;
    private static Set<String> dislikes;

    private static void loadCachedData(){
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
        if(likes == null){
            likes = new HashSet<>();
            try {
                List<AlienWordTranslation> translations = ParseQuery.getQuery(AlienWordTranslation.class)
                        .whereEqualTo("likes", App.getUser())
                        .fromLocalDatastore()
                        .find();
                for(AlienWordTranslation translation : translations){
                    likes.add(translation.getObjectId());
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        if(dislikes == null){
            dislikes = new HashSet<>();
            try {
                List<AlienWordTranslation> translations = ParseQuery.getQuery(AlienWordTranslation.class)
                        .whereEqualTo("dislikes", App.getUser())
                        .fromLocalDatastore()
                        .find();
                for(AlienWordTranslation translation : translations){
                    dislikes.add(translation.getObjectId());
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public static void cacheData(){
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
        try {
            final List<AlienWordTranslation> likes = ParseQuery.getQuery(AlienWordTranslation.class)
                    .whereEqualTo("likes", App.getUser())
                    .setLimit(PAGE_SIZE_LIKE_DISLIKE)
                    .find();
            final List<AlienWordTranslation> dislikes = ParseQuery.getQuery(AlienWordTranslation.class)
                    .whereEqualTo("dislikes", App.getUser())
                    .setLimit(PAGE_SIZE_LIKE_DISLIKE)
                    .find();
            AlienWordTranslation.unpinAllInBackground(new DeleteCallback() {
                @Override
                public void done(ParseException e) {
                    AlienWordTranslation.pinAllInBackground(likes);
                    AlienWordTranslation.pinAllInBackground(dislikes);
                }
            });
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static AlienRace getRaceByName(String name){
        loadCachedData();
        for(AlienRace race : races){
            if(race.getName().toUpperCase().equals(name.toUpperCase())){
                return race;
            }
        }
        return null;
    }

    public static AlienRace getRaceById(String id){
        loadCachedData();
        for(AlienRace race : races){
            if(race.getObjectId().equals(id)){
                return race;
            }
        }
        return null;
    }

    public static int getRacePosition(String id){
        loadCachedData();
        for(int i = 0; i < races.size(); i++){
            if(races.get(i).getObjectId().equals(id)){
                return i;
            }
        }
        return -1;
    }

    public static List<String> getRacesName(){
        loadCachedData();
        List<String> racesName = new ArrayList<>();
        for(AlienRace race : races){
            racesName.add(race.getName());
        }
        return racesName;
    }

    public static boolean isTranslationLiked(AlienWordTranslation translation){
        loadCachedData();
        return likes.contains(translation.getObjectId());
    }

    public static boolean isTranslationDisliked(AlienWordTranslation translation){
        loadCachedData();
        return dislikes.contains(translation.getObjectId());
    }

    public static void likeTranslation(AlienWordTranslation translation){
        likes.add(translation.getObjectId());
        dislikes.remove(translation.getObjectId());
        translation.addLike(App.getUser());
        translation.removeDislike(App.getUser());
        translation.saveEventually();
    }

    public static void dislikeTranslation(AlienWordTranslation translation){
        dislikes.add(translation.getObjectId());
        likes.remove(translation.getObjectId());
        translation.addDislike(App.getUser());
        translation.removeLike(App.getUser());
        translation.saveEventually();
    }

    public static void getWords(String word, AlienRace race, int page, FindCallback<AlienWord> callback){
        ParseQuery<AlienWord> query = ParseQuery.getQuery(AlienWord.class);
        if(Util.isNotEmpty(word)){
            query.whereEqualTo("word", word);
        }
        if(race != null){
            query.whereEqualTo("race", race);
        }
        query.whereGreaterThan("usersCount", 0)
                .addAscendingOrder("word")
                .addAscendingOrder("_updated_at")
                .setLimit(PAGE_SIZE_WORD)
                .setSkip(PAGE_SIZE_WORD * page)
                .findInBackground(callback);
    }

    public static void getWordsByUser(User user, int page, FindCallback<AlienWord> callback){
        ParseQuery.getQuery(AlienWord.class)
                .whereEqualTo("users", user)
                .whereGreaterThan("usersCount", 0)
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
                .whereGreaterThan("usersCount", 0)
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