package cafe.adriel.nmsalphabet.util;

import com.parse.FindCallback;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cafe.adriel.nmsalphabet.App;
import cafe.adriel.nmsalphabet.model.AlienRace;
import cafe.adriel.nmsalphabet.model.AlienWord;
import cafe.adriel.nmsalphabet.model.AlienWordTranslation;
import cafe.adriel.nmsalphabet.model.User;

public class DbUtil {
    public static int PAGE_SIZE_RACE            = 100;
    public static int PAGE_SIZE_LIKE_DISLIKE    = 100;
    public static int PAGE_SIZE_WORD            = 50;
    public static int PAGE_SIZE_TRANSLATION     = 5;

    private static List<AlienRace> races;
    private static Set<String> likes;
    private static Set<String> dislikes;

    public static void cacheData(){
        try {
            List<AlienRace> races = ParseQuery.getQuery(AlienRace.class)
                    .addAscendingOrder("name")
                    .setLimit(PAGE_SIZE_RACE)
                    .find();
            AlienRace.unpinAll();
            AlienRace.pinAllInBackground(races);
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
    }

    public static void loadUserLikesAndDislikes(){
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

            likes = new HashSet<>();
            dislikes = new HashSet<>();

            for(AlienWordTranslation translation : likedTranslations){
                likes.add(translation.getObjectId());
            }
            for(AlienWordTranslation translation : dislikedTranslations){
                dislikes.add(translation.getObjectId());
            }
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
        translation.saveInBackground();
    }

    public static void dislikeTranslation(AlienWordTranslation translation){
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
                .addDescendingOrder("likesCount")
                .addAscendingOrder("dislikesCount")
                .addAscendingOrder("word")
                .addDescendingOrder("_created_at")
                .setLimit(PAGE_SIZE_WORD)
                .setSkip(PAGE_SIZE_WORD * page)
                .findInBackground(callback);
    }

    public static void getWordsByUser(User user, int page, FindCallback<AlienWord> callback){
        ParseQuery.getQuery(AlienWord.class)
                .whereEqualTo("users", user)
                .whereGreaterThan("usersCount", 0)
                .addAscendingOrder("word")
                .addDescendingOrder("_created_at")
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
                    .addDescendingOrder("_created_at")
                    .getFirst();
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
}