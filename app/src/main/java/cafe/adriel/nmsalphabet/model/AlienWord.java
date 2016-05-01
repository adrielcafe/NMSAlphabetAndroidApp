package cafe.adriel.nmsalphabet.model;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseRelation;

import java.io.Serializable;

@ParseClassName("AlienWord")
public class AlienWord extends ParseObject implements Serializable {

    public String getWord(){
        return getString("word");
    }

    public void setWord(String word){
        put("word", word);
    }

    public AlienRace getRace(){
        return (AlienRace) getParseObject("race");
    }

    public void setRace(AlienRace race){
        put("race", race);
    }

    public ParseRelation getUsers() {
        return getRelation("users");
    }

    public void addUser(User user){
        getUsers().add(user);
    }

    public void removeUser(User user){
        getUsers().remove(user);
    }

}