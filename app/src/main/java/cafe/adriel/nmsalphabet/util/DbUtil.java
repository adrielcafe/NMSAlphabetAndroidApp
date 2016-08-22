package cafe.adriel.nmsalphabet.util;

import com.parse.ParseCloud;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import cafe.adriel.nmsalphabet.model.AlienRace;
import cafe.adriel.nmsalphabet.model.AlienWordTranslation;
import io.paperdb.Paper;

public class DbUtil {
    public static int PAGE_SIZE_LIKE_DISLIKE        = 100;
    public static int PAGE_SIZE_RACES               = 100;

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
//                    .whereEqualTo("likes", App.getUser())
                    .selectKeys(Collections.singletonList("objectId"))
                    .setLimit(PAGE_SIZE_LIKE_DISLIKE)
                    .find();
            List<AlienWordTranslation> dislikedTranslations = ParseQuery.getQuery(AlienWordTranslation.class)
//                    .whereEqualTo("dislikes", App.getUser())
                    .selectKeys(Collections.singletonList("objectId"))
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

    public static AlienRace getRaceByPosition(int position){
        loadCachedData();
        if(races.size() < position){
            return races.get(position);
        } else {
            return null;
        }
    }

    public static List<String> getRacesName(){
        loadCachedData();
        List<String> racesName = new ArrayList<>();
        for(AlienRace race : races){
            racesName.add(race.getName());
        }
        return racesName;
    }

    public static Map<String, AlienWordTranslation> translateWords(List<String> words, AlienRace race, String language){
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("words", words);
            params.put("raceId", race.getObjectId());
            params.put("language", language);
            return ParseCloud.callFunction("translateWords", params);
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
}