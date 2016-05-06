package cafe.adriel.nmsalphabet.model;

import com.parse.ParseClassName;
import com.parse.ParseObject;

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

}