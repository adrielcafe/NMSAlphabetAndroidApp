package cafe.adriel.nmsalphabet.model;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseRelation;

import java.io.Serializable;

@ParseClassName("AlienWordTranslation")
public class AlienWordTranslation extends ParseObject implements Serializable {

    public String getTranslation(){
        return getString("translation");
    }

    public void setTranslation(String translation){
        put("translation", translation);
    }

    public String getLanguage(){
        return getString("language");
    }

    public void setLanguage(String language){
        put("language", language);
    }

    public AlienWord getWord(){
        return (AlienWord) getParseObject("word");
    }

    public void setWord(AlienWord word){
        put("word", word);
    }

    public AlienRace getRace(){
        return (AlienRace) getParseObject("race");
    }

    public void setRace(AlienRace race){
        put("race", race);
    }

    public int getUsersCount(){
        int count = getInt("usersCount");
        return count < 0 ? 0 : count;
    }

    public int getLikesCount(){
        int count = getInt("likesCount");
        return count < 0 ? 0 : count;
    }

    public int getDislikesCount(){
        int count = getInt("dislikesCount");
        return count < 0 ? 0 : count;
    }

    public ParseRelation getUsers() {
        return getRelation("users");
    }

    public ParseRelation getLikes() {
        return getRelation("likes");
    }

    public ParseRelation getDislikes() {
        return getRelation("dislikes");
    }

    public void addUser(User user){
        if(getUsers() != null) {
            getUsers().add(user);
        }
    }

    public void removeUser(User user){
        if(getUsers() != null) {
            getUsers().remove(user);
        }
    }

    public void addLike(User user){
        if(getLikes() != null) {
            getLikes().add(user);
        }
    }

    public void removeLike(User user){
        if(getLikes() != null) {
            getLikes().remove(user);
        }
    }

    public void addDislike(User user){
        if(getDislikes() != null) {
            getDislikes().add(user);
        }
    }

    public void removeDislike(User user){
        if(getDislikes() != null) {
            getDislikes().remove(user);
        }
    }

}