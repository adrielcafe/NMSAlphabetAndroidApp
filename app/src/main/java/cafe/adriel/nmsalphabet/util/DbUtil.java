package cafe.adriel.nmsalphabet.util;

import com.parse.FindCallback;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import cafe.adriel.nmsalphabet.App;
import cafe.adriel.nmsalphabet.model.AlienRace;
import cafe.adriel.nmsalphabet.model.AlienWord;
import cafe.adriel.nmsalphabet.model.AlienWordTranslation;
import cafe.adriel.nmsalphabet.model.User;
import io.paperdb.Paper;

public class DbUtil {
    public static int PAGE_SIZE_LIKE_DISLIKE        = 100;
    public static int PAGE_SIZE_RACES               = 100;
    public static int PAGE_SIZE_WORDS               = 50;
    public static int PAGE_SIZE_TRANSLATIONS        = 50;
    public static int PAGE_SIZE_BEST_TRANSLATIONS   = 3;

    private static List<AlienRace> races;
    private static LinkedList<String> likes;
    private static LinkedList<String> dislikes;

    public static void cacheData(){
        Paper.book().destroy();
        try {
            List<AlienRace> races = ParseQuery.getQuery(AlienRace.class)
                    .addAscendingOrder("name")
                    .setLimit(PAGE_SIZE_RACES)
                    .find();
            AlienRace.unpinAll();
            AlienRace.pinAllInBackground(races);
        } catch (Exception e){
            e.printStackTrace();
        }
        try {
            List<AlienWordTranslation> likedTranslations = ParseQuery.getQuery(AlienWordTranslation.class)
                    .whereEqualTo("likes", App.getUser())
                    .selectKeys(Arrays.asList("objectId"))
                    .setLimit(PAGE_SIZE_LIKE_DISLIKE)
                    .find();
            List<AlienWordTranslation> dislikedTranslations = ParseQuery.getQuery(AlienWordTranslation.class)
                    .whereEqualTo("dislikes", App.getUser())
                    .selectKeys(Arrays.asList("objectId"))
                    .setLimit(PAGE_SIZE_LIKE_DISLIKE)
                    .find();

            likes = new LinkedList<>();
            dislikes = new LinkedList<>();

            for(AlienWordTranslation translation : likedTranslations){
                likes.add(translation.getObjectId());
            }
            for(AlienWordTranslation translation : dislikedTranslations){
                dislikes.add(translation.getObjectId());
            }

            Paper.book().write("likes", likes);
            Paper.book().write("dislikes", dislikes);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

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
            likes = Paper.book().read("likes", new LinkedList<String>());
        }
        if(dislikes == null){
            dislikes = Paper.book().read("dislikes", new LinkedList<String>());
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
        loadCachedData();
        likes.add(translation.getObjectId());
        dislikes.remove(translation.getObjectId());
        translation.addLike(App.getUser());
        translation.removeDislike(App.getUser());
        translation.saveInBackground();
    }

    public static void dislikeTranslation(AlienWordTranslation translation){
        loadCachedData();
        dislikes.add(translation.getObjectId());
        likes.remove(translation.getObjectId());
        translation.addDislike(App.getUser());
        translation.removeLike(App.getUser());
        translation.saveInBackground();
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
                .addDescendingOrder("usersCount")
                .addAscendingOrder("word")
                .addDescendingOrder("_created_at")
                .setLimit(PAGE_SIZE_WORDS)
                .setSkip(PAGE_SIZE_WORDS * page)
                .findInBackground(callback);
    }

    public static void getWordsByUser(User user, int page, FindCallback<AlienWord> callback){
        ParseQuery.getQuery(AlienWord.class)
                .whereEqualTo("users", user)
                .addAscendingOrder("word")
                .addDescendingOrder("_created_at")
                .setLimit(PAGE_SIZE_WORDS)
                .setSkip(PAGE_SIZE_WORDS * page)
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

    public static void getBestTranslations(AlienRace race, AlienWord word, String language, FindCallback<AlienWordTranslation> callback){
        ParseQuery.getQuery(AlienWordTranslation.class)
                .whereEqualTo("race", race)
                .whereEqualTo("word", word)
                .whereEqualTo("language", language)
                .whereGreaterThan("likesCount", 0)
                .addDescendingOrder("likesCount")
                .addAscendingOrder("dislikesCount")
                .addAscendingOrder("word")
                .setLimit(PAGE_SIZE_BEST_TRANSLATIONS)
                .findInBackground(callback);
    }

    public static void getTranslations(AlienRace race, AlienWord word, String language, int page, FindCallback<AlienWordTranslation> callback){
        ParseQuery.getQuery(AlienWordTranslation.class)
                .whereEqualTo("race", race)
                .whereEqualTo("word", word)
                .whereEqualTo("language", language)
                .addDescendingOrder("likesCount")
                .addAscendingOrder("dislikesCount")
                .addAscendingOrder("word")
                .setLimit(PAGE_SIZE_TRANSLATIONS)
                .setSkip(PAGE_SIZE_TRANSLATIONS * page)
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
                    .addDescendingOrder("_created_at")
                    .getFirst();
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
}